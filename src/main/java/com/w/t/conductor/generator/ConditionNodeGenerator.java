package com.w.t.conductor.generator;

import cn.hutool.json.JSONUtil;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
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

    Logger logger = LoggerFactory.getLogger(ConditionNodeGenerator.class);

    public ConditionNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }

    public ConditionNodeGenerator(TaskInfo nodeInfo, Task globalDef) {
        super(nodeInfo, globalDef);
    }


    private static Map<String, String> LOGCIN_SYMBOL = initMap();


    /**
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        logger.info("当前进入condition构建节点，携带全局变量值:{}", JSONUtil.toJsonStr(globalDef));
        List<Task> logicTaskList = new ArrayList<>();
        if (!nodeInfo.getNodeType().equals(NodeType.CONDITION_NODE)) {
            return LogicNode.builder().node(logicTaskList).build();
        }
        final List<TaskInfo> ifTaskInfos = nodeInfo.getIfTaskInfos();
        final List<TaskInfo> elseTaskInfos = nodeInfo.getElseTaskInfos();

        final List<LogicNode> ifLogicNodes = transLogic(ifTaskInfos, globalDef);
        final List<LogicNode> elsLogicNodes = transLogic(elseTaskInfos, globalDef);

        List<Task> ifTask = new ArrayList<>();
        List<Task> elseTask = new ArrayList<>();

        ifLogicNodes.stream().forEach(e -> {
            if (e.getNode() != null && !e.getNode().isEmpty()){
                ifTask.addAll(e.getNode());
            }
        });
        elsLogicNodes.stream().forEach(e -> {
            if (e.getNode() != null && !e.getNode().isEmpty()){
                elseTask.addAll(e.getNode());
            }
        });

        final Map<String, Object> conditionObj = nodeInfo.getConditionObj();
        final String key = String.valueOf(conditionObj.get("globalKey"));
        final String compare = String.valueOf(conditionObj.get("conditionExpression"));
        final String value = String.valueOf(conditionObj.get("conditionValue"));
        //TODO get key's value
        String keyValue = "999";
        String switchValue = "0", otherSwitchValue = "0";
        //TODO distinguish expression hard code temporary
        if (keyValue.equals(value)) {
            switchValue = "1";
        } else {
            otherSwitchValue = "1";
        }
        Map<String, List<Task>> caseObj = new HashMap<>();
        caseObj.put(switchValue, ifTask);
        caseObj.put(otherSwitchValue, elseTask);
        logicTaskList.add(getSwitchNodeB("1", caseObj, null));
        return LogicNode.builder().node(logicTaskList).build();
    }


    private static Map<String, String> initMap() {
        Map<String, String> logicSymbol = new HashMap<>();
        logicSymbol.put(">", "Number");
        logicSymbol.put(">=", "Number");
        logicSymbol.put("<", "Number");
        logicSymbol.put("<=", "Number");
        logicSymbol.put("==", "Number");
        logicSymbol.put("包含", "String");
        logicSymbol.put("等于", "String");
        //。。。。。。。。
        return logicSymbol;
    }

}
