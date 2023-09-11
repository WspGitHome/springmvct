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

        List<List<List<String>>> line = new ArrayList<>();

        List<List<String>>  line_a_1 = new ArrayList<>();
        List<String> line_a_1_1 = new ArrayList<>();
        line_a_1_1.add("A");

        List<List<String>>  line_a_2 = new ArrayList<>();
        List<String> line_a_2_1 = new ArrayList<>();
        line_a_2_1.add("B");

        List<List<String>>  line_a_3 = new ArrayList<>();
        List<String> line_b_1 = new ArrayList<>();
        List<String> line_b_2 = new ArrayList<>();
        List<String> line_b_3 = new ArrayList<>();

        line_b_1.add("B");
        line_b_1.add("C");

        line_b_2.add("A");
        line_b_2.add("C");
        line_b_2.add("B");

        line_b_3.add("B");

        line_a_3.add(line_b_1);
        line_a_3.add(line_b_2);
        line_a_3.add(line_b_3);

        List<List<String>>  line_a_4 = new ArrayList<>();
        List<List<String>>  line_a_5 = new ArrayList<>();





    }

}
