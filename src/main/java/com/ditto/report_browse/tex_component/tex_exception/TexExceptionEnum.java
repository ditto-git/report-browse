package com.ditto.report_browse.tex_component.tex_exception;




public enum TexExceptionEnum {

    DEFAULT(7733200,"操作失败，请联系管理员"),


    TEMP_NO_CONTAIN(773320, "模板维护中......"),
    TEMP_IMPORT_ERROR(773321, "模板导入异常，请联系管理员"),
    TEMP_MATCH_ERROR(773322, "模板匹配失败，请验证文件名及内容"),
    TEMP_IO_ERROR(773323, "操作失败，请联系管理员"),


    TEMP_CODE_NULL(773324,"模板编码不能为空"),
    CELL_CODE_NULL(773325,"字段编码不能为空"),
    FILE_EXPORT_ERROR(230301, "文件导出失败,请联系管理员");




    private  final int code;
    private  final String  description;




    TexExceptionEnum(int code, String description) {
        this.code = code;
        this.description = description;
    }


    public int getCode() {
        return this.code;
    }

    public static int getCodeByDescription(String description) {
        for (TexExceptionEnum e: TexExceptionEnum.values()){
            if (e.description.equals(description)){
                return e.code;
            }
        }
        return DEFAULT.getCode();
    }

    public String getDescription() {
        return this.description;
    }

    public static String getDescriptionByCode(int code) {
        for (TexExceptionEnum e: TexExceptionEnum.values()){
            if (e.code == code){
                return e.description;
            }
        }
        return DEFAULT.getDescription();
    }
}