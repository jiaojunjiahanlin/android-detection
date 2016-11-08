package com.qiniu.liuhanlin.android_detection;

import android.app.Activity;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.netease.LDNetDiagnoService.LDNetDiagnoListener;
import com.netease.LDNetDiagnoService.LDNetDiagnoService;
import com.netease.LDNetDiagnoUtils.Client;
import com.netease.LDNetDiagnoUtils.Probe;
import com.netease.LDNetDiagnoUtils.TAG;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cz.msebera.android.httpclient.HttpEntity;
import cz.msebera.android.httpclient.HttpResponse;
import cz.msebera.android.httpclient.NameValuePair;
import cz.msebera.android.httpclient.client.HttpClient;
import cz.msebera.android.httpclient.client.entity.UrlEncodedFormEntity;
import cz.msebera.android.httpclient.client.methods.HttpPost;
import cz.msebera.android.httpclient.impl.client.DefaultHttpClient;
import cz.msebera.android.httpclient.message.BasicNameValuePair;
import cz.msebera.android.httpclient.protocol.HTTP;
import cz.msebera.android.httpclient.util.EntityUtils;

public class IDActivity extends Activity implements View.OnClickListener,
        LDNetDiagnoListener {
    private Button btn;
    private String probe;
    private ProgressBar progress;
    private TextView text;
    private EditText edit;
    private String showInfo = "";
    private boolean isRunning = false;
    private LDNetDiagnoService _netDiagnoService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_id);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(this);
        progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.INVISIBLE);
        text = (TextView) findViewById(R.id.text);
        edit = (EditText) findViewById(R.id.domainName);
        edit.clearFocus();
        TAG.tag="id";
    }

    @Override
    public void onClick(View v) {
        if (v == btn) {
            if (!isRunning) {
                showInfo = "";
                String domainName = edit.getText().toString().trim();
                _netDiagnoService = new LDNetDiagnoService(getApplicationContext(),
                        "rover", "网络诊断应用", "1.1.0", "hellowrold@qq.com",
                        "未知", domainName, "explorer", "ISOCountyCode",
                        "MobilCountryCode", "MobileNetCode", this);
                // 设置是否使用JNIC 完成traceroute
                _netDiagnoService.setIfUseJNICTrace(false);
//              _netDiagnoService.setIfUseJNICConn(true);
//                probe = GetProbe();
                probe = "{ \"id\" :\"58218661afd9f809fb000002\", \"up_host\" : \"upload.qiniu.com\", \"download_url\" : \"http://7xt44n.com1.z0.glb.clouddn.com//00001.jpeg\", \"ping_host\" : \"www.baidu.com\", \"trace_host\" : \"7xt44n.com1.z0.glb.clouddn.com\", \"created_at\" : \"2016-11-07T08:01:37.609Z\", \"title\" : \"任务1\", \"description\" : \"<p>哈哈</p>\" }";
                try {
                    Probe.probe=new JSONObject(probe);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                if( probe != "")
                {
                    try {
                        System.out.printf("probe-----------" +Probe.probe.getString("id") );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    _netDiagnoService.execute(probe);
                }
                progress.setVisibility(View.VISIBLE);
                text.setText("Traceroute with max 30 hops...");
                btn.setText("停止诊断");
                btn.setEnabled(true);
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
//        SimpleMailSender sse = new SimpleMailSender();
//        sse.email(log,domainName);
        Log.i("client-----------", Client.client.toString());
    }

    @Override
    public void OnNetDiagnoUpdated(String log) {
        showInfo += log;
        text.setText(showInfo);
    }

    public String GetProbe() {
        try {
            HttpPost httpPost = new HttpPost("");
            List<NameValuePair> params = new ArrayList<NameValuePair>();
            params.add(new BasicNameValuePair("model", android.os.Build.MODEL));
            HttpEntity entity = new UrlEncodedFormEntity(params, HTTP.UTF_8);
            httpPost.setEntity(entity);
            HttpClient httpClient = new DefaultHttpClient();
            HttpResponse httpResp = httpClient.execute(httpPost);
            int statusCode = httpResp.getStatusLine().getStatusCode();
            if (statusCode == 200) {
                return EntityUtils.toString(httpResp.getEntity(), HTTP.UTF_8);
            } else {
                return null;
            }
        } catch (Exception e) {

        }
        return null;
    }


}


