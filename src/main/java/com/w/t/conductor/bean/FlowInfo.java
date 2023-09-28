package com.w.t.conductor.bean;

import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import lombok.Builder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.bean
 * @Classname FlowInfo
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/09/28 10:54
 * @Version 1.0
 */
public class FlowInfo {

    List<TaskInfo> flowTaskList;//任务节点集合

    Map<String, Object> globalVariable;//全局变量

    //TODO 在转换的过程里随时给该对象赋值，便于后续选择相应节点取值（用于全局变量的动态值）
    Map<String, Task> currentFlowNodeId2RefernceTask;//存储一个功能节点Id->taskReferenceNameList【分析建模查询结果获取值的task_reference名字需特殊标识 用于动态变量=>根据Id从下属的taskReferenceNameList中获取唯一的值】

    Integer faildTerminate = 1;//其他参数如对节点设置失败终止或者忽略,0忽略，1终止

    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add("11");
        a.add("22");
        a.add("33");
        final String s = a.get(-1);
        System.out.println(1);
    }
}
