package com.ditto.report_browse.tex_component.tex_exception;

public class TexException extends RuntimeException{
    private static final long serialVersionUID = -7864604160297181941L;

    /** 错误码 */
    protected final TexExceptionEnum exceptionEnum;



    /**
     * 指定错误码构造通用异常
     * @param exceptionEnum 错误码
     */
    public TexException(final TexExceptionEnum exceptionEnum) {
        super(exceptionEnum.getDescription());
        this.exceptionEnum = exceptionEnum;
    }


    /**
     * Getter method for property <tt>exceptionEnum</tt>.
     *
     * @return property value of exceptionEnum
     */
    public TexExceptionEnum getExceptionEnum() {
        return exceptionEnum;
    }


}
