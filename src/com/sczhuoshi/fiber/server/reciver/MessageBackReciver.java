package com.sczhuoshi.fiber.server.reciver;

import java.lang.ref.WeakReference;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.TextView;

import com.sczhuoshi.fiber.server.BackService;

/**
 * Socket 消息接收
 * 
 * @author PengPeng
 *
 */
public class MessageBackReciver extends BroadcastReceiver {
	private WeakReference<TextView> textView;
	private WeakReference<Handler> handlerReference;

	public MessageBackReciver(TextView tv, Handler mHandler) {
		textView = new WeakReference<TextView>(tv);
		handlerReference = new WeakReference<Handler>(mHandler);
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		TextView tv = textView.get();
		Handler mHandler = handlerReference.get();
		if (action.equals(BackService.HEART_BEAT_ACTION)) {
//			if (null != tv) {
//				tv.setText("Get a heart heat");
//			}
		} else {
			String returnData = intent.getStringExtra("message");
//			tv.setText(returnData);
			if (mHandler != null) {
				Message msg = mHandler.obtainMessage();
				msg.arg1 = returnData.length();
				msg.obj = returnData;
				mHandler.sendMessage(msg);// 结果返回给UI处理
			}
		}
	};
}