package com.ditto.report_browse.tex_component.tex_console.service;


import com.baomidou.mybatisplus.extension.service.IService;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplateCell;
import com.ditto.report_browse.tex_component.tex_util.request.ExportFileResponseUtil;
import com.ditto.report_browse.tex_component.tex_util.request.ImportFileMultipartUtil;


/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ditto
 * @since 2025-08-18
 */
public interface TexTemplateCellService extends IService<TexTemplateCell> {

     void replaceExTemplate(ImportFileMultipartUtil importFileMultipartUtil) ;

     void importExTemplateCell(ImportFileMultipartUtil importFileMultipartUtil) ;

     void exportExTemplateCell(ExportFileResponseUtil exportFileResponseUtil) ;

}
