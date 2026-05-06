package com.equip.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equip.entity.EquipmentLedger;
import com.equip.entity.PageQuery;

import java.util.List;

public interface EquipmentLedgerService {

    IPage<EquipmentLedger> pageQuery(int current, int size, String equipmentName, String deviceNo, String[] department, String useStatus);

    void add(EquipmentLedger equipmentLedger);

    void update(EquipmentLedger equipmentLedger);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    EquipmentLedger getById(Long id);

    List<String> getUseStatusList();

    List<String> getDepartmentList();

    void importExcel(byte[] fileBytes);

    byte[] exportExcel(PageQuery query);
}