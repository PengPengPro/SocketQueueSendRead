package com.sczhuoshi.fiber.common;

import java.math.BigInteger;
import java.util.Locale;

import android.os.RemoteException;

import com.sczhuoshi.fiber.server.BackService;
import com.sczhuoshi.fiber.server.IBackServiceTest;

public class SysConvert {
	private final static String TAG = "SysConvert";
	/* 左，�? 推进电机S0 S3 */
	private final static String left_push_S0_raw = "2";
	private final static String left_push_S3_raw = "10";
	private final static String right_push_S0_raw = "2";
	private final static String right_push_S3_raw = "10";
	/* 左，�? 调节电机S0 S3 */
	private final static String left_adjust_S0_raw = "4";
	private final static String left_adjust_S3_raw = "18";
	private final static String right_adjust_S0_raw = "4";
	private final static String right_adjust_S3_raw = "18";
	/* 电机系数ratio */
	private final static String left_push_ratio_raw = "1";
	private final static String right_push_ratio_raw = "1";
	private final static String left_adjust_ratio_raw = "1.8";
	private final static String right_adjust_ratio_raw = "1.8";
	/* �?大推进距�? */
	private static String left_push_max_length_raw = "280";
	private static String right_push_max_length_raw = "280";
	/* 熔接推进参数 */
	private final static String welding_push_parameter_V_raw = "8";
	private final static String welding_parameter_ratio_raw = "1";
	private final static String welding_parameter_overlap_raw = "23";
	/*放电宽度*/
	private final static String correction_1_raw_ = "114";
	private final static String correction_2_raw_ = "140";
	
	/*发�?�消息间隔时�? 180*/
	private static final int SEND_MSG_DELAY_TIME_30 = 30;
	private static final int SEND_MSG_DELAY_TIME_50 = 50;
	private static final int SEND_MSG_DELAY_TIME_180 = 180;
	
	/**
	 * 读取机器信息
	 * @param iBackService
	 * @throws RemoteException
	 */
	public static void readBaseParameter(IBackServiceTest iBackService) throws RemoteException {
		// 熔接机编号：
		// cmd:5a 4a 2 48 00 a5
		// ret:55 0E 48 53 31 35 30 34 31 37 30 30 37 AA
		// 编号= S150417007
		iBackService.addToMessageBox("5A 4A 02 48 00 A5", SEND_MSG_DELAY_TIME_50); /**/
		// 更换电极次数�?
		// cmd:5a 4a 2 44 00 a5
		// ret:55 05 44 00 AA
		// 次数=00
		iBackService.addToMessageBox("5A 4A 02 44 00 A5", SEND_MSG_DELAY_TIME_50); /**/
		// 当前熔接次数
		// cmd:5a 4a 2 49 00 a5
		// ret:55 06 49 00 00 AA
		// 熔接次数= 00 00
		iBackService.addToMessageBox("5A 4A 02 49 00 A5", SEND_MSG_DELAY_TIME_50); /**/
		// 版本号：
		// cmd:5a 4a 2 c1 00 a5
		// ret:55 06 C1 8E 00 AA
		// ver: 0x8E = 142 /100 -> V1.42
		iBackService.addToMessageBox("5A 4A 02 C1 00 A5", SEND_MSG_DELAY_TIME_50); /**/
		// 发�?�激活码给熔接机：如�?12345678 （激活码都为8个字符）
		// cmd:5A CD 08 31 32 33 34 35 36 37 38 A5
		// �?活不成功ret:55 06 CC 00 xx AA
		// �?活成功ret:
		// 55 06 CC 01 xx AA
		/*放入换电极的次数*/
		/*B4存入 换电极的次数*/
		iBackService.addToMessageBox(SysConvert.send_cmd_readByte(0xB5),10); /*换电极的次数*/
		/*写入B5*/
		// iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xB5, value),SEND_MSG_DELAY_TIME_180); /**/	
	}
	
