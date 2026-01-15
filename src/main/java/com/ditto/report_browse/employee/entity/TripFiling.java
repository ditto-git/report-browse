package com.ditto.report_browse.employee.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.ditto.report_browse.tex_component.tex_import.annotation.TEX_Relation;
import com.ditto.report_browse.tex_component.tex_import.annotation.TEX_Template;
import lombok.Data;

@Data
@TableName("trip_filing")
@TEX_Template(cellStartRow=9)
public class TripFiling {

    @TableId
    private String id;
    // 组织
    @TEX_Template(cellHead="B3",headContent = "部门",cellIndex = 1)
    @ExcelProperty(index =1 )
    @TEX_Relation(property = "org")
    private String org;

    // 姓名
    @TEX_Template(cellHead="C4",headContent = "姓名",cellIndex = 2)
    @ExcelProperty(index =2 )
    private String name;

    // 身份证号
    @TEX_Template(cellHead="D4",headContent = "身份证号",cellIndex = 3)
    @ExcelProperty(index =3 )
    private String idNumber;

    //人员编号
    @TEX_Template(cellHead="E4",headContent = "员工编号",cellIndex = 4)
    @ExcelProperty(index =4 )
    private String pernr;

    //所在国家
    @TEX_Relation(property="szGJ")
    @TEX_Template(cellHead="F3",headContent = "所在国家",cellIndex = 5)
    @ExcelProperty(index =5 )
    private String szAddress;

    //出行国家
    @TEX_Relation(property="cxGJ")
    @TEX_Template(cellHead="G3",headContent = "出行国家（地区）" ,cellIndex = 6)
    @ExcelProperty(index =6 )
    private String cxAddress;

    // 日期
    @TEX_Template(cellHead="H5",headContent = "出发日期",cellIndex = 7)
    @ExcelProperty(index =7 )
    private String date;

    // 交通工具
    @TEX_Template(cellHead="I5",headContent = "交通工具",cellIndex = 8)
    @ExcelProperty(index =8 )
    @TEX_Relation(property = "vehicle")
    private String vehicle;

    // 航班号
    @TEX_Template(cellHead="J5",headContent = "航班号（车次等）",cellIndex = 9)
    @ExcelProperty(index =9 )
    private String flightNumber;

    // 出发地
    @TEX_Relation(property="szCity")
    @ExcelProperty(index =10 )
    @TEX_Template(cellHead="K5",headContent = "出发地",cellIndex = 10)
    private String dep;

    // 目的地
    @TEX_Relation(property="cxCity",parentField = "cxAddress")
    @ExcelProperty(index =11 )
    @TEX_Template(cellHead="L5",headContent = "目的地",cellIndex = 11)
    private String dst;

    // 省份
    @TEX_Template(cellHead="M5",headContent = "省",cellIndex = 12)
    @ExcelProperty(index =12 )
    @TEX_Relation(property="province")
    private String province;

    // 城市
    @TEX_Template(cellHead="N5",headContent = "地市",cellIndex = 13)
    @ExcelProperty(index =13 )
    @TEX_Relation(property="city",parentField = "province")
    private String city;

    // 地区
    @ExcelProperty(index =14 )
    @TEX_Template(cellHead="O3",headContent = "其他需要说明的情况",cellIndex = 14)
    private String quit;



}
