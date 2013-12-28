package com.vission.mylauncherjb;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class Mail {  
    public static void SendEmail(String id) {  
         //SimpleEmail email = new SimpleEmail();
         HtmlEmail email = new HtmlEmail();
         email.setHostName("smtp.163.com");//邮件服务器  
         email.setAuthentication("newtaiji", "taiji2013");//smtp认证的用户名和密码  
         try {
            email.addTo("changing.zhang@gmail.com");//收信者
            email.setFrom("newtaiji@163.com", "xxx");//发信者  
            email.setSubject("newtaiji的测试邮件");//标题  
            email.setCharset("UTF-8");//编码格式  
            email.setMsg("这是一封newtaiji的测试邮件");//内容  
            email.send();//发送  
            System.out.println("send ok..........");
        } catch (EmailException e) {
            e.printStackTrace();
        } 
        
    }  
}  