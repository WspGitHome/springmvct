package com.w.t.entity;

import lombok.Builder;
import lombok.Data;

/**
 * @Packagename com.w.t.dao
 * @Classname SlideShow
 * @Description 轮播图
 * @Authors Mr.Wu
 * @Date 2022/06/09 14:09
 * @Version 1.0
 */
@Data
@Builder
public class SlideShow {

    private String name;
    private String url;
    private String id;
    private int sorted;
}
