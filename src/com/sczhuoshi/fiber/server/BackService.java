package com.sczhuoshi.fiber.server;

import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.concurrent.ArrayBlockingQueue;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.sczhuoshi.fiber.common.SysConvert;

@SuppressLint("NewApi")
public class BackService extends Service {
	
	private static final String TAG = "BackService";
	private static final long HEART_BEAT_RATE = 3 * 1000;
	public static final String HOST = "192.168.1.20";
	public static final int PORT = 2001;
	public static final String MESSAGE_ACTION = "com.sczhuoshi.message_ACTION";
	public static final String HEART_BEAT_ACTION = "com.sczhuoshi.heart_beat_ACTION";
	
	private ReadThread mReadThread;
	private SendThread mSendThread;
	private LocalBroadcastManager mLocalBroadcastManager;
	private WeakReference<Socket> mSocket;
	private SoftReference<ArrayBlockingQueue<byte[]>> mSend_msg_queue;
	private final int MessageQueueCapacity = 1000; /*message boxs capacity*/

	/**
	 * For heart Beat
	 */
	private Handler mHandler = new Handler();
	private Runnable heartBeatRunnable = new Runnable() {
		@Override
		public void run() {
			if (System.currentTimeMillis() - sendTime >= HEART_BEAT_RATE) {
//				boolean isSuccess = addToMessageQueueBoxs("5A 0A 83 45 08 00 00 00 00 A5");
				boolean isSuccess = addToMessageQueueBoxs("");
				//send check connected cmd to socketServer ,if send fail Re-initialize a socket
//				boolean isSuccess = sendMsg("");
				if (!isSuccess) {
					mHandler.removeCallbacks(heartBeatRunnable);
					mReadThread.release();
					mSendThread.release();
					releaseLastSocket(mSocket);
					new InitSocketReadSendThread().start();
				}
			}
			mHandler.postDelayed(this, HEART_BEAT_RATE);
		}
	};

	private long sendTime = 0L;
	
	private IBackServiceTest.Stub iBackService = new IBackServiceTest.Stub() {
		@Override
		public boolean addToMessageBox(String message, int delayTime) throws RemoteException {
			// TODO Auto-generated method stub
//			return false;
			return addToMessageQueueBoxs(message);
		}
	};

	@Override
	public IBinder onBind(Intent arg0) {
		return iBackService;
	}
	
	@Override
	public boolean onUnbind(Intent intent) {
		// TODO Auto-generated method stub
		mHandler.removeCallbacks(heartBeatRunnable);
		mReadThread.release();
		mSendThread.release();
		releaseLastSocket(mSocket);
		return super.onUnbind(intent);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
		new InitSocketReadSendThread().start();
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
	}
	
