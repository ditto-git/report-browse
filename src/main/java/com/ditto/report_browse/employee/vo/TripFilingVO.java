package com.ditto.report_browse.employee.vo;

import lombok.Data;

@Data
public class TripFilingVO extends PageVO{
    // 组织/部门
    private String org;

    // 姓名/身份证号/员工编号（模糊匹配）
    private String name;


}
