package com.w.t.conductor.bean;

import com.netflix.conductor.sdk.workflow.def.tasks.Http;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname HttpInfo
 * @Description
 * @Authors Mr.Wu
 * @Date 2023/08/31 15:14
 * @Version 1.0
 */
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HttpInfo {

    private String appName;//调用的微服务名称 用于加后缀时间戳拼接referName作为唯一标识
    private String url;
    private String contentType;
    private String accept;
    private Http.Input.HttpMethod method;
    private String body;
    private Map<String, Object> headers;
    private int connectionTimeout = 10000;
    private int readTimeout = 10000;

}
