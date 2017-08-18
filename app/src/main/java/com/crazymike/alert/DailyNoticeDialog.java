package com.crazymike.alert;


import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.crazymike.R;
import com.crazymike.models.DailyNotice;
import com.crazymike.web.WebViewActivity;

/**
 * Created by user1 on 2017/3/16.
 */

public class DailyNoticeDialog extends AlertDialog implements View.OnClickListener{

    private Context mContext;
    private DailyNotice mDailyNotice;

    public DailyNoticeDialog(Context context, DailyNotice dailyNotice) {
        super(context);
        mContext = context;
        this.mDailyNotice = dailyNotice;
        initView();
    }

    private void initView(){
        View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_daily_notice, null);
        setView(view);

        ImageView imageViewClose = (ImageView) view.findViewById(R.id.imageView_close);
        TextView textViewTitle = (TextView) view.findViewById(R.id.textView_title);
        TextView textViewContent = (TextView) view.findViewById(R.id.textView_content);
        Button buttonConfirm = (Button) view.findViewById(R.id.button_confirm);

        textViewTitle.setText(mDailyNotice.getTitle());
        textViewContent.setText(mDailyNotice.getContent());
        buttonConfirm.setText(mContext.getResources().getString(mDailyNotice.getIs_url().equals("t") ? R.string.go_now : R.string.ok));
        imageViewClose.setOnClickListener(this);
        buttonConfirm.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch(v.getId()) {
            case R.id.button_confirm:
                if(mDailyNotice.getIs_url().equals("t")){
                    WebViewActivity.startActivity(mContext,mDailyNotice.getUrl());
                }
                dismiss();
                break;
            case R.id.imageView_close:
                dismiss();
                break;
        }
    }
}