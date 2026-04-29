package com.equip.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

@Data
@TableName("device_standard")
public class DeviceStandard {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String standard;

    private String createTime;

    private String updateTime;
}