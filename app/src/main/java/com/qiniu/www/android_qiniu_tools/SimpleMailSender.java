package com.qiniu.www.android_qiniu_tools;

/**
 * Created by Yuting on 2015/9/23.
 */
import android.util.Log;

import java.util.Date;
import java.util.Properties;
import javax.mail.Address;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import java.net.URL;
import java.io.InputStream;

public class SimpleMailSender {

    /**
     * 以文本格式发送邮件
     * @param mailInfo 待发送的邮件的信息
     */

    public void email( final String s, final String title) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                Log.i("mail", "进入发送邮件");
                StringBuffer result = new StringBuffer();
                String str;
                URL infoUrl = null;
                InputStream inStream = null;

                MailSenderInfo mailInfo = new MailSenderInfo();
                mailInfo.setMailServerHost("smtp.exmail.qq.com");
                mailInfo.setMailServerPort("25");
                mailInfo.setValidate(true);
                //填写发送的邮箱的地址
                mailInfo.setUserName("qwebtest@qiniu.com");
                //您的邮箱密码
                mailInfo.setPassword("");
                mailInfo.setFromAddress("qwebtest@qiniu.com");
                mailInfo.setToAddress("client-report@qiniu.com");
                mailInfo.setSubject("七牛网络测试-"+title);
                mailInfo.setContent(s);
                Log.i("mail", "设置邮件配置成功");
                //这个类主要来发送邮件
                SimpleMailSender sms = new SimpleMailSender();
                sms.sendTextMail(mailInfo);//发送文体格式
                // sms.sendHtmlMail(mailInfo);//发送html格式
            }

        }).start();
    }
    public boolean sendTextMail(MailSenderInfo mailInfo) {
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        if (mailInfo.isValidate()) {
            // 如果需要身份认证，则创建一个密码验证器
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro,authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            mailMessage.setRecipient(Message.RecipientType.TO, to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // 设置邮件消息的主要内容
            String mailContent = mailInfo.getContent();
            mailMessage.setText(mailContent);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
            ex.getCause();
            Log.i("ping", "发送失败1"+ex.getCause());
        }
        Log.i("ping", "发送失败2");
        return false;
    }

    /**
     * 以HTML格式发送邮件
     * @param mailInfo 待发送的邮件信息
     */
    public static boolean sendHtmlMail(MailSenderInfo mailInfo){
        // 判断是否需要身份认证
        MyAuthenticator authenticator = null;
        Properties pro = mailInfo.getProperties();
        //如果需要身份认证，则创建一个密码验证器
        if (mailInfo.isValidate()) {
            authenticator = new MyAuthenticator(mailInfo.getUserName(), mailInfo.getPassword());
        }
        // 根据邮件会话属性和密码验证器构造一个发送邮件的session
        Session sendMailSession = Session.getDefaultInstance(pro,authenticator);
        try {
            // 根据session创建一个邮件消息
            Message mailMessage = new MimeMessage(sendMailSession);
            // 创建邮件发送者地址
            Address from = new InternetAddress(mailInfo.getFromAddress());
            // 设置邮件消息的发送者
            mailMessage.setFrom(from);
            // 创建邮件的接收者地址，并设置到邮件消息中
            Address to = new InternetAddress(mailInfo.getToAddress());
            // Message.RecipientType.TO属性表示接收者的类型为TO
            mailMessage.setRecipient(Message.RecipientType.TO,to);
            // 设置邮件消息的主题
            mailMessage.setSubject(mailInfo.getSubject());
            // 设置邮件消息发送的时间
            mailMessage.setSentDate(new Date());
            // MiniMultipart类是一个容器类，包含MimeBodyPart类型的对象
            Multipart mainPart = new MimeMultipart();
            // 创建一个包含HTML内容的MimeBodyPart
            BodyPart html = new MimeBodyPart();
            // 设置HTML内容
            html.setContent(mailInfo.getContent(), "text/html; charset=utf-8");
            mainPart.addBodyPart(html);
            // 将MiniMultipart对象设置为邮件内容
            mailMessage.setContent(mainPart);
            // 发送邮件
            Transport.send(mailMessage);
            return true;
        } catch (MessagingException ex) {
            ex.printStackTrace();
        }
        return false;
    }
}