	/**
	 * 求反�?
	 * @throws RemoteException
	 */
	public static int inverseCode(int value) throws RemoteException {
        String result = Integer.toBinaryString(value);
        
        String temp = "";
        for (int i = 0; i < "00000001".length() - result.length(); i++) {
            temp += "0";
		}
        result = temp + result; 
		char[] datas = result.toCharArray();
		int [] flag = new int[datas.length];
		for (int i = 0; i < datas.length; i++) {
			if(datas[i] == '0'){
				flag[i] = -1;
			} else if(datas[i] == '1'){
				flag[i] = -2;
			}
		}
		
		for (int i = 0; i < flag.length; i++) {
			if(flag[i] == -1){
				datas[i] = '1';
			} else if(flag[i] == -2){
				datas[i] = '0';
			}
		}
		
		BigInteger src = new BigInteger(new String(datas), 2);//转换为BigInteger类型
		//转换�?2进制并输出结�?
		return Integer.parseInt(src.toString());
	}
	
	public static String dcimalToBinary(int value) throws RemoteException {
		return Integer.toBinaryString(value); 
	}

	/**
	 * 录入默认参数
	 * @param iBackService
	 * @throws RemoteException
	 */
	public static void WriteDefaultParameter(IBackServiceTest iBackService) throws RemoteException {
		if(iBackService == null){
			return ;
		}
		/*左，�? 推进电机S0 S3*/		
		int left_push_S0 = StringUtils.toInt(left_push_S0_raw, 0);
		int left_push_S3 = StringUtils.toInt(left_push_S3_raw, 0);
		int right_push_S0 = StringUtils.toInt(right_push_S0_raw, 0);
		int right_push_S3 = StringUtils.toInt(right_push_S3_raw, 0);
		/*左，�? 调节电机S0 S3*/
		int left_adjust_S0 = StringUtils.toInt(left_adjust_S0_raw, 0);
		int left_adjust_S3 = StringUtils.toInt(left_adjust_S3_raw, 0);
		int right_adjust_S0 = StringUtils.toInt(right_adjust_S0_raw, 0);
		int right_adjust_S3 = StringUtils.toInt(right_adjust_S3_raw, 0);
		/*电机系数ratio*/
		int left_push_ratio = StringUtils.toInt(left_push_ratio_raw.replace(".", ""), 0);
		int right_push_ratio = StringUtils.toInt(right_push_ratio_raw.replace(".", ""), 0);
		int left_adjust_ratio = StringUtils.toInt(left_adjust_ratio_raw.replace(".", ""), 0);
		int right_adjust_ratio = StringUtils.toInt(right_adjust_ratio_raw.replace(".", ""), 0);
		/*�?大推进距�?*/
		//String left_push_max_length_raw = left_push_max_length_et.getText().toString();
		//String right_push_max_length_raw = right_push_max_length_et.getText().toString();
		//Log.i(TAG, "left_push_max_length_raw: " + left_push_max_length_raw);
		//Log.i(TAG, "right_push_max_length_raw: " + right_push_max_length_raw);
		String left_push_max_length_raw_ = left_push_max_length_raw.substring(0, left_push_max_length_raw.length()-1);
		String right_push_max_length_raw_ = right_push_max_length_raw.substring(0, right_push_max_length_raw.length()-1);
		
		int left_push_max_length = StringUtils.toInt(left_push_max_length_raw_, 0);
		int right_push_max_length = StringUtils.toInt(right_push_max_length_raw_, 0);
		/*熔接推进参数*/
		int welding_push_parameter_V = StringUtils.toInt(welding_push_parameter_V_raw, 0);
		int welding_parameter_ratio = StringUtils.toInt(welding_parameter_ratio_raw.replace(".", ""), 0);
		int welding_parameter_overlap = StringUtils.toInt(welding_parameter_overlap_raw, 0);

		/*左，�? 推进电机S0 S3*/		
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA0,left_push_S0),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA1,left_push_S3),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA2,right_push_S0),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA3,right_push_S3),SEND_MSG_DELAY_TIME_180); /**/
		/*左，�? 调节电机S0 S3*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA4,left_adjust_S0),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA5,left_adjust_S3),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA6,right_adjust_S0),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA7,right_adjust_S3),SEND_MSG_DELAY_TIME_180); /**/
		/*电机系数ratio，特殊处理把小数点去�?*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA8,left_push_ratio),SEND_MSG_DELAY_TIME_180); /*特殊处理把小数点去掉*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xA9,right_push_ratio),SEND_MSG_DELAY_TIME_180); /*特殊处理把小数点去掉*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xAA,left_adjust_ratio),SEND_MSG_DELAY_TIME_180); /*特殊处理把小数点去掉*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xAB,right_adjust_ratio),SEND_MSG_DELAY_TIME_180); /*特殊处理把小数点去掉*/
		/*特殊处理，读取出来后�?要去掉两�?0*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xAC,left_push_max_length),SEND_MSG_DELAY_TIME_180); /*读取出来后需要去掉两�?0*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xAD,right_push_max_length),SEND_MSG_DELAY_TIME_180); /*读取出来后需要去掉两�?0*/
		
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xAE, welding_push_parameter_V),SEND_MSG_DELAY_TIME_180); /**/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xB0, welding_parameter_ratio),SEND_MSG_DELAY_TIME_180); /*特殊处理把小数点去掉*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xB1, welding_parameter_overlap),SEND_MSG_DELAY_TIME_180); /**/
		
		int correction_1 = StringUtils.toInt(correction_1_raw_, 0);
		int correction_2 = StringUtils.toInt(correction_2_raw_, 0);
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xB2, correction_1),SEND_MSG_DELAY_TIME_180); /*放电的宽度min*/
		iBackService.addToMessageBox(SysConvert.send_cmd_writeByte(0xB3, correction_2),SEND_MSG_DELAY_TIME_180);  /*放电的宽度max*/
		/*B4存入 换电极的次数*/
	}
	
	/**
	 * 机器号转�? 
	 * 十六进制编码转化为字符串
	 * 
	 * @param s
	 * @return
	 */
	public static String toStringHex(String s, int replaceTimes) {

		byte[] baKeyword = new byte[s.length() / 2];
		for (int i = 0; i < baKeyword.length; i++) {
			try {
				baKeyword[i] = (byte) (0xff & Integer.parseInt(s.substring(i * 2, i * 2 + 2), 16));
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		try {
			s = new String(baKeyword, "utf-8");// UTF-16le:Not
		} catch (Exception e1) {
			e1.printStackTrace();
		}
		
//		校验码的生成�?2位）：（（（�?12位的 ASCII值的�? /100）的余数�? *（更换电极次�?+1））/100的余数�?�如果这个余数为1位的，则前面补�??0”�??
//		例子：前12位的 ASCII值的�? = 53+ 31 +35+ 30+ 34+ 31 +37+ 30 +30 +37+ 30+ 30=636
//		636 /100的余 = 36
//		36*�?0+1））/100的余 = 36

		
		/*加上更换次数*/
		String replaceTimes_ = "";
		if ((replaceTimes + "").length() == 1) {
			replaceTimes_ = ("0" + replaceTimes);
		} else {
			replaceTimes_ = (replaceTimes +"");
		}
//		s = "S150515001";
		s = s + replaceTimes_ ;
		String returnValue = "";
		int ascii[] = stringToAscii(s);
		int result = 0;
		for (int i = 0; i < ascii.length; i++) {
			result = result + ascii[i];
//			System.out.println("ascii[i]: " + ascii[i]);
		}
		System.out.println("result: " + result);
		result = result % 100;
		result = result * (replaceTimes + 1) % 100;
		
		/*加上剩余次数*/
		if ((result + "").length() == 1) {
			returnValue = s + returnValue  + ("0" + result); 
		} else {
			returnValue = s + returnValue  + (result); 
		}
		
//		(((�?12位的 ASCII值的�? /100）的余数) *(更换电极次数+1))/100的余�?
		
		System.out.println("returnValue: " + returnValue);
//		return s;
		return returnValue;
	}
	
	/**
	 * 字符串转Ascii�?
	 * @param str
	 * @return
	 */
	public static int [] stringToAscii(String str){
		char[] baKeyword = str.toCharArray();
		int [] ascii = new int[baKeyword.length];
		try {
			for (int i = 0; i < baKeyword.length; i++) {
				try {
					int value = getAscii(baKeyword[i]);
					ascii[i] = value;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return ascii;
	}
	
	/**
	 * 把激活码转成Ascii码数组，然后再装成字符串
	 * @param str "7S0542XA";
	 * @return
	 */
	//public static int [] activationCodeToAscii(String str){
	public static String activationCodeToAscii(String str){
		char[] baKeyword = str.toCharArray();
		int [] ascii = new int[baKeyword.length];
		try {
			for (int i = 0; i < baKeyword.length; i++) {
				try {
					int value = getAscii(baKeyword[i]);
					ascii[i] = value;
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		
		//cmd:5A CD 08 31 32 33 34 35 36 37 38 A5 
		byte a[] = { 0x5a, (byte) 0xCD, (byte) 0x08, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		
		for (int i = 0; i < a.length; i++) {
			try {
				if(i >= ascii.length){
					
				} else {
					a[i + 3] = (byte) ascii[i];
				}
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		
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
	/**
	 * 字符 转Ascii�?
	 * @param cn
	 * @return
	 */
	public static int getAscii(char cn) {
		byte[] bytes = (String.valueOf(cn)).getBytes();
		if (bytes.length == 1) { // 单字节字�?
			return bytes[0];
		} else if (bytes.length == 2) { // 双字节字�?
			int hightByte = 256 + bytes[0];
			int lowByte = 256 + bytes[1];
			int ascii = (256 * hightByte + lowByte) - 256 * 256;
			return ascii;
		} else {
			return 0; // 错误
		}
	}
	
	/**
	 * 两位数字中间加小数点
	 * @param value
	 * @return
	 */
	public static String formatString(int value) {
		String temp = value+"";
		if ((temp).length() >= 2) {
			temp = (temp).substring(0, 1) + "." + (temp).substring(1, temp.length());
			// System.out.println("temp: " + temp);
		}
		return temp;
	}
	
	/**
	 * 发�?�放电命�?
	 * @param current
	 * @param time_length 100就是1s
	 */
	public static String send_fire(int current, int time_length) {
		int _current = (int) Math.round((current * 40.96));
		byte a[] = { 0x5a, 0x0a, (byte) 0x81, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };

		a[3] = (byte) _current;
		a[4] = (byte) (_current >> 8);

		a[5] = (byte) time_length;
		a[6] = (byte) (time_length >> 8);
		
//		writeToOutStrem(a);
//		
//		int _current = (int) Math.round((current * 40.96));
//		byte a[] = { 0x5a, 0x0a, (byte) 0x81, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
//		a[3] = (byte) _current;
//		a[4] = (byte) (_current >> 8);
//		a[5] = (byte) time_length;
//		a[6] = (byte) (time_length >> 8);
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
	
	/**
	 * 电机移动
	 * @param motor_id
	 * @param dir
	 * @param speed
	 * @param run_model
	 * @param steps
	 * @return
	 */
	public static String motor_run(int motor_id, int dir, int speed, int run_model, int steps) {
//		String msg = "";
//		String a[] = { "5a", "0a", "82", "00", "00","00", "00", "00", "00",  "a5" };
//		// byte a[] = { 0x5a, 0x0a, (byte) 0x82, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
//		// steps = fill_vacancy(steps);
//		a[3] = motor_id;
//		a[4] = dir;
//		a[5] = speed;
//		a[6] = run_model;
//		a[7] = steps;
//		a[8] = (fill_vacancy((Integer.parseInt(steps) >> 8) + ""));
//		for (int i = 0; i < a.length; i++) {
//			msg += a[i];
//		}
		
		byte a[] = { 0x5a, 0x0a, (byte) 0x82, 0x0, 0x0, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		a[3] = (byte) motor_id;
		a[4] = (byte) dir;
		a[5] = (byte) speed;
		a[6] = (byte) run_model;
		a[7] = (byte) steps;
		a[8] = (byte) (steps >> 8);
		
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
	
	/**
	 * 字符串位数补�? len == 2
	 * @param str
	 * @return
	 */
	public static String fill_vacancy(String str){
		int len = str.length();
		if(len == 1){
			str = "0" + str;
		} else if(len == 2){
		}
		return str;
	}
	

//	 public static String send_cmd_set(String cmd, String param1, String param2) {
//	// public static String send_cmd_set(int cmd, int param1, int param2) {
//		String a[] = { "5a", "0a", "83", "00", "01","00", "00", "00", "00",  "a5" };
//		// byte a[] = { 0x5a, 0x0a, (byte) 0x83, 0x0, 0x01, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
//		//a[3] = (byte) cmd;
//		//a[6] = (byte) param1;
//		//a[7] = (byte) param2;
//		cmd = cmd.replace("0x", ""); 
//		param1 = param1.replace("0x", ""); 
//		param2 = param2.replace("0x", "");
//		
//		a[3] = cmd;
//		a[6] = param1;
//		a[7] = param2;
//		
//		String ns = "";
//		for (int i = 0; i < a.length; i++) {
//			String hex = a[i];
//			ns = ns + hex;
//		}
//		// Log.i("ParameterSettingsAct: ", "写入 msg  --> " + ns);
//		return ns;
//	}

	/**
	 * 设置参数
	 * 
	 * @param cmd
	 * @param param1
	 * @param param2
	 */
	public static String send_cmd_set(int cmd, int param1, int param2) {
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, 0x0, 0x01, 0x0, 0x0, 0x0, 0x0,(byte) 0xa5 };
		a[3] = (byte) cmd;
		a[6] = (byte) param1;
		a[7] = (byte) (param2 >> 8);

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
	
	/**
	 * 读一个字�?
	 * @return
	 */
	public static String send_cmd_readByte(int addr){
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, (byte) 0xD8, 0x00, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		a[4] = (byte) addr;
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
	
	/**
	 * 写一个字�?
	 * @param addr
	 * @param para1
	 * @return
	 */
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
	
	/**
	 * 读两个字�?
	 * @param addr
	 * @return
	 */
	public static String send_cmd_readWord(int addr){
		// 5A 0A 83 DA addr 00 00 00 00 A5
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, (byte) 0xDA, 0x00, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		a[4] = (byte) addr;
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
	
	/**
	 * 写两个字�?
	 * @param addr
	 * @param para1
	 * @return
	 */
	public static String send_cmd_writeWord(int addr, int para1, int para2){
		/*-------------0-----1------------2------------3-----4----5----6----7----8--------9------*/		
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, (byte) 0xDB, 0x00, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		a[4] = (byte) addr;
		
		a[6] = (byte) para1;
		a[7] = (byte) para2;
//		a[5] = (byte) para1;
//		a[6] = (byte) para2;
		
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

	
	/**
	 * 发�?�命�?
	 * @param cmd
	 * @param param1
	 */ 
	public static String send_cmd_toByteArr(int cmd) {
		byte a[] = { 0x5a, 0x0a, (byte) 0x83, 0x0, 0x00, 0x0, 0x0, 0x0, 0x0, (byte) 0xa5 };
		a[3] = (byte) cmd;
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
	
	/**
	 * 发�?�命�?
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
	
	/**
	 * 16进制字符串数组转字节数组
	 * @param arr
	 * @return
	 */
	public static byte [] StringArrToByteArr(String [] arr){
		String msg = "";
		for (int i = 0; i < arr.length; i++) {
			msg += arr[i];
		}
//		Log.i("ParameterSettingsAct: ", "msg  --> " + msg);
		byte[] msgBytes = SysConvert.HexString2Bytes(msg);
		return msgBytes;
	}
	
	/**
	 * 16进制字符串转字节数组
	 * @param msg
	 * @return
	 */
	public static  byte [] HexStringToByteArr(String msg){
//		String msg = "";
//		for (int i = 0; i < arr.length; i++) {
//			msg += arr[i];
//		}
//		Log.i("ParameterSettingsAct: ", "msg  --> " + msg);
		msg = msg.replace(" ", "");
		byte[] msgBytes = SysConvert.HexString2Bytes(msg);
		return msgBytes;
	}
	
	/**
	 * 参数设置�?10进制�?16进制后，在转二维数组
	 * 
	 * @param strValue
	 * @return
	 */
	public static String [] StringToArray(String strValue) {
		String [] arr_str = new String[2];
		int len = strValue.length();
		switch (len) {
		case 1:
			arr_str[0] = "00";
			arr_str[1] = "0" + strValue;
			break;
		case 2:
			arr_str[0] = "00";
			arr_str[1] = strValue;
			break;
		case 3:
//			strValue = strValue.substring(0, 2) + "0" + strValue.substring(2, 3);
//			arr_str[0] = strValue.substring(2, 4); 
//			arr_str[1] = strValue.substring(0, 2);
			// strValue = strValue.substring(0, 2) + "0" + strValue.substring(2, 3);
			arr_str[0] = strValue.substring(2, 3); 
			arr_str[1] = strValue.substring(0, 2);
			break;
		case 4:
			arr_str[0] = strValue.substring(2, 4);
			arr_str[1] = strValue.substring(0, 2);
			break;
		}
		return arr_str;
	}

	public static byte[] HexString2Bytes(String hexstr) {
		byte[] b = new byte[hexstr.length() / 2];
		int j = 0;
		for (int i = 0; i < b.length; i++) {
			char c0 = hexstr.charAt(j++);
			char c1 = hexstr.charAt(j++);
			b[i] = (byte) ((parse(c0) << 4) | parse(c1));
		}
		return b;
	}

	private static int parse(char c) {
		if (c >= 'a')
			return (c - 'a' + 10) & 0x0f;
		if (c >= 'A')
			return (c - 'A' + 10) & 0x0f;
		return (c - '0') & 0x0f;
	}

	public static byte[] hexStringToBytes(String input) {
		input = input.toLowerCase(Locale.US);
		int n = input.length() / 2;
		byte[] output = new byte[n];
		int l = 0;
		for (int k = 0; k < n; k++) {
			char c = input.charAt(l++);
			byte b = (byte) ((c >= 'a' ? (c - 'a' + 10) : (c - '0')) << 4);
			c = input.charAt(l++);
			b |= (byte) (c >= 'a' ? (c - 'a' + 10) : (c - '0'));
			output[k] = b;
		}
		return output;
	}

	/**
	 * 10进制�?16进制
	 * 
	 * @param Num
	 * @return
	 */
	public static String decimalToHexString(int Num) {
		final char digits[] = { '0', '1', '2', '3', '4', '5', '6', '7', '8',
				'9', 'A', 'B', 'C', 'D', 'E', 'F' };
		// int Num = 9000;// 要转换的数字
		int length = 32;
		char[] result = new char[length];
		do {
			result[--length] = digits[Num & 15];
			Num >>>= 4;
		} while (Num != 0);
		String str = "";
		for (int i = length; i < result.length; i++) {
			String item = String.valueOf(result[i]);
			// if(item.length() == 1){
			// item = "0" + item;
			// }
			str += item;
		}
		return str;
	}

	/**
	 * 16进制�?10进制
	 * 
	 * @param hex
	 * @return
	 */
	public static int hexStringToDecimal(String hex) {
		hex = hex.replace(",", "");
		return Integer.parseInt(Integer.valueOf(hex, 16).toString());
	}

	/**
	 * 16进制�?2进制
	 * 
	 * @param hex
	 * @return
	 */
	public static int hexStringToBinary(String hex) {
		return Integer.parseInt(Integer.toBinaryString(Integer.parseInt(Integer
				.valueOf(hex, 16).toString())));
	}

	/**
	 * 16进制�?2进制,取低�?
	 * 
	 * @param hex
	 * @return
	 */
	public static int hexStringTobinaryPickBits(String hex) {
		String temp = Integer.toBinaryString(Integer.parseInt(Integer.valueOf(
				hex, 16).toString()));
		temp = temp.substring(temp.length() - 1, temp.length());
		return Integer.parseInt(temp);
	}

	/**
	 * 
	 * @param hex
	 * @return
	 */
	public static byte[] hexStringToByte(String hex) {
		int len = (hex.length() / 2);
		byte[] result = new byte[len];
		char[] achar = hex.toCharArray();
		for (int i = 0; i < len; i++) {
			int pos = i * 2;
			result[i] = (byte) (toByte(achar[pos]) << 4 | toByte(achar[pos + 1]));
		}
		return result;
	}

	/**
	 * 
	 * @param c
	 * @return
	 */
	private static byte toByte(char c) {
		byte b = (byte) "0123456789ABCDEF".indexOf(c);
		return b;
	}

	// public static byte[] HexString2Bytes(String src) {
	// byte[] ret = new byte[6];
	// byte[] tmp = src.getBytes();
	// for (int i = 0; i < 6; ++i) {
	// ret[i] = uniteBytes(tmp[i * 2], tmp[i * 2 + 1]);
	// }
	// return ret;
	// }
	//
	// private static byte uniteBytes(byte src0, byte src1) {
	// byte _b0 = Byte.decode("0x" + new String(new byte[] { src0
	// })).byteValue();
	// _b0 = (byte) (_b0 << 4);
	// byte _b1 = Byte.decode("0x" + new String(new byte[] { src1
	// })).byteValue();
	// byte ret = (byte) (_b0 | _b1);
	// return ret;
	// }
}
