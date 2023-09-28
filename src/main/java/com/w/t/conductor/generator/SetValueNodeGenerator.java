package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.SetVariable;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.MicroserviceType;
import com.w.t.conductor.bean.TaskInfo;
import org.springframework.stereotype.Component;

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
        changeValue(nodeInfo);
        return LogicNode.builder().build();
    }

    private void changeValue(TaskInfo taskInfo) {
        //全部变量
        final Map<String, Object> globalVariable = taskInfo.getGlobalVariable();
        //改变值相关信息
        final Map<String, Object> variableObj = taskInfo.getVariableObj();
        //动态相关节点
        //TODO 如何放入并获取 ????
        final Map<String, Task> currentFlowNodeId2RefernceTask = taskInfo.getCurrentFlowNodeId2RefernceTask();

    }

}
