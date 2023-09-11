package com.w.t.conductor.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.util.List;
import java.util.Map;

/**
 * @Packagename com.w.t.conductor.util
 * @Classname TaskInfo
 * @Description
 * @Authors Mr.Wu
 * @Version 1.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class AbTask {


    Map<String, Object> globalVariable;//全局变量

    @Builder.Default
    Integer faildTerminate = 1;//其他参数如对节点设置失败终止或者忽略,0忽略，1终止


}
