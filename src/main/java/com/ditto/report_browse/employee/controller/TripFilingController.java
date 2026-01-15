package com.ditto.report_browse.employee.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ditto.report_browse.employee.entity.TripFiling;
import com.ditto.report_browse.employee.service.TripFilingService;
import com.ditto.report_browse.employee.vo.TripFilingVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/TripFiling")
public class TripFilingController {
    @Autowired
    private TripFilingService tripFilingService;

    @RequestMapping("getTemplate")
   public void readTemplate(HttpServletResponse response){
        tripFilingService.readTemplate(response);
   }

    @RequestMapping("readData")
    public void readTempData(MultipartHttpServletRequest request, HttpServletResponse response) throws IOException {
        tripFilingService.readData(request,response);
    }

    @RequestMapping("query-tripFilings")
    public Page<TripFiling> query(TripFilingVO tripFilingVO)  {
       return tripFilingService.query(tripFilingVO);
    }





}