	/**
	 * msg(String) added to messageBoxs
	 * @param msg
	 * @return
	 */
	public boolean addToMessageQueueBoxs(String msg) {
		try {
			msg = msg.replace(",", "");
			byte [] msgBytes = SysConvert.HexStringToByteArr(msg);
			addToMessageQueueBoxs(msgBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * msg(byte[]) added to messageBoxs
	 * @param msg
	 * @return
	 */
	public boolean addToMessageQueueBoxs(byte [] msg) {
		try {
			ArrayBlockingQueue<byte[]> send_msg_abq =  mSend_msg_queue.get();
			if (mSend_msg_queue != null && send_msg_abq!=null) {
				System.out.println("msg: " + msg);
				//send_msg_abq.add(SysConvert.HexStringToByteArr(msg));
				send_msg_abq.add(msg);
				int size = mSend_msg_queue.get().size();
				System.out.println("size: " + size);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * init socket readThread and SendThread
	 */
	private void initSocket_sendThread_readThread() {
		try {
			Socket so = new Socket(HOST, PORT);
			mSocket = new WeakReference<Socket>(so);
			mReadThread = new ReadThread(so);
			mReadThread.start();
			
			mHandler.postDelayed(heartBeatRunnable, HEART_BEAT_RATE);//初始化成功后，就准备发送心跳包
			/*初始化*/
			ArrayBlockingQueue<byte[]> send_msg_abq = new ArrayBlockingQueue<byte[]>(MessageQueueCapacity);
			mSend_msg_queue = new SoftReference<ArrayBlockingQueue<byte[]>>(send_msg_abq);
			mSendThread = new SendThread(send_msg_abq);
			mSendThread.start();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * release socket
	 * @param mSocket
	 */
	private void releaseLastSocket(WeakReference<Socket> mSocket) {
		try {
			if (null != mSocket) {
				Socket sk = mSocket.get();
				if (!sk.isClosed()) {
					sk.close();
				}
				sk = null;
				mSocket = null;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * init socket readThrea and SendThread
	 * @author PengPeng
	 *
	 */
	class InitSocketReadSendThread extends Thread {
		@Override
		public void run() {
			super.run();
			BackService.this.initSocket_sendThread_readThread();
		}
	}
	
	/**
	 *	Thread to send content by Socket 
	 * @author PengPeng
	 *
	 */
	class SendThread extends Thread {
		private SoftReference<ArrayBlockingQueue<byte[]>> mSend_msg_queue;
		private boolean isStart = true;
		
		public SendThread(ArrayBlockingQueue<byte[]> send_msg_abq) {
			mSend_msg_queue = new SoftReference<ArrayBlockingQueue<byte[]>>(send_msg_abq);
		}
		
		public void release() {
			isStart = false;
			if (null != mSend_msg_queue) {
				ArrayBlockingQueue<byte[]> arrayBlockingQueues = mSend_msg_queue.get();
				arrayBlockingQueues.clear();
				arrayBlockingQueues = null;
				mSocket = null;
			}
		}

		@Override
		public synchronized void run() {
			super.run();
			ArrayBlockingQueue<byte[]> send_msg_abq = mSend_msg_queue.get();
			if (null != send_msg_abq) {
				while(isStart){
					int queueSize = send_msg_abq.size();
					if(queueSize > 0){
						byte[] msgBytes = mSend_msg_queue.get().remove();
						
						if (null == mSocket || null == mSocket.get()) {
							return ;
						}
						
						try {
							Socket soc = mSocket.get();
							if (!soc.isClosed() && !soc.isOutputShutdown()) {
								OutputStream out = soc.getOutputStream();
//								byte[] msgBytes = SysConvert.StringToByteArr(msg);
								writeToOutStrem(out, msgBytes);
								// Thread.sleep(160);
								Thread.sleep(120);
//								Thread.sleep(10);
								sendTime = System.currentTimeMillis();//每次发送成数据，就改一下最后成功发送的时间，节省心跳间隔时间
							} else {
								//send msg failed
							}
						} catch (Exception e) {
							// TODO: handle exception
							e.printStackTrace();
						}
					}
				}
			}
		}
	}
	
	public static StringBuffer sb = new StringBuffer();

	/**
	 * Thread to read content from Socket 
	 * @author PengPeng
	 *
	 */
	class ReadThread extends Thread {
		private WeakReference<Socket> mWeakSocket;
		private boolean isStart = true;

		public ReadThread(Socket socket) {
			mWeakSocket = new WeakReference<Socket>(socket);
		}

		public void release() {
			isStart = false;
			releaseLastSocket(mWeakSocket);
		}

		@Override
		public void run() {
			super.run();
			Socket socket = mWeakSocket.get();
			if (null != socket) {
				try {
//					while (!socket.isClosed() && !socket.isInputShutdown() && isStart && ((length = is.read(buffer)) != -1)) {
//					if (length > 0) {}
//					}
					DataInputStream in = new DataInputStream(socket.getInputStream());
					while(isStart){
						byte bytes = in.readByte();
						String ns = "";
						for (int i = 0; i < 1; i++) {
							String hex = Integer.toHexString(bytes & 0xFF);
							if (hex.length() == 1) {
								hex = '0' + hex;
							}
							ns = ns + hex + ",";
						}
						// Log.i(TAG + "cmd", "---《》read receive data ns:" + ns);
						sb.append(ns);
						String returnData = "";
						if(sb.toString().length() > 10){
							returnData = dealWithData3(sb);
						}
						// Log.i(TAG + "cmd", "--> raw data sb.toString():" + sb.toString());
						if (!returnData.equalsIgnoreCase("")) {
							// 收到服务器过来的消息，就通过Broadcast发送出去
							/*receive hear beat msg*/							
							if(returnData.equalsIgnoreCase("55 0A 45 C0 00 00 00 00 00 AA")){
								Intent intent = new Intent(HEART_BEAT_ACTION);
								mLocalBroadcastManager.sendBroadcast(intent);
							} else {
								// other msg replay
								Intent intent = new Intent(MESSAGE_ACTION);
								intent.putExtra("message", returnData);
								mLocalBroadcastManager.sendBroadcast(intent);
							}
							/**
							if (ns.equals("550645c000aa")) {// 处理心跳回复
								Intent intent = new Intent(HEART_BEAT_ACTION);
								mLocalBroadcastManager.sendBroadcast(intent);
							} else {
								// 其他消息回复
								Intent intent = new Intent(MESSAGE_ACTION);
								intent.putExtra("message", ns);
								mLocalBroadcastManager.sendBroadcast(intent);
							}
							*/
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		
		/**
		 * 
		 * @param sb
		 * @return
		 */
		private String dealWithData3(StringBuffer sb) {
			String rawData = sb.toString().replace(" ", "");
			String returnData = "";
			String MODEL_START = "55";
//			String MODEL_End = "00,aa";
			 String MODEL_End = "aa";
			int index = rawData.indexOf(MODEL_End);
			int MODEL_LENGTH = 0;
			/*计算MODEL_LENGTH长度*/
			if(rawData.length() > 5){
				int len = SysConvert.hexStringToDecimal(rawData.substring(3, 5));
				MODEL_LENGTH = len * 3;
				System.out.println("MODEL_LENGTH: " + MODEL_LENGTH);
			} else {
				return "";
			}
			/*验证index 第一次*/
			if (MODEL_LENGTH != index + MODEL_End.length() + 1) {
				 index = rawData.indexOf(MODEL_End, index + MODEL_End.length());
			}
			
			/*验证index 第二次*/
			if (MODEL_LENGTH != index + MODEL_End.length() + 1) {
				index = rawData.indexOf(MODEL_End, index + MODEL_End.length());
			}
			
			if (rawData.startsWith(MODEL_START) && rawData.contains(MODEL_End)) {
				Log.i(TAG, "----收到的数据---> rawData " + rawData);
				/*处理熔接机返回的错位问题*/
//				if (rawData.contains("55,0a,67,01,") && rawData.contains("55,0a,64,")){
//					rawData = rawData.replace("55,0a,67,01,", "");
//					Log.i(TAG, "----收到的数据22222---> rawData " + rawData);
//				} else if(rawData.contains("55,0a,67,02,") && rawData.contains("55,0a,64,")){
//					rawData = rawData.replace("55,0a,67,02,", "");
//					Log.i(TAG, "----收到的数据22222---> rawData " + rawData);
//				}
				
				returnData = rawData.substring(0, index + MODEL_End.length());
				
				rawData = rawData.substring(index + MODEL_End.length(), rawData.length());
				if (returnData.startsWith(MODEL_START) && returnData.endsWith(MODEL_End)) {
					if (rawData.startsWith(",")) {
						rawData = rawData.substring(1);
					}
					sb.setLength(0);//清空SB
					sb.append(rawData);
				} else {
					return "";
				}
			} else {
				/*数据长度大于最大字节数指令的长度*/
				if (!rawData.startsWith(MODEL_START) && rawData.length() > "55,0a,00,00,00,00,00,00,00,aa,".length()){
					returnData = rawData.substring(0, index + MODEL_End.length());
					rawData = rawData.substring(index + MODEL_End.length(), rawData.length());
					// System.out.println("rawData: " + rawData);
					if (rawData.startsWith(",")) {
						rawData = rawData.substring(1);
					}
					sb.setLength(0); //清空SB
					sb.append(rawData);
					returnData = "";
					// System.out.println("rawData: " + rawData);					
				}
			}
			return returnData;
		}
		
		/**
		 * 
		 * @param sb
		 * @return
		 */
//		private String dealWithData2(StringBuffer sb) {
//			String rawData = sb.toString().replace(" ", "");
//			String returnData = "";
//			String MODEL_START = "55";
//			String MODEL_End = "00,aa";
//			int index = rawData.indexOf(MODEL_End);
//			if (rawData.startsWith(MODEL_START) && rawData.contains(MODEL_End)) {
//				Log.i(TAG, "----收到的数据---> rawData " + rawData);
//				/*处理熔接机返回的错位问题*/
////				if (rawData.contains("55,0a,67,01,") && rawData.contains("55,0a,64,")){
////					rawData = rawData.replace("55,0a,67,01,", "");
////					Log.i(TAG, "----收到的数据22222---> rawData " + rawData);
////				} else if(rawData.contains("55,0a,67,02,") && rawData.contains("55,0a,64,")){
////					rawData = rawData.replace("55,0a,67,02,", "");
////					Log.i(TAG, "----收到的数据22222---> rawData " + rawData);
////				}
//				
//				returnData = rawData.substring(0, index + MODEL_End.length());
//				
//				rawData = rawData.substring(index + MODEL_End.length(), rawData.length());
//				if (returnData.startsWith(MODEL_START) && returnData.endsWith(MODEL_End)) {
//					if (rawData.startsWith(",")) {
//						rawData = rawData.substring(1);
//					}
//					sb.setLength(0);//清空SB
//					sb.append(rawData);
//				} else {
//					return "";
//				}
//			} else {
//				/*数据长度大于最大字节数指令的长度*/
//				if (!rawData.startsWith(MODEL_START) && rawData.length() > "55,0a,00,00,00,00,00,00,00,aa,".length()){
//					returnData = rawData.substring(0, index + MODEL_End.length());
//					rawData = rawData.substring(index + MODEL_End.length(), rawData.length());
//					// System.out.println("rawData: " + rawData);
//					if (rawData.startsWith(",")) {
//						rawData = rawData.substring(1);
//					}
//					sb.setLength(0); //清空SB
//					sb.append(rawData);
//					returnData = "";
//					// System.out.println("rawData: " + rawData);					
//				}
//			}
//			return returnData;
//		}
	}
	
	/**
	 * 把字节数组写入输出流
	 * @param a
	 * @throws IOException
	 */
	public void writeToOutStrem(OutputStream out, byte[] msgBytes) throws IOException {
		String ns = "";
		for (int i = 0; i < msgBytes.length; i++) {
			String hex = Integer.toHexString(msgBytes[i] & 0xFF);
			if (hex.length() == 1) {
				hex = '0' + hex;
			}
			ns = ns + hex;
		}
		 Log.i(TAG, "发送的数据--->>>>>" + ns);
		if (out != null) {
			out.write(msgBytes);
			out.flush();
		}
	}
}