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
	 * 发送命令
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
				// 熔接机编号：
				// cmd:5a 4a 2 48 00 a5
				// ret:55 0E 48 53 31 35 30 34 31 37 30 30 37 AA
				// 编号= S150417007
				iBackService.addToMessageBox("5A 4A 02 48 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// 更换电极次数：
				// cmd:5a 4a 2 44 00 a5
				// ret:55 05 44 00 AA
				// 次数=00
				iBackService.addToMessageBox("5A 4A 02 44 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// 当前熔接次数
				// cmd:5a 4a 2 49 00 a5
				// ret:55 06 49 00 00 AA
				// 熔接次数= 00 00
				iBackService.addToMessageBox("5A 4A 02 49 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// 版本号：
				// cmd:5a 4a 2 c1 00 a5
				// ret:55 06 C1 8E 00 AA
				// ver: 0x8E = 142 /100 -> V1.42
				iBackService.addToMessageBox("5A 4A 02 C1 00 A5", SEND_MSG_DELAY_TIME_80); /**/
				// 发送激活码给熔接机：如：12345678 （激活码都为8个字符）
				// cmd:5A CD 08 31 32 33 34 35 36 37 38 A5
				// 激活不成功ret:55 06 CC 00 xx AA
				// 激活成功ret:
				// 55 06 CC 01 xx AA
				/*放入换电极的次数*/
				/*B4存入 换电极的次数*/
				iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB5),SEND_MSG_DELAY_TIME_80); /*换电极的次数*/
				
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