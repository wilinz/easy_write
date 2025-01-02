package com.eazywrite.app.data.model;

import org.litepal.crud.LitePalSupport;

public class BillBean extends LitePalSupport {

    //账户账号
    private String account;
    //类型图标id
    private int imageId;
    //类别
    private String name;
    //备注
    private String beiZhu;
    //金额
    private String moneyCount;
    //日期
    private String date;
    //类别名称（就比如交通类,地铁，公交车等）
    //判断是支出还是收入（boolean）



    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBeiZhu() {
        return beiZhu;
    }

    public void setBeiZhu(String beiZhu) {
        this.beiZhu = beiZhu;
    }

    public String getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(String moneyCount) {
        this.moneyCount = moneyCount;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}
