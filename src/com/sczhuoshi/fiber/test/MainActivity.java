package com.sczhuoshi.fiber.test;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sczhuoshi.fiber.common.SysConvert;

@SuppressLint("HandlerLeak")
public class MainActivity extends ASocketActivit {
	private static final String TAG = "MainActivity";

	private TextView mResultText;
	private EditText mEditText_2;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mResultText = (TextView) findViewById(R.id.resule_text);
		mEditText_2 = (EditText) findViewById(R.id.content_edit_2);
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
//		android.os.Process.killProcess(android.os.Process.myPid());
	}
	
	/**
	 * ��������
	 * @param cmd
	 * @param param1
	 */ 
	public static String send_cmd_toByteArr(int cmd, int param1) {
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };

		a[3] = (byte) cmd;
		a[4] = (byte) param1;
		String ns = "";
		for (int i = 0; i < a.length; i++) {
			String hex = Integer.toHexString(a[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ns = ns + hex;
		}
		return ns;
	}
	
	private static final int SEND_MSG_DELAY_TIME_180 = 180;
	private static final int SEND_MSG_DELAY_TIME_100 = 100;
	private static final int SEND_MSG_DELAY_TIME_10 = 10;
	
	public static String send_cmd_writeByte(int addr, int para1){
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, (byte) 0xD9, 0x00, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		a[4] = (byte) addr;
		a[6] = (byte) para1;
//		a[7] = (byte) (para1 >> 8);
		String ns = "";
		for (int i = 0; i < a.length; i++) {
			String hex = Integer.toHexString(a[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ns = ns + hex;
		}
		return ns;
	}
	
	public void onClick(View view) {
		String content_2 = mEditText_2.getText().toString();

		switch (view.getId()) {
		case R.id.send:
			int SEND_MSG_DELAY_TIME_30 = 30;
			try {
//				iBackService.addToMessageBox("5A4A024800A5",SEND_MSG_DELAY_TIME_10);
//				iBackService.addToMessageBox("5A4A024400A5",SEND_MSG_DELAY_TIME_10);
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB5),SEND_MSG_DELAY_TIME_10);
//				int value = 2;
//				int resultValue = SysConvert.inverseCode(value + 1);
//				String cmd = SysConvert.send_cmd_writeByte(0xB5, resultValue);
//				iBackService.addToMessageBox(cmd,SEND_MSG_DELAY_TIME_180);
//				iBackService.addToMessageBox(SysConvert.send_cmd_toByteArr(0x71,3),SEND_MSG_DELAY_TIME_180); /*����*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB5),SEND_MSG_DELAY_TIME_10);
				
				// �۽ӻ���ţ�
				// cmd:5a 4a 2 48 00 a5
				// ret:55 0E 48 53 31 35 30 34 31 37 30 30 37 AA
				// ���= S150417007
				iBackService.addToMessageBox("5A 4A 02 48 00 A5", SEND_MSG_DELAY_TIME_100); /**/
				// �����缫������
				// cmd:5a 4a 2 44 00 a5
				// ret:55 05 44 00 AA
				// ����=00
				iBackService.addToMessageBox("5A 4A 02 44 00 A5", SEND_MSG_DELAY_TIME_100); /**/
				// ��ǰ�۽Ӵ���
				// cmd:5a 4a 2 49 00 a5
				// ret:55 06 49 00 00 AA
				// �۽Ӵ���= 00 00
				iBackService.addToMessageBox("5A 4A 02 49 00 A5", SEND_MSG_DELAY_TIME_100); /**/
				// �汾�ţ�
				// cmd:5a 4a 2 c1 00 a5
				// ret:55 06 C1 8E 00 AA
				// ver: 0x8E = 142 /100 -> V1.42
				iBackService.addToMessageBox("5A 4A 02 C1 00 A5", SEND_MSG_DELAY_TIME_100); /**/
				// ���ͼ�������۽ӻ����磺12345678 �������붼Ϊ8���ַ���
				// cmd:5A CD 08 31 32 33 34 35 36 37 38 A5
				// ����ɹ�ret:55 06 CC 00 xx AA
				// ����ɹ�ret:
				// 55 06 CC 01 xx AA
				/*���뻻�缫�Ĵ���*/
				/*B4���� ���缫�Ĵ���*/
				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB5),10); /*���缫�Ĵ���*/
				
				
//				05-25 09:47:08.036: I/BackService(25501): ���͵�����--->>>>>
//				05-25 09:47:09.880: I/BackService(25501): ���͵�����--->>>>>5a0a83d8b500000000a5
//				05-25 09:47:10.001: I/BackService(25501): ���͵�����--->>>>>5a0a83d9b500fc0000a5
//				05-25 09:47:10.121: I/BackService(25501): ���͵�����--->>>>>5a0a83710300000000a5
//				05-25 09:47:10.242: I/BackService(25501): ���͵�����--->>>>>5a0a83d8b500000000a5
//				05-25 09:47:10.443: I/BackService(25501): ----�յ�������---> rawData 55,0a,d8,b5,07,fc,00,00,00,aa,
//				05-25 09:47:10.474: I/BackService(25501): ----�յ�������---> rawData 55,0a,d8,b5,07,fc,00,00,00,aa,


//				iBackService.addToMessageBox("5acd084b3336375736304ca5",SEND_MSG_DELAY_TIME_180);
//				iBackService.addToMessageBox("5a0a83940100000000a5",SEND_MSG_DELAY_TIME_180);
//				iBackService.addToMessageBox("5a0a83940100000000a5",SEND_MSG_DELAY_TIME_180);
//				iBackService.addToMessageBox("5a0a83d9b500fd0000a5",SEND_MSG_DELAY_TIME_180);
				
//				iBackService.addToMessageBox(SysConvert.send_cmd_toByteArr(0x71,3),SEND_MSG_DELAY_TIME_180); /*����*/

//				iBackService.addToMessageBox("5A4A24800A5",SEND_MSG_DELAY_TIME_10); // �۽ӻ����
//				iBackService.addToMessageBox("5A4A24400A5",SEND_MSG_DELAY_TIME_10); // �����缫����
//				iBackService.addToMessageBox("5A4A24900A5",SEND_MSG_DELAY_TIME_10); // ��ǰ�۽Ӵ���
//				iBackService.addToMessageBox("5A4A2C100A5",SEND_MSG_DELAY_TIME_10); // �汾��
//				SysConvert.readBaseParameter(iBackService);
//				iBackService.addToMessageBox(send_cmd_toByteArr(0x7D,00),SEND_MSG_DELAY_TIME_10);

//				SysConvert.readParameter(iBackService);
				// SysConvert.WriteDefaultParameter(iBackService);

//				/*���� �ƽ����S0 S3*/		
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA0)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA1)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA2)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA3)); /**/
//				/*���� ���ڵ��S0 S3*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA4)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA5)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA6)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA7)); /**/
//				/*���ϵ��*/		
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA8)); /*���⴦���С����ȥ��*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xA9)); /*���⴦���С����ȥ��*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xAA)); /*���⴦���С����ȥ��*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xAB)); /*���⴦���С����ȥ��*/
//				/*���⴦����ȡ��������Ҫ������0*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xAC)); /*��ȡ��������Ҫȥ������0*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xAD)); /*��ȡ��������Ҫȥ������0*/
//				
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xAE)); /**/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB0)); /*���⴦���С����ȥ��*/
//				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB1)); /*�ص���*/
				
				//5A 0A 83 D1 00 00 00 00 00 A5
				//5A 0A 83 D2 00 00 00 00 00 A5
				//5A 0A 83 D3 00 00 00 00 00 A5
