package com.kovizone.poi.ooxml.plus.command;

import com.kovizone.poi.ooxml.plus.ExcelHelper;
import com.kovizone.poi.ooxml.plus.style.ExcelStyleManager;
import com.kovizone.poi.ooxml.plus.util.StringUtils;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.ss.util.RegionUtil;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * 工作表基础命令
 *
 * @author KoviChen
 */
public class ExcelCommand {

    public ExcelCommand(Workbook workbook, int cellSize, Map<String, Object> vars, ExcelStyleManager excelStyleManager) {
        super();
        this.workbook = workbook;
        this.cellSize = cellSize;
        this.vars = vars;
        this.sheetIndex = 0;
        this.nextCellIndex = 0;
        this.nextRowIndex = 0;
        this.lateRenderCellWidth = new HashMap<>(16);
        this.lateRenderRowHeight = new HashMap<>(16);
        this.styleMap = excelStyleManager.styleMap(new ExcelStyleCommand(workbook));
    }

    /**
     * 工作表
     */
    protected Workbook workbook;
    /**
     * 样式管理
     */
    private Map<String, CellStyle> styleMap;
    /**
     * 工作簿
     */
    private Sheet sheet;
    /**
     * 行
     */
    private Row row;
    /**
     * 单元格
     */
    private Cell cell;
    /**
     * 默认最大宽度
     */
    private int cellSize;
    /**
     * 下一工作簿索引
     */
    private int sheetIndex;
    /**
     * 下一列索引
     */
    private int nextCellIndex;
    /**
     * 下一行索引
     */
    private int nextRowIndex;
    /**
     * 表头文本替换
     */
    private Map<String, Object> vars;

    /**
     * 获取当前单元格索引
     *
     * @return 当前单元格索引
     */
    public int currentCowIndex() {
        return nextCellIndex - 1;
    }

    /**
     * 获取当前行索引
     *
     * @return 当前行索引
     */
    public int currentRowIndex() {
        return nextRowIndex - 1;
    }

    /**
     * 获取当前工作簿索引
     *
     * @return 当前工作簿索引
     */
    public int currentSheetIndex() {
        return sheetIndex - 1;
    }

    /**
     * 创建工作簿
     */
    public void createSheet() {
        createSheet("Sheet#{sheetNum}");
    }

    /**
     * 创建工作簿
     *
     * @param sheetName 工作簿名
     */
    public void createSheet(String sheetName) {
        Sheet sheet;
        if (StringUtils.isEmpty(sheetName)) {
            if (!sheetName.contains(ExcelHelper.SHEET_NUM)) {
                sheetName = sheetName + ExcelHelper.SHEET_NUM;
            }
            sheet = workbook.createSheet(sheetName.replace(
                    ExcelHelper.SHEET_NUM,
                    String.valueOf((sheetIndex) + 1)));
        } else {
            sheet = workbook.createSheet();
        }
        this.sheet = sheet;

        // 重置列索引
        this.nextRowIndex = 0;
        // 重置行索引
        this.nextCellIndex = 0;
        if (sheetIndex > 1) {
            // 渲染之前的工作簿
            lateRender();
        }
        lateRenderFlag = false;
        sheetIndex++;
    }

    public void setSheetName(String sheetName) {
        if (!sheetName.contains(ExcelHelper.SHEET_NUM)) {
            sheetName = sheetName + ExcelHelper.SHEET_NUM;
        }
        workbook.setSheetName(currentSheetIndex(), sheetName.replace(
                ExcelHelper.SHEET_NUM,
                String.valueOf(currentSheetIndex() + 1)));
    }

    protected Sheet getSheet() {
        return sheet;
    }

    /**
     * 创建行
     *
     * @return 行
     */
    public Row createRow() {
        Row row = sheet.createRow(nextRowIndex++);
        this.row = row;
        // 重置行索引
        this.nextCellIndex = 0;
        if (defaultRowHeight != null) {
            setRowHeight(defaultRowHeight);
        }
        return row;
    }

    /**
     * 创建行<BR/>
     * 注入样式
     *
     * @return 行
     */
    public Row createRow(String styleName) {
        Row row = createRow();
        CellStyle cellStyle = styleMap.get(styleName);
        if (cellStyle != null) {
            row.setRowStyle(cellStyle);
        }
        return row;
    }

    public Row getRow() {
        return this.row;
    }

    /**
     * 创建单元格
     *
     * @return 单元格
     */
    public Cell createCell() {
        Cell cell = row.createCell(nextCellIndex++);
        this.cell = cell;
        if (defaultColumnWidth != null) {
            setCellWidth(defaultColumnWidth);
        }
        return cell;
    }

    /**
     * 创建单元格<BR/>
     * 注入样式
     *
     * @return 单元格
     */
    public Cell createCell(String styleName) {
        Cell cell = createCell();
        CellStyle cellStyle = styleMap.get(styleName);
        if (cellStyle != null) {
            cell.setCellStyle(cellStyle);
        }
        return cell;
    }

    public Cell getCell() {
        return this.cell;
    }

    private Integer defaultColumnWidth = null;

