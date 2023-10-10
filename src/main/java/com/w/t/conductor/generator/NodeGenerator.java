package com.w.t.conductor.generator;

import com.alibaba.druid.util.StringUtils;
import com.netflix.conductor.sdk.workflow.def.tasks.*;
import com.w.t.conductor.bean.HttpInfo;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.NodeType;
import com.w.t.conductor.bean.TaskInfo;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static com.w.t.conductor.util.RandomCodeGenerator.getRandomWithTimstamp;

/**
 * @Packagename com.w.t.conductor
 * @Classname NodeGenerator
 * @Description 只暴露逻辑节点，不对外提供具体节点
 * @Authors Mr.Wu
 * @Version 1.0
 */
@Data
@AllArgsConstructor
public abstract class NodeGenerator {
    //appname命名规则  logicId_功能标识_特殊标识位_random  notice:不能以数字开头 会报错

    public static final String SET_VALUE_REFERENCE_ID = "globalValue";//初始化全局变量的唯一标识

    //特殊占位符
    public static final String SPECIAL_PLACEHOLDER = "_normal";//普通
    public static final String SPECIAL_GLOBAL_VARIABLE_CAN_SET = "_pre_global_can_set";//特殊标识taskReferenceName代表该节点点可以用来获取值赋予全局变量(每个逻辑几点下只允许存在一个能够赋值的节点所以map不需要集合)
    public static final String SPECIAL_GET_LOG_FLAG = "_daas_getlog_flag";//特殊标识获取状态节点名字包含，所有节点获取任务状态的名字里需要加上(用于后期方便提取日志)

    //各个逻辑节点名称罗列
    public static final String STATIC_DATA_EXTRACT="_data_extract";
    public static final String STATIC_DATA_A="_data_a";

    //status
    public static final String STATIC_RUN="_run";
    public static final String STATIC_GET_STATUS="_getstatus";


    public NodeGenerator(TaskInfo taskInfo) {
        this.nodeInfo = taskInfo;
    }

    public TaskInfo nodeInfo;

    public Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask;


    /**
     * 单逻辑节点只能为串行
     * 逻辑节点最终排列流程图需要 List<List<LogicNode>> 决定串并行，LogicNode内有List<Task>
     * 页面或者调用逻辑给的应该是List<TaskInfo>
     *
     * @return
     */
    abstract public LogicNode getLogicNode() throws Exception;

    public Http getHttpNode(HttpInfo httpInfo) {
        Http.Input input = new Http.Input();
        input.setUri(httpInfo.getUrl());
        if (httpInfo.getHeaders() != null && !httpInfo.getHeaders().isEmpty()) {
            input.setHeaders(httpInfo.getHeaders());
        }
        input.setBody(httpInfo.getBody());
        input.setMethod(httpInfo.getMethod());
        input.setContentType(httpInfo.getContentType());
        input.setConnectionTimeOut(httpInfo.getConnectionTimeout());
        input.setReadTimeOut(httpInfo.getReadTimeout());
        String httpName = StringUtils.isEmpty(httpInfo.getAppName()) ? "http" : httpInfo.getAppName();
        String referName = getReferenceName(httpName);//referName作为节点的id在单次流程里必须唯一
        return new Http(referName).input(input);
    }

    //no used now
    public JQ getJQNode() {
        return null;
    }

    //设置变量节点
    public SetVariable getSetVariableNode(Map<String, String> keyValue) {
        SetVariable setVariableObj = new SetVariable(SET_VALUE_REFERENCE_ID);
        keyValue.entrySet().forEach(e -> {
            setVariableObj.input(e.getKey(), e.getValue());
        });
        return setVariableObj;
    }

    public SetVariable getSetVariableNode() {
        Map<String, String> keyValue = new HashMap<>();
        SetVariable setVariableObj = new SetVariable(SET_VALUE_REFERENCE_ID);
        keyValue.entrySet().forEach(e -> {
            setVariableObj.input(e.getKey(), e.getValue());
        });
        return setVariableObj;
    }


    //终止节点
    public Terminate getTerminate(String reason) {
        if (StringUtils.isEmpty(reason)) {
            reason = "由于存在节点任务失败导致终止！";
        }
        final Terminate terminate = new Terminate(getReferenceName("Terminate"), reason);
        return terminate;
    }

