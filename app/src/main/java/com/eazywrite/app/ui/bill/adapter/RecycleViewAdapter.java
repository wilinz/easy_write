package com.eazywrite.app.ui.bill.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eazywrite.app.R;
import com.eazywrite.app.ui.bill.fragment.AddItemFragment;
import com.eazywrite.app.ui.bill.fragment.FragmentOne;
import com.eazywrite.app.ui.bill.fragment.FragmentTwo;
import com.eazywrite.app.ui.bill.fragment.OutputBean;
import com.eazywrite.app.ui.bill.fragment.MyDialogFragment;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {
    private ArrayList<OutputBean> beans;
    private ArrayList<OutputBean> coloredBeans;
    private Context mContext;
    private GridLayoutManager layoutManager;
    private FragmentManager fragmentManager;
    private AddItemFragment addItemFragment;
    private FragmentTwo mFragmentTwo;
    private FragmentOne mFragmentOne;

    public RecycleViewAdapter(ArrayList<OutputBean> beans, Context context, ArrayList<OutputBean> coloredBeans
            , GridLayoutManager layoutManager, FragmentManager fragmentManager
            , AddItemFragment addItemFragment, FragmentOne fragment) {
        super();
        this.beans = beans;
        this.addItemFragment = addItemFragment;
        this.mContext = context;
        this.coloredBeans = coloredBeans;
        this.layoutManager = layoutManager;
        this.fragmentManager = fragmentManager;
        this.mFragmentOne = fragment;
        addItemFragment.getInOrOut("out");
    }

    public RecycleViewAdapter(ArrayList<OutputBean> beans, Context context, ArrayList<OutputBean> coloredBeans
            , GridLayoutManager layoutManager, FragmentManager fragmentManager
            , AddItemFragment addItemFragment, FragmentTwo fragment) {
        super();
        this.beans = beans;
        this.addItemFragment = addItemFragment;
        this.mContext = context;
        this.coloredBeans = coloredBeans;
        this.layoutManager = layoutManager;
        this.fragmentManager = fragmentManager;
        this.mFragmentTwo = fragment;

    }
    boolean isClick = false;
    int prePosition;

    public void resume() {
        ImageView icon = (ImageView) layoutManager.findViewByPosition(prePosition).findViewById(R.id.icon);
        icon.setImageResource(beans.get(prePosition).getImageId());
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ViewHolder view = new ViewHolder(LayoutInflater.from(mContext).inflate(R.layout.item, parent, false));
        view.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view1) {
                if(!isClick){
                    int position  = view.getAdapterPosition();
                    view.icon.setImageResource(coloredBeans.get(position).getImageId());
                    prePosition = position;
                    isClick = true;
                }else{
                    int position  = view.getAdapterPosition();
                    ImageView icon = (ImageView) layoutManager.findViewByPosition(prePosition).findViewById(R.id.icon);
                    icon.setImageResource(beans.get(prePosition).getImageId());
                    view.icon.setImageResource(coloredBeans.get(position).getImageId());
                    prePosition = position;
                }
                addItemFragment.getImage(coloredBeans.get(prePosition).getImageId());
                addItemFragment.getName(beans.get(prePosition).getName());
                showDialog(addItemFragment);

            }
        });
        return view;
    }
    RecycleViewAdapter mAdapter;

    private void showDialog(AddItemFragment addItemFragment){
        BottomSheetDialogFragment dialogFragment = new MyDialogFragment(addItemFragment);
        dialogFragment.show(fragmentManager,"DialogFragment");

        if(mFragmentOne==null){
            addItemFragment.getInOrOut("in");
            return;
        } else if (mFragmentTwo == null) {
            addItemFragment.getInOrOut("out");
            return;
        }
    }


    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.icon.setImageResource(beans.get(position).getImageId());
        holder.name.setText(beans.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return beans.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        private ImageView icon;
        private TextView name;
        private View mView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            name = itemView.findViewById(R.id.name);
            mView = itemView;
        }
    }
}
