package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.ForkJoin;
import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.HttpInfo;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.MicroserviceDetail;
import com.w.t.conductor.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataANodeGenerator
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/09/01 11:58
 * @Version 1.0
 */
public class ForkNodeGenerator extends NodeGenerator {


    public ForkNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }


    /**
     * ForkJoin 特殊 它包含各类逻辑节点
     *
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {

        List<Task> logicTaskList = new ArrayList<>();
        if (nodeInfo.getType() != 2) {
            logicTaskList.add(new ForkJoin(getReferenceName("fork"), new Task[]{}));
        }

        //TODO build fork obj by parallelTask
        final List<List<TaskInfo>> parallelTask = nodeInfo.getParallelTask();

        logicTaskList.add(new ForkJoin(getReferenceName("fork"), new Task[]{}));
        return LogicNode.builder().node(logicTaskList).build();
    }

}
