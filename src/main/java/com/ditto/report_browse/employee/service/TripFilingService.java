package com.ditto.report_browse.employee.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ditto.report_browse.employee.entity.TripFiling;
import com.ditto.report_browse.employee.vo.TripFilingVO;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

public interface TripFilingService  {

      void readTemplate(HttpServletResponse response);

      void readData(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException;

      Page<TripFiling> query(TripFilingVO tripFiling);

}
