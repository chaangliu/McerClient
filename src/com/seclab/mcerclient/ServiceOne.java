package com.seclab.mcerclient;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import com.seclab.mcerclient.rc4decryption.NewDec;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.view.WindowManager;
import android.widget.RemoteViews;
import android.widget.Toast;

public class ServiceOne extends Service {
	private NotificationManager nm;
	private Notification notification;
	private File tempFile = null;
	private boolean cancelUpdate = false;
	private MyHandler myHandler;
	private int download_precent = 0;
	private RemoteViews views;
	private int notificationId = 1234;

	// String address = intent.getStringExtra("url");
	String urll = null ;
	
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onStart(Intent intent, int startId) {
		super.onStart(intent, startId);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {

		// Toast.makeText(ServiceOne.this, "fuck", Toast.LENGTH_LONG).show();

		nm = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		notification = new Notification();
		notification.icon = android.R.drawable.stat_sys_download;
		// notification.icon=android.R.drawable.stat_sys_download_done;
		notification.tickerText = "��������..";
		notification.when = System.currentTimeMillis();
		notification.defaults = Notification.DEFAULT_LIGHTS;

		// ���������������ؽ�����ʾ��views
		views = new RemoteViews(getPackageName(), R.layout.update);
		notification.contentView = views;

		// PendingIntent contentIntent=PendingIntent.getActivity(this,0,new
		// Intent(this,ND.class),0);
		// notification.setLatestEventInfo(this,"","", contentIntent);

		// ������������ӵ���������
		nm.notify(notificationId, notification);

		myHandler = new MyHandler(Looper.myLooper(), this);

		// ��ʼ��������������views
		Message message = myHandler.obtainMessage(3, 0);
		myHandler.sendMessage(message);

		urll = intent.getStringExtra("url");//��ô�������������أ�ʵ�ڲ�����SharedPreferences
		// �����߳̿�ʼִ����������
		downFile(intent.getStringExtra("url"));// ������intent��getStringExra��������һ��activity��ֵ
		// downFile("http://192.168.23.1:8080/MobileUpload/one.avi");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	// ���ظ����ļ�
	private void downFile(final String url) {
		new Thread() {
			public void run() {
				try {
					HttpClient client = new DefaultHttpClient();
					// params[0]�������ӵ�url
					HttpGet get = new HttpGet(url);
					HttpResponse response = client.execute(get);
					HttpEntity entity = response.getEntity();
					long length = entity.getContentLength();
					InputStream is = entity.getContent();
					if (is != null) {
						File rootFile = new File(
								Environment.getExternalStorageDirectory(),
								"/Mcer");
						if (!rootFile.exists() && !rootFile.isDirectory())
							rootFile.mkdir();

						tempFile = new File(
								Environment.getExternalStorageDirectory(),

								"/Mcer/"
										+ url.substring(url.lastIndexOf("/") + 1));
						if (tempFile.exists())
							tempFile.delete();
						tempFile.createNewFile();


						// �Ѷ�������Ϊ��������һ�����л���������
						BufferedInputStream bis = new BufferedInputStream(is);

						// ����һ���µ�д����������ȡ����ͼ������д�뵽�ļ���
						FileOutputStream fos = new FileOutputStream(tempFile);
						// ��д������Ϊ��������һ�����л����д����
						BufferedOutputStream bos = new BufferedOutputStream(fos);

						int read;
						long count = 0;
						int precent = 0;
						byte[] buffer = new byte[1024];
						while ((read = bis.read(buffer)) != -1 && !cancelUpdate) {
							bos.write(buffer, 0, read);
							count += read;
							precent = (int) (((double) count / length) * 100);

							// ÿ�������5%��֪ͨ�����������޸����ؽ���
							if (precent - download_precent >= 5) {
								download_precent = precent;
								Message message = myHandler.obtainMessage(3,
										precent);
								myHandler.sendMessage(message);
							}
						}
						bos.flush();
						bos.close();
						fos.flush();
						fos.close();
						is.close();
						bis.close();
					}

					if (!cancelUpdate) {
						Message message = myHandler.obtainMessage(2, tempFile);
						myHandler.sendMessage(message);
					} else {
						tempFile.delete();
					}
				} catch (ClientProtocolException e) {
					Message message = myHandler.obtainMessage(4, "����ʧ��");
					myHandler.sendMessage(message);
				} catch (IOException e) {
					Message message = myHandler.obtainMessage(4, "����ʧ��");
					myHandler.sendMessage(message);
				} catch (Exception e) {
					Message message = myHandler.obtainMessage(4, "����ʧ��");
					myHandler.sendMessage(message);
				}
			}
		}.start();
	}

	// //��װ���غ��apk�ļ�
	// private void Instanll(File file,Context context){
	// Intent intent = new Intent(Intent.ACTION_VIEW);
	// intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
	// intent.setAction(android.content.Intent.ACTION_VIEW);
	// intent.setDataAndType(Uri.fromFile(file),"application/vnd.android.package-archive");
	// context.startActivity(intent);
	// }

	// ������ɺ��txt�ļ�
	private void openFile(File f, Context context) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		intent.setAction(android.content.Intent.ACTION_VIEW);

		/* ����getMIMEType()��ȡ��MimeType */
		String type = "text/plain";
		/* ����intent��file��MimeType */
		intent.setDataAndType(Uri.fromFile(f), type);
		context.startActivity(intent);
	}

	/* �¼������� */
	class MyHandler extends Handler {
		private Context context;
//		Intent intent = null ; 
//		String urll = intent.getStringExtra("url");
		
		
		public MyHandler(Looper looper, Context c) {
			super(looper);
			this.context = c;
		}

		@Override
		public void handleMessage(Message msg) {

			super.handleMessage(msg);// ���ø���Handler�ĳ�Ա�ĺ���handleMessage
			if (msg != null) {
				switch (msg.what) {
				case 0:
					Toast.makeText(context, msg.obj.toString(),
							Toast.LENGTH_SHORT).show();
					break;
				case 1:
					break;
				case 2:// ���سɹ�
					download_precent = 0;
					nm.cancel(notificationId);

					/* ���� */
					String toBeDecryptedPath = Environment
							.getExternalStorageDirectory() +

					"/Mcer/" + urll.substring(urll.lastIndexOf("/") + 1);
					String decryptedPath = Environment
							.getExternalStorageDirectory() +

					"/Mcer/dec/" + urll.substring(urll.lastIndexOf("/") + 1);//Ϊ�δ˴��ĳ�һ���Ѿ����ڵ�·���ͻᱨ���ܴ����ļ��������ļ���С��0B��
					Toast.makeText(context, toBeDecryptedPath, Toast.LENGTH_LONG).show();
//					// String toBeDecryptedPath =
					// Environment.getExternalStorageDirectory()+"/Mcer/"+
					// address.substring(address.lastIndexOf("/") + 1);
					// String decryptedPath =
					// Environment.getExternalStorageDirectory()+"/Mcer/decrypted/"+
					// address.substring(address.lastIndexOf("/") + 1);
					try {
						NewDec.start(toBeDecryptedPath, decryptedPath);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}


					final File msg1 = (File) msg.obj;
					Builder builder = new AlertDialog.Builder(ServiceOne.this);
					builder.setTitle("��ʾ");
					builder.setMessage("������ɣ��ļ������"
							+ Environment.getExternalStorageDirectory()
							+ "/Mcer/" + "������");
					builder.setPositiveButton("��",
							new AlertDialog.OnClickListener() {

								@Override
								public void onClick(DialogInterface dialog,
										int which) {
									openFile(msg1, context);
									// ֹͣ����ǰ�ķ���

								}
							});
					builder.setNegativeButton("�ݲ���", null);
					final AlertDialog dialog = builder.create();
					dialog.getWindow().setType(
							(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

					// // ������ɺ��������������Ϣ��ִ�а�װ��ʾ
					// download_precent = 0;
					// nm.cancel(notificationId);
					//
					// new
					// AlertDialog.Builder(ServiceOne.this).setTitle("��ʾ").setMessage("������ɣ��ļ������"+
					// Environment.getExternalStorageDirectory()+
					// "/Mcer/" + "������")
					// .setPositiveButton("��", new
					// AlertDialog.OnClickListener(){
					//
					// @Override
					// public void onClick(DialogInterface dialog, int which) {
					// openFile((File) msg.obj, context);
					// // ֹͣ����ǰ�ķ���
					// stopSelf();
					//
					// }}).setNegativeButton("�ݲ���", null).show();
					//

					// Instanll((File) msg.obj, context);
					dialog.show();
					stopSelf();
					break;
				case 3:

					// ����״̬���ϵ����ؽ�����Ϣ
					views.setTextViewText(R.id.update_tvProcess, "������"
							+ download_precent + "%");
					views.setProgressBar(R.id.update_pbDownload, 100,
							download_precent, false);
					notification.contentView = views;
					nm.notify(notificationId, notification);
					break;
				case 4:
					nm.cancel(notificationId);
					break;
				}
			}
		}
	}
}