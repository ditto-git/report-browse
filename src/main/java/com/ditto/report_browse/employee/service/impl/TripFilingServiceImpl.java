package com.ditto.report_browse.employee.service.impl;

import com.alibaba.excel.EasyExcel;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ditto.report_browse.employee.entity.TripFiling;
import com.ditto.report_browse.employee.mapper.TripFilingMapper;
import com.ditto.report_browse.employee.service.TripFilingService;
import com.ditto.report_browse.employee.vo.TripFilingVO;
import com.ditto.report_browse.tex_component.tex_exception.TexException;
import com.ditto.report_browse.tex_component.tex_import.check.HeadCheck;
import com.ditto.report_browse.tex_component.tex_import.check.RelationCheck;
import com.ditto.report_browse.tex_component.tex_import.importTemp.Dict;
import com.ditto.report_browse.tex_component.tex_import.importTemp.RelationAnalysis;
import com.ditto.report_browse.tex_component.tex_util.request.ExportFileResponseUtil;
import com.ditto.report_browse.tex_component.tex_util.request.ImportFileMultipartUtil;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

import static com.ditto.report_browse.tex_component.tex_exception.TexExceptionEnum.FILE_EXPORT_ERROR;


@Service
public class TripFilingServiceImpl implements TripFilingService {
    @Autowired
    private TripFilingMapper tripFilingMapper;

    // 1. 热门省份列表（对应原 PROVINCE.get("TOP")）
    public static final List<Dict> HOT_PROVINCE_LIST = new ArrayList<>();

    // 2. 所有省份对应的城市列表（按原 PROVINCE_CITIES 的 value 顺序，直接存 Dict）
    // 注：若需区分"哪个省份下的城市"，可拆分为多个 List（如 BEIJING_CITY_LIST、GUANGDONG_CITY_LIST）
    public static final List<Dict> ALL_CITY_LIST = new ArrayList<>();

    // 3. 所有城市对应的区县列表（按原 CITY_DISTRICTS 的 value 顺序，直接存 Dict）
    public static final List<Dict> ALL_DISTRICT_LIST = new ArrayList<>();

    public static final List<Dict> SZ_GJ = new ArrayList<>();
    public static final List<Dict> CX_GJ = new ArrayList<>();
    public static final List<Dict> CX_CITY = new ArrayList<>();
    public static final List<Dict> SZ_CITY = new ArrayList<>();
    public static final List<Dict> VEHICLE=new ArrayList<>();

    public static final List<Dict> ORG=new ArrayList<>();

    public static final Map<String, List<Dict>> dicts = new HashMap<>();


