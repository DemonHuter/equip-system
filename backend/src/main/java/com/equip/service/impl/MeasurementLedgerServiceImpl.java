package com.equip.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.equip.entity.MeasurementLedger;
import com.equip.mapper.MeasurementLedgerMapper;
import com.equip.service.MeasurementLedgerService;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class MeasurementLedgerServiceImpl implements MeasurementLedgerService {

    @Autowired
    private MeasurementLedgerMapper ledgerMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private SimpleDateFormat dateSdf = new SimpleDateFormat("yyyy-MM-dd");

    private boolean isExcelDate(double value) {
        return value >= 1.0 && value <= 2958465.0;
    }

    @Override
    public IPage<MeasurementLedger> pageQuery(int current, int size, String deviceName, String deviceNo, String[] department, String nextInspectionDate) {
        Page<MeasurementLedger> page = new Page<>(current, size);
        QueryWrapper<MeasurementLedger> wrapper = new QueryWrapper<>();
        if (deviceName != null && !deviceName.isEmpty()) {
            wrapper.like("device_name", deviceName);
        }
        if (deviceNo != null && !deviceNo.isEmpty()) {
            wrapper.like("device_no", deviceNo);
        }
        System.out.println("科室查询条件: " + (department == null ? "null" : java.util.Arrays.toString(department)));
        if (department != null && department.length > 0) {
            wrapper.in("department", (Object[]) department);
        }
        if (nextInspectionDate != null && !nextInspectionDate.isEmpty()) {
            wrapper.like("next_inspection_date", nextInspectionDate);
        }
        return ledgerMapper.selectPage(page, wrapper);
    }

    @Override
    public void add(MeasurementLedger ledger) {
        String now = sdf.format(new Date());
        ledger.setCreateTime(now);
        ledger.setUpdateTime(now);
        ledgerMapper.insert(ledger);
    }

    @Override
    public void update(MeasurementLedger ledger) {
        ledger.setUpdateTime(sdf.format(new Date()));
        ledgerMapper.updateById(ledger);
    }

    @Override
    public void delete(Long id) {
        ledgerMapper.deleteById(id);
    }

    @Override
    public void batchDelete(List<Long> ids) {
        ledgerMapper.deleteBatchIds(ids);
    }

    @Override
    public MeasurementLedger getById(Long id) {
        return ledgerMapper.selectById(id);
    }

    @Override
    public void importExcel(MultipartFile file) {
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            FormulaEvaluator evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            DataFormatter dataFormatter = new DataFormatter();

            Sheet sheet = workbook.getSheetAt(0);
            List<MeasurementLedger> ledgers = new ArrayList<>();
            String now = sdf.format(new Date());

            System.out.println("开始从第4行（索引3）读取数据...");
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    System.out.println("第 " + (i+1) + " 行为空，跳过");
                    continue;
                }

                System.out.println("读取第 " + (i+1) + " 行数据");

                MeasurementLedger ledger = new MeasurementLedger();
                ledger.setDeviceName(getCellValue(dataFormatter, evaluator, row.getCell(0), "deviceName"));
                ledger.setSpecModel(getCellValue(dataFormatter, evaluator, row.getCell(1), "specModel"));
                ledger.setDeviceNo(getCellValue(dataFormatter, evaluator, row.getCell(2), "deviceNo"));
                ledger.setInspectionUnit(getCellValue(dataFormatter, evaluator, row.getCell(3), "inspectionUnit"));
                ledger.setInspectionDate(getCellValue(dataFormatter, evaluator, row.getCell(4), "inspectionDate"));
                ledger.setTestResult(getCellValue(dataFormatter, evaluator, row.getCell(6), "testResult"));
                ledger.setCertNo(getCellValue(dataFormatter, evaluator, row.getCell(7), "certNo"));
                ledger.setCycle(getCellValue(dataFormatter, evaluator, row.getCell(8), "cycle"));
                ledger.setNextInspectionDate(getCellValue(dataFormatter, evaluator, row.getCell(9), "nextInspectionDate"));
                ledger.setRemark(getCellValue(dataFormatter, evaluator, row.getCell(10), "remark"));
                ledger.setDepartment(getCellValue(dataFormatter, evaluator, row.getCell(11), "department"));
                ledger.setJudgmentStandard(getCellValue(dataFormatter, evaluator, row.getCell(12), "judgmentStandard"));
                ledger.setCreateTime(now);
                ledger.setUpdateTime(now);
                ledgers.add(ledger);

                System.out.println("添加数据: 设备名称=" + ledger.getDeviceName() + ", 设备编号=" + ledger.getDeviceNo());
            }

            System.out.println("读取到 " + ledgers.size() + " 条数据");
            if (!ledgers.isEmpty()) {
                System.out.println("开始插入数据...");
                for (MeasurementLedger ledger : ledgers) {
                    ledgerMapper.insert(ledger);
                }
                System.out.println("数据插入完成，共插入 " + ledgers.size() + " 条数据");
            } else {
                System.out.println("没有读取到数据，可能是Excel文件格式不正确或数据起始行设置错误");
            }
        } catch (Exception e) {
            System.out.println("Excel导入失败: " + e.getMessage());
            e.printStackTrace();
            throw new RuntimeException("Excel导入失败: " + e.getMessage(), e);
        }
    }

    @Override
    public List<String> getDepartments() {
        QueryWrapper<MeasurementLedger> wrapper = new QueryWrapper<>();
        wrapper.select("distinct department")
               .isNotNull("department")
               .ne("department", "");
        List<MeasurementLedger> list = ledgerMapper.selectList(wrapper);
        Set<String> departments = new HashSet<>();
        for (MeasurementLedger ledger : list) {
            if (ledger.getDepartment() != null && !ledger.getDepartment().isEmpty()) {
                departments.add(ledger.getDepartment());
            }
        }
        return new ArrayList<>(departments);
    }

    @Override
    public byte[] exportExcel(List<MeasurementLedger> ledgers) {
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            InputStream templateStream = classLoader.getResourceAsStream("template/measurement_ledger_template.xlsx");
            if (templateStream == null) {
                throw new RuntimeException("模板文件不存在");
            }

            try (Workbook workbook = new XSSFWorkbook(templateStream)) {
                Sheet sheet = workbook.getSheetAt(0);

                int dataRowIndex = 3;

                for (MeasurementLedger ledger : ledgers) {
                    Row row = sheet.createRow(dataRowIndex++);

                    int colIndex = 0;
                    row.createCell(colIndex++).setCellValue(ledger.getDeviceName());
                    row.createCell(colIndex++).setCellValue(ledger.getSpecModel());
                    row.createCell(colIndex++).setCellValue(ledger.getDeviceNo());
                    row.createCell(colIndex++).setCellValue(ledger.getInspectionUnit());
                    row.createCell(colIndex++).setCellValue(ledger.getInspectionDate());
                    // row.createCell(colIndex++).setCellValue("");
                    row.createCell(colIndex++).setCellValue(ledger.getTestResult());
                    row.createCell(colIndex++).setCellValue(ledger.getCertNo());
                    row.createCell(colIndex++).setCellValue(ledger.getCycle());
                    row.createCell(colIndex++).setCellValue(ledger.getNextInspectionDate());
                    row.createCell(colIndex++).setCellValue(ledger.getRemark());
                    row.createCell(colIndex++).setCellValue(ledger.getDepartment());
                    row.createCell(colIndex++).setCellValue(ledger.getJudgmentStandard());
                }

                java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
                workbook.write(out);
                return out.toByteArray();
            }
        } catch (Exception e) {
            throw new RuntimeException("Excel导出失败: " + e.getMessage(), e);
        }
    }

    private String getCellValue(DataFormatter dataFormatter, FormulaEvaluator evaluator, Cell cell, String fieldName) {
        if (cell == null) {
            return "";
        }

        CellType cellType = cell.getCellType();

        switch (cellType) {
            case STRING:
                return cell.getStringCellValue().trim();
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return sdf.format(cell.getDateCellValue());
                }
                double numericValue = cell.getNumericCellValue();
                if (isExcelDate(numericValue)) {
                    return convertExcelDateToString(numericValue);
                }
                if (numericValue == Math.floor(numericValue)) {
                    return String.valueOf((long) numericValue);
                }
                return String.valueOf(numericValue);
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