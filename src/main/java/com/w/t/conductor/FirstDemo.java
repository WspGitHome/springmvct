package com.w.t.conductor;


import cn.hutool.json.JSONUtil;
import com.netflix.conductor.common.metadata.workflow.WorkflowDef;
import com.netflix.conductor.common.run.Workflow;
import com.netflix.conductor.sdk.workflow.def.ConductorWorkflow;
import com.netflix.conductor.sdk.workflow.def.WorkflowBuilder;
import com.netflix.conductor.sdk.workflow.def.tasks.*;
import com.netflix.conductor.sdk.workflow.executor.WorkflowExecutor;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;


/**
 * @Packagename com.w.t.conductor
 * @Classname FirstDemo
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/08/07 17:45
 * @Version 1.0
 */
public class FirstDemo {

    private final WorkflowExecutor executor;


    public FirstDemo(WorkflowExecutor executor) {
        this.executor = executor;
        this.executor.initWorkers(FirstDemo.class.getPackageName());
    }

    //TODO api/workflow/{taskRefernceName}  ->加运行参数。 可以执行任务并立刻返回实例id
    //TODO 获取失败异常，来自我们接口自定义可以通过如下实现  api/workflow/{workflowId} 获取实例运行后的json, 提取 $..terminationReason 并过滤${ 开始的即可


    public ConductorWorkflow<Map<String, String>> createTemplateWorkflow() {
        WorkflowBuilder<Map<String, String>> workBuilder = new WorkflowBuilder<>(executor);

        //http1
        String referName = "httprun" + System.currentTimeMillis();
        String body ="taskId="+ConductorWorkflow.input.get("extract_task_id");
        Map<String,Object> httpRunHeader = new HashMap<>();
        httpRunHeader.put("Authorization",ConductorWorkflow.input.get("authotization"));
        httpRunHeader.put("Cookie",ConductorWorkflow.input.get("cookies"));

        Http.Input input = new Http.Input();
        input.setContentType("application/x-www-form-urlencoded");
        input.setConnectionTimeOut(5000);
        input.setReadTimeOut(5000);
        input.setUri("http://daas.smartsteps.com/dataextract/manager/taskrun");
        input.setBody(body);
        input.setMethod(Http.Input.HttpMethod.POST);
        input.setHeaders(httpRunHeader);
        Http httprun = new Http(referName).input(input);


        //jq 后来发现这个东西可有可无
        final JQ jq1 = new JQ("jq_01", ".jq_result | .message").input("jq_result", JSONUtil.parseObj(httprun.taskOutput.map("response")).get("body"));
        final JQ jq2 = new JQ("jq_02", ".jq_result | .code").input("jq_result", JSONUtil.parseObj(httprun.taskOutput.map("response")).get("body"));

        //http2
        String referName2 ="http_get_status_"+System.currentTimeMillis();
        Http http_get_status = new Http(referName2);
        String body2 = "taskId="+httprun.taskOutput.map("response").map("body").get("code")+"&msg="+httprun.taskOutput.map("response").map("body").get("message");
        Http.Input input2 = new Http.Input();
        input2.setContentType("application/x-www-form-urlencoded");
        input2.setConnectionTimeOut(5000);
        input2.setReadTimeOut(5000);
        input2.setUri("http://123.57.192.38/gnfront/bury/getTaskStatus");
        input2.setBody(body2);
        input2.setMethod(Http.Input.HttpMethod.POST);
        http_get_status.input(input2);


        Terminate terminate = new Terminate("terminate_01", http_get_status.taskOutput.map("response").map("body").get("code"));
        Terminate terminate2 = new Terminate("terminate_02", http_get_status.taskOutput.map("response").map("body").get("message"));

        //switch
        Switch aSwitch = new Switch("switch_001", http_get_status.taskOutput.map("response").map("body").get("result"))
//                .switchCase("-1", new Terminate("terminate_01",http_get_status.taskOutput.map("response").map("body").get("message")))//读不到变量
                .switchCase("-1", terminate)
                .switchCase("faild", terminate2)
                .defaultCase(new ArrayList<>());


        //http3
        String referName3 ="http_get_"+System.currentTimeMillis();
        Http httpget = new Http(referName3);
        Http.Input input3 = new Http.Input();
        input3.setConnectionTimeOut(5000);
        input3.setReadTimeOut(5000);
        input3.setUri("https://orkes-api-tester.orkesconductor.com/api");
        input3.setMethod(Http.Input.HttpMethod.GET);
        httpget.input(input3);

        final ConductorWorkflow<Map<String, String>> conductorWorkflow =
                workBuilder.name("api_for_test_parallel_dowhile_v3")//任务流名称
                        .ownerEmail("user@example.com").version(1).timeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY, 0).description("fisrt demo")//基本信息

