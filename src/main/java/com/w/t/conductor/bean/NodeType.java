package com.w.t.conductor.bean;

import lombok.Getter;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname MicroserviceType
 * @Description
 * @Authors Mr.Wu
 * @Version 1.0
 */
public enum NodeType {

    //系统节点
    JOIN_NODE("join_node", 1001, "并行节点标识"),
    CONDITION_NODE("condition_node", 100, "条件判断节点标识"),
    INIT_VARIABLE_NODE("init_variable_node", 200, "初始化变量节点标识"),
    SET_VARIABLE_NODE("set_variable_node", 2001, "设置变量值节点标识"),


    //功能节点
    DATA_EXTRACT("data_extract", 1, "数据集成类型任务"),
    DATA_A("data_a", 2, "简单触发接口类型任务");


    NodeType(String name, int code, String desc) {
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