    private Short defaultRowHeight = null;

    /**
     * 设置列默认列宽<BR/>
     * 创建行后默认宽度会清除<BR/>
     *
     * @param width 宽度
     */
    public void setDefaultColumnWidth(int width) {
        defaultColumnWidth = width;
    }

    /**
     * 设置列默认行高<BR/>
     * 创建行后默认高度会清除<BR/>
     *
     * @param height 高度
     */
    public void setDefaultRowHeight(short height) {
        defaultRowHeight = height;
    }

    private boolean lateRenderFlag;
    private Map<Integer, Integer> lateRenderCellWidth;
    private Map<Integer, Short> lateRenderRowHeight;

    public void lateRender() {
        if (!lateRenderFlag) {
            lateRenderCellWidth.forEach(sheet::setColumnWidth);
            lateRenderCellWidth = new HashMap<>(16);

            lateRenderRowHeight.forEach((key, value) -> {
                sheet.getRow(key).setHeight(value);
            });
            lateRenderRowHeight = new HashMap<>(16);

            lateRenderFlag = true;
        }
    }

    public void setRowHeight(short height) {
        lateRenderRowHeight.put(currentRowIndex(), height);
    }

    /**
     * 设置当前列宽度<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     *
     * @param width 宽度
     */
    public void setCellWidth(int width) {
        lateRenderCellWidth.put(currentCowIndex(), width);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range() {
        range(null,
                null,
                null,
                null,
                null);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     *
     * @param cellRangeAddress 合并对象
     */
    public void range(CellRangeAddress cellRangeAddress) {
        range(cellRangeAddress,
                null,
                null,
                null,
                null);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range(BorderStyle border) {
        range(null,
                border,
                border,
                border,
                border);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range(BorderStyle borderTopAndBottom, BorderStyle borderLeftAndRight) {
        range(null,
                borderTopAndBottom,
                borderLeftAndRight,
                borderTopAndBottom,
                borderLeftAndRight);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range(BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        range(null,
                borderTop,
                borderRight,
                borderBottom,
                borderLeft);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range(CellRangeAddress region, BorderStyle border) {
        range(region,
                border,
                border,
                border,
                border);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range(CellRangeAddress region, BorderStyle borderTopAndBottom, BorderStyle borderLeftAndRight) {
        range(region,
                borderTopAndBottom,
                borderLeftAndRight,
                borderTopAndBottom,
                borderLeftAndRight);
    }

    /**
     * 合并若干行<BR/>
     * 在新建Sheet或执行指令时渲染<BR/>
     */
    public void range(CellRangeAddress region, BorderStyle borderTop, BorderStyle borderRight, BorderStyle borderBottom, BorderStyle borderLeft) {
        if (region == null) {
            region = new CellRangeAddress(currentRowIndex(), currentRowIndex(), 0, cellSize);
        }
        sheet.addMergedRegionUnsafe(region);
        if (borderTop != null) {
            RegionUtil.setBorderTop(borderTop, region, sheet);
        }
        if (borderRight != null) {
            RegionUtil.setBorderRight(borderRight, region, sheet);
        }
        if (borderBottom != null) {
            RegionUtil.setBorderBottom(borderBottom, region, sheet);
        }
        if (borderLeft != null) {
            RegionUtil.setBorderLeft(borderLeft, region, sheet);
        }
    }

    public String eval(String target) {
        if (vars == null) {
            return target;
        }
        Set<Map.Entry<String, Object>> entrySet = vars.entrySet();
        for (Map.Entry<String, Object> entry : entrySet) {
            target = target.replace(entry.getKey(), String.valueOf(entry.getValue()));
        }
        return target;
    }

    public int getCellSize() {
        return cellSize;
    }

    public void setCellValue(Object value) {
        if (value instanceof String) {
            cell.setCellValue((String) value);
        }
        if (value instanceof Boolean) {
            cell.setCellValue((Boolean) value);
        }
        if (value instanceof LocalDateTime) {
            cell.setCellValue((LocalDateTime) value);
        }
        if (value instanceof LocalDate) {
            cell.setCellValue((LocalDate) value);
        }
        if (value instanceof Date) {
            cell.setCellValue((Date) value);
        }
        if (value instanceof Calendar) {
            cell.setCellValue((Calendar) value);
        }
        if (value instanceof Byte) {
            cell.setCellValue((Byte) value);
        }
        if (value instanceof Short) {
            cell.setCellValue((Short) value);
        }
        if (value instanceof Integer) {
            cell.setCellValue((Integer) value);
        }
        if (value instanceof Long) {
            cell.setCellValue((Long) value);
        }
        if (value instanceof Float) {
            cell.setCellValue((Float) value);
        }
        if (value instanceof Double) {
            cell.setCellValue((Double) value);
        }
        if (value instanceof RichTextString) {
            cell.setCellValue((RichTextString) value);
        }
    }

    public CellStyle getCellStyle(String styleName) {
        return styleMap.get(styleName);
    }

    public void setCellStyle(String styleName) {
        this.cell.setCellStyle(styleMap.get(styleName));
    }

}