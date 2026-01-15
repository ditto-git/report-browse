package com.ditto.report_browse.tex_component.tex_console.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * <p>
 * 
 * </p>
 *
 * @author ditto
 * @since 2025-08-17
 */
@TableName("ex_template")
@Data
public class TexTemplate implements Serializable {

    private static final long serialVersionUID = 1L;


    /**
     * 模板编码
     */
    @TableId
    private String templateCode;

    /**
     * 模板路径
     */
    private String templateUrl;

    /**
     * 默认为空，mapper。 填写后查固定数据库表
     */
    private String templateTable;

    /**
     * 模板名
     */
    private String templateName;

    /**
     * 文件名
     */
    private String fileName;

    /**
     * 模板类型(0自定义表格，1纵向表，2横向表)
     */
    private String templateType;

    /**
     * 模板状态(0维护中，1使用中，2删除)
     */
    private String templateStatus;


    @TableField(exist = false)
    private List<TexTemplateCell> texTemplateCells;


}
