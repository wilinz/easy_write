package com.eazywrite.app.ui.bill.fragment;

import androidx.lifecycle.MutableLiveData;

import java.time.LocalDateTime;

public class OutputBean {


    //图片资源id
    private int imageId;
    //账目类别
    private String category;
    //金额
    private StringBuilder moneyCount;
    //备注
    private StringBuilder beiZhu;
    //账目名称
    private String name;
    //false为支出
    private String inOrOut;
    //#月#日格式
    private String dayMonth;
    //#年格式
    private String year;
    //时间
    private LocalDateTime time;

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

    @Override
    public String toString() {
        return "OutputBean{" +
                "imageId=" + imageId +
                ", category='" + category + '\'' +
                ", moneyCount=" + moneyCount +
                ", beiZhu=" + beiZhu +
                ", name='" + name + '\'' +
                ", inOrOut=" + inOrOut +
                ", dayMonth='" + dayMonth + '\'' +
                ", year='" + year + '\'' +
                ", time=" + time +
                ", date=" + date +
                '}';
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getInOrOut() {
        return inOrOut;
    }

    public void setInOrOut(String inOrOut) {
        this.inOrOut = inOrOut;
    }

    public String getDayMonth() {
        return dayMonth;
    }

    public void setDayMonth(String dayMonth) {
        this.dayMonth = dayMonth;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    //#月#日格式（旧Recyclerview里的内容，我没改）
    private StringBuilder date;


    public int getImageId() {
        return imageId;
    }

    public void setImageId(int imageId) {
        this.imageId = imageId;
    }

    public String getName() {
        if(name==null)name="账目名称";
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public StringBuilder getBeiZhu() {
        return beiZhu;
    }

    public void setBeiZhu(StringBuilder beiZhu) {
        this.beiZhu = beiZhu;
    }

    public StringBuilder getDate() {
        return date;
    }

    public void setDate(StringBuilder date) {
        this.date = date;
    }

    public StringBuilder getMoneyCount() {
        return moneyCount;
    }

    public void setMoneyCount(StringBuilder moneyCount) {
        this.moneyCount = moneyCount;
    }


}
