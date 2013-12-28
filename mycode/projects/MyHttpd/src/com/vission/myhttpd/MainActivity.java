package com.vission.myhttpd;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URLDecoder;
import java.util.StringTokenizer;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class MainActivity extends Activity {

	final static String TAG = "MyHttpd";
	Thread httpThread;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		httpThread = new Thread(new SimpleHttpServer());
		httpThread.start();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	class SimpleHttpServer implements Runnable {  
	    /** 
	     * 
	     */  
	    ServerSocket serverSocket;//������Socket
	  
	    /** 
	     * �����������˿�, Ĭ��Ϊ 80. 
	     */  
	    public int PORT=8080;//��׼HTTP�˿�  
	  
	    /** 
	     * ��ʼ������ Socket �߳�. 
	     */  
	    public SimpleHttpServer() {  
	        try {  
	            serverSocket=new ServerSocket(PORT);  
	        } catch(Exception e) {  
	            System.out.println("�޷�����HTTP������:"+e.getLocalizedMessage());  
	        }  
	        if(serverSocket==null)  System.exit(1);//�޷���ʼ������  
	        //new Thread(this).start();  
	        System.out.println("HTTP��������������,�˿�:"+PORT);  
	    }  
	  
	    /** 
	     * ���з��������߳�, �����ͻ������󲢷�����Ӧ. 
	     */  
	    public void run() {  
	        while(true) {  
	            try {  
	                Socket client=null;//�ͻ�Socket  
	                int contentLength = 0;// �ͻ��˷��͵� HTTP ���������ĳ���  
	                client=serverSocket.accept();//�ͻ���(������ IE �������)�Ѿ����ӵ���ǰ������  
	                if(client!=null) {  
	                    System.out.println("���ӵ����������û�:"+client);  
	                    try {  
	                        // ��һ�׶�: ��������  
	                        BufferedReader in=new BufferedReader(new InputStreamReader(  
	                                client.getInputStream()));  
	  
	                        System.out.println("�ͻ��˷��͵�������Ϣ:\n===================");  
	                        // ��ȡ��һ��, �����ַ  
	                        String line=in.readLine();  
	                        System.out.println(line);  
	                        String resource=line.substring(line.indexOf('/'),line.lastIndexOf('/')-5);  
	                        //����������Դ�ĵ�ַ  
	                        resource=URLDecoder.decode(resource, "UTF-8");//������ URL ��ַ  
	                        String method = new StringTokenizer(line).nextElement().toString();// ��ȡ���󷽷�, GET ���� POST  
	  
	                        // ��ȡ������������͹������������ͷ����Ϣ  
	                        while( (line = in.readLine()) != null) {  
	                            System.out.println(line);  
	  
	                            // ��ȡ POST �����ݵ����ݳ���  
	                            if(line.startsWith("Content-Length")) {  
	                                try {  
	                                    contentLength = Integer.parseInt(line.substring(line.indexOf(':') + 1).trim());  
	                                } catch (Exception e) {  
	                                    e.printStackTrace();  
	                                }  
	                            }  
	  
	                            if(line.equals("")) break;  
	                        }  
	  
	                        // ��ʾ POST ���ύ������, �������λ����������岿��  
	                        if("POST".equalsIgnoreCase(method) && contentLength > 0) {  
	                            System.out.println("��������Ϊ POST ��ʽ�ύ�ı�����");  
	                            for(int i = 0; i < contentLength; i++) {  
	                                System.out.print((char)in.read());  
	                            }  
	  
	                            System.out.println();  
	                        }  
	  
	                        System.out.println("������Ϣ����\n===================");  
	                        System.out.println("�û��������Դ��:"+resource);  
	                        System.out.println("�����������: " + method);  
	  
	                        // GIF ͼƬ�Ͷ�ȡһ����ʵ��ͼƬ���ݲ����ظ��ͻ���  
	                        if(resource.endsWith(".gif")) {  
	                            fileService("images/test.gif", client);  
	                            closeSocket(client);  
	                            continue;  
	                        }  
	  
	                        // ���� JPG ��ʽ�ͱ��� 404  
	                        if(resource.endsWith(".jpg")) {  
	                                                    PrintWriter out=new PrintWriter(client.getOutputStream(),true);  
	                        out.println("HTTP/1.0 404 Not found");//����Ӧ����Ϣ,������Ӧ��  
	                        out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ  
	                        out.close();  
	                        closeSocket(client);  
	                        continue;  
	                        } else {  
	                            // �� writer �Կͻ��� socket ���һ�� HTML ����  
	                            PrintWriter out=new PrintWriter(client.getOutputStream(),true);  
	                            out.println("HTTP/1.0 200 OK");//����Ӧ����Ϣ,������Ӧ��  
	                            out.println("Content-Type:text/html;charset=GBK");
	                            out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ  

	                            out.println("<h1> Hello Http Server</h1>");  
	                            out.println("���, ����һ�� Java HTTP ������ demo Ӧ��.<br>");  
	                            out.println("�������·����: " + resource + "<br>");  
	                            out.println("����һ��֧������·����ͼƬ:<img src='abc.gif'><br>" +  
	                                    "<a href='abc.gif'>�����abc.gif, �Ǹ�����������·����ͼƬ�ļ�.</a>");  
	                            out.println("<br>���Ǹ��ᷴ�� 404 ����ĵ�ͼƬ:<img src='test.jpg'><br><a href='test.jpg'>�����test.jpg</a><br>");  
	                            out.println("<form method=post action='/'>POST �� <input name=username value='�û�'> <input name=submit type=submit value=submit></form>");  
	                            out.close();  
	  
	                            closeSocket(client);  
	                        }  
	                    } catch(Exception e) {  
	                        System.out.println("HTTP����������:"+e.getLocalizedMessage());  
	                    }  
	                }  
	                //System.out.println(client+"���ӵ�HTTP������");//���������һ��,��������Ӧ�ٶȻ����  
	            } catch(Exception e) {  
	                System.out.println("HTTP����������:"+e.getLocalizedMessage());  
	            }  
	        }  
	    }  
	  
	    /** 
	     * �رտͻ��� socket ����ӡһ��������Ϣ. 
	     * @param socket �ͻ��� socket. 
	     */  
	    void closeSocket(Socket socket) {  
	        try {  
	            socket.close();  
	        } catch (IOException ex) {  
	            ex.printStackTrace();  
	        }  
	                            System.out.println(socket + "�뿪��HTTP������");  
	    }  
	  
	    /** 
	     * ��ȡһ���ļ������ݲ����ظ��������. 
	     * @param fileName �ļ��� 
	     * @param socket �ͻ��� socket. 
	     */  
	        void fileService(String fileName, Socket socket)  
	    {  
	  
	        try  
	        {  
	            PrintStream out = new PrintStream(socket.getOutputStream(), true);  
	            File fileToSend = new File(fileName);  
	            if(fileToSend.exists() && !fileToSend.isDirectory())  
	            {  
	                out.println("HTTP/1.0 200 OK");//����Ӧ����Ϣ,������Ӧ��  
	                out.println("Content-Type:application/binary");  
	                out.println("Content-Length:" + fileToSend.length());// ���������ֽ���  
	                out.println();// ���� HTTP Э��, ���н�����ͷ��Ϣ  
	  
	                FileInputStream fis = new FileInputStream(fileToSend);  
	                byte data[] = new byte[fis.available()];  
	                fis.read(data);  
	                out.write(data);  
	                out.close();  
	                fis.close();  
	            }  
	        }  
	        catch(Exception e)  
	        {  
	            System.out.println("�����ļ�ʱ����:" + e.getLocalizedMessage());
	        }  
	    }
	}
	  

}
