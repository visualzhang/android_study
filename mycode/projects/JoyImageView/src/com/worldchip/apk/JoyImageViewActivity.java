package com.worldchip.apk;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;

import com.worldchip.apk.info.SMTPClient;
import com.worldchip.apk.info.UniqId;
import com.worldchip.apk.up.Config;
import com.worldchip.apk.up.NetworkTool;
import com.worldchip.apk.up.UpdateActivity;

public class JoyImageViewActivity extends ListActivity {
    protected static final String TAG = "JoyImageViewActivity";
    private static final String PATH = "/sdcard/.thumbnails";// "/flash/.thumbnails/";
    private List<String> items = null;
    private List<String> paths = null;
    final private String rootPath = "/";
    private DBAdapter dbAdapter = null;
    private MyListAdapter listAdapter;
    // private final String INTERNAL="/sdcard/";
    // private final String USBHOST="/usbhost/";
    private LinkedList<String> extens = null;
    int delay = 40; // Milliseconds of delay in the update loop
    int maxBarValue = 10000;
    int typeBar = 0; // Determines type progress bar: 0 = spinner, 1 =
    // horizontal

    ProgressThread progThread;
    ProgressDialog progDialog;
    UniqId uId;
    String MACHINE_ID_KEY = "machine_unique_id_key";
    String MACHINE_TIME_KEY = "machine_time_key";
    private final int UPDATE_OR_NOT = 1;
    private final int UPDATE_NOT_NEED = 2;
    private final long APP_INFO_INTERVAL = 7*24*36000;
    public ProgressDialog pBar;
    SharedPreferences mPrefs;
    Thread appInfoThread = new Thread(new Runnable() {
		
		public void run() {
			handleAppInfo();
		}
	});
    Thread updatThread = new Thread(new Runnable() {
		
		public void run() {
			Looper.prepare();			
			updateNewRelease();
		}
	});
    
