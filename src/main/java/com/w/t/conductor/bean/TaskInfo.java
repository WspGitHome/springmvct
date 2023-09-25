package com.w.t.conductor.bean;

import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname TaskInfo
 * @Description
 * @Authors Mr.Wu
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class TaskInfo extends AbTask {

    private MicroserviceType mircType; //服务类型

    private Map<String, Object> param;//页面选择节点的具体参数值（任务id或者页面填写的参数，优先于全局变量，value可能填写全局变量）

    private Integer type = 1;//默认1 普通节点，2 并行节点，3 条件节点



    //并行节点
    private List<List<TaskInfo>> parallelTask; //type = 2 时 并行的任务集

    private List<Integer> waitForIndex= new ArrayList<>();//type = 2 时候(弃用，与条件判断节点相矛盾)，用于并行节点后面的节点是否需要等待前方的并行节点


    //条件判断节点
    private Map<String, Object> conditionObj;//type = 3 时用  key： globalKey|conditionExpression|conditionValue

    private List<TaskInfo> ifTaskInfos;//type = 3

    private List<TaskInfo> elseTaskInfos;//type = 3

}