    //Switch节点
    public Switch getSwitchNode(String conditionObj, Map<String, Task> caseObj, List<Task<?>> defaultObj) {
        final Switch aSwitch = new Switch(getReferenceName("switch"), conditionObj);
        caseObj.entrySet().stream().forEach(e -> {
            aSwitch.switchCase(e.getKey(), e.getValue());
        });
        if (defaultObj != null) {
            aSwitch.defaultCase(defaultObj);
        }
        return aSwitch;
    }


    //Switch节点
    public Switch getSwitchNodeB(String conditionObj, Map<String, List<Task>> caseObj, List<Task<?>> defaultObj) {
        Switch aSwitch = new Switch(getReferenceName("switch"), conditionObj, true);
        caseObj.entrySet().stream().forEach(e -> {
            Task[] tasks = e.getValue().toArray(Task[]::new);
            aSwitch.switchCase(e.getKey(), tasks);
        });
        if (defaultObj != null) {
            aSwitch.defaultCase(defaultObj);
        }
        return aSwitch;
    }

    //doWhile节点
    //$." + referName2 + "['response']['body']['result'] === value
    public DoWhile getUsedDowhileNode(String condition, Wait waitTask, Task task) {
        DoWhile doWhile = new DoWhile(getReferenceName("dowhile"), condition, waitTask, task);
        return doWhile;
    }

    public Wait getWaitNode(Integer waitSeconds) {
        return new Wait(getReferenceName("wait"), Duration.ofSeconds(waitSeconds));
    }

    //wait节点
    //默认写的是5s，但实际最小等待时间测试为30s左右
    public Wait getDefaultWaitNode() {
        return new Wait(getReferenceName("wait"), Duration.ofSeconds(5));
    }

    public String getReferenceName(String taskIdName) {
        return taskIdName + getRandomWithTimstamp();
    }


    public String paramReplace(String toReplaceStr, Map<String, Object> param) throws IOException, TemplateException {
        Configuration configuration = new Configuration();
        StringTemplateLoader templateLoader = new StringTemplateLoader();
        templateLoader.putTemplate("templateLoaderTrans", toReplaceStr);
        configuration.setTemplateLoader(templateLoader);
        Template template = null;
        template = configuration.getTemplate("templateLoaderTrans");
        String result = FreeMarkerTemplateUtils.processTemplateIntoString(template, param);
        return result;
    }

    public List<LogicNode> transLogic(List<TaskInfo> taskInfos,Map<String,Task> map) throws Exception {
        List<LogicNode> logicNodes = new ArrayList<>();
        for (TaskInfo taskInfo : taskInfos) {
            final LogicNode logicNode = nodeFactory(taskInfo,map).getLogicNode();
            logicNodes.add(logicNode);
        }
        return logicNodes;
    }

    public NodeGenerator nodeFactory(TaskInfo taskInfo,Map<String,Task> map) {
        final NodeType mircType = taskInfo.getNodeType();
        if (NodeType.DATA_EXTRACT.equals(mircType)) {
            return new DataExtractNodeGenerator(taskInfo,map);
        }
        if (NodeType.DATA_A.equals(mircType)) {
            return new DataANodeGenerator(taskInfo,map);
        }
        if (NodeType.JOIN_NODE.equals(mircType)) {
            return new ForkNodeGenerator(taskInfo,map);
        }
        if (NodeType.CONDITION_NODE.equals(mircType)) {
            return new ConditionNodeGenerator(taskInfo,map);
        }
        if (NodeType.SET_VARIABLE_NODE.equals(mircType)) {
            return new SetValueNodeGenerator(taskInfo,map);
        }
        if (NodeType.INIT_VARIABLE_NODE.equals(mircType)) {
            return new InitGlobalValueNodeGenerator(taskInfo);
        }
        throw new RuntimeException("节点未开放！");
    }

    public static String getIdFromDynamic(String input) {
        // 匹配 $. 与第一个 [] 之间的内容
        Pattern pattern1 = Pattern.compile("\\$\\.(.*?)\\[");
        Matcher matcher1 = pattern1.matcher(input);

        if (matcher1.find()) {
            String match1 = matcher1.group(1);
            return match1;
        }
        return null;
    }

    public static List<String> getLocationFromDynamic(String input) {
        List<String> outLocation = new ArrayList<>();
        // 匹配两个 [] 中的内容
        Pattern pattern2 = Pattern.compile("\\[(.*?)\\]");
        Matcher matcher2 = pattern2.matcher(input);

        while (matcher2.find()) {
            outLocation.add(matcher2.group(1));
        }
        return outLocation;
    }

}
