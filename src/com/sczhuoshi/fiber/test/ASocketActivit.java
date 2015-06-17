package com.sczhuoshi.fiber.test;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.TextView;

import com.sczhuoshi.fiber.server.BackService;
import com.sczhuoshi.fiber.server.IBackServiceTest;
import com.sczhuoshi.fiber.server.reciver.MessageBackReciver;


/**
 *  继承该类后，如果需要接收指令，须初始化Handler。调用 setmHandler(Handler mHandler) {} 方法。
 * @author PengPeng
 *
 */
public class ASocketActivit extends Activity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.initFilter();
	}

	@Override
	protected void onStart() {
		super.onStart();
		this.registerReceiver();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		this.unregisterReceiver();
	}
	
	private Intent mServiceIntent;
	protected IBackServiceTest iBackService;
	private MessageBackReciver mReciver;
	private IntentFilter mIntentFilter;
	private LocalBroadcastManager mLocalBroadcastManager;
	protected Handler mHandler ;
	
//	private Handler mHandler = new Handler(){
//		@Override
//		public void handleMessage(Message msg) {
//			// TODO Auto-generated method stub
//			super.handleMessage(msg);
//		}
//	};
	
	/**
	 * 初始化Filter
	 */
	private void initFilter(){
		mLocalBroadcastManager = LocalBroadcastManager.getInstance(this);
		mReciver = new MessageBackReciver(new TextView(this), mHandler);
		mServiceIntent = new Intent(this, BackService.class);
		mIntentFilter = new IntentFilter();
		mIntentFilter.addAction(BackService.HEART_BEAT_ACTION);
		mIntentFilter.addAction(BackService.MESSAGE_ACTION);
	}

	/**
	 * 设置handler
	 * @param mHandler
	 */
	public void setmHandler(Handler mHandler) {
		this.mHandler = mHandler;
		mReciver = new MessageBackReciver(new TextView(this), mHandler);
	}

	/**
	 * 注册广播, 绑定服务
	 */
	private void registerReceiver(){
		mLocalBroadcastManager.registerReceiver(mReciver, mIntentFilter);
		bindService(mServiceIntent, conn, BIND_AUTO_CREATE);
	}
	
	/**
	 * 注销广播，注销服务
	 */
	private void unregisterReceiver(){
		unbindService(conn);
		mLocalBroadcastManager.unregisterReceiver(mReciver);
	}
	
	private ServiceConnection conn = new ServiceConnection() {
		@Override
		public void onServiceDisconnected(ComponentName name) {
			iBackService = null;
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			iBackService = IBackServiceTest.Stub.asInterface(service);
		}
	};
}