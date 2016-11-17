package com.network.QNetDiagnoUtils;

import org.json.JSONObject;

/**
 * Created by liuhanlin on 2016/11/8.
 */
public class Probe {
    public static JSONObject probe =new JSONObject();

    public static JSONObject GetProbe(){

        return probe;
    }
    public static void SetProbe(JSONObject p){
        Probe.probe= p;
    }

}
