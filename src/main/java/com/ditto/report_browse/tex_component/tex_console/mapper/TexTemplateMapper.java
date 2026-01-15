package com.ditto.report_browse.tex_component.tex_console.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplate;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ditto
 * @since 2025-08-17
 */
@Mapper
public interface TexTemplateMapper extends BaseMapper<TexTemplate> {

    TexTemplate getExTemplate(@Param("t_code")String t_code);

    int maintenance(@Param("t_code")String t_code);

}
