package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.DoWhile;
import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import com.netflix.conductor.sdk.workflow.def.tasks.Switch;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.HttpInfo;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.MicroserviceDetail;
import com.w.t.conductor.bean.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataExtractNodeGenerator
 * @Description
 * @Authors Mr.Wu
 */
public class DataExtractNodeGenerator extends NodeGenerator {

    Logger logger = LoggerFactory.getLogger(DataExtractNodeGenerator.class);

    private static final String I_RUN = "DATA_EXTRACT_RUN";
    private static final String I_GET_STATUS = "DATA_EXTRACT_GET_STATUS";

    public DataExtractNodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }

    public DataExtractNodeGenerator(TaskInfo nodeInfo, Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask) {
        super(nodeInfo, currentFlowDynamicSetValueNodeId2RefernceTask);
    }

    /**
     * 默认一个逻辑节点有如下几部分组成，不同业务的逻辑节点根据现有接口的情况不同在实现的可能也各有差异
     *
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        logger.info("当前进入data_extract_Node,时间：{}",System.currentTimeMillis());

        List<Task> logicTaskList = new ArrayList<>();
        //触发节点构建
        final MicroserviceDetail dataExtractRunInfo = MicroserviceDetail.valueOf(I_RUN);
        String templateParam = dataExtractRunInfo.getBody();
        final Map<String, Object> param = nodeInfo.getParam();
        final String runbody = super.paramReplace(templateParam, param);

        HttpInfo dataextractrun = HttpInfo.builder().appName(nodeInfo.getId()+STATIC_DATA_EXTRACT+STATIC_RUN+SPECIAL_GLOBAL_VARIABLE_CAN_SET)//TODO 还原normal
                .url(dataExtractRunInfo.getUrl())
                .method(dataExtractRunInfo.getMethod())
                .accept(dataExtractRunInfo.getAccept())
                .contentType(dataExtractRunInfo.getContentType())
                .body(runbody)
                .readTimeout(dataExtractRunInfo.getReadTimeOut())
                .connectionTimeout(dataExtractRunInfo.getConnectionTimeOut())
                .headers(null)//按服务添加token，此处暂时不添加token //TODO 设置长时间的token
                .build();
        Http dataextractrunNode = getHttpNode(dataextractrun);

        //构建获取状态的节点
        final MicroserviceDetail dataExtractGetStatusInfo = MicroserviceDetail.valueOf(I_GET_STATUS);
        String statusParam = dataExtractGetStatusInfo.getBody();
        statusParam = statusParam + dataextractrunNode.taskOutput.map("response").map("body").get("code");//重点如何获取上个接口返回值
        HttpInfo dataextractstatusInfo = HttpInfo.builder().appName(nodeInfo.getId()+STATIC_DATA_EXTRACT+STATIC_GET_STATUS+SPECIAL_PLACEHOLDER)
                .url(dataExtractGetStatusInfo.getUrl())
                .method(dataExtractGetStatusInfo.getMethod())
                .accept(dataExtractGetStatusInfo.getAccept())
                .contentType(dataExtractGetStatusInfo.getContentType())
                .body(statusParam)
                .readTimeout(dataExtractGetStatusInfo.getReadTimeOut())
                .connectionTimeout(dataExtractGetStatusInfo.getConnectionTimeOut())
                .headers(null)//按服务添加token，此处暂时不添加token //TODO 服务考虑设置长时间的token 和 cookie 或者不需要填写，如果接口需要cookie获取用户则不行
                .build();
        Http dataextractstatusInfoNode = getHttpNode(dataextractstatusInfo);

        //构建循环节点
        String dowhileValue = "-1";//dowhileValue 为获取状态的结果 根据具体接口配置相应的值 代表返回的结果为dowhileValue的值的时候 会一直循环
        String getStatusDowhileConditon = "$." + dataextractstatusInfoNode.getTaskReferenceName() + "['response']['body']['result'] === " + dowhileValue;
        DoWhile usedDowhileNode = getUsedDowhileNode(getStatusDowhileConditon, getDefaultWaitNode(), dataextractstatusInfoNode);

        //构建终止节点 是否使用看属性是否开启
        String switchExpression = dataextractstatusInfoNode.taskOutput.map("response").map("body").get("result");
        String terminateReason = dataextractstatusInfoNode.taskOutput.map("response").map("body").get("message");
        Map<String, Task> caseObj = new HashMap<>();
        caseObj.put("0", getTerminate(terminateReason));
        Switch switchNode = getSwitchNode(switchExpression, caseObj, new ArrayList<>());

        logicTaskList.add(dataextractrunNode);
        logicTaskList.add(usedDowhileNode);

        if (nodeInfo.getFaildTerminate() == 1) {
            logicTaskList.add(switchNode);
        }

        logicTaskList.stream().forEach(e -> {
            if (e.getTaskReferenceName().contains(SPECIAL_GLOBAL_VARIABLE_CAN_SET)) {
                currentFlowDynamicSetValueNodeId2RefernceTask.put(nodeInfo.getId(), e);
            }
        });
        return LogicNode.builder().node(logicTaskList).build();
    }

}
