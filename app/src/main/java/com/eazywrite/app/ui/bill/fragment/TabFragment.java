package com.eazywrite.app.ui.bill.fragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.eazywrite.app.R;
import com.eazywrite.app.data.model.ChatBody;
import com.eazywrite.app.data.model.ChatBodyKt;
import com.eazywrite.app.data.model.ChatResponse;
import com.eazywrite.app.data.model.PageBean;
import com.eazywrite.app.data.repository.OpenaiRepository;
import com.eazywrite.app.ui.bill.adapter.PageAdapter;
import com.eazywrite.app.ui.bill.adapter.PageTwoAdapter;
import com.eazywrite.app.util.JsonKt;
import com.google.gson.Gson;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import kotlin.Unit;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;

public class TabFragment extends Fragment {


    private RecyclerView recyclerView;
    private PageBean pageBean;

    private Message message;

    private List<PageBean.ResultDTO.NewslistDTO> list;
    //感兴趣的关键词
    private String keyword = "北京";
    private String url = null;
    public static TabFragment newInstance(String label) {
        Bundle args = new Bundle();
        args.putString("label", label);
        TabFragment fragment = new TabFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_tab, container, false);


        recyclerView = view.findViewById(R.id.recyclerView_article);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);





        String label = getArguments().getString("label");

        message = new Message();
        if (label.equals("旅游资讯")){
            url = "https://apis.tianapi.com/travel/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }else if (label.equals("娱乐新闻")){
            url = "https://apis.tianapi.com/huabian/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 2;
        }else if (label.equals("社会新闻")){
            url = "https://apis.tianapi.com/social/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 2;
        }else if (label.equals("动漫资讯")){
            url = "https://apis.tianapi.com/dongman/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }else if (label.equals("互联网资讯")){
            url = "https://apis.tianapi.com/internet/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }else if (label.equals("健康知识")){
            url = "https://apis.tianapi.com/health/index?key=a91afee29bb010a296c554e1647ea058&num=50&rand=1";
            message.what = 1;
        }

        HttpLoggingInterceptor logging = new HttpLoggingInterceptor();
        logging.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient okHttpClient = new OkHttpClient.Builder().addInterceptor(logging).build();

        Request request = new Request.Builder()
                .url(url)
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {

            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String data = response.body().string();
                Gson gson = new Gson();
                pageBean = gson.fromJson(data,PageBean.class);
                handler.sendMessage(message);
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();

    }

        Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            if (message.what == 1){
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                PageAdapter pageAdapter = new PageAdapter(pageBean.getResult().getNewslist(), getContext());
                recyclerView.setAdapter(pageAdapter);
            }else if (message.what == 2){
                recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                PageTwoAdapter pagetwoAdapter = new PageTwoAdapter(pageBean.getResult().getNewslist(), getContext());
                recyclerView.setAdapter(pagetwoAdapter);
            }
            return false;
        }
    });
}

