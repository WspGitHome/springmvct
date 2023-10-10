package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.SetVariable;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname SetValueNodeGenerator
 * @Description 该节点至少包含一个变量赋值
 * @Authors Mr.Wu
 */
public class SetValueNodeGenerator extends NodeGenerator {

    Logger logger = LoggerFactory.getLogger(SetValueNodeGenerator.class);

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
        List<Map<String, Object>> variableObjList = nodeInfo.getVariableObjList();
        SetVariable setVariableObj = new SetVariable(getReferenceName(SET_VALUE_REFERENCE_ID));
        for (Map<String, Object> vavariableObj : variableObjList) {
            final int type = Integer.parseInt(String.valueOf(vavariableObj.get("type")));
            if (type == 1) {
                //修改为静态值
                setVariableObj.input(String.valueOf(vavariableObj.get("globalKey")), String.valueOf(vavariableObj.get("variableValue")));
            } else if (type == 2) {
                //修改值为动态相关节点的表达式值
                //TODO 如何放入并获取 ????
                final Map<String, Task> currentFlowNodeId2RefernceTask = nodeInfo.getCurrentFlowDynamicSetValueNodeId2RefernceTask();

            }

        }
        logicTaskList.add(setVariableObj);
        return LogicNode.builder().node(logicTaskList).build();
    }


}
