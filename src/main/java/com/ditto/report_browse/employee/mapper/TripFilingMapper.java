package com.ditto.report_browse.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ditto.report_browse.employee.entity.Person;
import com.ditto.report_browse.employee.entity.TripFiling;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TripFilingMapper extends BaseMapper<TripFiling> {
}