    @SuppressLint("HandlerLeak")
	Handler updateHandler = new Handler(){
    	public void handleMessage(Message msg) {
    		switch (msg.what) {
			case UPDATE_OR_NOT:
				Bundle bundle = msg.getData();
				Dialog dialog = new AlertDialog.Builder(JoyImageViewActivity.this)
				.setTitle("软件更新")
				.setMessage(bundle.getString("msg"))
				// 设置内容
				.setPositiveButton("更新",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								pBar = new ProgressDialog(JoyImageViewActivity.this);
								pBar.setTitle("正在下载");
								pBar.setMessage("请稍候...");
								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
								downFile(Config.UPDATE_SERVER
										+ Config.UPDATE_APKNAME);
							}

						})
				.setNegativeButton("暂不更新",
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,
									int whichButton) {
								// 点击"取消"按钮之后退出程序
								//finish();
							}
						}).create();// 创建
				// 显示对话框
				dialog.show();
				break;
			case UPDATE_NOT_NEED:
				Bundle bundle1 = msg.getData();
	    		Dialog dialog1 = new AlertDialog.Builder(JoyImageViewActivity.this)
				.setTitle("软件更新").setMessage(bundle1.getString("msg"))// 设置内容
				.setPositiveButton("确定",// 设置确定按钮
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog,int which) {
								//finish();
							}
						}).create();// 创建
				// 显示对话框
				dialog1.show();
				break;
			default:
				break;
			}
    	};
    };
    @Override
    protected void onCreate(Bundle icicle) {
        super.onCreate(icicle);
        setTitle(R.string.welcome);
        setContentView(R.layout.main);
        getRootView(rootPath);
        appInfoThread.start();
        updatThread.start();
    }

    private void handleAppInfo() {
        //generate the unique id for this app
        mPrefs = PreferenceManager.getDefaultSharedPreferences(this);
        String uIdString = mPrefs.getString(MACHINE_ID_KEY, "NULL");
        Editor editor = mPrefs.edit();
        if (uIdString.equals("NULL")) {
            uId = UniqId.getInstance();
            String str = uId.getUniqIDHashString();
            editor.putString(MACHINE_ID_KEY, str);//write the id to the shared preferences
            editor.commit();
        }
        //append the last log to the file
        appendLog();
        long now = System.currentTimeMillis();
        long before = mPrefs.getLong(MACHINE_TIME_KEY, 0);
        if (now - before < APP_INFO_INTERVAL) return;
        //put the last time to the share preference
        Editor editor2 = mPrefs.edit();
        editor2.putLong(MACHINE_TIME_KEY, System.currentTimeMillis());
        editor2.commit();
        String info = getLog();//get all the info from the log file
        SMTPClient.sendAppInfo(info,uIdString);
    }
    private void appendLog() {
        try {
            FileOutputStream fos = openFileOutput("image.log", MODE_APPEND);
            String log = mPrefs.getString(MACHINE_ID_KEY, "NULL");
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss",Locale.getDefault());//设置日期格式
            String date = df.format(new Date(System.currentTimeMillis()));
            log = log + " " + date+"\n";
            fos.write(log.getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private String getLog() {
        String log = "";
        try {
            FileInputStream fis = openFileInput("image.log");
            int b;
            while (-1 != (b = fis.read())) {
                log = log + String.format("%c", b);
            }
            fis.close();
            return log;
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return log;
    }
    @Override
    protected void onResume() {
        // 当删除了一些文件以后，数据库还是会存在其缓存路径，这里发送消息，MediaScannerReceiver接受到消息以后
        // 会对数据库文件进行更新——会出现加载不到一些图片
        // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(
        // "file://"
        // + Environment.getExternalStorageDirectory())));
        // }
        super.onResume();
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
        case KeyEvent.KEYCODE_BACK:// 处理掉 MENU,不让系统接受
            // System.exit(0);
            JoyImageViewActivity.this.finish();
            return true;
        default:
            break;
        }
        return super.onKeyUp(keyCode, event);
        // return false;
    }
    
    @Override
    protected void onStart() {
    	// TODO Auto-generated method stub
    	super.onStart();
    	registerSDReceiver();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
        unregisterReceiver(sdReceiver);//使用完注销广播监听函数
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();

        if (progThread != null)
            progThread.setState(ProgressThread.DONE);
        if (dbAdapter != null)
            dbAdapter.close();
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        switch (id) {
        case 0: // Spinner
            progDialog = new ProgressDialog(this);
            // progDialog.setCancelable(false); //防止客户按“返回”键
            progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
            progDialog.setMessage(getText(R.string.scan));
            progThread = new ProgressThread(handler);
            progThread.setState(ProgressThread.RUNNING);
            progThread.start();
            return progDialog;
        case 1: // Horizontal
            progDialog = new ProgressDialog(this);
            // progDialog.setCancelable(false);
            progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
            progDialog.setMax(maxBarValue);
            progDialog.setMessage("Dollars in checking account:");
            progThread = new ProgressThread(handler);
            progThread.start();
            return progDialog;
        default:
            return null;
        }
    }

    //自己写一个广播监听函数
    private final MySdBroadcastReceiver sdReceiver =  new MySdBroadcastReceiver();
    public class MySdBroadcastReceiver extends BroadcastReceiver
    {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED"))//SD卡已经成功挂载
            {
            	if( intent.getData().toString().contains("sdcard")){
	            	items.add("sdcard");
	            	paths.add(Common.SD_CARD_PATH);
	            	listAdapter.notifyDataSetChanged();
            	}
            }

            if (intent.getAction().equals("android.intent.action.MEDIA_REMOVED")//各种未挂载状态
                    ||intent.getAction().equals("android.intent.action.ACTION_MEDIA_UNMOUNTED")
                    ||intent.getAction().equals("android.intent.action.MEDIA_UNMOUNTED")
                    ||intent.getAction().equals("android.intent.action.ACTION_MEDIA_BAD_REMOVAL"))
            {
            	if( intent.getData().toString().contains("sdcard")){
	            	items.remove("sdcard");
	            	paths.remove(Common.SD_CARD_PATH);
	            	listAdapter.notifyDataSetChanged();
            	}
            }
        }
    }

    public void registerSDReceiver() {
        //在IntentFilter中选择你要监听的行为
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);

        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);

        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);

        //intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);

        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);

        //intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);

        //intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);

        intentFilter.addDataScheme("file");

        registerReceiver(sdReceiver, intentFilter);//注册监听函数
    }

    private boolean IsSDCardExist() {
    	ProcessBuilder builder = new ProcessBuilder("df");  
        Process process;
        String result = "";
		try {
			process = builder.start ( );
			InputStream is = process.getInputStream ( ) ;  
	        byte[] buffer = new byte[1024] ;
	        while ( is.read(buffer) != -1  ) {  
	        	result = result + new String (buffer) ;  
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
		return result.contains("/sdcard/external_sdcard");
		/*
        if (android.os.Environment.getExternalStorageState().equals(
                    android.os.Environment.MEDIA_MOUNTED)) {
            return true;
        } else {
            return false;
        }
        */
    }

    private void getRootView(String filePath) {
        items = new ArrayList<String>();
        paths = new ArrayList<String>();
        items.add("internal");
        paths.add(Common.INTERNAL_MEMORY_PATH);
        if (IsSDCardExist()) {
            items.add("sdcard");
            paths.add(Common.SD_CARD_PATH);
        }

        // items.add("usbhost");
        // paths.add("/usbhost/");

        // items.add("update");
        // paths.add("update");
        listAdapter = new MyListAdapter(this, items, paths);
        setListAdapter(listAdapter);
        listAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        String path = paths.get(position).toString();
        Log.i("onListItemClick", "path=" + path);
        // sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse(
        // "file://"
        // + Environment.getExternalStorageDirectory())));
        if (path.equals("update")) // 重新扫描
        {
            updateImages();
        } else {
            /*
             * Intent intent = new Intent();
             * intent.setClass(JoyImageViewActivity.this, ImageListView.class);
             * intent.putExtra("path",path);
             * JoyImageViewActivity.this.startActivity(intent);
             */

            Intent intent = new Intent();
            intent.setClass(JoyImageViewActivity.this, ImageGalleryView.class);
            intent.putExtra("path", path);
            // intent.putExtra("id", 0);
            // List list=bitmaps.get(position).tag;
            // intent.putExtra("data", (String[])list.toArray(new
            // String[list.size()]));
            // Log.i("ImageGridView_setOnItemClickListener",
            // "position="+position+"; path="+path);
            JoyImageViewActivity.this.startActivity(intent);
            this.finish();
        }
    }

    private void updateImages() {
        typeBar = 0;
        dbAdapter = new DBAdapter(this);
        new AlertDialog.Builder(JoyImageViewActivity.this)
        .setIcon(R.drawable.icon)
        .setTitle(R.string.redue)
        .setMessage(R.string.scan_redue)
        .setPositiveButton(R.string.ok,
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
            int whichButton) {
                showDialog(typeBar);
            }
        })
        .setNegativeButton(R.string.cancel,
        new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog,
            int whichButton) {

                /* User clicked OK so do some stuff */
            }
        }).show();
    }

    public void getExtens() {
        extens.add(".JPEG");
        extens.add(".JPG");
        extens.add(".PNG");
        extens.add(".GIF");
        extens.add(".BMP");
    }

    public void saveImageFile(File file) {
        Log.i(TAG + "_before accept", "code comes to saveImageFile, the name="
              + file.getName() + "the path=" + file.getAbsolutePath());
        file.listFiles(new FileFilter() {
            public boolean accept(File file) {
                String name = file.getName();
                int i = name.lastIndexOf('.');
                if (i != -1) {
                    name = name.substring(i).toUpperCase();
                    if (extens.contains(name)) {
                        Log.i(TAG + "_accept-file",
                              "for ready to savePicture! name="
                              + file.getName() + "; the path="
                              + file.getAbsolutePath());

                        savePicture(file);
                        return true;
                    }
                } else if (file.isDirectory()) {
                    saveImageFile(file);
                }
                return false;
            }

            private void savePicture(File file) {
                // TODO Auto-generated method stub
                String name = file.getName();
                String album = file.getParent();
                String path = file.getAbsolutePath();
                Log.i(TAG + "_savePicture", "the name=" + name
                      + "; the parent=" + album + "; the path=" + path);

                album = album.substring(album.lastIndexOf("/") + 1);
                Log.i(TAG, "after sub, the album=" + album);

                // Thumbnail Picture
                Bitmap bitmap = ImageCommon.getFitSizePicture(file);
                if (bitmap == null) {
                    Resources res = getResources();
                    bitmap = BitmapFactory.decodeResource(res, R.drawable.icon);
                }
                long rowId = dbAdapter.insertImage(name, album, path, bitmap);

                Log.i(TAG, "after inserted the rowid=" + rowId);
                if (rowId == -1) {
                    Log.i(TAG, "insert new image has err!");
                    dbAdapter.close();
                }
            }
        }                      );
    }

    // Handler on the main (UI) thread that will receive messages from the
    // second thread and update the progress.

    final Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            // Get the current value of the variable total from the message data
            // and update the progress bar.
            int total = msg.getData().getInt("total");
            Log.i(TAG, "Handler total=" + total);
            // progDialog.setProgress(total);
            if (total == 1) {
                Log.i(TAG,
                      "dismissDialog, the dialog will shutdown! the tatal="
                      + total);
                dismissDialog(typeBar);
                progThread.setState(ProgressThread.DONE);
            }
        }
    };

    // Inner class that performs progress calculations on a second thread.
    // Implement
    // the thread by subclassing Thread and overriding its run() method. Also
    // provide
    // a setState(state) method to stop the thread gracefully.

    private class ProgressThread extends Thread {

        // Class constants defining state of the thread
        final static int DONE = 0;
        final static int RUNNING = 1;

        Handler mHandler;
        int mState;
        int total;

        // Constructor with an argument that specifies Handler on main thread
        // to which messages will be sent by this thread.

        ProgressThread(Handler h) {
            mHandler = h;
        }

        // Override the run() method that will be invoked automatically when
        // the Thread starts. Do the work required to update the progress bar on
        // this
        // thread but send a message to the Handler on the main UI thread to
        // actually
        // change the visual representation of the progress. In this example we
        // count
        // the index total down to zero, so the horizontal progress bar will
        // start full and
        // count down.

        @Override
        public void run() {
            Log.i(TAG, "coming run again!");
            // mState = RUNNING;
            if (mState == RUNNING) {
                // The method Thread.sleep throws an InterruptedException if
                // Thread.interrupt()
                // were to be issued while thread is sleeping; the exception
                // must be caught.
                try {
                    // Control speed of update (but precision of delay not
                    // guaranteed)
                    total = 0;
                    if (dbAdapter == null) {
                        total = 1;
                        Thread.sleep(delay);
                    } else {
                        dbAdapter.open();
                        dbAdapter.deleteAllImage();

                        extens = new LinkedList<String>();
                        getExtens();

                        Log.i(TAG, "create new dir");
                        File dir = new File(PATH);
                        if (dir.exists()) {
                            dir.delete();
                        }
                        dir.mkdir();

                        File file = new File(Common.INTERNAL_MEMORY_PATH);
                        saveImageFile(file);
                        dbAdapter.close();

                        total = 1;
                        Thread.sleep(delay);
                    }
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread was Interrupted");
                }

                // Send message (with current value of total as data) to Handler
                // on UI thread
                // so that it can update the progress bar.

                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                b.putInt("total", total);
                msg.setData(b);
                mHandler.sendMessage(msg);

                // total--; // Count down
            } else {
                // 结束当前进程
                // dbAdapter.close();
                Log.i(TAG, "Thread interrupt!");
                Thread.currentThread().interrupt();
            }
        }

        // Set current state of thread (use state=ProgressThread.DONE to stop
        // thread)
        public void setState(int state) {
            mState = state;
        }
    }
    //public class Update{
    //	private static final String TAG = "Update";
    //	public ProgressDialog pBar;
    //	private Handler handler = new Handler();

    	private int newVerCode = 0;
    	private String newVerName = "";

    	//@Override
    	//protected void onCreate(Bundle savedInstanceState) {
    	//	super.onCreate(savedInstanceState);
    		//setContentView(R.layout.main);
    	public void updateNewRelease(){	
    		if (getServerVerCode()) {
    			int vercode = Config.getVerCode(JoyImageViewActivity.this);
    			if (newVerCode > vercode) {
    				doNewVersionUpdate();
    			} else {
    				return;
    				//notNewVersionShow();
    			}
    		}

    	}

    	private boolean getServerVerCode() {
    		try {
    			String verjson = NetworkTool.getContent(Config.UPDATE_SERVER
    					+ Config.UPDATE_VERJSON);
    			JSONArray array = new JSONArray(verjson);
    			if (array.length() > 0) {
    				JSONObject obj = array.getJSONObject(0);
    				try {
    					newVerCode = Integer.parseInt(obj.getString("verCode"));
    					newVerName = obj.getString("verName");
    				} catch (Exception e) {
    					newVerCode = -1;
    					newVerName = "";
    					return false;
    				}
    			}
    		} catch (Exception e) {
    			Log.e(TAG, e.getMessage());
    			return false;
    		}
    		return true;
    	}

    	private void notNewVersionShow() {
    		int verCode = Config.getVerCode(JoyImageViewActivity.this);
    		String verName = Config.getVerName(JoyImageViewActivity.this);
    		StringBuffer sb = new StringBuffer();
    		sb.append("当前版本:");
    		sb.append(verName);
    		//sb.append(" Code:");
    		//sb.append(verCode);
    		sb.append(",\n已是最新版,无需更新!");
    		Message msg = updateHandler.obtainMessage();
    		msg.what = UPDATE_NOT_NEED;
    		Bundle bundle = new Bundle();
    		bundle.putString("msg", sb.toString());
    		msg.setData(bundle);
    		msg.sendToTarget();
    		/*
    		Dialog dialog = new AlertDialog.Builder(JoyImageViewActivity.this)
    				.setTitle("软件更新").setMessage(sb.toString())// 设置内容
    				.setPositiveButton("确定",// 设置确定按钮
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,int which) {
    								finish();
    							}
    						}).create();// 创建
    		// 显示对话框
    		dialog.show();
    		*/
    	}

    	private void doNewVersionUpdate() {
    		int verCode = Config.getVerCode(JoyImageViewActivity.this);
    		String verName = Config.getVerName(JoyImageViewActivity.this);
    		StringBuffer sb = new StringBuffer();
    		sb.append("当前版本:");
    		sb.append(verName);
    		//sb.append(" Code:");
    		//sb.append(verCode);
    		sb.append(", 发现新版本:");
    		sb.append(newVerName);
    		//sb.append(" Code:");
    		//sb.append(newVerCode);
    		sb.append(", 是否更新?");
    		Message msg = updateHandler.obtainMessage();
    		msg.what = UPDATE_OR_NOT;
    		Bundle bundle = new Bundle();
    		bundle.putString("msg", sb.toString());
    		msg.setData(bundle);
    		msg.sendToTarget();
    		/*
    		Dialog dialog = new AlertDialog.Builder(JoyImageViewActivity.this)
    				.setTitle("软件更新")
    				.setMessage(sb.toString())
    				// 设置内容
    				.setPositiveButton("更新",// 设置确定按钮
    						new DialogInterface.OnClickListener() {

    							public void onClick(DialogInterface dialog,int which) {
    								pBar = new ProgressDialog(JoyImageViewActivity.this);
    								pBar.setTitle("正在下载");
    								pBar.setMessage("请稍候...");
    								pBar.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    								downFile(Config.UPDATE_SERVER
    										+ Config.UPDATE_APKNAME);
    							}

    						})
    				.setNegativeButton("暂不更新",
    						new DialogInterface.OnClickListener() {
    							public void onClick(DialogInterface dialog,
    									int whichButton) {
    								// 点击"取消"按钮之后退出程序
    								finish();
    							}
    						}).create();// 创建
    		// 显示对话框
    		dialog.show();
    		*/
    	}

    	void downFile(final String url) {
    		pBar.show();
    		new Thread() {
    			public void run() {
    				HttpClient client = new DefaultHttpClient();
    				HttpGet get = new HttpGet(url);
    				HttpResponse response;
    				try {
    					response = client.execute(get);
    					HttpEntity entity = response.getEntity();
    					long length = entity.getContentLength();
    					InputStream is = entity.getContent();
    					FileOutputStream fileOutputStream = null;
    					if (is != null) {

    						File file = new File(
    								Environment.getExternalStorageDirectory(),
    								Config.UPDATE_SAVENAME);
    						fileOutputStream = new FileOutputStream(file);

    						byte[] buf = new byte[1024];
    						int ch = -1;
    						int count = 0;
    						while ((ch = is.read(buf)) != -1) {
    							fileOutputStream.write(buf, 0, ch);
    							count += ch;
    							if (length > 0) {
    							}
    						}

    					}
    					fileOutputStream.flush();
    					if (fileOutputStream != null) {
    						fileOutputStream.close();
    					}
    					down();
    				} catch (ClientProtocolException e) {
    					e.printStackTrace();
    				} catch (IOException e) {
    					e.printStackTrace();
    				}
    			}

    		}.start();

    	}

    	void down() {
    		handler.post(new Runnable() {
    			public void run() {
    				pBar.cancel();
    				update();
    			}
    		});

    	}

    	void update() {

    		Intent intent = new Intent(Intent.ACTION_VIEW);
    		intent.setDataAndType(Uri.fromFile(new File(Environment
    				.getExternalStorageDirectory(), Config.UPDATE_SAVENAME)),
    				"application/vnd.android.package-archive");
    		startActivity(intent);
    	}

    //}
}

