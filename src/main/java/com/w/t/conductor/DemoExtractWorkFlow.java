package com.w.t.conductor;

import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.WorkflowBuilder;
import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import com.netflix.conductor.sdk.workflow.def.tasks.SimpleTask;
import com.netflix.conductor.sdk.workflow.def.tasks.Task;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;

/**
 * @Packagename com.w.t.conductor
 * @Classname DemoExtractWorkFlow
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/08/08 16:29
 * @Version 1.0
 */

public class DemoExtractWorkFlow {

    private final WorkflowExecutor executor;

    public DemoExtractWorkFlow(WorkflowExecutor executor) {
        this.executor = executor;
        this.executor.initWorkers(DemoExtractWorkFlow.class.getPackageName());
    }

    public ConductorWorkflow<DataExtract> createApiForTestParallelSubTask() {

        WorkflowBuilder<DataExtract> builder = new WorkflowBuilder<>(executor);
        //TODO 具体逻辑填充

/*        builder.name("api_for_test_parallel_sub_task")
                .description("并行测试")
                .ownerEmail("lsg2")
                .version(1)
                .timeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY,0)
                .add(new Task<Object>() {
                })*/
/*        executor.loadTaskDefs("/tasks.json");
        executor.loadWorkflowDefs("/simple_workflow.json");*/

        ConductorWorkflow<DataExtract> conductorWorkflow = builder.build();
        conductorWorkflow.registerWorkflow(true, true);
        return conductorWorkflow;
    }

    public static void main(String[] args) throws Exception {
        WorkflowExecutor executor = new WorkflowExecutor("http://localhost:8080/api/");
        String location = "./x.json";
        executor.loadWorkflowDefs(location);
        System.out.println(1);
    }

}
