package com.netease.LDNetDiagnoService;

import android.os.SystemClock;
import android.util.Log;

import com.netease.LDNetDiagnoUtils.Client;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.Map;

/**
 * Created by liuhanlin on 16/1/8.
 */
public class LDNetDownload {


    //Private fields
    private static final int EXPECTED_SIZE_IN_BYTES = 1048576;//1MB 1024*1024

    private static final double EDGE_THRESHOLD = 176.0;
    private static final double BYTE_TO_KILOBIT = 0.0078125;
    private static final double KILOBIT_TO_MEGABIT = 0.0009765625;



    LDNetDownloadListener listener; // 回传ping的结果
    private final int _sendCount; // 每次ping发送数据包的个数

    public LDNetDownload(LDNetDownloadListener listener, int theSendCount) {
        super();
        this.listener = listener;
        this._sendCount = theSendCount;
    }




    public interface LDNetDownloadListener {
        public void OnNetDownloadFinished(String log);
    }

    public void download(final String download_url) {
        final LDNetDownloadListener listener0 = this.listener;

        new Thread(new Runnable() {

            @Override
            public void run() {

                InputStream stream = null;
                try {

                    int bytesIn = 0;
                    final StringBuilder log = new StringBuilder();
                    String downloadFileUrl = "http://liuhanlin-work.qiniudn.com/360%E5%8E%8B%E7%BC%A9_3.2.0.2060.exe";
                    if (download_url!=""){
                         downloadFileUrl =download_url;
                    }

                    long startCon = System.currentTimeMillis();
                    URL url = new URL(downloadFileUrl);
                    URLConnection con = url.openConnection();
                    con.setUseCaches(false);
                    Map<String, List<String>> respHeaders = con.getHeaderFields();
                    JSONObject respJson = new JSONObject();
                    for(Map.Entry<String,List<String>> entry : respHeaders.entrySet()){
                        if (entry.getKey()==null){
                            continue;
                        }
                        for(String value : entry.getValue()){
                            respJson.put(entry.getKey(),value);

                        }
                    }

                    System.out.println("json----------"+respJson);
                    long connectionLatency = System.currentTimeMillis() - startCon;
                    stream = con.getInputStream();
                    long start = System.currentTimeMillis();
                    int currentByte = 0;
                    long updateStart = System.currentTimeMillis();
                    long updateDelta = 0;
                    int bytesInThreshold = 0;

                    while ((currentByte = stream.read()) != -1) {
                        bytesIn++;
                        bytesInThreshold++;
                    }
                    InetAddress remoteIP = InetAddress.getByName("liuhanlin-work.qiniudn.com");
                    double downloadTime = (System.currentTimeMillis() - start);
                    log.append("\n" + "开始下载...\n");
                    Client.client.put("dresp_headers", respJson.toString());
                    log.append("下载速度：－－－－" + calculate(downloadTime / 1000, 7168) + "KB/s" + "下载时间：- - -" + downloadTime / 1000 + "秒");
                    listener0.OnNetDownloadFinished(log.toString());
                    Client.client.put("download_info", log.toString());
                    Client.client.put("remote_ip",remoteIP);
                    //Prevent AritchmeticException
                    if (downloadTime == 0) {
                        downloadTime = 1;
                    }

                } catch (MalformedURLException e) {

                } catch (IOException e) {

                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        if (stream != null) {
                            stream.close();
                        }
                    } catch (IOException e) {
                        //Suppressed
                    }
                }

            }
        }).start();
    }

    public void exec(String download_url) {
        StringBuilder log = new StringBuilder();
        Log.i("down", "down" + log.toString());
        download(download_url);
        SystemClock.sleep(15000);

    }

    private double calculate(final double downloadTime, final double bytesIn) {
        //from mil to sec
        int bytespersecond = (int)(bytesIn / downloadTime);


        return bytespersecond;
    }


}
