package com.equip.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equip.common.Response;
import com.equip.entity.BatchDeleteRequest;
import com.equip.entity.EquipmentLedger;
import com.equip.entity.PageQuery;
import com.equip.service.EquipmentLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/equipment")
public class EquipmentLedgerController {

    @Autowired
    private EquipmentLedgerService equipmentLedgerService;

    @PostMapping("/page")
    public Response<IPage<EquipmentLedger>> pageQuery(@RequestBody PageQuery query) {
        IPage<EquipmentLedger> page = equipmentLedgerService.pageQuery(
                query.getCurrent(),
                query.getSize(),
                query.getDeviceName(),
                query.getDeviceNo(),
                query.getDepartment(),
                query.getNextInspectionDate()
        );
        return Response.success(page);
    }

    @GetMapping("/get/{id}")
    public Response<EquipmentLedger> getById(@PathVariable Long id) {
        EquipmentLedger equipmentLedger = equipmentLedgerService.getById(id);
        return Response.success(equipmentLedger);
    }

    @GetMapping("/status-list")
    public Response<List<String>> getUseStatusList() {
        List<String> statusList = equipmentLedgerService.getUseStatusList();
        return Response.success(statusList);
    }

    @GetMapping("/departments")
    public Response<List<String>> getDepartmentList() {
        List<String> departmentList = equipmentLedgerService.getDepartmentList();
        return Response.success(departmentList);
    }

    @PostMapping("/add")
    public Response<Void> add(@RequestBody EquipmentLedger equipmentLedger) {
        equipmentLedgerService.add(equipmentLedger);
        return Response.success();
    }

    @PutMapping("/update")
    public Response<Void> update(@RequestBody EquipmentLedger equipmentLedger) {
        equipmentLedgerService.update(equipmentLedger);
        return Response.success();
    }

    @DeleteMapping("/delete/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        equipmentLedgerService.delete(id);
        return Response.success();
    }

    @DeleteMapping("/batchDelete")
    public Response<Void> batchDelete(@RequestBody BatchDeleteRequest request) {
        equipmentLedgerService.batchDelete(request.getIds());
        return Response.success();
    }

    @PostMapping("/import")
    public Response<Void> importExcel(@RequestParam("file") MultipartFile file) throws IOException {
        equipmentLedgerService.importExcel(file.getBytes());
        return Response.success();
    }

    @PostMapping("/export")
    public void exportExcel(@RequestBody(required = false) PageQuery query, HttpServletResponse response) throws IOException {
        if (query == null) {
            query = new PageQuery();
        }
        byte[] excelData = equipmentLedgerService.exportExcel(query);
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=设备台账.xlsx");
        response.getOutputStream().write(excelData);
    }
}