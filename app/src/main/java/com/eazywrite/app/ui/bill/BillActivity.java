package com.eazywrite.app.ui.bill;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Window;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.WindowCompat;

import com.eazywrite.app.R;
import com.eazywrite.app.util.ActivityKt;

public class BillActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        ActivityKt.setWindow(this);

    }
}