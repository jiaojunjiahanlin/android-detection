package com.network.QNetDiagnoService;

import android.os.SystemClock;
import android.util.Log;

import com.network.QNetDiagnoUtils.Client;
import com.network.QNetDiagnoUtils.TempFile;
import com.qiniu.android.http.ResponseInfo;
import com.qiniu.android.storage.UpCompletionHandler;
import com.qiniu.android.storage.UploadManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * Created by liuhanlin on 15/12/31.
 */
public class LDNetUp {


    private volatile String keyv;

    LDNetUpListener listener; // 回传ping的结果
    private final int _sendCount; // 每次ping发送数据包的个数

    public LDNetUp(LDNetUpListener listener, int theSendCount) {
        super();
        this.listener = listener;
        this._sendCount = theSendCount;
    }




    public interface LDNetUpListener {
        public void OnNetUpFinished(String log);
        public void OnNetUpdated(String log);
    }
    /**
     * 获取运营商信息
     */
    public String up() throws IOException, JSONException {
        int size=2048;
        JSONObject obj;
//        Configuration configuration=new Configuration.Builder().build();
        UploadManager uploadManager = new UploadManager();
        final File f = TempFile.createFile(size);

        String key = "qiniutest.zip";
        obj = new JSONObject(tokenstring());
        String token = obj.optString("uptoken");
        Log.i("up", "token-reak" + token);
        final StringBuilder log = new StringBuilder();
        final LDNetUpListener listener0=this.listener;
        uploadManager.put(f, key, token,
                new UpCompletionHandler() {
                    @Override
                    public void complete(String key, ResponseInfo info, JSONObject res) {
                        JSONObject up = new JSONObject();
                        listener0.OnNetUpFinished("\n" + "开始上传\n");
                        //  res 包含hash、key等信息，具体字段取决于上传策略的设置。
                        Log.i("up", "response0" + key + ",\r\n " + info + ",\r\n " + res);
                        log.append(info + ",\r\n " + res);
                        try {
                            up.put("header",info);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            up.put("body",res);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        listener0.OnNetUpFinished(log.toString());
                        try {
                            Client.client.put("up_info", up.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                }, null);
        Log.i("up", "response1" + keyv);
        SystemClock.sleep(10000);
       // this.listener.OnNetUpFinished(log.toString());

      // this.listener.OnNetUpdated(log.toString());

        return keyv;
    }



    private String tokenstring() {

        StringBuffer result = new StringBuffer();
        String str="";
        URL infoUrl = null;
        InputStream inStream = null;
        try
        {
            long now = System.currentTimeMillis();
            Random ra =new Random();

            int rb=ra.nextInt(900)+100;
            Log.i("ping", "ip" + now);
            infoUrl = new URL("http://jssdk.demo.qiniu.io/uptoken");
            URLConnection connection = infoUrl.openConnection();
            HttpURLConnection httpConnection = (HttpURLConnection)connection;
            int responseCode = httpConnection.getResponseCode();
            if(responseCode == HttpURLConnection.HTTP_OK)
            {
                inStream = httpConnection.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inStream,"utf-8"));
                StringBuilder strber = new StringBuilder();
                String line = null;
                while ((line = reader.readLine()) != null)
                    strber.append(line + "\n");
                inStream.close();
                //从反馈的结果中提取出IP地址
                Log.i("ping", "ip" + strber);
                str=strber.toString();

            }
        }
        catch(MalformedURLException e) {
            e.printStackTrace();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        Log.i("up", "token" + str);
        return str;
    }



    public void exec() {
        UpTask upTask = new UpTask();
        StringBuilder log = new StringBuilder();
        Log.i("up", "log-init" + log.toString());
        try {
            String status = up();
            Log.i("up", "status" + status);
            log.append("\t" + status);

          //  this.listener.OnNetUpFinished(log.toString());
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }


    public class UpTask {


    }

}
