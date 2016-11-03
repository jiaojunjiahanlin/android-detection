package com.netease.LDNetDiagnoUtils;

/**
 * Created by liuhanlin on 2016/11/2.
 */


import org.json.JSONObject;

/**
 * Created by liuhanlin on 2016/11/2.
 */
public  class Client {
    public static JSONObject client =new JSONObject();

    public static JSONObject GetClient(){

        return client;
    }
    public static void GetClient(JSONObject c){
        Client.client= c;
    }

}
