package com.equip.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equip.entity.MeasurementLedger;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface MeasurementLedgerService {

    IPage<MeasurementLedger> pageQuery(int current, int size, String deviceName, String deviceNo, String[] department, String nextInspectionDate);

    void add(MeasurementLedger ledger);

    void update(MeasurementLedger ledger);

    void delete(Long id);

    void batchDelete(List<Long> ids);

    MeasurementLedger getById(Long id);

    void importExcel(MultipartFile file);

    List<String> getDepartments();

    byte[] exportExcel(List<MeasurementLedger> ledgers);

}