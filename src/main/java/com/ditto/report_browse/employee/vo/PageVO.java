package com.ditto.report_browse.employee.vo;

import lombok.Data;

/**
 * 全局通用分页参数VO
 * 所有需要分页的查询接口，都可以继承该类，无需重复编写分页参数
 * 完整分页必备参数，无任何冗余，适配所有业务场景
 */
@Data
public class PageVO {
    /**
     * 当前页码，默认值 1 (必填，前端不传默认查第一页)
     */
    private Integer pageNum = 1;

    /**
     * 每页显示条数，默认值 10 (必填，前端不传默认每页10条)
     */
    private Integer pageSize = 10;

    /**
     * 排序字段 (可选，比如：date、id、name)
     * 配合orderBy使用，前端传实体类的属性名即可，如：date
     */
    private String orderBy;

    /**
     * 排序方式 (可选，默认倒序)
     * asc = 升序  desc = 倒序  不传默认desc
     */
    private String sortType = "desc";
}