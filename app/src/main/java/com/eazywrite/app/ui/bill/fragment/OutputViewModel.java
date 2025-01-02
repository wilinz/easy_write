package com.eazywrite.app.ui.bill.fragment;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.MutableLiveData;

import java.util.ArrayList;

public class OutputViewModel extends AndroidViewModel {

    public MutableLiveData<ArrayList<OutputBean>> outputBean = new MutableLiveData<>();
    public MutableLiveData<ArrayList<OutputBean>> outputBeanColored = new MutableLiveData<>();
    public MutableLiveData<ArrayList<OutputBean>> inputBean = new MutableLiveData<>();
    public MutableLiveData<ArrayList<OutputBean>> inputBeanColored = new MutableLiveData<>();

    public OutputViewModel(@NonNull Application application) {
        super(application);
    }



}
