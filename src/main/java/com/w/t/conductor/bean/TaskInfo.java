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

    private NodeType nodeType; //节点服务类型

    private Map<String, Object> param;//页面选择节点的具体参数值（任务id或者页面填写的参数，优先于全局变量，value可能填写全局变量）


    //并行节点
    private List<List<TaskInfo>> parallelTask; //并行的任务集
    private List<Integer> waitForIndex = new ArrayList<>();//用于并行节点后面的节点是否需要等待前方的并行节点


    //条件判断节点
    private Map<String, Object> conditionObj;// key： globalKey|conditionExpression|conditionValue
    private List<TaskInfo> ifTaskInfos;
    private List<TaskInfo> elseTaskInfos;


    //初始化全局变量节点
    Map<String, Object> globalVariable;//全局变量传递～

    //赋值节点
    Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask; //存储Task节点对象用于获取前面某个节点输出值
    private List<Map<String, Object>> variableObjList;//key： globalKey|variableValue｜type(1静态值，2动态值)


    @Builder.Default
    Integer faildTerminate = 1;//其他参数如对节点设置失败终止或者忽略,0忽略，1终止


}
