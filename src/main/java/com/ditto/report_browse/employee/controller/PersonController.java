package com.ditto.report_browse.employee.controller;


import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ditto.report_browse.employee.service.PersonService;
import com.ditto.report_browse.tex_component.tex_console.entity.TexTemplateCell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.Mapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/Person")
public class PersonController {
    @Autowired
    private PersonService personService;

    @RequestMapping("/HMC_XZ_S")
    public void HMC_XZ_S(HttpServletResponse response) {
        personService.HMC_XZ_S(response);
    }
    @RequestMapping("/HMC_XZ_N")
    public void HMC_XZ_N(HttpServletResponse response) {
        personService.HMC_XZ_N(response);
    }
    @RequestMapping("/HMC_XZ_E")
    public void HMC_XZ_E(HttpServletResponse response) {
        personService.HMC_XZ_E(response);
    }

    @RequestMapping("/HMC_XZ")
    public void HMC_XZ(HttpServletResponse response) {
        personService.HMC_XZ(response);
    }

    @RequestMapping("/RY_CJ")
    public void RY_CJ(HttpServletResponse response) {
        personService.RY_CJ(response);
    }

    @RequestMapping("/RY_CJ_N")
    public void RY_CJ_N(HttpServletResponse response) {
        personService.RY_CJ_N(response);
    }

    @RequestMapping("/RY_CJ_S")
    public void RY_CJ_S(HttpServletResponse response) {
        personService.RY_CJ_S(response);
    }

    @GetMapping("/query-head")
    public List<TexTemplateCell> head(String templateCode) {
        return   personService.head(templateCode);
    }

    @GetMapping("/query")
    public Page<Map<String, Object>> query(String templateCode, int pageNum, int pageSize) {
      return  personService.query(templateCode,pageNum,pageSize);
    }










}
