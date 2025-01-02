package com.eazywrite.app.ui.bill;

import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.eazywrite.app.R;
import com.just.agentweb.AgentWeb;


public class AgentWebActivity extends AppCompatActivity {

    private AgentWeb agentWeb;

    private LinearLayout linWeb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_agent_web);
        //活动管理器
        ActivityCollector.addActivity(this);

        Intent  intent = getIntent();
        linWeb= (LinearLayout) findViewById(R.id.agentweb);
        agentWeb=AgentWeb.with(this)
                .setAgentWebParent(linWeb, new LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator()//进度条
                .createAgentWeb()
                .ready()
                .go(intent.getStringExtra("Uri"));
    }



    @Override

    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if (agentWeb.handleKeyEvent(keyCode, event)) {

            return true;

        }

        return super.onKeyDown(keyCode, event);

    }

    @Override

    protected void onPause() {

        agentWeb.getWebLifeCycle().onPause();

        super.onPause();

    }

    @Override

    protected void onResume() {

        agentWeb.getWebLifeCycle().onResume();

        super.onResume();

    }

    @Override

    protected void onDestroy() {

        agentWeb.getWebLifeCycle().onDestroy();

        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }

}