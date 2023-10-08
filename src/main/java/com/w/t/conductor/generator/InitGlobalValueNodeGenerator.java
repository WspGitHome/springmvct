package com.w.t.conductor.generator;

import cn.hutool.json.JSONUtil;
import com.netflix.conductor.sdk.workflow.def.tasks.SetVariable;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataANodeGenerator
 * @Description
 * @Authors Mr.Wu
 */
public class InitGlobalValueNodeGenerator extends NodeGenerator {

    Logger logger = LoggerFactory.getLogger(InitGlobalValueNodeGenerator.class);

    public InitGlobalValueNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }


    /**
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        logger.info("当前进入初始化全局变量节点，携带全局变量值:{}，准备初始化的值：{}", JSONUtil.toJsonStr(globalDef), JSONUtil.toJsonStr(nodeInfo.getGlobalVariable()));
        List<Task> logicTaskList = new ArrayList<>();
        final Map<String, Object> globalVariable = nodeInfo.getGlobalVariable();
        if (globalVariable != null && !globalVariable.entrySet().isEmpty()) {
            SetVariable setVariableNode = getSetVariableNode(globalVariable.entrySet().stream().collect(Collectors.toMap(Map.Entry::getKey, e -> String.valueOf(e.getValue()))));
            logicTaskList.add(setVariableNode);
        }
        return LogicNode.builder().node(logicTaskList).build();
    }

}
