package com.w.t.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.w.t.entity.Report;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Packagename com.w.t.dao
 * @Classname ReportDao
 * @Description
 * @Authors Mr.Wu
 * @Date 2022/06/01 08:48
 * @Version 1.0
 */
@Mapper
public interface ReportDao extends BaseMapper<Report> {
}
