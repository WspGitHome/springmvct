package com.w.t.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.w.t.entity.Instruction;
import org.apache.ibatis.annotations.Mapper;

/**
 * @Packagename com.w.t.dao
 * @Classname InstructionDao
 * @Description
 * @Authors Mr.Wu
 * @Date 2022/06/01 08:49
 * @Version 1.0
 */
@Mapper
public interface InstructionDao extends BaseMapper<Instruction> {
}
