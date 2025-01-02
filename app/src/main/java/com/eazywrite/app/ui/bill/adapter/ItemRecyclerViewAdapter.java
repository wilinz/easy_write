package com.eazywrite.app.ui.bill.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.eazywrite.app.R;
import com.eazywrite.app.ui.bill.fragment.OutputBean;

import java.util.List;

public class ItemRecyclerViewAdapter extends RecyclerView.Adapter<ItemRecyclerViewAdapter.ViewHolder> {
    private List<OutputBean> mList ;
    private Context  mContext;

    public ItemRecyclerViewAdapter(List<OutputBean> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.item_layout_restall,parent,false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        OutputBean outputBean = mList.get(position);
//        holder.icon.setImageResource(outputBean.getImageId());
//        holder.name.setText(outputBean.getName());
//        holder.beiZhu.setText(outputBean.getBeiZhu());
//        holder.money.setText(outputBean.getMoneyCount());

        if (position == (mList.size()-1)){
            holder.line.setBackgroundColor(Color.WHITE);
        }
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView icon;
        TextView beiZhu;
        TextView money;
        TextView name;

        ImageView line;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            beiZhu = itemView.findViewById(R.id.beiZhu);
            money = itemView.findViewById(R.id.money);
            name = itemView.findViewById(R.id.name);
            line = itemView.findViewById(R.id.line_background);
        }
    }
}
