package com.w.t.conductor.bean;

import cn.hutool.core.lang.UUID;
import com.w.t.conductor.generator.NodeGenerator;
import com.w.t.conductor.util.RandomCodeGenerator;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

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

    @Builder.Default
    String id = RandomCodeGenerator.getRandomWithTimstampPre();//当前任务流中节点的唯一标识 不能以数字开头

}
