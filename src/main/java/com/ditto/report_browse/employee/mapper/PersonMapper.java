package com.ditto.report_browse.employee.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ditto.report_browse.employee.entity.Person;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ditto
 * @since 2025-07-22
 */
@Mapper
public interface PersonMapper extends BaseMapper<Person> {

     List<Map<String, Object>> HMC_XZ(Page<Map<String, Object>> page);
     List<Map<String, Object>> HMC_XZ0();

     List<Map<String, Object>> HMC_XZ1();
     List<Map<String, Object>> HMC_XZ2();

     List<Map<String, Object>> RY_CJ(Page<Map<String, Object>> page);
     List<Map<String, Object>> RY_CJ1();
     List<Map<String, Object>> RY_CJ2();

}
