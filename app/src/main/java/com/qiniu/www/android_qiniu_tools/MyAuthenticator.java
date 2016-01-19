package com.qiniu.www.android_qiniu_tools;

/**
 * Created by Yuting on 2015/9/23.
 */

import javax.mail.*;

public class MyAuthenticator extends Authenticator {
    String userName="";
    String password="";

    public MyAuthenticator(){
    }
    public MyAuthenticator(String username, String password) {
        this.userName = username;
        this.password = password;
    }
    protected PasswordAuthentication getPasswordAuthentication(){
        return new PasswordAuthentication(userName, password);
    }
}