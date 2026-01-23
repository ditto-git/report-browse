package com.ditto.report_browse.tex_component.tex_util;



import lombok.AllArgsConstructor;
import lombok.Data;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
@Data
@AllArgsConstructor
public class TexFormula {


   private String  property;
   private Map<String, String> cellFormulas;
   private int weight ;

}
