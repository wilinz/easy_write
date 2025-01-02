package com.eazywrite.app.ui.bill.adapter;

import com.eazywrite.app.data.model.WeekBillBean;
import com.eazywrite.app.ui.bill.fragment.InputViewModel;

import java.util.List;

public interface CallbackData {

    void addData(List<WeekBillBean> weekBillBeanList);
}
