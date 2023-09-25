package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.HttpInfo;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.MicroserviceDetail;
import com.w.t.conductor.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname ConditionNodeGenerator
 * @Description
 * @Authors Mr.Wu
 * @Version 1.0
 */
public class ConditionNodeGenerator extends NodeGenerator {

    public ConditionNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }


    /**
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {

        List<Task> logicTaskList = new ArrayList<>();
        if (nodeInfo.getType() != 3) {
            return LogicNode.builder().node(logicTaskList).build();
        }
        final List<TaskInfo> ifTaskInfos = nodeInfo.getIfTaskInfos();
        final List<TaskInfo> elseTaskInfos = nodeInfo.getElseTaskInfos();

        final Map<String, Object> conditionObj = nodeInfo.getConditionObj();
        final String key  = String.valueOf(conditionObj.get("globalKey"));
        final String compare  = String.valueOf(conditionObj.get("conditionExpression"));
        final String value  = String.valueOf(conditionObj.get("conditionValue"));

        List<Task> ifTask = new ArrayList<>();
        List<Task> elseTask = new ArrayList<>();

        return LogicNode.builder().node(logicTaskList).build();
    }


}
