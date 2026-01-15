package com.ditto.report_browse.tex_component.tex_export;

import lombok.Getter;


@Getter
public class IndexSetCellData {
    private boolean downward;

    private int rowIndex;

    private int cellIndex;

    private String value;


    private IndexSetCellData(){}

    public IndexSetCellData(int rowIndex, int cellIndex, String value) {
        this.downward = false;
        this.rowIndex = rowIndex;
        this.cellIndex = cellIndex;
        this.value = value;
    }
    public IndexSetCellData(int rowIndex, int cellIndex, String value,boolean downward) {
        this.downward = downward;
        this.rowIndex = rowIndex;
        this.cellIndex = cellIndex;
        this.value = value;
    }
}
