package com.eazywrite.app.ui.bill.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;
import java.util.List;


public class InputViewModel extends AndroidViewModel {
    public InputViewModel(@NonNull Application application) {
        super(application);
    }

    public MutableLiveData<List<OutputBean>> getBean() {
        return bean;
    }

    public void setBean(List<OutputBean> mList) {
        bean.setValue(mList);
    }

    private MutableLiveData<List<OutputBean>> bean = new MutableLiveData<>();

}