package com.linxcool.andbase.retrofit;

/**
 * 适用于响应代码为 json 且格式如下<pre> {
 *     code: 0
 *     data: {}
 *     msg:"this is a message!"
 * }
 * </pre>
 * Created by huchanghai on 2017/3/10.
 */
public class Reply<T> {

    private static final int CODE_NULL = -123456789;
    private static final int CODE_SUC = 0;

    // 状态码
    private int code = CODE_NULL;
    private int status = CODE_NULL;
    private int retCode = CODE_NULL;

    // 数据内容
    private T data;
    private T result;

    // 扩展数据
    private T ext;

    // 错误信息
    private String msg;
    private String desc;

    public static <T> Reply<T> success(T body) {
        Reply<T> reply = new Reply<T>();
        reply.setCode(CODE_SUC);
        reply.setData(body);
        return reply;
    }

    public boolean isOk() {
        return getCode() == CODE_SUC;
    }

    public int getCode() {
        if (code != CODE_NULL) return code;
        if (status != CODE_NULL) return status;
        if (retCode != CODE_NULL) return retCode;
        return CODE_SUC;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getErrMsg() {
        if (msg != null) return msg;
        if (desc != null) return desc;
        return null;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        if(data != null) return data;
        if(result != null) return result;
        return null;
    }

    public void setData(T data) {
        this.data = data;
    }

    public T getExt() {
        return ext;
    }

    public void setExt(T ext) {
        this.ext = ext;
    }
}