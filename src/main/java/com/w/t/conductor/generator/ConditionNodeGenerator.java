package com.w.t.conductor.generator;

import cn.hutool.json.JSONUtil;
import com.netflix.conductor.sdk.workflow.def.tasks.Switch;
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

    public ConditionNodeGenerator(TaskInfo nodeInfo, Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask) {
        super(nodeInfo, currentFlowDynamicSetValueNodeId2RefernceTask);

    }

    /**
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        List<Task> logicTaskList = new ArrayList<>();
        if (!nodeInfo.getNodeType().equals(NodeType.CONDITION_NODE)) {
            return LogicNode.builder().node(logicTaskList).build();
        }
        final List<TaskInfo> ifTaskInfos = nodeInfo.getIfTaskInfos();
        final List<TaskInfo> elseTaskInfos = nodeInfo.getElseTaskInfos();

        final List<LogicNode> ifLogicNodes = transLogic(ifTaskInfos,currentFlowDynamicSetValueNodeId2RefernceTask);
        final List<LogicNode> elsLogicNodes = transLogic(elseTaskInfos,currentFlowDynamicSetValueNodeId2RefernceTask);

        List<Task> ifTask = new ArrayList<>();
        List<Task> elseTask = new ArrayList<>();

        ifLogicNodes.stream().forEach(e -> {
            if (e.getNode() != null && !e.getNode().isEmpty()) {
                ifTask.addAll(e.getNode());
            }
        });
        elsLogicNodes.stream().forEach(e -> {
            if (e.getNode() != null && !e.getNode().isEmpty()) {
                elseTask.addAll(e.getNode());
            }
        });

        final Map<String, Object> conditionObj = nodeInfo.getConditionObj();
        final String key = String.valueOf(conditionObj.get("globalKey"));
        final String compare = String.valueOf(conditionObj.get("conditionExpression"));
        final String value = String.valueOf(conditionObj.get("conditionValue"));//此处手输值必为静态值

        StringBuffer variableKey = new StringBuffer();
        variableKey.append("${workflow.variables.").append(key).append("}");
        String keyValue = variableKey.toString();

//        String newExpression = "(function () { if($.compareResult == '" + value + "') {return \"true\";} return \"false\"; })();";
        String newExpression = getResult(compare, value);
        Map<String, List<Task>> caseObj = new HashMap<>();
        caseObj.put("true", ifTask);
        caseObj.put("false", elseTask);
        Switch switchNodeB = getSwitchNodeB(newExpression, caseObj, null);
        switchNodeB.input("compareResult", keyValue);
        logicTaskList.add(switchNodeB);
        return LogicNode.builder().node(logicTaskList).build();
    }


    //调整为ES版本
    private String getResult(String compareExpression, String inputValue) {
        String newExpression = "";
        switch (compareExpression) {
            case "等于": {
                newExpression = "(function () { if($.compareResult === '" + inputValue + "') {return \"true\";} return \"false\"; })();";
                break;
            }
            case "不等于": {
                newExpression = "(function () { if($.compareResult != '" + inputValue + "') {return \"true\";} return \"false\"; })();";
                break;
            }
            case "包含": {
                newExpression = "(function () { if($.compareResult.indexOf('" + inputValue + "')!= -1) {return \"true\";} return \"false\"; })();";
                break;
            }
            case "不包含": {
                newExpression = "(function () { if($.compareResult.indexOf('" + inputValue + "') === -1) {return \"true\";} return \"false\"; })();";
                break;
            }
            case "开头包含": {
                newExpression = "(function () { if($.compareResult.indexOf('" + inputValue + "') === 0) {return \"true\";} return \"false\"; })();";
                break;
            }

            case "结尾包含": {
                newExpression = "(function () { if($.compareResult.indexOf('" + inputValue + "') === $.compareResult.length - 1 ) {return \"true\";} return \"false\"; })();";
                break;
            }

            case "为空": {
                newExpression = "(function () { if(!$.compareResult) {return \"true\";} return \"false\"; })();";
                break;
            }

            case "不为空": {
                newExpression = "(function () { if($.compareResult) {return \"true\";} return \"false\"; })();";
                break;
            }

            case "==": {
                newExpression = "(function () { if($.compareResult == " + inputValue + ") {return \"true\";} return \"false\"; })();";
                break;
            }

            case "!=": {
                newExpression = "(function () { if($.compareResult != " + inputValue + ") {return \"true\";} return \"false\"; })();";
                break;
            }

            case ">": {
                newExpression = "(function () { if($.compareResult > " + inputValue + ") {return \"true\";} return \"false\"; })();";
                break;
            }
            case "<": {
                newExpression = "(function () { if($.compareResult < " + inputValue + ") {return \"true\";} return \"false\"; })();";
                break;
            }
            case ">=": {
                newExpression = "(function () { if($.compareResult >= " + inputValue + ") {return \"true\";} return \"false\"; })();";
                break;
            }

            case "<=": {
                newExpression = "(function () { if($.compareResult <= " + inputValue + ") {return \"true\";} return \"false\"; })();";
                break;
            }

        }
        return newExpression;

    }

    public static void main(String[] args) {
        List<String> add = new ArrayList<>();
        add.add("a");
        add.add("b");
        add.add("c");
        add.add(0, "x");
        System.out.println(1);
    }

}
