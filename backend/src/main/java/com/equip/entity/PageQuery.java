package com.equip.entity;

import lombok.Data;

@Data
public class PageQuery {

    private int current;
    private int size;
    private String deviceName;
    private String deviceNo;
    private String[] department;
    private String nextInspectionDate;

}