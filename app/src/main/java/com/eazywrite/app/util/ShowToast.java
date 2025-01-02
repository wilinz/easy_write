package com.eazywrite.app.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ShowToast {
    public static void showToast(Context context, String text) {
        Toast toast=Toast.makeText(context,text,Toast.LENGTH_LONG);
        toast.show();
    }
}
