package com.equip.entity;

import com.fasterxml.jackson.annotation.JsonSetter;
import lombok.Data;

@Data
public class PageQuery {

    private int current;
    private int size;
    private String deviceName;
    private String deviceNo;
    private String[] department;
    private String nextInspectionDate;

    @JsonSetter("department")
    public void setDepartment(Object department) {
        if (department == null) {
            this.department = null;
        } else if (department instanceof String[]) {
            String[] arr = (String[]) department;
            if (arr.length == 0) {
                this.department = null;
            } else {
                this.department = arr;
            }
        } else if (department instanceof String) {
            String deptStr = (String) department;
            if (deptStr.isEmpty()) {
                this.department = null;
            } else {
                this.department = new String[]{deptStr};
            }
        }
    }
}