package com.eazywrite.app.ui.bill.adapter;

import static org.litepal.LitePalApplication.getContext;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.eazywrite.app.R;
import com.eazywrite.app.data.model.PageBean;
import com.eazywrite.app.ui.bill.AgentWebActivity;

import java.util.List;

public class PageTwoAdapter extends RecyclerView.Adapter<PageTwoAdapter.ViewHolder> {

    private List<PageBean.ResultDTO.NewslistDTO> mPageBeanList;

    private Context mContext;

    public PageTwoAdapter(List<PageBean.ResultDTO.NewslistDTO> mPageBeanList, Context mContext) {
        this.mPageBeanList = mPageBeanList;
        this.mContext = mContext;
    }

    @NonNull
    @Override
    public PageTwoAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.pagetwo_item,parent,false);
        return new PageTwoAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageTwoAdapter.ViewHolder holder, int position) {
        PageBean.ResultDTO.NewslistDTO pageBean = mPageBeanList.get(position);
        //写逻辑
//        holder.description.setText(pageBean.getDescription());
        holder.title.setText(pageBean.getTitle());

        holder.relativeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse(pageBean.getUrl());
                Intent intent = new Intent(mContext, AgentWebActivity.class);
                intent.putExtra("Uri",uri.toString());
                mContext.startActivity(intent);
            }
        });
        Glide.with(getContext()).load(pageBean.getPicUrl()).into(holder.imageView);
    }

    @Override
    public int getItemCount() {
        return mPageBeanList == null ? 0 : mPageBeanList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        TextView title;
//        TextView description;
        RelativeLayout relativeLayout;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            relativeLayout = itemView.findViewById(R.id.relativelayout_page2);
            imageView = itemView.findViewById(R.id.imageview_pagetwo);
            title = itemView.findViewById(R.id.textview_titletwo);
//            description = itemView.findViewById(R.id.textview_description);

        }
    }
}
