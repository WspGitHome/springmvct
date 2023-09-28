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

    //系统节点
    JOIN_NODE("join_node", 1001, "并行节点标识"),
    CONDITION_NODE("condition_node", 100, "条件判断节点标识"),
    SET_VARIABLE_NODE("set_variable_node", 200, "设置变量节点标识"),


    //功能节点
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
