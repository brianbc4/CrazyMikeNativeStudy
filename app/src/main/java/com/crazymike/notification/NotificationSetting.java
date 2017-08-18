package com.crazymike.notification;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.crazymike.R;
import com.crazymike.util.PreferencesKey;
import com.crazymike.util.PreferencesTool;
import com.crazymike.util.ToastTool;

public class NotificationSetting extends AppCompatActivity implements CompoundButton.OnCheckedChangeListener{

    public static void startActivity(Context context) {
        Intent intent = new Intent(context, NotificationSetting.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_setting);

        TextView textViewNotice = (TextView) findViewById(R.id.textViewNotice);
        LinearLayout linearLayoutBack = (LinearLayout) findViewById(R.id.linearLayoutBack);
        CheckBox checkBox = (CheckBox) findViewById(R.id.checkBox);

        textViewNotice.setText(R.string.notification_settings_notice);
        linearLayoutBack.setOnClickListener(v -> finish());
        checkBox.setChecked(!PreferencesTool.getInstance().get(PreferencesKey.IS_NOTIFICATION_CLOSE, Boolean.class));
        checkBox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        PreferencesTool.getInstance().put(PreferencesKey.IS_NOTIFICATION_CLOSE, !PreferencesTool.getInstance().get(PreferencesKey.IS_NOTIFICATION_CLOSE, Boolean.class));
        ToastTool.getInstance(this).showDefault(getResources().getString(isChecked ? R.string.activate_notification : R.string.deactivate_notification), Toast.LENGTH_SHORT);
    }
}
