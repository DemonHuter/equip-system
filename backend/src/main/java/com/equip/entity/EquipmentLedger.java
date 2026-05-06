package com.equip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("equipment_ledger")
public class EquipmentLedger {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String equipmentName;

    private String specModel;

    private String serialNumber;

    private String deviceNo;

    private String manufacturer;

    private String enableDate;

    private String expectedScrapDate;

    private String installationLocation;

    private String keeper;

    private String maintenanceStatus;

    private String department;

    private String useStatus;

    private String createTime;

    private String updateTime;
}