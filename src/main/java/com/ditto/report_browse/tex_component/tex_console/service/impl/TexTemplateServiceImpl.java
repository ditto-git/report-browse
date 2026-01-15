package com.ditto.report_browse.tex_component.tex_console.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplate;
import com.ditto.report_browse.tex_component.tex_console.mapper.TexTemplateMapper;
import com.ditto.report_browse.tex_component.tex_console.service.TexTemplateCellService;
import com.ditto.report_browse.tex_component.tex_console.service.TexTemplateService;
import com.ditto.report_browse.tex_component.tex_util.TexThreadLocal;
import com.ditto.report_browse.tex_component.tex_util.request.ImportFileMultipartUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.ditto.report_browse.tex_component.tex_console.constants.TexConstants.MAINTAIN;


/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ditto
 * @since 2025-08-17
 */
@Service
@Slf4j
public class TexTemplateServiceImpl extends ServiceImpl<TexTemplateMapper, TexTemplate> implements TexTemplateService {







    @Autowired
    private TexTemplateCellService texTemplateCellService;

    @Autowired
    private TexTemplateMapper texTemplateMapper;


    public void initExTemplate(TexTemplate texTemplate){
        texTemplate.setTemplateStatus(MAINTAIN);
        save(texTemplate);
    }


    public boolean notDuplicate(TexTemplate texTemplate){
        return lambdaQuery().eq(TexTemplate::getTemplateCode, texTemplate.getTemplateCode()).list().isEmpty();
    }



    public void replaceExTemplate(ImportFileMultipartUtil importFileMultipartUtil) {
        TexTemplate texTemplate =getById(importFileMultipartUtil.getFileName());

        //是否初始化
        if(texTemplate ==null){ return;}

        //模板状态0维护中 乐观锁
        if(!this.maintenance(importFileMultipartUtil.getFileName())){return;}
        log.info("{}...乐观锁..........");

        //缓存exTemplate
        TexThreadLocal.setExTemplate(texTemplate);

        /*OSS插入模板文档, 数据库插入模板内容*/
        try {
            texTemplateCellService.replaceExTemplate(importFileMultipartUtil);
        }catch (Exception e){
            log.error("{}...模板解析失败.....", texTemplate.getTemplateUrl(),e);
        }finally {
            //清楚缓存exTemplate
            TexThreadLocal.clear();
            //模板状态1使用中 解乐观锁
            this.lambdaUpdate().set(TexTemplate::getTemplateStatus,1).eq(TexTemplate::getTemplateCode, texTemplate.getTemplateCode()).update();
            log.info("{}...释放乐观锁..........", texTemplate.getTemplateUrl());
        }

    }








    public TexTemplate getExTemplate(String templateCode){
         return texTemplateMapper.getExTemplate(templateCode);
    }


    private boolean maintenance(String templateCode){
      //  return this.lambdaUpdate().set(ExTemplate::getTemplateStatus, 0).eq(ExTemplate::getTemplateStatus, 1).update();
        return texTemplateMapper.maintenance(templateCode)>0;
    }





}
