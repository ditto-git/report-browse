package com.ditto.report_browse.tex_component.tex_util;

import com.alibaba.excel.EasyExcel;
import com.alibaba.excel.context.AnalysisContext;
import com.alibaba.excel.event.AnalysisEventListener;
import com.alibaba.excel.metadata.data.ReadCellData;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

public class TexCellStyle {



    public static void insertDataWithSpecifiedStyleRow(InputStream ossIs,
                                                         Integer sheetNo,
                                                         Integer styleRowNum) throws Exception {
        // 校验必传参数
        if (ossIs == null) {
            throw new IllegalArgumentException("OSS Excel输入流不能为空");
        }
        if (styleRowNum == null || styleRowNum < 0) {
            throw new IllegalArgumentException("样式行行号必须为非负整数（行索引从0开始）");
        }

        int targetSheet = sheetNo == null ? 0 : sheetNo;

        try {
            // 1. 提取指定行的所有列样式（行号由参数动态控制）
            extractThirdRowStyles(ossIs, targetSheet, styleRowNum);

        } finally {
            // 无论是否异常，都清空样式缓存，避免内存泄漏/多线程污染
            ThirdRowStyleHolder.clear();
        }
    }


    /**
     * EasyExcel 4.0.3 适配：提取Excel第三行（行索引2）的所有列原生CellStyle
     * @param inputStream Excel模板输入流（OSS流/本地流均可）
     * @param sheetNo 要处理的Sheet索引
     */
    public static void extractThirdRowStyles(InputStream inputStream, int sheetNo,int styleRowNum) throws Exception {
        try (XSSFWorkbook workbook = new XSSFWorkbook(inputStream)) {
            // 获取目标Sheet，POI 5.2.5 原生读取，适配4.0.3
            XSSFSheet sheet = workbook.getSheetAt(sheetNo);
            if (sheet == null) {
                throw new RuntimeException("指定Sheet索引不存在：" + sheetNo);
            }

            // 获取第三行（行索引2，Excel界面第三行），POI行索引与EasyExcel一致
            XSSFRow thirdRow = sheet.getRow(styleRowNum);
            if (thirdRow == null) {
                throw new RuntimeException("Excel模板第三行不存在，无法提取样式");
            }

            // 遍历第三行所有有效单元格，按列索引升序提取样式
            int lastCellNum = thirdRow.getLastCellNum();
            for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
                XSSFCell cell = thirdRow.getCell(colIndex);
                CellStyle cellStyle = null;
                if (cell != null) {
                    // 提取POI原生CellStyle，POI 5.2.5 兼容，4.0.3 可直接复用
                    cellStyle = cell.getCellStyle();
                }
                ThirdRowStyleHolder.THIRD_ROW_STYLES.add(cellStyle);
            }

            // 校验样式提取结果
            if (ThirdRowStyleHolder.THIRD_ROW_STYLES.isEmpty()) {
                throw new RuntimeException("第三行无有效单元格，无法提取样式");
            }
            System.out.println("第三行样式提取成功，共提取 " + ThirdRowStyleHolder.THIRD_ROW_STYLES.size() + " 列样式");
        } catch (Exception e) {
            throw new RuntimeException("提取第三行样式失败：" + e.getMessage(), e);
        }
    }


    /**
     * EasyExcel 4.0.3 适配：从第三行（行索引2）插入数据，所有行应用第三行样式
     * @param outputStream 内存输出流（后续转OSS输入流）
     * @param newData 待插入的无实体类数据
     * @param sheetNo 要处理的Sheet索引
     */
    private static void writeDataToExcel(ByteArrayOutputStream outputStream, List<List<Object>> newData, int sheetNo) {
        // 初始化4.0.3 样式拦截器，传入第三行样式
        ThirdRowStyleWriteHandler styleHandler = new ThirdRowStyleWriteHandler();

        EasyExcel.write(outputStream)
                .sheet(sheetNo, "数据列表") // 指定Sheet索引和名称（名称可自定义）
                .relativeHeadRowIndex(2) // 核心：设置起始行索引=2，从第三行开始插入数据
                .registerWriteHandler(styleHandler) // 注册样式拦截器，全局复用第三行样式
                .doWrite(newData); // 写入无实体类数据，4.0.3 原生支持
    }

}
