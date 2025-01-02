package com.eazywrite.app.data.model;

import android.util.Log;

public class RegisterResponse {


    /**
     * code
     */
    private Integer code;
    /**
     * msg
     */
    private String msg;
    /**
     * data
     */
    private Object data;

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public void getAll() {
        Log.d("HelloWorld", "data:"+data+" msg:"+msg+" code:"+code);
    }
}
