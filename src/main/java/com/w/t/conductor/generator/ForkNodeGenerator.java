package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.ForkJoin;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataANodeGenerator
 * @Description
 * @Authors Mr.Wu
 */
public class ForkNodeGenerator extends NodeGenerator {

    Logger logger = LoggerFactory.getLogger(ForkNodeGenerator.class);

    public ForkNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }

    public ForkNodeGenerator(TaskInfo nodeInfo, Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask) {
        super(nodeInfo, currentFlowDynamicSetValueNodeId2RefernceTask);
    }

    /**
     * ForkJoin 特殊 它包含各类逻辑节点
     *
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        logger.info("当前进入fork_Node,时间：{}",System.currentTimeMillis());
        List<Task> logicTaskList = new ArrayList<>();
        if (!nodeInfo.getNodeType().equals(NodeType.JOIN_NODE)) {
            logicTaskList.add(new ForkJoin(getReferenceName("fork"), new Task[]{}));
            return LogicNode.builder().node(logicTaskList).build();
        }

        //build fork obj by parallelTask
        final List<Integer> waitForIndex = nodeInfo.getWaitForIndex();
        final List<List<TaskInfo>> parallelTask = nodeInfo.getParallelTask();
        List<List<Task>> taskList = new ArrayList<>();
        List<String> waitReferenceNameList = new ArrayList<>();
        for (int i = 0; i < parallelTask.size(); i++) {
            List<Task> singleTask = new ArrayList<>();
            List<TaskInfo> taskInfos = parallelTask.get(i);
            List<LogicNode> logicNodes = transLogic(taskInfos,currentFlowDynamicSetValueNodeId2RefernceTask);
            logicNodes.stream().forEach(e -> {
                singleTask.addAll(e.getNode());
            });
            taskList.add(singleTask);
        }

        Task[][] forkedTasks = new Task[taskList.size()][];

        for (int i = 0; i < taskList.size(); i++) {
            int index = i;
            List<Task> tasks = taskList.get(index);
            forkedTasks[i] = new Task[tasks.size()]; // Initialize the second dimension
            for (int j = 0; j < tasks.size(); j++) {
                forkedTasks[i][j] = tasks.get(j);
            }
            waitForIndex.stream().forEach(e -> {
                if (e == index) {
                    waitReferenceNameList.add(tasks.get(tasks.size() - 1).getTaskReferenceName());
                }
            });
        }
        //get join refernce
        ForkJoin forkJoin = new ForkJoin(getReferenceName("fork"), forkedTasks);
        String[] joinReferenceArr = new String[]{};
        forkJoin.joinOn(waitReferenceNameList.toArray(joinReferenceArr));

        logicTaskList.add(forkJoin);
        return LogicNode.builder().node(logicTaskList).build();
    }

}
