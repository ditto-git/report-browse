package com.ditto.report_browse.employee.entity;

import com.alibaba.excel.annotation.ExcelProperty;
import com.alibaba.excel.annotation.write.style.ContentStyle;
import com.alibaba.excel.annotation.write.style.HeadRowHeight;
import com.alibaba.excel.enums.poi.BorderStyleEnum;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
@HeadRowHeight(20)
@ContentStyle(borderBottom = BorderStyleEnum.THIN,borderTop = BorderStyleEnum.THIN,borderRight = BorderStyleEnum.THIN)
public class Person {

    @ExcelProperty(value = "pernr",order = 1)
    private String pernr;
    @ExcelProperty(value = "name",order = 2)
    private String name;
    @ExcelProperty(value = "sex",order = 3)
    private String sex;
    @ExcelProperty(value = "birthday",order = 4)
    private String birthday;
    @ExcelProperty(value = "major",order = 5)
    private String major;
    @ExcelProperty(value = "org",order = 6)
    private String org;
    @ExcelProperty(value = "id_number",order = 2)
    private String id_number;



}
