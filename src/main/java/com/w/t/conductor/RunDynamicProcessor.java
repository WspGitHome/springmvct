package com.w.t.conductor;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.WorkflowBuilder;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import com.w.t.conductor.bean.MicroserviceType;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.TaskInfo;
import com.w.t.conductor.generator.DataANodeGenerator;
import com.w.t.conductor.generator.DataExtractNodeGenerator;
import com.w.t.conductor.generator.ForkNodeGenerator;
import com.w.t.conductor.generator.NodeGenerator;
import com.w.t.conductor.util.RandomCodeGenerator;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname RunDynamicProcessor
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/09/08 17:43
 * @Version 1.0
 */
public class RunDynamicProcessor {

    private final static String conductorServerURL = "http://172.16.0.73:8080/api/";

    private final WorkflowExecutor executor;

    public RunDynamicProcessor(WorkflowExecutor executor) {
        this.executor = executor;
        this.executor.initWorkers(RunDynamicProcessor.class.getPackageName());
    }


    public static void main(String[] args) throws Exception {

        //模拟页面或者二次处理后的参数
        List<TaskInfo> taskInfos = new ArrayList<>();
        Map<String, Object> param_data_extract_01 = new HashMap<>();
        param_data_extract_01.put("taskId", "9999");
        TaskInfo data_extract_01 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_extract_01).build();

        TaskInfo data_a_01 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        TaskInfo data_a_02 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();

        Map<String, Object> param_data_extract_02 = new HashMap<>();
        param_data_extract_02.put("taskId", "8888");
        TaskInfo data_extract_02 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_extract_02).build();

        taskInfos.add(data_extract_01);
        taskInfos.add(data_a_01);
        taskInfos.add(data_a_02);
        taskInfos.add(data_extract_02);

        final List<LogicNode> logicNodes = transLogic(taskInfos);
        //构建任务流对象

        String workflowId = null;
        WorkflowExecutor executor = new WorkflowExecutor(conductorServerURL);//TODO 程序启动时全局构建该对象
        try {
            RunDynamicProcessor runDynamicProcessor = new RunDynamicProcessor(executor);
            Map<String, String> runParamter = new HashMap<>();
            final CompletableFuture<Workflow> execute = runDynamicProcessor.createCondutorFlow(logicNodes).execute(runParamter);
            workflowId = execute.get().getWorkflowId();
        } catch (Exception e){
          e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        System.out.println("已提交，当前生成的实例id:" + workflowId);


    }

    private ConductorWorkflow<Map<String, String>> createCondutorFlow(List<LogicNode> logicNodes) {
        WorkflowBuilder<Map<String, String>> workBuilder = new WorkflowBuilder<>(executor);
        workBuilder.name(" work_flow_name_" + RandomCodeGenerator.getRandomWithTimstamp()).ownerEmail("Daas@Smartstemp.com")
                .version(1).timeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY, 0).description("desc");//base info
        for (LogicNode logicNode : logicNodes) {
            //串行
            if (logicNode.getType() == 1) {
                logicNode.getNode().stream().forEach(e -> {
                    workBuilder.add(e);
                });
            }
        }
        final ConductorWorkflow<Map<String, String>> conductorWorkflow = workBuilder.build();
        conductorWorkflow.registerWorkflow(true, true);
        return conductorWorkflow;
    }


    private static List<LogicNode> transLogic(List<TaskInfo> taskInfos) throws Exception {
        List<LogicNode> logicNodes = new ArrayList<>();
        for (TaskInfo taskInfo : taskInfos) {
            final LogicNode logicNode = nodeFactory(taskInfo).getLogicNode();
            logicNodes.add(logicNode);
        }
        return logicNodes;
    }

    private static NodeGenerator nodeFactory(TaskInfo taskInfo) {
        final MicroserviceType mircType = taskInfo.getMircType();
        if (MicroserviceType.DATA_EXTRACT.equals(mircType)) {
            return new DataExtractNodeGenerator(taskInfo);
        }
        if (MicroserviceType.DATA_A.equals(mircType)) {
            return new DataANodeGenerator(taskInfo);
        }
        if (MicroserviceType.JOIN_TASK.equals(mircType)) {
            return new ForkNodeGenerator(taskInfo);
        }
        throw new RuntimeException("节点未开放！");
    }
}
