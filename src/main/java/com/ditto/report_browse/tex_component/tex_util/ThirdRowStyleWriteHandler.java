package com.ditto.report_browse.tex_component.tex_util;


import com.alibaba.excel.metadata.Head;
import com.alibaba.excel.metadata.data.WriteCellData;
import com.alibaba.excel.write.handler.CellWriteHandler;
import com.alibaba.excel.write.handler.context.CellWriteHandlerContext;
import com.alibaba.excel.write.metadata.holder.WriteSheetHolder;
import com.alibaba.excel.write.metadata.holder.WriteTableHolder;
import com.alibaba.excel.write.style.column.AbstractColumnWidthStyleStrategy;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.xssf.usermodel.XSSFCell;

import java.util.ArrayList;
import java.util.List;

@Slf4j
/**
 * 样式复用拦截器：将第三行（行索引2）的样式应用到所有插入的单元格
 * 入参：thirdRowStyles - 第三行所有单元格的样式列表（按列索引顺序存储）
 */




public class ThirdRowStyleWriteHandler extends AbstractColumnWidthStyleStrategy {
    // 核心：存储第三行每列的样式，index=列索引，value=对应列CellStyle
    private   final List<CellStyle> THIRD_ROW_STYLES = new ArrayList<>();

    /**
     * 清空样式缓存，避免多线程/多次调用污染
     */
    private  void clear() {
        THIRD_ROW_STYLES.clear();
    }



    int relativeRowIndexRecord = -1;


    @Override
    protected void setColumnWidth(WriteSheetHolder writeSheetHolder, List<WriteCellData<?>> cellDataList, Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
   /*       if(relativeRowIndex==0){
            // 遍历第三行所有有效单元格，按列索引升序提取样式
            int lastCellNum = cell.getRow().getLastCellNum();
            for (int colIndex = 0; colIndex < lastCellNum; colIndex++) {
                CellStyle cellStyle = null;
                if (cell != null) {
                    // 提取POI原生CellStyle，POI 5.2.5 兼容，4.0.3 可直接复用
                    cellStyle = cell.getCellStyle();
                }
                ThirdRowStyleHolder.THIRD_ROW_STYLES.add(cellStyle);
            }
        }*/

    }

    @Override
    public void afterCellCreate(WriteSheetHolder writeSheetHolder, WriteTableHolder writeTableHolder,
                                Cell cell, Head head, Integer relativeRowIndex, Boolean isHead) {
  /*      int colIndex = cell.getColumnIndex(); // 当前单元格列索引
        List<CellStyle> thirdRowStyles = ThirdRowStyleHolder.THIRD_ROW_STYLES;

        // 列索引在样式范围内，应用第三行对应列的原生样式
        if (colIndex < thirdRowStyles.size()) {
            CellStyle targetStyle = thirdRowStyles.get(colIndex);
            if (targetStyle != null) {
                // POI 5.2.5 原生方法：直接为单元格设置样式，4.0.3 完美兼容
                cell.setCellStyle(targetStyle);
            }
        } else if (!thirdRowStyles.isEmpty()) {
            // 扩展：新列超出第三行列数时，复用最后一列样式（可选，可删除）
            CellStyle lastStyle = thirdRowStyles.get(thirdRowStyles.size() - 1);
            cell.setCellStyle(lastStyle);
        }*/
    }
}



