package com.ditto.report_browse.employee.service.impl;

import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.ditto.report_browse.employee.entity.TripFiling;
import com.ditto.report_browse.employee.mapper.TripFilingMapper;
import com.ditto.report_browse.tex_component.tex_import.check.HeadCheck;
import com.ditto.report_browse.tex_component.tex_import.check.RelationCheck;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class TripFilingListener extends AnalysisEventListener<TripFiling> {
    private final TripFilingMapper tripFilingMapper;
    private List<TripFiling> list = new ArrayList<>();
    private static final int BATCH_SIZE = 300;

    // 构造器注入Mapper
    public TripFilingListener(TripFilingMapper tripFilingMapper) {
        this.tripFilingMapper = tripFilingMapper;
    }

    @Override
    public void invoke(TripFiling data, AnalysisContext context) {
        RelationCheck.check().throwsCheckEasyExcel(data);
        list.add(data);
        if (list.size() >= BATCH_SIZE) {
            batchInsert();
            list.clear();
        }
    }

    @Override
    public void doAfterAllAnalysed(AnalysisContext context) {
        if (!list.isEmpty()) {
            batchInsert();
        }
    }


    public void invokeHeadMap(Map<Integer, String> headMap, AnalysisContext context) {
        HeadCheck.check().throwsCheckEasyExcel(context.readRowHolder().getRowIndex(),headMap);
    }

    private void batchInsert() {
        tripFilingMapper.insert(list);
    }


}
