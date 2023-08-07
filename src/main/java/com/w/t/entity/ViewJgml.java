package com.w.t.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

/**
 * @Packagename com.w.t.entity
 * @Classname JgView
 * @Description
 * @Authors Mr.Wu
 * @Date 2022/04/14 18:03
 * @Version 1.0
 */
@Data
public class ViewJgml {

    @TableField("ID")
    private String ID;
    @TableField("TaskID")
    private String TaskID;
    @TableField("VarietyCode")
    private String FormCode;
    @TableField("VarietyCode")
    private String VarietyCode;
    @TableField("VarietyName")
    private String VarietyName;
    @TableField("TargetCode")
    private String TargetCode;
    @TableField("TargetName")
    private String TargetName;
    @TableField("ThisPrice")
    private String ThisPrice;
    @TableField("PriceDate")
    private String PriceDate;
    @TableField("UnitCode")
    private String UnitCode;
    @TableField("Unit")
    private String Unit;
    @TableField("StandardCode")
    private String StandardCode;
    @TableField("StandardName")
    private String StandardName;
    @TableField("IfReplace")
    private String IfReplace;
    @TableField("Status")
    private String Status;
    @TableField("Remark")
    private String Remark;
    @TableField("DELETEFLAG")
    private String DELETEFLAG;
    @TableField("ADDUSERID")
    private String ADDUSERID;
    @TableField("ADDTIME")
    private String ADDTIME;
    @TableField("EDITUSERID")
    private String EDITUSERID;
    @TableField("EDITTIME")
    private String EDITTIME;
    @TableField("STATIONCODE")
    private String STATIONCODE;
    @TableField("REGIONCODE")
    private String REGIONCODE;
    @TableField("REGIONNAME")
    private String REGIONNAME;
    @TableField("channel")
    private String channel;
    @TableField("brand")
    private String brand;
    @TableField("In_time")
    private String In_time;
}