//					iBackService.addToMessageBox("5A 0A 83 D0 00 00 00 00 00 A5");
//					iBackService.addToMessageBox("5A 0A 83 D1 00 00 00 00 00 A5");
//					iBackService.addToMessageBox("5A 0A 83 D2 00 00 00 00 00 A5");
//				boolean isAdd_1 = iBackService.addToMessageBox("5a0a837d0000000000a5");
//				boolean isAdd_2 = iBackService.addToMessageBox("5A 0A 83 D1 00 00 00 00 00 A5");
//				boolean isAdd_3 = iBackService.addToMessageBox("5A 0A 83 D2 00 00 00 00 00 A5");
//				boolean isAdd_3 = iBackService.addToMessageBox("5A 0A 83 D3 00 00 00 00 00 A5");
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}

//			for (int i = 0; i < 10; i++) {
//				try {
//					boolean isAdd = iBackService.addToMessageBox(content);//Send Content by socket
//					Toast.makeText(this, isAdd ? "success" : "fail", Toast.LENGTH_SHORT).show();
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
			break;
		case R.id.send_2:
			for (int i = 0; i < 10; i++) {
				try {
					boolean isAdd = iBackService.addToMessageBox(content_2,180);//Send Content by socket
					Toast.makeText(this, isAdd ? "success" : "fail", Toast.LENGTH_SHORT).show();
				} catch (RemoteException e) {
					e.printStackTrace();
				}
			}
			break;
		case R.id.send_3:
//			for (int i = 0; i < 30; i++) {
//				try {
//					boolean isAdd1 = iBackService.addToMessageBox(content);//Send Content by socket
//					boolean isAdd2 = iBackService.addToMessageBox("5A 0A 83 45 08 00 00 00 00 A5");//Send Content by socket
//					boolean isAdd3 = iBackService.addToMessageBox(content);//Send Content by socket
//				} catch (RemoteException e) {
//					e.printStackTrace();
//				}
//			}
			break;
		case R.id.send_4:
			Intent intent = new Intent(MainActivity.this, GetMachineCodeAct.class);
			startActivity(intent);
			break;
		default:
			break;
		}
	}
}