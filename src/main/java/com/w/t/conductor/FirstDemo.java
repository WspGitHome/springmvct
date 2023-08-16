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
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


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
        String body = "taskId=" + ConductorWorkflow.input.get("extract_task_id");
        Map<String, Object> httpRunHeader = new HashMap<>();
        httpRunHeader.put("Authorization", ConductorWorkflow.input.get("authotization"));
        httpRunHeader.put("Cookie", ConductorWorkflow.input.get("cookies"));
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

        final SetVariable setVariable1 = new SetVariable("changeInputValue").input("cookies", "changedCookis");
        final SetVariable setVariable2 = new SetVariable("changeInputValue2").input("cookies", "ssssssssssss");
        SetVariable setVariable = new SetVariable("setValue0001");

        setVariable.input("json",httprun.taskOutput.map("response").map("body").get("code"));



        //http2
        String referName2 = "http_get_status_" + System.currentTimeMillis();
        Http http_get_status = new Http(referName2);
//        String body2 = "taskId=" + httprun.taskOutput.map("response").map("body").get("code") + "&msg=" + httprun.taskOutput.map("response").map("body").get("message");
//        String body2 = "taskId=" + httprun.taskOutput.map("response").map("body").get("code") + "&msg=" +setVariable.taskOutput.map("result").list("constDataSource").get("下单年份",0);
        String body2 = "taskId=" + httprun.taskOutput.map("response").map("body").get("code") + "&msg=" +setVariable.getInput().get("json");

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
        String referName3 = "http_get_" + System.currentTimeMillis();
        Http httpget = new Http(referName3);
        Http.Input input3 = new Http.Input();
        input3.setConnectionTimeOut(5000);
        input3.setReadTimeOut(5000);
        input3.setUri("https://orkes-api-tester.orkesconductor.com/api");
        input3.setBody("a="+setVariable.taskOutput.map("result").list("constDataSource").get("下单年份",0));
        input3.setMethod(Http.Input.HttpMethod.GET);
        httpget.input(input3);

        final ConductorWorkflow<Map<String, String>> conductorWorkflow =
                workBuilder.name("getResult_"+System.currentTimeMillis())//任务流名称
                        .ownerEmail("user@example.com").version(1).timeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY, 0).description("fisrt demo")//基本信息

                        .add(httprun)
                        .add(jq1)
                        .add(jq2)
                        .add(setVariable1)
                        .add(setVariable)
                        .add(new DoWhile("do_while01", "$." + referName2 + "['response']['body']['result'] === 1", new Wait("wait_01", Duration.ofSeconds(5)), http_get_status))
                        .add(setVariable2)
                        .add(aSwitch)
                        .add(httpget)
                        .build();

        conductorWorkflow.registerWorkflow(true, true);
        return conductorWorkflow;
    }


    public ConductorWorkflow<Map<String, String>> createForkWorkflow() {

        WorkflowBuilder<Map<String,String>> workBuilder = new WorkflowBuilder<>(executor);
        //construct elements
        //httpleft1
        String referName1 = "httpleft1"+System.currentTimeMillis();
        Http httpleft1 = new Http(referName1);
        Http.Input input1 = new Http.Input();
        input1.setConnectionTimeOut(5000);
        input1.setReadTimeOut(5000);
        input1.setUri("https://orkes-api-tester.orkesconductor.com/api");
        input1.setMethod(Http.Input.HttpMethod.GET);
        httpleft1.input(input1);


        //httpMiddle
        String referNmae2 = "httpmiddle"+System.currentTimeMillis();
        Http httpmiddle = new Http(referNmae2);
        String body2 = "taskId=b";
        Http.Input input2 = new Http.Input();
        input2.setContentType("application/x-www-form-urlencoded");
        input2.setConnectionTimeOut(5000);
        input2.setReadTimeOut(5000);
        input2.setUri("http://123.57.192.38/gnfront/bury/getTaskStatus");
        input2.setBody(body2);
        input2.setMethod(Http.Input.HttpMethod.POST);
        httpmiddle.input(input2);


        //httpMiddle2
        String referName2_1 = "httpmiddle2"+System.currentTimeMillis();
        Http httpGet = new Http(referName2_1);
        Http.Input input2_1 = new Http.Input();
        input2_1.setConnectionTimeOut(5000);
        input2_1.setReadTimeOut(5000);
        input2_1.setUri("https://orkes-api-tester.orkesconductor.com/api");
        input2_1.setMethod(Http.Input.HttpMethod.GET);
        httpGet.input(input2_1);



        //httpRight
        String referNmae3 = "httpRight"+System.currentTimeMillis();
        Http httpRight = new Http(referNmae3);
        String body3 = "taskId=c";
        Http.Input input3 = new Http.Input();
        input3.setContentType("application/x-www-form-urlencoded");
        input3.setConnectionTimeOut(5000);
        input3.setReadTimeOut(5000);
        input3.setUri("http://123.57.192.38/gnfront/bury/getTaskStatus");
        input3.setBody(body3);
        input3.setMethod(Http.Input.HttpMethod.POST);
        httpRight.input(input3);


        //line4
        String referName4 = "httpline"+System.currentTimeMillis();
        Http httpGet1 = new Http(referName4);
        Http.Input input4 = new Http.Input();
        input4.setConnectionTimeOut(5000);
        input4.setReadTimeOut(5000);
        input4.setUri("https://orkes-api-tester.orkesconductor.com/api");
        input4.setMethod(Http.Input.HttpMethod.GET);
        httpGet1.input(input4);

        //line5
        String referName5 = "httpline2"+System.currentTimeMillis();
        Http httpGet2 = new Http(referName5);
        Http.Input input5 = new Http.Input();
        input5.setConnectionTimeOut(5000);
        input5.setReadTimeOut(5000);
        input5.setUri("https://orkes-api-tester.orkesconductor.com/api");
        input5.setMethod(Http.Input.HttpMethod.GET);
        httpGet2.input(input5);



        final ConductorWorkflow<Map<String, String>> conductorWorkflow = workBuilder.name("api_for_test_fork").ownerEmail("user@example.com").version(1).timeoutPolicy(WorkflowDef.TimeoutPolicy.ALERT_ONLY, 0).description("fork demo")//基本信息
                .add(new ForkJoin("fork_01", new Task[]{httpleft1},new Task[]{new DoWhile("get_user_details","$." + referNmae2 + "['response']['body']['result'] === 1",httpmiddle),httpGet},new Task[]{httpRight}).joinOn(httpGet.getTaskReferenceName()))
                .add(httpGet1).add(httpGet2).build();
        conductorWorkflow.registerWorkflow(true,true);
        return conductorWorkflow;
    }


    public static String getGenerateName(){
        final long currentTimeMillis = System.currentTimeMillis();
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
      return   String.valueOf(currentTimeMillis);
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

        final ExecutorService executorService = Executors.newFixedThreadPool(20);

        String conductorServerURL =
                "http://localhost:8080/api/";

        WorkflowExecutor executor = new WorkflowExecutor(conductorServerURL);
        // Create the new shipment workflow
        FirstDemo firstDemo = new FirstDemo(executor);
        final ConductorWorkflow<Map<String, String>> templateWorkflow = firstDemo.createTemplateWorkflow();
//        final ConductorWorkflow<Map<String, String>> templateWorkflow = firstDemo.createForkWorkflow();
        Map<String, String> runParamter = new HashMap<>();
        runParamter.put("authotization", "i am auth");
        runParamter.put("cookies", "i am cookies");
        runParamter.put("extract_task_id", "i am extract_task_id");
        final CompletableFuture<Workflow> executionFuture = templateWorkflow.execute(runParamter);//TODO带着运行参数立刻执行

        try {
            Thread.sleep(Long.parseLong("3000"));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        executor.shutdown();
        System.out.println("已提交");

    }

}
