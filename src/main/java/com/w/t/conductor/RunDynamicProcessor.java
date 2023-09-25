package com.w.t.conductor;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.WorkflowBuilder;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;
import com.w.t.conductor.bean.MicroserviceType;
import com.w.t.conductor.bean.LogicNode;
import com.w.t.conductor.bean.TaskInfo;
import com.w.t.conductor.generator.DataANodeGenerator;
import com.w.t.conductor.generator.DataExtractNodeGenerator;
import com.w.t.conductor.generator.ForkNodeGenerator;
import com.w.t.conductor.generator.NodeGenerator;

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

//        serialRun();

        parallelRun();

    }

    private static void parallelRun() throws Exception {

        List<TaskInfo> all = new ArrayList<>();
        TaskInfo data_a_01 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();

        all.add(data_a_01);

        TaskInfo parallelTasks = new TaskInfo();
        parallelTasks.setType(2);
        parallelTasks.setMircType(MicroserviceType.JOIN_TASK);
        List<List<TaskInfo>> parallelTask = new ArrayList<>();

        List<TaskInfo> taskInfos_1 = new ArrayList<>();
        TaskInfo data_a_1_1 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        TaskInfo data_neibu_0111 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();

        Map<String, Object> param_data_extract_1_1 = new HashMap<>();
        param_data_extract_1_1.put("taskId", "1111");
        TaskInfo data_extract_1_1 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_extract_1_1).build();
        taskInfos_1.add(data_a_1_1);
        taskInfos_1.add(data_extract_1_1);
        //构建一个新的
        TaskInfo parallelTasks_02 = new TaskInfo();
        parallelTasks_02.setType(2);
        parallelTasks_02.setMircType(MicroserviceType.JOIN_TASK);
        List<List<TaskInfo>> parallelTask_neibu = new ArrayList<>();
        List<TaskInfo> taskInfos_neibu_1 = new ArrayList<>();
        TaskInfo data_a_neibu_1 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        taskInfos_neibu_1.add(data_a_neibu_1);

        List<TaskInfo> taskInfos_neibu_2 = new ArrayList<>();
        Map<String, Object> param_data_neibu_extract_1_1 = new HashMap<>();
        param_data_neibu_extract_1_1.put("taskId", "neibu1111");
        TaskInfo data_neibu_extract_1_1 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_neibu_extract_1_1).build();
        taskInfos_neibu_2.add(data_neibu_extract_1_1);

        List<TaskInfo> taskInfos_neibu_3 = new ArrayList<>();
        TaskInfo data_a_neibu_3 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        taskInfos_neibu_3.add(data_a_neibu_3);

        parallelTask_neibu.add(taskInfos_neibu_1);
        parallelTask_neibu.add(taskInfos_neibu_2);
        parallelTask_neibu.add(taskInfos_neibu_3);

        parallelTasks_02.setParallelTask(parallelTask_neibu);
        taskInfos_1.add(parallelTasks_02);
        taskInfos_1.add(data_neibu_0111);



        List<TaskInfo> taskInfos_2 = new ArrayList<>();
        Map<String, Object> param_data_extract_2_1 = new HashMap<>();
        param_data_extract_2_1.put("taskId", "2222");
        TaskInfo data_extract_2_1 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_extract_2_1).build();
        taskInfos_2.add(data_extract_2_1);

        List<TaskInfo> taskInfos_3 = new ArrayList<>();
        TaskInfo data_a_3_1 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        Map<String, Object> param_data_extract_3_2 = new HashMap<>();
        param_data_extract_3_2.put("taskId", "3333");
        TaskInfo data_extract_3_2 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_extract_3_2).build();
        TaskInfo data_a_3_3 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        taskInfos_3.add(data_a_3_1);
        taskInfos_3.add(data_extract_3_2);
        taskInfos_3.add(data_a_3_3);

        parallelTask.add(taskInfos_1);
        parallelTask.add(taskInfos_2);
        parallelTask.add(taskInfos_3);
        parallelTasks.setParallelTask(parallelTask);
        List<Integer> waitIndex = new ArrayList<>();
        waitIndex.add(0);
        waitIndex.add(1);
        parallelTasks.setWaitForIndex(waitIndex);
        all.add(parallelTasks);

        Map<String, Object> param_data_extract_03_3_2 = new HashMap<>();
        param_data_extract_03_3_2.put("taskId", "4444");
        TaskInfo data_extract_03 = TaskInfo.builder().mircType(MicroserviceType.DATA_EXTRACT).param(param_data_extract_03_3_2).build();
        all.add(data_extract_03);

        TaskInfo data_a_04 = TaskInfo.builder().mircType(MicroserviceType.DATA_A).build();
        all.add(data_a_04);

        final List<LogicNode> logicNodes = transLogic(all);
        //构建任务流对象

        String workflowId = null;
        final long start = System.currentTimeMillis();
        WorkflowExecutor executor = new WorkflowExecutor(conductorServerURL);//TODO 程序启动时全局构建该对象
        try {
            RunDynamicProcessor runDynamicProcessor = new RunDynamicProcessor(executor);
            Map<String, String> runParamter = new HashMap<>();
            final CompletableFuture<Workflow> execute = runDynamicProcessor.createCondutorFlow(logicNodes).execute(runParamter);
            workflowId = execute.get().getWorkflowId();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }


        System.out.println("已提交并行任务，花费时间：" + (System.currentTimeMillis() - start) + "当前生成的实例id:" + workflowId);
        System.out.println(1);

    }

    private static void serialRun() throws Exception {
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
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            executor.shutdown();
        }

        System.out.println("已提交串行任务，当前生成的实例id:" + workflowId);
    }

    private ConductorWorkflow<Map<String, String>> createCondutorFlow(List<LogicNode> logicNodes) {
        WorkflowBuilder<Map<String, String>> workBuilder = new WorkflowBuilder<>(executor);
        workBuilder.name(" work_flow_name").ownerEmail("Daas@Smartstemp.com")
                .version(1).timeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY, 0).description("desc");//base info
        for (LogicNode logicNode : logicNodes) {
            logicNode.getNode().stream().forEach(e -> {
                workBuilder.add(e);
            });
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
