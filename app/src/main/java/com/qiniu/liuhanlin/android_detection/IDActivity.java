package com.qiniu.liuhanlin.android_detection;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.network.QNetDiagnoService.LDNetDiagnoListener;
import com.network.QNetDiagnoService.LDNetDiagnoService;
import com.network.QNetDiagnoUtils.Client;
import com.network.QNetDiagnoUtils.TAG;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
    private Handler handler = new Handler();


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
    }

    @Override
    public void onClick(View v) {
        TAG.tag="id";
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
                //在UI线程更新UI
                _netDiagnoService.execute(probe);
                progress.setVisibility(View.VISIBLE);
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

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    PostJson();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    @Override
    public void OnNetDiagnoUpdated(String log) {
        showInfo += log;
        text.setText(showInfo);
    }


    public String PostJson() throws IOException {
        String request = "http://3113abe4.ngrok.io/api/report/create";
        String msg = "";
        try{
            HttpURLConnection conn = (HttpURLConnection) new URL(request).openConnection();
            //设置请求方式,请求超时信息
            conn.setRequestMethod("POST");
            conn.setReadTimeout(50000);
            conn.setConnectTimeout(50000);
            conn.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            //设置运行输入,输出:
            conn.setDoOutput(true);
            conn.setDoInput(true);
            //Post方式不能缓存,需手动设置为false
            conn.setUseCaches(false);
            //我们请求的数据:
            String data = Client.client.toString();
            //这里可以写一些请求头的东东...
            //获取输出流
            OutputStream out = conn.getOutputStream();
            out.write(data.getBytes());
            out.flush();
            if (conn.getResponseCode() == 200) {
                // 获取响应的输入流对象
                InputStream is = conn.getInputStream();
                // 创建字节输出流对象
                ByteArrayOutputStream message = new ByteArrayOutputStream();
                // 定义读取的长度
                int len = 0;
                // 定义缓冲区
                byte buffer[] = new byte[1024];
                // 按照缓冲区的大小，循环读取
                while ((len = is.read(buffer)) != -1) {
                    // 根据读取的长度写入到os对象中
                    message.write(buffer, 0, len);
                }
                // 释放资源
                is.close();
                message.close();
                // 返回字符串
                msg = new String(message.toByteArray());
                return msg;
            }
        }catch(Exception e){e.printStackTrace();}
        return msg;
    }


}