    static {
        // 初始化热门省份（对应原 PROVINCE.put("TOP", ...) 的 value）
        HOT_PROVINCE_LIST.add(new Dict("北京",null));
        HOT_PROVINCE_LIST.add(new Dict("上海",null));
        HOT_PROVINCE_LIST.add(new Dict("广东省",null));
        dicts.put("province",HOT_PROVINCE_LIST);
        // 初始化所有城市（对应原 PROVINCE_CITIES 所有 value 的合并）
        // 北京市的城市
        ALL_CITY_LIST.add(new Dict("北京市","北京"));
        // 上海市的城市,"
        ALL_CITY_LIST.add(new Dict("上海市","上海"));
        // 广东省的城市
        ALL_CITY_LIST.add(new Dict("广州市","广东省"));
        ALL_CITY_LIST.add(new Dict("深圳市","广东省"));
        ALL_CITY_LIST.add(new Dict("佛山市","广东省"));
        dicts.put("city",ALL_CITY_LIST);
        // 可继续添加其他省份的城市（如江苏、浙江）,"广州市
//        ALL_CITY_LIST.add(new Dict("南京市","广州市"));
//        ALL_CITY_LIST.add(new Dict("苏州市","广州市"));
//        ALL_CITY_LIST.add(new Dict("杭州市","广州市"));
//        ALL_CITY_LIST.add(new Dict("宁波市","广州市"));

        // 初始化所有区县（对应原 CITY_DISTRICTS 所有 value 的合并）
        // 北京市的区县
        ALL_DISTRICT_LIST.add(new Dict("东城区","北京市"));
        ALL_DISTRICT_LIST.add(new Dict("西城区","北京市"));
        ALL_DISTRICT_LIST.add(new Dict("朝阳区","北京市"));
        ALL_DISTRICT_LIST.add(new Dict("海淀区","北京市"));
        // 上海市的区县
        ALL_DISTRICT_LIST.add(new Dict("黄浦区","上海市"));
        ALL_DISTRICT_LIST.add(new Dict("静安区","上海市"));
        ALL_DISTRICT_LIST.add(new Dict("徐汇区","上海市"));
        ALL_DISTRICT_LIST.add(new Dict("长宁区","上海市"));
        // 广州市的区县
        ALL_DISTRICT_LIST.add(new Dict("天河区","广州市"));
        ALL_DISTRICT_LIST.add(new Dict("越秀区","广州市"));
        ALL_DISTRICT_LIST.add(new Dict("海珠区","广州市"));
        ALL_DISTRICT_LIST.add(new Dict("番禺区","广州市"));
        // 深圳市的区县
        ALL_DISTRICT_LIST.add(new Dict("南山区","深圳市"));
        ALL_DISTRICT_LIST.add(new Dict("福田区","深圳市"));
        ALL_DISTRICT_LIST.add(new Dict("罗湖区","深圳市"));
        ALL_DISTRICT_LIST.add(new Dict("宝安区","深圳市"));
        // 佛山市的区县
        ALL_DISTRICT_LIST.add(new Dict("禅城区","佛山市"));
        ALL_DISTRICT_LIST.add(new Dict("南海区","佛山市"));
        ALL_DISTRICT_LIST.add(new Dict("顺德区","佛山市"));
        dicts.put("district",ALL_DISTRICT_LIST);

        SZ_GJ.add(new Dict("中国",null));
        dicts.put("szGJ",SZ_GJ);

        SZ_CITY.add(new Dict("北京市","中国"));
        SZ_CITY.add(new Dict("天津市","中国"));
        SZ_CITY.add(new Dict("上海市","中国"));
        SZ_CITY.add(new Dict("重庆市","中国"));
        SZ_CITY.add(new Dict("河北省","中国"));
        SZ_CITY.add(new Dict("山西省","中国"));
        SZ_CITY.add(new Dict("辽宁省","中国"));
        SZ_CITY.add(new Dict("吉林省","中国"));
        SZ_CITY.add(new Dict("黑龙江省","中国"));
        SZ_CITY.add(new Dict("江苏省","中国"));
        SZ_CITY.add(new Dict("浙江省","中国"));
        SZ_CITY.add(new Dict("安徽省","中国"));
        SZ_CITY.add(new Dict("福建省","中国"));
        SZ_CITY.add(new Dict("江西省","中国"));
        SZ_CITY.add(new Dict("山东省","中国"));
        SZ_CITY.add(new Dict("河南省","中国"));
        SZ_CITY.add(new Dict("湖北省","中国"));
        SZ_CITY.add(new Dict("湖南省","中国"));
        SZ_CITY.add(new Dict("广东省","中国"));
        SZ_CITY.add(new Dict("海南省","中国"));
        SZ_CITY.add(new Dict("四川省","中国"));
        SZ_CITY.add(new Dict("贵州省","中国"));
        SZ_CITY.add(new Dict("云南省","中国"));
        SZ_CITY.add(new Dict("陕西省","中国"));
        SZ_CITY.add(new Dict("甘肃省","中国"));
        SZ_CITY.add(new Dict("青海省","中国"));
        SZ_CITY.add(new Dict("台湾省","中国"));
        SZ_CITY.add(new Dict("内蒙古自治区","中国"));
        SZ_CITY.add(new Dict("广西壮族自治区","中国"));
        SZ_CITY.add(new Dict("西藏自治区","中国"));
        SZ_CITY.add(new Dict("宁夏回族自治区","中国"));
        SZ_CITY.add(new Dict("新疆维吾尔自治区","中国"));
        SZ_CITY.add(new Dict("香港特别行政区","中国"));
        SZ_CITY.add(new Dict("澳门特别行政区","中国"));
        dicts.put("szCity",SZ_CITY);

        CX_GJ.add(new Dict("中国_CN",null));
        CX_GJ.add(new Dict("美国_US",null));
        CX_GJ.add(new Dict("俄罗斯_RU",null));
        CX_GJ.add(new Dict("英国_GB",null));
        CX_GJ.add(new Dict("法国_FR",null));
        dicts.put("cxGJ",CX_GJ);


        CX_CITY.add(new Dict("北京市","中国_CN"));
        CX_CITY.add(new Dict("上海市","中国_CN"));
        CX_CITY.add(new Dict("广东省","中国_CN"));
        CX_CITY.add(new Dict("四川省","中国_CN"));
        CX_CITY.add(new Dict("西藏自治区","中国_CN"));
        CX_CITY.add(new Dict("加利福尼亚州","美国_US"));
        CX_CITY.add(new Dict("得克萨斯州","美国_US"));
        CX_CITY.add(new Dict("纽约州","美国_US"));
        CX_CITY.add(new Dict("佛罗里达州","美国_US"));
        CX_CITY.add(new Dict("伊利诺伊州","美国_US"));
        CX_CITY.add(new Dict("莫斯科市","俄罗斯_RU"));
        CX_CITY.add(new Dict("圣彼得堡市","俄罗斯_RU"));
        CX_CITY.add(new Dict("西伯利亚州","俄罗斯_RU"));
        CX_CITY.add(new Dict("克拉斯诺亚尔斯克边疆区","俄罗斯_RU"));
        CX_CITY.add(new Dict("鞑靼斯坦共和国","俄罗斯_RU"));
        CX_CITY.add(new Dict("大伦敦市","英国_GB"));
        CX_CITY.add(new Dict("英格兰伯明翰郡","英国_GB"));
        CX_CITY.add(new Dict("苏格兰爱丁堡区","英国_GB"));
        CX_CITY.add(new Dict("威尔士加的夫郡","英国_GB"));
        CX_CITY.add(new Dict("北爱尔兰贝尔法斯特郡","英国_GB"));
        CX_CITY.add(new Dict("法兰西岛大区","法国_FR"));
        CX_CITY.add(new Dict("普罗旺斯-阿尔卑斯-蓝色海岸大区","法国_FR"));
        CX_CITY.add(new Dict("奥弗涅-罗讷-阿尔卑斯大区","法国_FR"));
        CX_CITY.add(new Dict("新阿基坦大区","法国_FR"));
        CX_CITY.add(new Dict("布列塔尼大区","法国_FR"));
        dicts.put("cxCity",CX_CITY);


        VEHICLE.add(new Dict("列车_01",null));
        VEHICLE.add(new Dict("汽车_02",null));
        VEHICLE.add(new Dict("飞机_03",null));
        VEHICLE.add(new Dict("客轮_04",null));
        VEHICLE.add(new Dict("其他_05",null));
        dicts.put("vehicle",VEHICLE);

        ORG.add(new Dict("研发部_10036",null));
        ORG.add(new Dict("人事部_10037",null));
        ORG.add(new Dict("财务部_10038",null));
        ORG.add(new Dict("市场部_10039",null));
        ORG.add(new Dict("运营部_10040",null));
        ORG.add(new Dict("行政部_10041",null));
        ORG.add(new Dict("销售部_10042",null));
        ORG.add(new Dict("法务部_10059",null));
        ORG.add(new Dict("产品部_10050",null));
        ORG.add(new Dict("客服部_10051",null));
        ORG.add(new Dict("采购部_10052",null));
        ORG.add(new Dict("生产部_10053",null));
        dicts.put("org",ORG);




    }

