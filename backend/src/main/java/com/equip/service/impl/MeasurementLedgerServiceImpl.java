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
import java.util.List;

@Service
public class MeasurementLedgerServiceImpl implements MeasurementLedgerService {

    @Autowired
    private MeasurementLedgerMapper ledgerMapper;

    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private FormulaEvaluator evaluator;

    /**
     * 判断一个数字是否是Excel日期格式
     * Excel日期是从1900年1月1日开始的天数
     */
    private boolean isExcelDate(double value) {
        // Excel日期范围：1900-01-01 到 9999-12-31
        // 对应的Excel天数范围：1.0 到 2958465.0
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
        // 打印科室查询条件，以便调试
        System.out.println("科室查询条件: " + (department == null ? "null" : java.util.Arrays.toString(department)));
        if (department != null && department.length > 0) {
            wrapper.in("department", department);
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
        System.out.println("开始导入Excel文件: " + file.getOriginalFilename());
        System.out.println("文件大小: " + file.getSize() + " bytes");
        try (InputStream inputStream = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(inputStream)) {
            // 初始化FormulaEvaluator
            evaluator = workbook.getCreationHelper().createFormulaEvaluator();
            
            System.out.println("Excel文件读取成功，工作表数量: " + workbook.getNumberOfSheets());
            
            // 遍历所有工作表
            for (int sheetIndex = 0; sheetIndex < workbook.getNumberOfSheets(); sheetIndex++) {
                Sheet sheet = workbook.getSheetAt(sheetIndex);
                System.out.println("工作表 " + sheetIndex + " 名称: " + sheet.getSheetName());
                System.out.println("工作表 " + sheetIndex + " 总行数: " + (sheet.getLastRowNum() + 1));
                System.out.println("工作表 " + sheetIndex + " 第一行索引: " + sheet.getFirstRowNum());
                System.out.println("工作表 " + sheetIndex + " 最后一行索引: " + sheet.getLastRowNum());
            }
            
            // 使用第二个工作表（Sheet2），因为它包含实际数据
            Sheet sheet = workbook.getSheetAt(1);
            List<MeasurementLedger> ledgers = new ArrayList<>();
            String now = sdf.format(new Date());

            // 从第四行开始读取数据（索引为3），因为第三行是表头
            System.out.println("开始从第4行（索引3）读取数据...");
            for (int i = 3; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) {
                    System.out.println("第 " + (i+1) + " 行为空，跳过");
                    continue;
                }
                
                System.out.println("读取第 " + (i+1) + " 行数据");
                
                MeasurementLedger ledger = new MeasurementLedger();
                ledger.setDeviceName(getCellValue(row.getCell(0), "deviceName"));
                ledger.setSpecModel(getCellValue(row.getCell(1), "specModel"));
                ledger.setDeviceNo(getCellValue(row.getCell(2), "deviceNo"));
                ledger.setInspectionUnit(getCellValue(row.getCell(3), "inspectionUnit"));
                ledger.setInspectionDate(getCellValue(row.getCell(4), "inspectionDate"));
                ledger.setActualImplementation(getCellValue(row.getCell(5), "actualImplementation"));
                ledger.setTestResult(getCellValue(row.getCell(6), "testResult"));
                ledger.setCertNo(getCellValue(row.getCell(7), "certNo"));
                ledger.setCycle(getCellValue(row.getCell(8), "cycle"));
                ledger.setNextInspectionDate(getCellValue(row.getCell(9), "nextInspectionDate"));
                ledger.setRemark(getCellValue(row.getCell(10), "remark"));
                ledger.setDepartment(getCellValue(row.getCell(11), "department"));
                ledger.setJudgmentStandard(getCellValue(row.getCell(12), "judgmentStandard"));
                ledger.setCreateTime(now);
                ledger.setUpdateTime(now);
                ledgers.add(ledger);
                
                System.out.println("添加数据: 设备名称=" + ledger.getDeviceName() + ", 设备编号=" + ledger.getDeviceNo());
            }

            System.out.println("读取到 " + ledgers.size() + " 条数据");
            // 批量插入数据
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
        // 从数据库中获取所有科室，去重
        QueryWrapper<MeasurementLedger> wrapper = new QueryWrapper<>();
        wrapper.select("distinct department");
        wrapper.isNotNull("department");
        wrapper.ne("department", "");
        List<MeasurementLedger> ledgers = ledgerMapper.selectList(wrapper);
        List<String> departments = new ArrayList<>();
        for (MeasurementLedger ledger : ledgers) {
            departments.add(ledger.getDepartment());
        }
        return departments;
    }

    private String getCellValue(Cell cell, String fieldName) {
        if (cell == null) return "";
        CellType cellType = cell.getCellType();
        switch (cellType) {
            case STRING:
                String cellValue = cell.getStringCellValue();
                // 处理自定义日期格式：yyyy年m月d日
                if (cellValue.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")) {
                    return cellValue.replace("年", "-").replace("月", "-").replace("日", "");
                }
                return cellValue;
            case NUMERIC:
                // 只针对应检日期和下次检定日期两个字段进行Excel日期格式转换
                if ((fieldName.equals("inspectionDate") || fieldName.equals("nextInspectionDate")) && 
                    (DateUtil.isCellDateFormatted(cell) || isExcelDate(cell.getNumericCellValue()))) {
                    // 日期格式转换为字符串年月日格式
                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                    return dateFormat.format(cell.getDateCellValue());
                } else {
                    // 数字类型转换为字符串，避免科学计数法
                    cell.setCellType(CellType.STRING);
                    return cell.getStringCellValue();
                }
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
            case FORMULA:
                try {
                    CellValue cellVal = evaluator.evaluate(cell);
                    if (cellVal.getCellType() == CellType.NUMERIC) {
                        // 只针对应检日期和下次检定日期两个字段进行Excel日期格式转换
                        if ((fieldName.equals("inspectionDate") || fieldName.equals("nextInspectionDate")) && 
                            (DateUtil.isCellDateFormatted(cell) || isExcelDate(cell.getNumericCellValue()))) {
                            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
                            return dateFormat.format(cell.getDateCellValue());
                        } else {
                            cell.setCellType(CellType.STRING);
                            return cell.getStringCellValue();
                        }
                    } else if (cellVal.getCellType() == CellType.STRING) {
                        String formulaValue = cellVal.getStringValue();
                        // 处理自定义日期格式：yyyy年m月d日
                        if (formulaValue.matches("\\d{4}年\\d{1,2}月\\d{1,2}日")) {
                            return formulaValue.replace("年", "-").replace("月", "-").replace("日", "");
                        }
                        return formulaValue;
                    } else {
                        return String.valueOf(cellVal);
                    }
                } catch (Exception e) {
                    return cell.getCellFormula();
                }
            default:
                return "";
        }
    }

}