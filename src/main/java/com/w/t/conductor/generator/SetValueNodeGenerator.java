package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.SetVariable;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.TaskInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataANodeGenerator
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/09/01 11:58
 * @Version 1.0
 */
public class SetValueNodeGenerator extends NodeGenerator {


    public SetValueNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }


    /**
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        List<Task> logicTaskList = new ArrayList<>();
        final Map<String, Object> globalVariable = nodeInfo.getGlobalVariable();
        SetVariable setVariableNode = getSetVariableNode(globalVariable.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))));
        logicTaskList.add(setVariableNode);
        return LogicNode.builder().node(logicTaskList).build();
    }

}
