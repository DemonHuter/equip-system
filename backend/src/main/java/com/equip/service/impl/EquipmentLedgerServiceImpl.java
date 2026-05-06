package com.equip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equip.entity.EquipmentLedger;
import com.equip.entity.PageQuery;
import com.equip.mapper.EquipmentLedgerMapper;
import com.equip.service.EquipmentLedgerService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class EquipmentLedgerServiceImpl implements EquipmentLedgerService {

    @Autowired
    private EquipmentLedgerMapper equipmentLedgerMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");

    private boolean isExcelDate(double value) {
        return value >= 1.0 && value <= 2958465.0;
    }

    @Override
    public IPage<EquipmentLedger> pageQuery(int current, int size, String equipmentName, String deviceNo, String[] department, String useStatus) {
        Page<EquipmentLedger> page = new Page<>(current, size);
        QueryWrapper<EquipmentLedger> wrapper = buildQueryWrapper(equipmentName, deviceNo, department, useStatus);
        return equipmentLedgerMapper.selectPage(page, wrapper);
    }

    private QueryWrapper<EquipmentLedger> buildQueryWrapper(String equipmentName, String deviceNo, String[] department, String useStatus) {
        QueryWrapper<EquipmentLedger> wrapper = new QueryWrapper<>();
        if (equipmentName != null && !equipmentName.trim().isEmpty()) {
            wrapper.like("equipment_name", equipmentName.trim());
        }
        if (deviceNo != null && !deviceNo.trim().isEmpty()) {
            wrapper.like("device_no", deviceNo.trim());
        }
        if (department != null && department.length > 0) {
            wrapper.in("department", (Object[]) department);
        }
        if (useStatus != null && !useStatus.trim().isEmpty()) {
            wrapper.eq("use_status", useStatus.trim());
        }
        wrapper.orderByDesc("create_time");
        return wrapper;
    }

    @Override
    public void add(EquipmentLedger equipmentLedger) {
        String now = sdf.format(new Date());
        equipmentLedger.setCreateTime(now);
        equipmentLedger.setUpdateTime(now);
        equipmentLedgerMapper.insert(equipmentLedger);
    }

    @Override
    public void update(EquipmentLedger equipmentLedger) {
        equipmentLedger.setUpdateTime(sdf.format(new Date()));
        equipmentLedgerMapper.updateById(equipmentLedger);
    }

    @Override
    public void delete(Long id) {
        equipmentLedgerMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        equipmentLedgerMapper.deleteBatchIds(ids);
    }

    @Override
    public EquipmentLedger getById(Long id) {
        return equipmentLedgerMapper.selectById(id);
    }

    @Override
    public List<String> getUseStatusList() {
        return Arrays.asList("在用", "停用", "报废", "维修中");
    }

    @Override
    public List<String> getDepartmentList() {
        QueryWrapper<EquipmentLedger> wrapper = new QueryWrapper<>();
        wrapper.select("distinct department")
               .isNotNull("department")
               .ne("department", "");
        List<EquipmentLedger> list = equipmentLedgerMapper.selectList(wrapper);
        Set<String> departments = new HashSet<>();
        for (EquipmentLedger equipment : list) {
            if (equipment.getDepartment() != null && !equipment.getDepartment().isEmpty()) {
                departments.add(equipment.getDepartment());
            }
        }
        return new ArrayList<>(departments);
    }

    @Override
    public void importExcel(byte[] fileBytes) {
        try (InputStream inputStream = new ByteArrayInputStream(fileBytes);
             Workbook workbook = createWorkbook(inputStream, fileBytes)) {

            DataFormatter dataFormatter = new DataFormatter();
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();

            String now = sdf.format(new Date());
            int totalCount = 0;

            System.out.println("开始导入Excel，共 " + workbook.getNumberOfSheets() + " 个工作表");

            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                String sheetName = sheet.getSheetName();
                System.out.println("正在处理工作表: " + sheetName);

                List<EquipmentLedger> equipments = new ArrayList<>();

                int firstDataRow = 4;
                int firstDataCol = 2;
                System.out.println("数据起始行: 第 " + (firstDataRow + 1) + " 行, 数据起始列: 第 " + (firstDataCol + 1) + " 列");

                for (int i = firstDataRow; i <= sheet.getLastRowNum(); i++) {
                    Row row = sheet.getRow(i);
                    if (row == null || isRowEmpty(row, firstDataCol)) {
                        continue;
                    }

                    EquipmentLedger equipment = new EquipmentLedger();
                    equipment.setEquipmentName(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol), "equipmentName"));
                    equipment.setSpecModel(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 1), "specModel"));
                    equipment.setSerialNumber(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 2), "serialNumber"));
                    equipment.setDeviceNo(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 3), "deviceNo"));
                    equipment.setManufacturer(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 4), "manufacturer"));
                    equipment.setEnableDate(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 5), "enableDate"));
                    equipment.setExpectedScrapDate(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 6), "expectedScrapDate"));
                    equipment.setInstallationLocation(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 7), "installationLocation"));
                    equipment.setKeeper(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 8), "keeper"));
                    equipment.setMaintenanceStatus(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 9), "maintenanceStatus"));
                    equipment.setDepartment(sheetName);
                    equipment.setUseStatus(getCellValue(dataFormatter, evaluator, row.getCell(firstDataCol + 10), "useStatus"));
                    equipment.setCreateTime(now);
                    equipment.setUpdateTime(now);

                    if (!isEmpty(equipment.getEquipmentName()) && !isEmpty(equipment.getDeviceNo())) {
                        equipments.add(equipment);
                    }
                }

                if (!equipments.isEmpty()) {
                    System.out.println("工作表 " + sheetName + " 读取到 " + equipments.size() + " 条数据");
                    for (EquipmentLedger equipment : equipments) {
                        equipmentLedgerMapper.insert(equipment);
                    }
                    totalCount += equipments.size();
                }
            }

            System.out.println("Excel导入完成，共导入 " + totalCount + " 条数据");

        } catch (Exception e) {
            System.out.println("Excel导入失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Excel导入失败: " + e.getMessage(), e);
        }
    }

    @Override
    public byte[] exportExcel(PageQuery query) {
        try {
            String equipmentName = query != null ? query.getDeviceName() : null;
            String deviceNo = query != null ? query.getDeviceNo() : null;
            String[] department = query != null ? query.getDepartment() : null;
            String useStatus = query != null ? query.getNextInspectionDate() : null;

            QueryWrapper<EquipmentLedger> wrapper = buildQueryWrapper(equipmentName, deviceNo, department, useStatus);
            List<EquipmentLedger> equipments = equipmentLedgerMapper.selectList(wrapper);

            System.out.println("导出数据条数: " + equipments.size());

            ClassLoader classLoader = getClass().getClassLoader();
            InputStream templateStream = classLoader.getResourceAsStream("template/equipment_ledger_template.xlsx");
            Workbook workbook;

            if (templateStream == null) {
                System.out.println("模板文件不存在，使用默认模板");
                workbook = new XSSFWorkbook();
                Sheet sheet = workbook.createSheet("设备台账");
                String[] headers = {"序列", "小组", "计量器具名称", "规格型号", "序列号", "设备编号", "制造厂家", "启用日期", "预计报废日期", "安装位置", "保管人", "设备维修/维保", "使用状态"};
                Row headerRow = sheet.createRow(3);
                for (int i = 0; i < headers.length; i++) {
                    Cell cell = headerRow.createCell(i);
                    cell.setCellValue(headers[i]);
                }
            } else {
                workbook = new XSSFWorkbook(templateStream);
                templateStream.close();
            }

            Sheet sheet = workbook.getSheetAt(0);
            int dataRowIndex = 4;
            int seq = 1;

            for (EquipmentLedger equipment : equipments) {
                Row row = sheet.createRow(dataRowIndex++);

                int colIndex = 0;
                row.createCell(colIndex++).setCellValue(seq++);
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getDepartment()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getEquipmentName()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getSpecModel()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getSerialNumber()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getDeviceNo()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getManufacturer()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getEnableDate()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getExpectedScrapDate()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getInstallationLocation()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getKeeper()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getMaintenanceStatus()));
                row.createCell(colIndex++).setCellValue(nullToEmpty(equipment.getUseStatus()));
            }

            for (int i = 0; i < 13; i++) {
                sheet.autoSizeColumn(i);
            }

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            workbook.write(out);
            workbook.close();
            return out.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Excel导出失败: " + e.getMessage(), e);
        }
    }

    private String nullToEmpty(String value) {
        return value != null ? value : "";
    }

    private Workbook createWorkbook(InputStream inputStream, byte[] fileBytes) throws Exception {
        try {
            return new XSSFWorkbook(inputStream);
        } catch (Exception e) {
            System.out.println("不是xlsx格式，尝试xls格式...");
            return new HSSFWorkbook(new ByteArrayInputStream(fileBytes));
        }
    }

    private boolean isRowEmpty(Row row, int startCol) {
        for (int i = startCol; i < row.getLastCellNum(); i++) {
            Cell cell = row.getCell(i);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                String value = cell.toString().trim();
                if (!value.isEmpty()) {
                    return false;
                }
            }
        }
        return true;
    }

    private boolean isEmpty(String value) {
        return value == null || value.trim().isEmpty();
    }

    private String getCellValue(DataFormatter dataFormatter, FormulaEvaluator evaluator, Cell cell, String fieldName) {
        if (cell == null) {
            return "";
        }

        cell = evaluator.evaluateInCell(cell);

        CellType cellType = cell.getCellType();

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return dateSdf.format(cell.getDateCellValue());
                } else {
                    double numericValue = cell.getNumericCellValue();
                    if (isExcelDate(numericValue)) {
                        return convertExcelDateToString(numericValue);
                    }
                    if (numericValue == Math.floor(numericValue)) {
                        return String.valueOf((long) numericValue);
                    }
                    return String.valueOf(numericValue);
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    String formulaValue = dataFormatter.formatCellValue(cell, evaluator);
                    if (formulaValue != null && !formulaValue.isEmpty()) {
                        try {
                            double numericResult = Double.parseDouble(formulaValue);
                            if (isExcelDate(numericResult)) {
                                return convertExcelDateToString(numericResult);
                            }
                        } catch (NumberFormatException e) {
                        }
                    }
                    return formulaValue;
                } catch (Exception e) {
                    return "";
                }
            default:
                return "";
        }
    }

    private String convertExcelDateToString(double excelDate) {
        try {
            Date date = DateUtil.getJavaDate(excelDate);
            return dateSdf.format(date);
        } catch (Exception e) {
            return String.valueOf(excelDate);
        }
    }
}