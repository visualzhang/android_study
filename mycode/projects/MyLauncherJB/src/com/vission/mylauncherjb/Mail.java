package com.vission.mylauncherjb;

import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;

public class Mail {  
    public static void SendEmail(String id) {  
         //SimpleEmail email = new SimpleEmail();
         HtmlEmail email = new HtmlEmail();
         email.setHostName("smtp.163.com");//�ʼ�������  
         email.setAuthentication("newtaiji", "taiji2013");//smtp��֤���û���������  
         try {
            email.addTo("changing.zhang@gmail.com");//������
            email.setFrom("newtaiji@163.com", "xxx");//������  
            email.setSubject("newtaiji�Ĳ����ʼ�");//����  
            email.setCharset("UTF-8");//�����ʽ  
            email.setMsg("����һ��newtaiji�Ĳ����ʼ�");//����  
            email.send();//����  
            System.out.println("send ok..........");
        } catch (EmailException e) {
            e.printStackTrace();
        } 
        
    }  
}  