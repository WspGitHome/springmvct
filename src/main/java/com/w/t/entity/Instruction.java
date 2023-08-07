package com.w.t.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Packagename com.w.t.entity
 * @Classname instruction
 * @Description
 * @Authors Mr.Wu
 * @Date 2022/06/01 08:45
 * @Version 1.0
 */
@Data
@Builder
public class Instruction {

    private String id;
    private String name;
    private String year;
    private String url;
    private String createTime;


}
