package com.eazywrite.app.ui.profile.feedback;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class SendViewModel extends ViewModel {
    private MutableLiveData<String> label;
    private MutableLiveData<String> feedback;
    private MutableLiveData<Integer[]> picture;
    private MutableLiveData<String> number;

    public MutableLiveData<String> getLabel() {
        if(label == null) label = new MutableLiveData<>("");
        return label;
    }

    public MutableLiveData<String> getFeedback() {
        if(feedback == null) feedback = new MutableLiveData<>("");
        return feedback;
    }


    public MutableLiveData<Integer[]> getPicture() {
        if(picture == null) picture = new MutableLiveData<>(new Integer[]{0,0,0});
        return picture;
    }


    public MutableLiveData<String> getNumber() {
        if(number == null) number = new MutableLiveData<>("");
        return number;
    }

}
