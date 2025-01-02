package com.eazywrite.app.ui.bill.fragment;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.eazywrite.app.R;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

public class DataViewModel extends ViewModel {
    private MutableLiveData<LocalDateTime> mData;

    public MutableLiveData<LocalDateTime> getData() {
        if (mData==null){
            mData = new MutableLiveData<>();

            LocalDateTime localDateTime = LocalDateTime.ofInstant(
                    Instant.ofEpochMilli(
                            System.currentTimeMillis()
                    ), ZoneId.systemDefault()
            );

            mData.setValue(localDateTime);
        }
        return mData;
    }


    // 图片的资源id
    private MutableLiveData<Integer> imageId;
    //账目类别
    private MutableLiveData<String> category;
    //输入金额
    private MutableLiveData<String> count;
    //备注
    private MutableLiveData<String> note;
    //账目名称
    private MutableLiveData<String> titleName;
    //false是输入
    private MutableLiveData<String> inOrOut;
    //#月#日格式
    private MutableLiveData<String> dayMonth;
    //#年格式
    private MutableLiveData<String> year;

    public MutableLiveData<String> getTitleName() {
        if(titleName==null)titleName = new MutableLiveData<>();
        return titleName;
    }



    public void clear() {
        count = null;
        category = null;
        imageId = null;
        note = null;
        inOrOut = null;
        dayMonth = null;
        year = null;
        mData = null;
    }

    public MutableLiveData<String> getDayMonth() {
        if(dayMonth==null)dayMonth = new MutableLiveData<>();
        return dayMonth;
    }

    public MutableLiveData<String> getYear() {
        if(year==null)year = new MutableLiveData<>();
        return year;
    }

    public MutableLiveData<String> getInOrOut() {
        if(inOrOut==null) inOrOut = new MutableLiveData<>();
        return inOrOut;
    }


    public MutableLiveData<Integer> getImageId() {
        if(imageId==null){
            imageId = new MutableLiveData<>(R.drawable.baoxian);
        }
        return imageId;
    }

    public MutableLiveData<String> getNote() {
        if(note==null){
            note = new MutableLiveData<>();
            note.setValue("");
        }
        return note;
    }

    public MutableLiveData<String> getCategory() {
        if(category==null) category = new MutableLiveData<>();
        return category;
    }

    public MutableLiveData<String> getCount() {
        if(count==null){
            count = new MutableLiveData<>();
            count.setValue("0");
        }
        return count;
    }


}
