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
import com.eazywrite.app.databinding.FragmentBillTwoBinding;
import com.eazywrite.app.ui.bill.adapter.RecycleViewAdapter;

import java.util.ArrayList;


public class FragmentTwo extends Fragment {
    AddItemFragment mAddItemFragment;

    public FragmentTwo(AddItemFragment addItemFragment) {
        this.mAddItemFragment = addItemFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_bill_two, container,false);
        return mBinding.getRoot();
    }
    FragmentBillTwoBinding mBinding;

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
        initRecyclerView();
    }
    RecycleViewAdapter adapter;
    private void initRecyclerView() {
        GridLayoutManager layoutManager =  new GridLayoutManager(getActivity(),4);
        mBinding.recycleViewTwo.setLayoutManager(layoutManager);
        adapter = new RecycleViewAdapter(viewModel.inputBean.getValue(), getContext(),
                viewModel.inputBeanColored.getValue(), layoutManager,getActivity().getSupportFragmentManager(),
                mAddItemFragment, this);
        mBinding.recycleViewTwo.setAdapter(adapter);
    }

    OutputViewModel viewModel;
    private void initData() {
        viewModel = new ViewModelProvider(this).get(OutputViewModel.class);
        init();
        viewModel.inputBean.setValue(beans);
        viewModel.inputBeanColored.setValue(beansColored);
    }


    ArrayList<OutputBean> beans = new ArrayList<>();
    ArrayList<OutputBean> beansColored = new ArrayList<>();
    private void init() {
        beans.add(setResource("gongzi","工资"));
        beans.add(setResource("jianzhi","兼职"));
        beans.add(setResource("licai","理财"));
        beans.add(setResource("lijin","礼金"));
        beans.add(setResource("zhuanzhang","转账"));

        beansColored.add(setResource("gongzi1","工资"));
        beansColored.add(setResource("jianzhi1","兼职"));
        beansColored.add(setResource("licai1","理财"));
        beansColored.add(setResource("lijin1","礼金"));
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