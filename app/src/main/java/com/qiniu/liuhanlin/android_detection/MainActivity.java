package com.qiniu.liuhanlin.android_detection;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;


import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.netease.LDNetDiagnoService.LDNetDiagnoListener;
import com.netease.LDNetDiagnoService.LDNetDiagnoService;
import com.qiniu.www.android_qiniu_tools.SimpleMailSender;

public class MainActivity extends AppCompatActivity implements OnClickListener,
        LDNetDiagnoListener {
    private Button btn;
    private ProgressBar progress;
    private TextView text;
    private EditText edit;
    private String showInfo = "";
    private boolean isRunning = false;
    private LDNetDiagnoService _netDiagnoService;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        text = (TextView) findViewById(R.id.text);
        edit = (EditText) findViewById(R.id.domainName);
        edit.clearFocus();
    }

    @Override
    public void onClick(View v) {
        if (v == btn) {
            if (!isRunning) {
                showInfo = "";
                String domainName = edit.getText().toString().trim();
                _netDiagnoService = new LDNetDiagnoService(getApplicationContext(),
                        "testDemo", "网络诊断应用", "1.0.0", "huipang@corp.netease.com",
                        "deviceID(option)", domainName, "carriname", "ISOCountyCode",
                        "MobilCountryCode", "MobileNetCode", this);
                // 设置是否使用JNIC 完成traceroute
                _netDiagnoService.setIfUseJNICTrace(false);
//        _netDiagnoService.setIfUseJNICConn(true);
                _netDiagnoService.execute();
                progress.setVisibility(View.VISIBLE);
                text.setText("Traceroute with max 30 hops...");
                btn.setText("停止诊断");
                btn.setEnabled(false);
                edit.setInputType(InputType.TYPE_NULL);


            } else {
                progress.setVisibility(View.GONE);
                btn.setText("开始诊断");
                _netDiagnoService.cancel(true);
                _netDiagnoService = null;
                btn.setEnabled(true);
                edit.setInputType(InputType.TYPE_CLASS_TEXT);
            }

            isRunning = !isRunning;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (_netDiagnoService != null) {
            _netDiagnoService.stopNetDialogsis();
        }

    }

    @Override
    public void onBackPressed() {

        super.onBackPressed();

    }

    @Override
    public void OnNetDiagnoFinished(String log) {
        String domainName = edit.getText().toString().trim();
        text.setText(log);
        System.out.println("");
        progress.setVisibility(View.GONE);
        btn.setText("开始诊断");
        btn.setEnabled(true);
        edit.setInputType(InputType.TYPE_CLASS_TEXT);
        isRunning = false;
        SimpleMailSender sse = new SimpleMailSender();
        sse.email(log,domainName);
    }

    @Override
    public void OnNetDiagnoUpdated(String log) {
        showInfo += log;
        text.setText(showInfo);
    }

}


