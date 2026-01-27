package com.ditto.report_browse.employee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplate;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplateCell;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

public interface PersonService {


   void HMC_XZ(HttpServletResponse response);
   void HMC_XZ_S(HttpServletResponse response);
   void HMC_XZ_N(HttpServletResponse response);
   void HMC_XZ_E(HttpServletResponse response);
   void RY_CJ(HttpServletResponse response);
   void RY_CJ_N(HttpServletResponse response);
   void RY_CJ_S(HttpServletResponse response);
   List<TexTemplateCell> head(String templateCode);
   Page<Map<String, Object>> query(String templateCode, int pageNum, int pageSize);

}
