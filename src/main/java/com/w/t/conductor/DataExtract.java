package com.w.t.conductor;

import lombok.Data;

/**
 * @Packagename com.w.t.conductor
 * @Classname DataExtract
 * @Description 集成任务参数集合
 * @Authors Mr.Wu
 * @Date 2023/08/08 16:25
 * @Version 1.0
 */
@Data
public class DataExtract {

    private String taskId;

    private String instanceId;

    private String runType;
}
