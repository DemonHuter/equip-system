package com.equip.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.equip.common.Response;
import com.equip.entity.BatchDeleteRequest;
import com.equip.entity.MeasurementLedger;
import com.equip.entity.PageQuery;
import com.equip.service.MeasurementLedgerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

import java.util.List;

@RestController
@RequestMapping("/api/ledger")
public class MeasurementLedgerController {

    @Autowired
    private MeasurementLedgerService ledgerService;

    @PostMapping("/page")
    public Response<IPage<MeasurementLedger>> pageQuery(@RequestBody PageQuery query) {
        IPage<MeasurementLedger> page = ledgerService.pageQuery(
                query.getCurrent(),
                query.getSize(),
                query.getDeviceName(),
                query.getDeviceNo(),
                query.getDepartment(),
                query.getNextInspectionDate()
        );
        return Response.success(page);
    }

    @PostMapping("/add")
    public Response<Void> add(@RequestBody MeasurementLedger ledger) {
        ledgerService.add(ledger);
        return Response.success();
    }

    @PutMapping("/update")
    public Response<Void> update(@RequestBody MeasurementLedger ledger) {
        ledgerService.update(ledger);
        return Response.success();
    }

    @DeleteMapping("/delete/{id}")
    public Response<Void> delete(@PathVariable Long id) {
        ledgerService.delete(id);
        return Response.success();
    }

    @DeleteMapping("/batchDelete")
    public Response<Void> batchDelete(@RequestBody BatchDeleteRequest request) {
        ledgerService.batchDelete(request.getIds());
        return Response.success();
    }

    @GetMapping("/get/{id}")
    public Response<MeasurementLedger> getById(@PathVariable Long id) {
        MeasurementLedger ledger = ledgerService.getById(id);
        return Response.success(ledger);
    }

    @PostMapping("/import")
    public Response<Void> importExcel(@RequestParam("file") MultipartFile file) {
        ledgerService.importExcel(file);
        return Response.success();
    }

    @GetMapping("/departments")
    public Response<List<String>> getDepartments() {
        List<String> departments = ledgerService.getDepartments();
        return Response.success(departments);
    }

    @PostMapping("/export")
    public void exportExcel(@RequestBody PageQuery query, HttpServletResponse response) {
        // 设置分页为全部数据
        query.setCurrent(1);
        query.setSize(Integer.MAX_VALUE);
        
        IPage<MeasurementLedger> page = ledgerService.pageQuery(
                query.getCurrent(),
                query.getSize(),
                query.getDeviceName(),
                query.getDeviceNo(),
                query.getDepartment(),
                query.getNextInspectionDate()
        );
        
        System.out.println("导出数据数量: " + page.getRecords().size());
        
        byte[] excelBytes = ledgerService.exportExcel(page.getRecords());
        
        try {
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setHeader("Content-Disposition", "attachment; filename=\"measurement_ledger.xlsx\"");
            response.setContentLength(excelBytes.length);
            response.getOutputStream().write(excelBytes);
            response.getOutputStream().flush();
            System.out.println("导出Excel成功");
        } catch (Exception e) {
            System.err.println("导出Excel失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("导出Excel失败: " + e.getMessage(), e);
        }
    }

}