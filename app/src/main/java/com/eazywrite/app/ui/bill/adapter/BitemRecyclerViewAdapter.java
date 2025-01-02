package com.eazywrite.app.ui.bill.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eazywrite.app.R;
import com.eazywrite.app.data.model.WeekBillBean;

import java.util.List;

public class BitemRecyclerViewAdapter extends RecyclerView.Adapter<BitemRecyclerViewAdapter.ViewHolder> {

    private List<WeekBillBean> mWeekBillBean;

    private Context mContext;

    public BitemRecyclerViewAdapter(List<WeekBillBean> mWeekBillBean, Context mContext) {
        this.mWeekBillBean = mWeekBillBean;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public BitemRecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layouts,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BitemRecyclerViewAdapter.ViewHolder holder, int position) {
        WeekBillBean weekBillBean = mWeekBillBean.get(position);
//        holder.date.setText(weekBillBean.getWeekDate());
//        ItemRecyclerViewAdapter itemRecyclerViewAdapter = new ItemRecyclerViewAdapter(weekBillBean.getWeekBillBeanList(),mContext);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(mContext);
//        holder.recyclerView.setLayoutManager(linearLayoutManager);
//        holder.recyclerView.setAdapter(itemRecyclerViewAdapter);
    }

    @Override
    public int getItemCount() {
        return mWeekBillBean.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView date;
        RecyclerView recyclerView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            date = itemView.findViewById(R.id.textview_itemsdate);
            recyclerView = itemView.findViewById(R.id.recyclerView_items);
        }
    }
}
