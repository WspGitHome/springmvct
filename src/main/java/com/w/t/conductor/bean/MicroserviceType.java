package com.w.t.conductor.bean;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname MicroserviceType
 * @Description
 * @Authors Mr.Wu
 * @Version 1.0
 */
public enum MicroserviceType {

    JOIN_TASK("join_task", 0, "并行任务标识"),
    DATA_EXTRACT("data_extract", 1, "数据集成类型任务"),
    DATA_A("data_a", 2, "简单触发接口类型任务");


    MicroserviceType(String name, int code, String desc) {
        this.code = code;
        this.name = name;
        this.desc = desc;

    }

    @Getter
    private String name;
    @Getter
    private String desc;
    @Getter
    private int code;

    public static void main(String[] args) {

    }

}
