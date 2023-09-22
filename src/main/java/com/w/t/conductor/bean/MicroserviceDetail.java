package com.w.t.conductor.bean;

import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import lombok.Getter;


/**
 * @Packagename com.w.t.conductor.util
 * @Classname MicroserviceDetail
 * @Description 接口基本属性是固定的，存储接口的模版信息，真实请求时只需替换相应变参
 * @Authors Mr.Wu
 * @Date 2023/08/16 14:55
 * @Version 1.0
 */
public enum MicroserviceDetail {

    //数据集成服务-触发
    DATA_EXTRACT_RUN("http://daas.smartsteps.com/dataextract/manager/taskrun", Http.Input.HttpMethod.POST, "*/*", "application/x-www-form-urlencoded", "taskId=${taskId}&runtype=0", 60000, 60000),
    //数据集成服务-获取结果 -1 执行中， 0失败，1 执行成功
    DATA_EXTRACT_GET_STATUS("http://123.57.192.38/gnfront/bury/getTaskStatus", Http.Input.HttpMethod.POST, "*/*", "application/x-www-form-urlencoded", "taskId=", 60000, 60000),

    //A触发服务
    DATA_A_RUN("https://www.baidu.com/sugrec?&prod=pc_his&from=pc_web&json=1&sid=&hisdata=&_t=1695025460747&req=2&csor=0", Http.Input.HttpMethod.GET, "*/*", null, null, 60000, 60000);

    //TODO ....服务

    MicroserviceDetail(String url, Http.Input.HttpMethod method, String accept, String contentType, String body, int connectionTimeOut, int readTimeOut) {
        this.url = url;
        this.method = method;
        this.accept = accept;
        this.contentType = contentType;
        this.body = body;
        this.readTimeOut = readTimeOut;
        this.connectionTimeOut = connectionTimeOut;
    }


    @Getter
    private String url;//服务接口路由不包含网站ip 需根据实际部署情况获取网站前缀
    @Getter
    private Http.Input.HttpMethod method;
    @Getter
    private String accept;
    @Getter
    private String contentType;
    @Getter
    private String body;//此处模版里全部是key=value 形式。根据类型拼接,value值为#{}代表需替换
    @Getter
    private int connectionTimeOut;
    @Getter
    private int readTimeOut;


    public static void main(String[] args) {

    }

}
