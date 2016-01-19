package com.netease.LDNetDiagnoService;

import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

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

    public void download() {
        final LDNetDownloadListener listener0 = this.listener;

        new Thread(new Runnable() {

            @Override
            public void run() {

                InputStream stream = null;
                try {

                    int bytesIn = 0;
                    final StringBuilder log = new StringBuilder();
                    String downloadFileUrl = "http://liuhanlin-work.qiniudn.com/360%E5%8E%8B%E7%BC%A9_3.2.0.2060.exe";
                    long startCon = System.currentTimeMillis();
                    URL url = new URL(downloadFileUrl);
                    URLConnection con = url.openConnection();
                    con.setUseCaches(false);
                    long connectionLatency = System.currentTimeMillis() - startCon;
                    stream = con.getInputStream();

                    //Message msgUpdateConnection = Message.obtain(mHandler, MSG_UPDATE_CONNECTION_TIME);
                    // msgUpdateConnection.arg1 = (int) connectionLatency;
                    //mHandler.sendMessage(msgUpdateConnection);

                    long start = System.currentTimeMillis();
                    int currentByte = 0;
                    long updateStart = System.currentTimeMillis();
                    long updateDelta = 0;
                    int bytesInThreshold = 0;

                    while ((currentByte = stream.read()) != -1) {
                        bytesIn++;
                        bytesInThreshold++;
                    }

                    double downloadTime = (System.currentTimeMillis() - start);
                    log.append("下载速度：－－－－"+calculate(downloadTime/1000, 7168)+"KB/s"+"下载时间：- - -"+downloadTime/1000+"秒");
                    listener0.OnNetDownloadFinished(log.toString());
                    ;
                    //Prevent AritchmeticException
                    if (downloadTime == 0) {
                        downloadTime = 1;
                    }

                } catch (MalformedURLException e) {

                } catch (IOException e) {

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

    public void exec() {
        StringBuilder log = new StringBuilder();
        Log.i("down", "down" + log.toString());
        download();
        SystemClock.sleep(15000);

    }

    private double calculate(final double downloadTime, final double bytesIn) {
        //from mil to sec
        double bytespersecond = (bytesIn / downloadTime);


        return bytespersecond;
    }


}
