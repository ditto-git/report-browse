package com.ditto.report_browse.tex_component.tex_console.exListener;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;

import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplate;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplateCell;
import com.ditto.report_browse.tex_component.tex_console.mapper.TexTemplateCellMapper;
import com.ditto.report_browse.tex_component.tex_console.mapper.TexTemplateMapper;
import com.ditto.report_browse.tex_component.tex_util.TexThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.ditto.report_browse.tex_component.tex_console.constants.TexConstants.*;


@Slf4j
@Component
@Scope("prototype")
public class ExTemplateModelDataListener extends AnalysisEventListener<Map<Integer, String>> {



    @Autowired
    private TexTemplateCellMapper hexTemplateCellMapper;
    @Autowired
    private TexTemplateMapper exTemplateMapper;

    private static final String RC_CONNECT="&";
    private static final String CODE_CONNECT="_";
    private static final String PROPERTY_STARTS_WITH="{";
    private static final String PROPERTY_ENDS_WITH="}";
    private static final int PROPERTY_MIN_SIZE=3;


 


    @Override
    public void invoke(Map<Integer, String> data, AnalysisContext context) {
        TexTemplate texTemplate = TexThreadLocal.getExTemplate();
        log.info("{}...模板解析中...........", texTemplate.getTemplateUrl());
        data.forEach((key, value) -> {
            if (!StringUtils.hasText(value)) return;
            boolean contains = value.startsWith(PROPERTY_STARTS_WITH) && value.endsWith(PROPERTY_ENDS_WITH);
            if (contains) {
                if (value.length() <PROPERTY_MIN_SIZE){return;}
                //正则
                value = value.substring(1, value.length() - 1);
                TexTemplateCell exc =new TexTemplateCell();
                exc.setTemplateCode(texTemplate.getTemplateCode());
                exc.setCellStartRow(context.readRowHolder().getRowIndex().toString());
                exc.setCellStartCol(key.toString());
                exc.setCellProperty(value);
                TexThreadLocal.getExCells().add(exc);
            } else if (!value.trim().isEmpty()) {

                TexThreadLocal.getExHead().put(context.readRowHolder().getRowIndex().toString() + RC_CONNECT + key, value);
            }
        });

    }

    @Override
    @Transactional
    public void doAfterAllAnalysed(AnalysisContext context) {
        String v ;
        String headCell_r;
        String headCell_c;
        String head_r;
        String head_c;
        List<TexTemplateCell> hexTemplateCells = TexThreadLocal.getExCells();
        Map<String, String> exTemplateHead = TexThreadLocal.getExHead();

        for(TexTemplateCell exc: hexTemplateCells){
            v=exc.getCellProperty();
            headCell_r= exc.getCellStartRow() +RC_CONNECT+(Integer.parseInt(exc.getCellStartCol()) - 1);
            headCell_c= Integer.parseInt(exc.getCellStartRow())-1 +RC_CONNECT+ exc.getCellStartCol();
            head_r=exTemplateHead.get(headCell_r);
            head_c=exTemplateHead.get(headCell_c);
            //集合数据（横/列）
            if(v.startsWith(".")){
                if(head_c!=null){
                    exc.setHeadContent(head_c);
                    exc.setCellHead(headCell_c);
                    exc.setCellIndex(exc.getCellStartCol());
                    exc.setCellStartCol(null);
                }
                if(head_r!=null&&!head_r.contains("=")&&!head_r.contains("\\$")){
                    exc.setHeadContent(head_r);
                    exc.setCellHead(headCell_r);
                    exc.setCellIndex(exc.getCellStartRow());
                    exc.setCellStartRow(null);
                }
                exc.setCellProperty(v.substring(1));
            }else {
                //单元数据
                if(head_r!=null){
                    exc.setHeadContent(head_r);
                    exc.setCellHead(headCell_r);
                }
                if(head_c!=null){
                    exc.setHeadContent(head_c);
                    exc.setCellHead(headCell_c);
                }
            }
            exc.setCellCode(exc.getTemplateCode()+CODE_CONNECT+exc.getCellProperty());


        }


        int cCount=0;
        int rCount =0;

        for (int i = 0; i < hexTemplateCells.size(); i++) {
            TexTemplateCell cell = hexTemplateCells.get(i);
            if (cell.getCellStartCol() != null) {
                rCount++;
            }
            if (cell.getCellStartRow() != null) {
                cCount++;
            }
        }

        if(cCount== hexTemplateCells.size()){
            TexThreadLocal.getExTemplate().setTemplateType(TEMPLATE_TYPE_COlUMN);
        } else if(rCount == hexTemplateCells.size()){
            TexThreadLocal.getExTemplate().setTemplateType(TEMPLATE_TYPE_ROW);
        }else {
            TexThreadLocal.getExTemplate().setTemplateType(TEMPLATE_TYPE_X);
        }

        log.info("...所有数据解析完成！");

      saveData();

    }

    /**
     * ExTemplate存储数据库
     */
    @Transactional
    public void saveData() {
        List<TexTemplateCell> hexTemplateCells = TexThreadLocal.getExCells();
        TexTemplate texTemplate = TexThreadLocal.getExTemplate();
        if(hexTemplateCells.isEmpty()){log.info("{}...无参数的模板............", texTemplate.getTemplateUrl());return;}


        //查看现有TemplateCell
        QueryWrapper<TexTemplateCell> queryWrapper = new QueryWrapper<>();
        queryWrapper.lambda().eq(TexTemplateCell::getTemplateCode, texTemplate.getTemplateCode());
        List<TexTemplateCell> selectCells = hexTemplateCellMapper.selectList(queryWrapper);

        List<String> deleteIds = new ArrayList<>();
        //insertOrUpdate不识别updateStrategy   遍历,只更新下列参数
        String cell_r;
        String cell_c;
        String cell_i;
        String cell_head;
        String headContent;
        A:
        for (TexTemplateCell sexc : selectCells) {
            /* if(exTemplateCells.stream().noneMatch(e -> e.getCellCode().equals(exTemplateCell.getCellCol()))){}*/
            for (TexTemplateCell exc : hexTemplateCells) {
                if (sexc.getCellCode().equals(exc.getCellCode())) {
                    cell_c = exc.getCellStartCol();
                    cell_r = exc.getCellStartRow();
                    cell_i = exc.getCellIndex();
                    cell_head = exc.getCellHead();
                    headContent = exc.getHeadContent();
                    BeanUtils.copyProperties(sexc, exc);
                    exc.setCellStartCol(cell_c);
                    exc.setCellStartRow(cell_r);
                    exc.setCellIndex(cell_i);
                    exc.setCellHead(cell_head);
                    exc.setHeadContent(headContent);
                    continue A;
                }
            }
            //TemplateCell不在新的模板中 就删除
            deleteIds.add(sexc.getCellCode());

        }
        /*DCU*/
        if(!deleteIds.isEmpty()){
            hexTemplateCellMapper.deleteByIds(deleteIds);}
        hexTemplateCellMapper.insertOrUpdate(hexTemplateCells);
        exTemplateMapper.updateById(texTemplate);
        log.info("{}...存储数据库成功！", texTemplate.getTemplateUrl());
    }
}