    @Override
    public void readTemplate(HttpServletResponse response) {
        ExportFileResponseUtil.ResponseBuilder(response,"出行报备模板","xlsx");
        InputStream resourceAsStream= this.getClass().getClassLoader().getResourceAsStream("static/template/XC_BB_TEMP.xlsx");
        XSSFWorkbook workbook = null;
        try {
            workbook = new XSSFWorkbook(resourceAsStream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        workbook =(XSSFWorkbook) new RelationAnalysis(workbook, dicts).generateCascadeExcel(TripFiling.class);
        try (ServletOutputStream outputStream = response.getOutputStream()) {
            workbook.write(outputStream);
        } catch (Exception e) {
            throw new TexException(FILE_EXPORT_ERROR);
        }
    }

    @Override
    public void readData(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        InputStream inputStream = new ImportFileMultipartUtil(request, "file").getInputStream();

       // InputStream resourceAsStream= this.getClass().getClassLoader().getResourceAsStream("static/template/XC_BB_TEMP_DATA.xlsx");
    //   HeadCheck<TripFiling> tripFilingHeadCheck = new HeadCheck<>(TripFiling.class);
      //  tripFilingHeadCheck.interruptCheckPoi(resourceAsStream,0);s
        HeadCheck.create(TripFiling.class);
        RelationCheck.create(TripFiling.class,dicts);
        TripFilingListener filingListener = new TripFilingListener(tripFilingMapper);
        EasyExcel.read(inputStream, TripFiling.class,filingListener).headRowNumber(9)
                // 在 read 方法之后， 在 sheet方法之前都是设置ReadWorkbook的参数
                .sheet(0)
                .doRead();
    }

    @Override
    public Page<TripFiling>  query(TripFilingVO param) {
        List<TripFiling> dataList = Collections.emptyList();

        LambdaQueryWrapper<TripFiling> queryWrapper = new LambdaQueryWrapper<>();

// 2. 组织编码精准匹配
        if (StringUtils.hasText(param.getOrg())) {
            queryWrapper.eq(TripFiling::getOrg, param.getOrg());
        }

// 3. 封装【姓名/身份证/工号】模糊匹配公共逻辑，只写一次，全局复用
        if (StringUtils.hasText(param.getName())) {
            queryWrapper.and(wp -> wp.like(TripFiling::getName, param.getName())
                    .or().like(TripFiling::getIdNumber, param.getName())
                    .or().like(TripFiling::getPernr, param.getName()));
        }
        queryWrapper.orderByDesc(TripFiling::getDate);
        Page<TripFiling> page = new Page<>(param.getPageNum(), param.getPageSize());
        return tripFilingMapper.selectPage(page, queryWrapper);
    }


}
