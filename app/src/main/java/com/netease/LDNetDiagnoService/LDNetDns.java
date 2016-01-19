package com.netease.LDNetDiagnoService;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Random;

/**
 * Created by liuhanlin on 16/1/6.
 */
public class LDNetDns {


    LDNetDnsListener listener; // 回传ping的结果
    private final int _sendCount; // 每次ping发送数据包的个数

    public LDNetDns(LDNetDnsListener listener, int theSendCount) {
        super();
        this.listener = listener;
        this._sendCount = theSendCount;
    }




    public interface LDNetDnsListener {
        public void OnNetDnsFinished(String log);
    }

    public String dns() {
        StringBuffer result = new StringBuffer();
        String str="";
        URL infoUrl = null;
        InputStream inStream = null;
        try
        {
            //http://iframe.ip138.com/ic.asp
            //infoUrl = new URL("http://city.ip138.com/city0.asp");
            long now = System.currentTimeMillis();
            Random ra =new Random();

            int rb=ra.nextInt(900) + 100;
            Log.i("ping", "ip" + now);
            infoUrl = new URL("http://7563614540466"+rb+".testns.cdnunion.net/?callback=jQuery18102853321498259902_1442981784438&_="+now);
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

        return str;
    }


    public void exec() {
        StringBuilder log = new StringBuilder();
        Log.i("dns", "dns-init" + log.toString());

            String status = dns();
            Log.i("dns", "status" + status);
            log.append("\t" +"出口ip＋dns检测："+ status);

            this.listener.OnNetDnsFinished(log.toString());


    }


    public class UpTask {


    }

}
