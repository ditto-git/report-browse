package com.ditto.report_browse.tex_component.tex_util;

import org.apache.poi.ss.usermodel.CellStyle;

import java.util.ArrayList;
import java.util.List;

/**
 * EasyExcel 4.0.3 适配：第三行样式全局容器
 * 存储POI原生CellStyle（POI 5.2.5兼容），按列索引升序排列
 */
public class ThirdRowStyleHolder {
    // 核心：存储第三行每列的样式，index=列索引，value=对应列CellStyle
    public static final List<CellStyle> THIRD_ROW_STYLES = new ArrayList<>();

    /**
     * 清空样式缓存，避免多线程/多次调用污染
     */
    public static void clear() {
        THIRD_ROW_STYLES.clear();
    }
}