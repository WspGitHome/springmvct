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


    public SetValueNodeGenerator(TaskInfo nodeInfo, Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask) {
        super(nodeInfo, currentFlowDynamicSetValueNodeId2RefernceTask);

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
            final String globalKey = String.valueOf(vavariableObj.get("globalKey"));
            final String variableValue = String.valueOf(vavariableObj.get("variableValue"));
            if (type == 1) {
                //修改为静态值
                setVariableObj.input(globalKey, variableValue);
            } else if (type == 2) { //NOTIC :需要注意顺序问题，变量节点需要在当前赋值节点前
                //修改值为动态相关节点的表达式值
                final String idFromDynamic = getIdFromDynamic(variableValue);
                final List<String> locationFromDynamic = getLocationFromDynamic(variableValue);
                String arraySecondValue = "";
                if (locationFromDynamic.size() > 1) {
                    arraySecondValue = locationFromDynamic.get(1);
                }
                final String arrayFirstValue = locationFromDynamic.get(0);
                final Task putValueTask = currentFlowDynamicSetValueNodeId2RefernceTask.get(idFromDynamic);
                if (arraySecondValue.isEmpty()) {
                    final String getFromNodeValue = putValueTask.taskOutput.map("response").map("body").get(arrayFirstValue);
                    setVariableObj.input(globalKey, getFromNodeValue);
                } else {
                    //TODO setVariable.taskOutput.map("result").list("constDataSource").get("下单年份",0);
                    // list这种key需要特殊指定根据TASK referName提取任务类型 ，目前if里面支持单层jsonobj，else 目前默认写死支持分析建模结果解析
                    final String getFromNodeValue = putValueTask.taskOutput.map("response").map("body").list(arrayFirstValue).get(arrayFirstValue, Integer.parseInt(arraySecondValue));
                    setVariableObj.input(globalKey, getFromNodeValue);
                }
            }

        }
        logicTaskList.add(setVariableObj);
        return LogicNode.builder().node(logicTaskList).build();
    }


}
