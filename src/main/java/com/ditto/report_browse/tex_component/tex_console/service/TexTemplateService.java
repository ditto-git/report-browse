package com.ditto.report_browse.tex_component.tex_console.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplate;
import com.ditto.report_browse.tex_component.tex_util.request.ImportFileMultipartUtil;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ditto
 * @since 2025-08-17
 */
public interface TexTemplateService extends IService<TexTemplate> {
     TexTemplate getExTemplate(String templateCode);
     void initExTemplate(TexTemplate texTemplate);
     void replaceExTemplate(ImportFileMultipartUtil importFileMultipartUtil);

}
