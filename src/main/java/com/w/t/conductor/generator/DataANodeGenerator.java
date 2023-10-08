package com.w.t.conductor.generator;

import cn.hutool.json.JSONUtil;
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

/**
 * @Packagename com.w.t.conductor.util
 * @Classname DataANodeGenerator
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/09/01 11:58
 * @Version 1.0
 */
public class DataANodeGenerator extends NodeGenerator {

    Logger logger = LoggerFactory.getLogger(DataANodeGenerator.class);

    private static final String RUN = "DATA_A_RUN";


    public DataANodeGenerator(TaskInfo nodeInfo) {
        super(nodeInfo);
    }


    public DataANodeGenerator(TaskInfo nodeInfo,Task globalDef) {
        super(nodeInfo,globalDef);
    }


    /**
     * 默认一个逻辑节点有如下几部分组成，不同业务的逻辑节点根据现有接口的情况不同在实现的可能也各有差异
     *
     * @return
     * @throws Exception
     */
    @Override
    public LogicNode getLogicNode() throws Exception {
        logger.info("当前进入data_a构建节点，携带全局变量值:{}",JSONUtil.toJsonStr(globalDef));
        List<Task> logicTaskList = new ArrayList<>();
        //触发节点构建
        final MicroserviceDetail dataARunInfo = MicroserviceDetail.valueOf(RUN);

        HttpInfo dataArun = HttpInfo.builder().appName("data_a_run")
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
        return LogicNode.builder().node(logicTaskList).build();
    }

}
