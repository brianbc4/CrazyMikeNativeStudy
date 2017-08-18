package com.crazymike.util;

import android.content.Context;
import android.widget.Toast;

import com.crazymike.R;

/**
 * Created by Elliot on 2017/5/22.
 */

public class ToastTool {

    private static ToastTool instance = new ToastTool();

    private Context mContext;

    public static ToastTool getInstance(Context context) {
        instance.mContext = context;
        return instance;
    }

    public void showDefault(String message, int length) {
        Toast toast = Toast.makeText(mContext, message, length);
        toast.getView().setBackgroundResource(R.color.colorPrimary);
        toast.getView().setPadding(20, 20, 20, 20);
        toast.show();
    }
}
