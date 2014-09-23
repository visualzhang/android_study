package com.visual.sendmail;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import javax.activation.CommandMap;
import javax.activation.DataHandler;
import javax.activation.FileDataSource;
import javax.activation.MailcapCommandMap;
import javax.mail.Address;
import javax.mail.Authenticator;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Multipart;
import javax.mail.NoSuchProviderException;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import javax.mail.internet.MimeUtility;

public class MyMail
{
    private int port = 25;  //smtp协议使用的是25号端口
    private String server; // 发件人邮件服务器
    private String user;   // 使用者账号
    private String password; //使用者密码

    //构造发送邮件帐户的服务器，端口，帐户，密码
    public MyMail(String server, int port, String user, String passwd)
    {
        this.port = port;
        this.user = user;
        this.password = passwd;
        this.server = server;
        /* 这一段很关键，否则会报错，说什么格式不支持 mutilpart/mixed */
        MailcapCommandMap mc = (MailcapCommandMap) CommandMap.getDefaultCommandMap();
        mc.addMailcap("text/html;; x-java-content-handler=com.sun.mail.handlers.text_html");
        mc.addMailcap("text/xml;; x-java-content-handler=com.sun.mail.handlers.text_xml");
        mc.addMailcap("text/plain;; x-java-content-handler=com.sun.mail.handlers.text_plain");
        mc.addMailcap("multipart/*;; x-java-content-handler=com.sun.mail.handlers.multipart_mixed");
        mc.addMailcap("message/rfc822;; x-java-content-handler=com.sun.mail.handlers.message_rfc822");
        CommandMap.setDefaultCommandMap(mc);
    }

    /** 
     * @Title: sendEmail2 
     * @Description: 邮件发送，需要权限认证过程
     * @param subject  主题
     * @param recepits 接收者
     * @param sender 发送者
     * @param content 内容
     * @param attachments 附件
     * @return: void
     */
    public boolean sendEmail2(String subject, String recepits, String sender, String content, List<String> attachments)
    {
        Properties props = new Properties();
        props.setProperty("mail.transport.protocol", "smtp");
        props.setProperty("mail.smtp.host", server);
        props.put("mail.smtp.auth", "true");
        /* 用下面的端口是不行的，千万要注意 */
//        props.put("mail.smtp.port", "25");
//        props.put("mail.smtp.socketFactory.port", "25");
        props.put("mail.smtp.port", "465");
        props.put("mail.smtp.socketFactory.port", "465");
        props.put("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
        props.put("mail.smtp.socketFactory.fallback", "false");
        props.setProperty("mail.smtp.quitwait", "false");
        // 返回授权Base64编码
        PopupAuthenticator auth = new PopupAuthenticator(user, password);
        // 获取会话对象
        Session session = Session.getInstance(props, auth);
        // 设置为DEBUG模式
        session.setDebug(true);
        // 邮件内容对象组装
        MimeMessage message = new MimeMessage(session);
        try
        {
            Address addressFrom = new InternetAddress(sender, "test");
            Address addressTo = new InternetAddress(recepits, "hello");
            /* 邮件头 */
            message.setSubject(subject);         // 邮件主题
            message.setSentDate(new Date());     // 发送日期
            message.setFrom(addressFrom);        // 邮件发送者
            message.addRecipient(Message.RecipientType.TO, addressTo); // 邮件接收者

            /* 邮件主体 */
            Multipart multipart = new MimeMultipart();
            MimeBodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setText(content, "utf-8");
            multipart.addBodyPart(messageBodyPart);

            /* 邮件附件 */
            if (attachments != null && attachments.size() > 0)
            {
                for (String filePath : attachments)
                {
                    File file = new File(filePath);
                    if(file.exists())
                    {
                        MimeBodyPart attachPart = new MimeBodyPart();
                        FileDataSource source = new FileDataSource(filePath);
                        attachPart.setDataHandler(new DataHandler(source));
                        attachPart.setFileName(file.getName());
                        multipart.addBodyPart(attachPart);
                    }
                }
            }

            // 保存邮件内容
            message.setContent(multipart);

            // 获取SMTP协议客户端对象，连接到指定SMPT服务器
            Transport transport = session.getTransport("smtp");
            transport.connect(server, port, user, password);
            System.out.println("My Mail : connet it success!!!!");

            // 发送邮件到SMTP服务器
            Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
            Transport.send(message);
            System.out.println("My Mail : send it success!!!!");

            // 关闭连接
            transport.close();
            return true;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return false;
        }
    }

    /** 
     * @Title: sendEmail 
     * @Description: 发送邮件，不要权限认证，直接用账号和密码连接来发送
     * @param subject 主题
     * @param recepits 接收者
     * @param sender 发送者
     * @param content 内容
     * @param attachments 附件
     * @return: void
     */
    public boolean sendEmail(String subject, String recepits, String sender, String content, List<String> attachments)
    {
        Properties props = new Properties();
        props.put("mail.smtp.host", server);
        props.put("mail.smtp.port", String.valueOf(port));
        props.put("mail.smtp.auth", "false");
        Transport transport = null;
        Session session = Session.getDefaultInstance(props, null);
        // 设置为DEBUG模式
        session.setDebug(true);
        MimeMessage msg = new MimeMessage(session);
        try
        {
            transport = session.getTransport("smtp");
            transport.connect(server, user, password);    //建立与服务器连接
            msg.setSentDate(new Date());
            InternetAddress fromAddress = null;
            fromAddress = new InternetAddress(sender,"MyMail.sendMail");
            msg.setFrom(fromAddress);
            InternetAddress[] toAddress = new InternetAddress[1];
            toAddress[0] = new InternetAddress(recepits,"icast.smit");
            msg.setRecipients(Message.RecipientType.TO, toAddress);
            msg.setSubject(subject, "UTF-8");            //设置邮件标题
            MimeMultipart multi = new MimeMultipart();   //代表整个邮件邮件
            BodyPart textBodyPart = new MimeBodyPart();  //设置正文对象
            textBodyPart.setText(content);                  //设置正文
            multi.addBodyPart(textBodyPart);             //添加正文到邮件
            for (String path : attachments)
            {
                FileDataSource fds = new FileDataSource(path);   //获取磁盘文件
                BodyPart fileBodyPart = new MimeBodyPart();                       //创建BodyPart
                fileBodyPart.setDataHandler(new DataHandler(fds));           //将文件信息封装至BodyPart对象
                String fileNameNew = MimeUtility.encodeText(fds.getName(), "utf-8", null);      //设置文件名称显示编码，解决乱码问题
                fileBodyPart.setFileName(fileNameNew);  //设置邮件中显示的附件文件名
                multi.addBodyPart(fileBodyPart);        //将附件添加到邮件中
            }
            msg.setContent(multi);                      //将整个邮件添加到message中
            msg.saveChanges();
            transport.sendMessage(msg, msg.getAllRecipients());  //发送邮件
            System.out.println("My Mail : send email with attachment!");
            transport.close();
            return true;
        }
        catch (NoSuchProviderException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (MessagingException e)
        {
            e.printStackTrace();
            return false;
        }
        catch (UnsupportedEncodingException e)
        {
            e.printStackTrace();
            return false;
        }
    }

    class PopupAuthenticator extends Authenticator
    {
        private String userName;
        private String password;

        public PopupAuthenticator(String userName, String password)
        {
            this.userName = userName;
            this.password = password;
        }

        public PasswordAuthentication getPasswordAuthentication()
        {
            return new PasswordAuthentication(userName, password);
        }
    }
}
