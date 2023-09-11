package com.w.t.conductor.bean;

import com.netflix.conductor.sdk.workflow.def.tasks.ForkJoin;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname LogicNode
 * @Description
 * @Authors Mr.Wu
 * @Version 1.0
 */
@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class LogicNode {

    @Builder.Default
    private Integer type = 1;//1 普通节点，2 并行节点

    private List<List<LogicNode>> parallelNode;

    private ForkJoin forkJoin; //包含一系列横纵交错的List<Task>

    private List<Task> node; //逻辑节点
}
