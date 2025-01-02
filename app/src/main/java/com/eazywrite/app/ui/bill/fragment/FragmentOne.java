package com.eazywrite.app.ui.bill.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;

import com.eazywrite.app.R;
import com.eazywrite.app.databinding.FragmentBillOneBinding;
import com.eazywrite.app.ui.bill.AddBillContentActivity;
import com.eazywrite.app.ui.bill.adapter.RecycleViewAdapter;

import java.util.ArrayList;

public class FragmentOne extends Fragment {
    AddItemFragment mAddItemFragment;
    public FragmentOne(AddItemFragment addItemFragment) {
        this.mAddItemFragment = addItemFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bill_one,container,false);

        return  mBinding.getRoot();
    }

    FragmentBillOneBinding mBinding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initRecycleView();
    }

    RecycleViewAdapter adapter;
    private void initRecycleView() {
        GridLayoutManager layoutManager =  new GridLayoutManager(getActivity(),4);
        mBinding.recycleView.setLayoutManager(layoutManager);
        adapter = new RecycleViewAdapter(viewModel.outputBean.getValue(),getContext(),
                viewModel.outputBeanColored.getValue(),layoutManager,getActivity().getSupportFragmentManager()
        ,mAddItemFragment,this);
        mBinding.recycleView.setAdapter(adapter);
    }
    OutputViewModel viewModel;
    private void initData() {
        viewModel = new ViewModelProvider(this).get(OutputViewModel.class);
        init();
        viewModel.outputBean.setValue(beans);
        viewModel.outputBeanColored.setValue(beansColored);
    }

    ArrayList<OutputBean> beans = new ArrayList<>();
    ArrayList<OutputBean> beansColored = new ArrayList<>();

    private void init(){
        beans.add(setResource("baoxian1","保险"));
        beans.add(setResource("canying1","餐饮"));
        beans.add(setResource("fushi1","服饰"));
        beans.add(setResource("fuwu1","服务"));
        beans.add(setResource("gongyi1","公益"));
        beans.add(setResource("gouwu1","购物"));
        beans.add(setResource("jiaotong1","交通"));
        beans.add(setResource("jiaoyu","教育"));
        beans.add(setResource("lvxing1","旅行"));
        beans.add(setResource("yiliao1","医疗"));
        beans.add(setResource("yule1","娱乐"));
        beans.add(setResource("yundong1","运动"));
        beans.add(setResource("zhuanzhang","转账"));


        beansColored.add(setResource("baoxian","保险"));
        beansColored.add(setResource("canying","餐饮"));
        beansColored.add(setResource("fushi","服饰"));
        beansColored.add(setResource("fuwu","服务"));
        beansColored.add(setResource("gongyi","公益"));
        beansColored.add(setResource("gouwu","购物"));
        beansColored.add(setResource("jiaotong","交通"));
        beansColored.add(setResource("jiaoyu1","教育"));
        beansColored.add(setResource("lvxing","旅行"));
        beansColored.add(setResource("yiliao","医疗"));
        beansColored.add(setResource("yule","娱乐"));
        beansColored.add(setResource("yundong","运动"));
        beansColored.add(setResource("zhuanzhang1","转账"));


    }





    

    @Override
    public void onPause() {
        super.onPause();
        adapter.resume();
    }

    public OutputBean setResource(String id, String name){
        OutputBean bean = new OutputBean();
        bean.setImageId(getResources().getIdentifier(id,"drawable",getActivity().getPackageName()));
        bean.setName(name);
        return bean;
    }


}