                        .add(httprun)
                        .add(jq1)
                        .add(jq2)
                        .add(new DoWhile("do_while01","$."+referName2+"['response']['body']['result'] === 1",new Wait("wait_01", Duration.ofSeconds(5)),http_get_status))
                        .add(aSwitch)
                        .add(httpget)
                        .build();
        conductorWorkflow.registerWorkflow(true, true);
        return conductorWorkflow;
    }


    public static void main(String[] args) {
        testRun();
/*
        //testfor Http
        String referName = "httprun" + System.currentTimeMillis();
        String body ="taskId="+ConductorWorkflow.input.get("extract_task_id");
        Map<String,Object> httpRunHeader = new HashMap<>();
        httpRunHeader.put("Authorization",ConductorWorkflow.input.get("authotization"));
        httpRunHeader.put("Cookie",ConductorWorkflow.input.get("cookies"));

        final Http.Input input = new Http.Input();
        input.setContentType("application/x-www-form-urlencoded");
        input.setConnectionTimeOut(5000);
        input.setReadTimeOut(5000);
        input.setUri("http://daas.smartsteps.com/dataextract/manager/taskrun");
        input.setBody(body);
        input.setMethod(Http.Input.HttpMethod.POST);
        input.setHeaders(httpRunHeader);
        Http httprun = new Http(referName).input(input);

        //testfor JQ
        final JQ input1 = new JQ("jq_01", ".jq_result | .message").input("jq_result",httprun.taskOutput.map("reponse").get("body"));


        String referName2 = "http_get_status_11111111" ;
        final Http http2 = new Http(referName2);
        final DoWhile do_while01 = new DoWhile("do_while01", "$." + referName2 + "['response']['body']['result'] === 1", new Wait("22221"), http2);//condition 这样写可以，其他写法未发现
        final DoWhile do_while02 = new DoWhile("do_while01", http2.taskOutput.map("response").map("body").get("result") +"===1", new Wait("22221"), http2);//condition语法不支持

        System.out.println(JSONUtil.toJsonStr(do_while01));
        System.out.println(JSONUtil.toJsonStr(do_while02));
*/



    }

    private static void testRun() {
        String conductorServerURL =
                "http://localhost:8080/api/";

        WorkflowExecutor executor = new WorkflowExecutor(conductorServerURL);
        // Create the new shipment workflow
        FirstDemo firstDemo = new FirstDemo(executor);
        final ConductorWorkflow<Map<String, String>> templateWorkflow = firstDemo.createTemplateWorkflow();
        Map<String,String> runParamter = new HashMap<>();
        runParamter.put("authotization","i am auth");
        runParamter.put("cookies","i am cookies");
        runParamter.put("extract_task_id","i am extract_task_id");
        final CompletableFuture<Workflow> executionFuture = templateWorkflow.execute(runParamter);//TODO带着运行参数立刻执行
//        templateWorkflow.registerWorkflow(true);//TODO按名字重写一个任务流(更新)
/*        try {
            Workflow run = executionFuture.get(10, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (TimeoutException e) {
            e.printStackTrace();
        }*/
        executor.shutdown();
        System.out.println("已提交");

    }

}
