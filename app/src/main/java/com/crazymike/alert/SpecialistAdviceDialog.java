package com.crazymike.alert;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.crazymike.R;
import com.crazymike.models.Cssets;
import com.crazymike.util.PreferencesKey;
import com.crazymike.util.PreferencesTool;

import org.joda.time.LocalTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

/**
 * Created by ChaoJen on 2017/3/16.
 */

public class SpecialistAdviceDialog extends AlertDialog implements View.OnClickListener {

    private static final String TAG = SpecialistAdviceDialog.class.getSimpleName();

    private Listener listener;
    private Context context;
    private String productId;
    private String csServiceStartTime;
    private String csServiceEndTime;
    private Cssets cssets;
    private boolean isTravelProduct;

    public SpecialistAdviceDialog(Context context, Listener listener, String productId, boolean isTravelProduct, String csServiceStartTime, String csServiceEndTime, Cssets cssets) {
        super(context);
        this.context = context;
        this.productId = productId;
        this.isTravelProduct = isTravelProduct;
        this.listener = listener;
        this.csServiceStartTime = csServiceStartTime != null ? csServiceStartTime : context.getResources().getString(R.string.default_cs_service_start_time);
        this.csServiceEndTime = csServiceEndTime != null ? csServiceEndTime : context.getResources().getString(R.string.default_cs_service_end_time);
        this.cssets = cssets;

        if (PreferencesTool.getInstance().get(PreferencesKey.IS_DEVEL, Boolean.class)) {
            String terminalServiceStartTime = PreferencesTool.getInstance().get(PreferencesKey.TERMINAL_SERVICE_TIME_START, String.class);
            String terminalServiceEndTime = PreferencesTool.getInstance().get(PreferencesKey.TERMINAL_SERVICE_TIME_END, String.class);
            this.csServiceStartTime = terminalServiceStartTime.length() != 0 ? terminalServiceStartTime : context.getResources().getString(R.string.default_cs_service_start_time);
            this.csServiceEndTime = terminalServiceEndTime.length() != 0 ? terminalServiceEndTime : context.getResources().getString(R.string.default_cs_service_end_time);
        }
        initView();
    }

    private void initView() {
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_specialist_advice, null);
        setView(view);

        TextView textViewPhoneNumber = (TextView) view.findViewById(R.id.textView_phoneNumber);
        TextView textViewNumberTwo = (TextView) view.findViewById(R.id.textView_numberTwo);
        TextView textViewProductId = (TextView) view.findViewById(R.id.textView_productId);
        LinearLayout linearLayoutCancel = (LinearLayout) view.findViewById(R.id.linearLayout_cancel);
        LinearLayout linearLayoutButtons = (LinearLayout) view.findViewById(R.id.linearLayout_buttons);
        TextView textViewMessageNotice = (TextView) view.findViewById(R.id.textView_messageNotice);
        Button buttonCallNow = (Button) view.findViewById(R.id.button_callNow);
        Button buttonMessageOnline = (Button) view.findViewById(R.id.button_messageOnline);

        DateTimeFormatter dateTimeFormatter = DateTimeFormat.forPattern("HH:mm:ss");
        LocalTime csServiceStartDateTime = LocalTime.parse(csServiceStartTime, dateTimeFormatter);
        LocalTime csServiceEndDateTime = LocalTime.parse(csServiceEndTime, dateTimeFormatter);

        boolean isWorkingTime = LocalTime.now().isAfter(csServiceStartDateTime) && LocalTime.now().isBefore(csServiceEndDateTime);
        boolean isCallNowShow = true;
        boolean isMessageShow = false;
        try {
            if (isWorkingTime) {
                if (isTravelProduct) {
                    isCallNowShow = cssets.getW().getIsTrHorderTel().equals("t");
                    isMessageShow = cssets.getW().getIsTrHorderContact().equals("t");
                } else {
                    isCallNowShow = cssets.getW().getIsCmHorderTel().equals("t");
                    isMessageShow = cssets.getW().getIsCmHorderContact().equals("t");
                }
            } else {
                if (isTravelProduct) {
                    isCallNowShow = cssets.getH().getIsTrHorderTel().equals("t");
                    isMessageShow = cssets.getH().getIsTrHorderContact().equals("t");
                } else {
                    isCallNowShow = cssets.getH().getIsCmHorderTel().equals("t");
                    isMessageShow = cssets.getH().getIsCmHorderContact().equals("t");
                }
            }
        } catch (Exception e) {
            Log.e(TAG, e.toString());
        }
        buttonCallNow.setVisibility(isCallNowShow ? View.VISIBLE : View.GONE);
        textViewMessageNotice.setVisibility(isMessageShow ? View.VISIBLE : View.GONE);
        buttonMessageOnline.setVisibility(isMessageShow ? View.VISIBLE : View.GONE);
        linearLayoutButtons.setVisibility(isCallNowShow || isMessageShow ? View.VISIBLE : View.GONE);
        textViewPhoneNumber.setText(isTravelProduct ? context.getResources().getString(R.string.specialist_advice_phone_number_travel) : context.getResources().getString(R.string.specialist_advice_phone_number));
        textViewNumberTwo.setVisibility(isTravelProduct ? View.GONE : View.VISIBLE);
        textViewProductId.setText(String.format(context.getResources().getString(R.string.specialist_advice_product_id), productId));
        linearLayoutCancel.setOnClickListener(this);
        buttonCallNow.setOnClickListener(this);
        buttonMessageOnline.setOnClickListener(this);
    }

    public interface Listener {
        void onSpecialistAdviceCallNowClick(String phoneNumber);
        void onSpecialistAdviceMessageBtnClick();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.linearLayout_cancel:
                dismiss();
                break;
            case R.id.button_callNow:
                listener.onSpecialistAdviceCallNowClick(isTravelProduct ? context.getResources().getString(R.string.specialist_advice_phone_number_travel) : context.getResources().getString(R.string.specialist_advice_phone_number));
                break;
            case R.id.button_messageOnline:
                listener.onSpecialistAdviceMessageBtnClick();
                break;
        }
    }
}