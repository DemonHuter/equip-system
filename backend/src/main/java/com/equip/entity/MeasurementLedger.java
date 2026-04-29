package com.equip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;

@Data
@TableName("measurement_ledger")
public class MeasurementLedger implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String deviceName;

    private String specModel;

    private String deviceNo;

    private String inspectionUnit;

    private String inspectionDate;

    private String testResult;

    private String certNo;

    private String cycle;

    private String nextInspectionDate;

    private String remark;

    private String department;

    private String judgmentStandard;

    private String createTime;

    private String updateTime;

}