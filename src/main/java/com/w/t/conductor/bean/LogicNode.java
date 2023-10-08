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

    private List<List<LogicNode>> parallelNode;

    private List<Task> node; //逻辑节点
}
