package com.w.t.conductor.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    private List<Integer> waitForIndex;//用于并行节点后面的节点是否需要等待前方的并行节点

    private Integer type = 1;//默认1 普通节点，2 并行节点

    private List<List<TaskInfo>> parallelTask; //type = 2 时 并行的任务集


}
