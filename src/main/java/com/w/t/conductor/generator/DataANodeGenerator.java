package com.w.t.conductor.generator;

import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.w.t.conductor.bean.HttpInfo;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.MicroserviceDetail;
import com.w.t.conductor.bean.TaskInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataANodeGenerator
 * @Description
 * @Authors Mr.Wu
 */
public class DataANodeGenerator extends NodeGenerator {

    Logger logger = LoggerFactory.getLogger(DataANodeGenerator.class);

    private static final String I_RUN = "DATA_A_RUN";


    public DataANodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }

    public DataANodeGenerator(TaskInfo nodeInfo, Map<String, Task> currentFlowDynamicSetValueNodeId2RefernceTask) {
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
        logger.info("当前进入data_a_Node,时间：{}",System.currentTimeMillis());

        List<Task> logicTaskList = new ArrayList<>();
        //触发节点构建
        final MicroserviceDetail dataARunInfo = MicroserviceDetail.valueOf(I_RUN);

        HttpInfo dataArun = HttpInfo.builder().appName(nodeInfo.getId() + STATIC_DATA_A + STATIC_RUN + SPECIAL_GLOBAL_VARIABLE_CAN_SET)
                .url(dataARunInfo.getUrl())
                .method(dataARunInfo.getMethod())
                .accept(dataARunInfo.getAccept())
                .contentType(dataARunInfo.getContentType())
                .body(dataARunInfo.getBody())
                .readTimeout(dataARunInfo.getReadTimeOut())
                .connectionTimeout(dataARunInfo.getConnectionTimeOut())
                .build();
        Http dataArunNode = getHttpNode(dataArun);

        logicTaskList.add(dataArunNode);

        logicTaskList.stream().forEach(e -> {
            if (e.getTaskReferenceName().contains(SPECIAL_GLOBAL_VARIABLE_CAN_SET)) {
                currentFlowDynamicSetValueNodeId2RefernceTask.put(nodeInfo.getId(), e);
            }
        });
        return LogicNode.builder().node(logicTaskList).build();
    }

}
