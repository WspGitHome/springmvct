package com.w.t.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Builder;
import lombok.Data;

/**
 * @Packagename com.w.t.entity
 * @Classname report
 * @Description
 * @Authors Mr.Wu
 * @Date 2022/06/01 08:41
 * @Version 1.0
 */
@Data
@Builder
public class Report {

    private String id;
    private String title;
    private String url;
    private String year;
    private String month;
    private String quarter;
    private String catagory;
    @TableField("createTime")
    private String createTime;
}
