package com.sczhuoshi.fiber.test;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.RemoteException;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.sczhuoshi.fiber.common.SysConvert;

@SuppressLint("HandlerLeak")
public class GetMachineCodeAct extends ASocketActivit {
	private static final String TAG = "GetMachineCodeAct";
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.get_machine_code_act);
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
	private static final int SEND_MSG_DELAY_TIME_150 = 150;
	private static final int SEND_MSG_DELAY_TIME_100 = 100;
	private static final int SEND_MSG_DELAY_TIME_80 = 80;
	private static final int SEND_MSG_DELAY_TIME_50 = 50;
	private static final int SEND_MSG_DELAY_TIME_30 = 30;
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
		switch (view.getId()) {
		case R.id.send:
			try {
				// �۽ӻ���ţ�
				// cmd:5a 4a 2 48 00 a5
				// ret:55 0E 48 53 31 35 30 34 31 37 30 30 37 AA
				// ���= S150417007
				iBackService.addToMessageBox("5A 4A 02 48 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// �����缫������
				// cmd:5a 4a 2 44 00 a5
				// ret:55 05 44 00 AA
				// ����=00
				iBackService.addToMessageBox("5A 4A 02 44 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// ��ǰ�۽Ӵ���
				// cmd:5a 4a 2 49 00 a5
				// ret:55 06 49 00 00 AA
				// �۽Ӵ���= 00 00
				iBackService.addToMessageBox("5A 4A 02 49 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// �汾�ţ�
				// cmd:5a 4a 2 c1 00 a5
				// ret:55 06 C1 8E 00 AA
				// ver: 0x8E = 142 /100 -> V1.42
				iBackService.addToMessageBox("5A 4A 02 C1 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// ���ͼ�������۽ӻ����磺12345678 �������붼Ϊ8���ַ���
				// cmd:5A CD 08 31 32 33 34 35 36 37 38 A5
				// ����ɹ�ret:55 06 CC 00 xx AA
				// ����ɹ�ret:
				// 55 06 CC 01 xx AA
				/*���뻻�缫�Ĵ���*/
				/*B4���� ���缫�Ĵ���*/
				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB5),SEND_MSG_DELAY_TIME_80); /*���缫�Ĵ���*/
				
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
			break;
		default:
			break;
		}
	}
}