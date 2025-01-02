package com.eazywrite.app.ui.bill.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.eazywrite.app.R;
import com.eazywrite.app.databinding.MyDataTableBinding;

import java.util.List;

public class DateAdapter extends RecyclerView.Adapter<DateAdapter.ViewHolder>{
    List<String> xingQi;
    List<String> riQi;
    private int mSelectedPosition = 3;
    private FragmentActivity mActivity;

    public DateAdapter(List<String> xingQi, List<String> riQi, FragmentActivity activity) {
        this.xingQi = xingQi;
        this.riQi = riQi;
        mActivity = activity;

    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        MyDataTableBinding myDataTableBinding = DataBindingUtil.inflate(LayoutInflater.from(viewGroup.getContext()), R.layout.my_data_table,viewGroup,false);
        ViewHolder viewHolder=new ViewHolder(myDataTableBinding);

        myDataTableBinding.view3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int position = viewHolder.getLayoutPosition();
                mSelectedPosition = position;
                notifyDataSetChanged();
            }
        });
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.mBinding.riQi.setText(riQi.get(i));
        viewHolder.mBinding.xingQi.setText(xingQi.get(i));
        viewHolder.mBinding.riQi.setSelected(mSelectedPosition == i);

    }

    @Override
    public int getItemCount() {
        return xingQi.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder{

        MyDataTableBinding mBinding;
        public ViewHolder(@NonNull MyDataTableBinding itemView) {
            super(itemView.getRoot());
            mBinding = itemView;
        }
    }
}
