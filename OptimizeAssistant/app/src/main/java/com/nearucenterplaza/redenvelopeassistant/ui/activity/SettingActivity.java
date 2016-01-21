package com.nearucenterplaza.redenvelopeassistant.ui.activity;

import com.nearucenterplaza.redenvelopeassistant.R;
import com.nearucenterplaza.redenvelopeassistant.service.core.SettingHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class SettingActivity extends ActionBarActivity {
    RadioGroup mModeRg;
    RadioGroup mLanguageRg;
    RadioButton mAutoModeRb;
    RadioButton mSafeModeRb;
    RadioButton mChatPageOnlyModeRb;

    public static void actionTo(Context context) {
        Intent intent = new Intent(context, SettingActivity.class);
        context.startActivity(intent);
    }

    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_settings);
        initView();
    }

    void initView() {
        // find views
        mModeRg = (RadioGroup) findViewById(R.id.settings_re_mode_rg);
        mLanguageRg = (RadioGroup) findViewById(R.id.settings_language_rg);
        mAutoModeRb = (RadioButton) findViewById(R.id.settings_re_auto_mode_rb);
        mSafeModeRb = (RadioButton) findViewById(R.id.settings_re_safe_mode_rb);
        mChatPageOnlyModeRb = (RadioButton) findViewById(R.id.settings_re_chat_page_only_mode_rb);

        // set listeners
        mModeRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.settings_re_auto_mode_rb:
                        SettingHelper.setREAutoMode(true);
                        SettingHelper.setRESafeMode(false);
                        SettingHelper.setREChatOnlyMode(false);
                        break;
                    case R.id.settings_re_safe_mode_rb:
                        SettingHelper.setREAutoMode(false);
                        SettingHelper.setRESafeMode(true);
                        SettingHelper.setREChatOnlyMode(false);
                        break;
                    case R.id.settings_re_chat_page_only_mode_rb:
                        SettingHelper.setREAutoMode(false);
                        SettingHelper.setRESafeMode(false);
                        SettingHelper.setREChatOnlyMode(true);
                        break;
                    default:
                        break;
                }
            }
        });

        mLanguageRg.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.settings_language_simple_chinese_rb:
                        break;
                    case R.id.settings_language_traditional_chinese_rb:
                        break;
                    case R.id.settings_language_english_rb:
                        break;
                    default:
                        break;
                }
            }
        });

        // set values
        setTitle(getString(R.string.action_settings));

        if (SettingHelper.getREAutoMode()) {
            mAutoModeRb.setChecked(SettingHelper.getREAutoMode());
        } else if (SettingHelper.getRESafeMode()) {
            mSafeModeRb.setChecked(SettingHelper.getRESafeMode());
        } else if (SettingHelper.getREChatOnlyMode()) {
            mChatPageOnlyModeRb.setChecked(SettingHelper.getREChatOnlyMode());
        }
    }

}
