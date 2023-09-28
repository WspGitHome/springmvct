package com.w.t.conductor.generator;

import com.alibaba.druid.util.StringUtils;
import com.netflix.conductor.sdk.workflow.def.tasks.*;
import com.w.t.conductor.bean.HttpInfo;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.MicroserviceType;
import com.w.t.conductor.bean.TaskInfo;
import freemarker.cache.StringTemplateLoader;
import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.ui.freemarker.FreeMarkerTemplateUtils;

import java.io.IOException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    public static final String SET_VALUE_REFERENCE_ID = "globalValue";//每个任务流里唯一所以名字不需要随机，便于其他节点取赋值。
    public static final String PRE_GLOBAL_VARIABLE = "pre_global_";//特殊标识taskReferenceName代表该几点可以用来获取值赋予全局变量

    public TaskInfo nodeInfo;

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
        final Switch aSwitch = new Switch(getReferenceName("switch"), conditionObj);
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

    public List<LogicNode> transLogic(List<TaskInfo> taskInfos) throws Exception {
        List<LogicNode> logicNodes = new ArrayList<>();
        for (TaskInfo taskInfo : taskInfos) {
            final LogicNode logicNode = nodeFactory(taskInfo).getLogicNode();
            logicNodes.add(logicNode);
        }
        return logicNodes;
    }

    public NodeGenerator nodeFactory(TaskInfo taskInfo) {
        final MicroserviceType mircType = taskInfo.getMircType();
        if (MicroserviceType.DATA_EXTRACT.equals(mircType)) {
            return new DataExtractNodeGenerator(taskInfo);
        }
        if (MicroserviceType.DATA_A.equals(mircType)) {
            return new DataANodeGenerator(taskInfo);
        }
        if (MicroserviceType.JOIN_NODE.equals(mircType)) {
            return new ForkNodeGenerator(taskInfo);
        }
        if (MicroserviceType.CONDITION_NODE.equals(mircType)) {
            return new ConditionNodeGenerator(taskInfo);
        }
        if (MicroserviceType.INIT_VARIABLE_NODE.equals(mircType)) {
            return new InitGlobalValueNodeGenerator(taskInfo);
        }
        if (MicroserviceType.SET_VARIABLE_NODE.equals(mircType)) {
            return new SetValueNodeGenerator(taskInfo);
        }
        throw new RuntimeException("节点未开放！");
    }


    //用于标识能够获取返回值 赋值全局变量的节点id(taskReferenceName)
    public String getPreVariableFlag(String nodeTye) {
        if (StringUtils.isEmpty(nodeTye)) {
            return PRE_GLOBAL_VARIABLE+"fxjm_";
        }
        return PRE_GLOBAL_VARIABLE + nodeTye + "_";
    }


    enum SUPPORT_GLOBAL_VARIABLE{



    }


    public static void main(String[] args) {
        List<String> a = new ArrayList<>();
        a.add("2");
        a.add("3");
        a.add("4");

        String[] as = new String[]{};

        a.toArray(as);
        System.out.println(1);

    }
}
