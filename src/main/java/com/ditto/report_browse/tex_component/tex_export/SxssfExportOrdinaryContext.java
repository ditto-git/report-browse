package com.ditto.report_browse.tex_component.tex_export;


import com.ditto.report_browse.tex_component.tex_util.TexThreadLocal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
public class SxssfExportOrdinaryContext {

    @Autowired
    Map<String,SxssfExportOrdinary> SxssfExportOrdinary;


    public SxssfExportOrdinary sxssfExportOrdinary(){
        return SxssfExportOrdinary.get("SxssfExportOrdinary"+ TexThreadLocal.getExTemplate().getTemplateType());
    }


}
