package com.nearucenterplaza.redenvelopeassistant.service;

import com.nearucenterplaza.redenvelopeassistant.R;
import com.nearucenterplaza.redenvelopeassistant.ui.fragmant.WeChatFragment;
import com.nearucenterplaza.redenvelopeassistant.service.core.Notifier;
import com.nearucenterplaza.redenvelopeassistant.service.core.RedEnvelopeHelper;
import com.nearucenterplaza.redenvelopeassistant.service.core.SettingHelper;
import com.nearucenterplaza.redenvelopeassistant.utils.ActivityHelper;
import com.nearucenterplaza.redenvelopeassistant.utils.XLog;

import android.R.anim;
import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.app.Notification;
import android.os.Build;
import android.text.TextUtils;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class WechatAccService extends AccessibilityService {

	public static void log(String message) {
		XLog.e("WechatAccService", message);
	} 
	
	/**
	 * {@inheritDoc}
	 */
	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
	@Override
	public void onServiceConnected() {
		AccessibilityServiceInfo accessibilityServiceInfo = getServiceInfo();
		if (accessibilityServiceInfo == null)
			accessibilityServiceInfo = new AccessibilityServiceInfo();
		accessibilityServiceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
		accessibilityServiceInfo.flags |= AccessibilityServiceInfo.FLAG_REPORT_VIEW_IDS;
		accessibilityServiceInfo.packageNames = new String[] { WeChatFragment.WECHAT_PACKAGENAME };
		accessibilityServiceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_ALL_MASK;
		accessibilityServiceInfo.notificationTimeout = 10;
		setServiceInfo(accessibilityServiceInfo);
		// 4.0之后可通过xml进行配置,以下加入到Service里面
		/*
		 * <meta-data android:name="android.accessibilityservice"
		 * android:resource="@xml/accessibility" />
		 */
		Notifier.getInstance().notify(getString(R.string.app_name), getString(R.string.wechat_acc_service_start_notification), getString(R.string.wechat_acc_service_start_notification),
				Notifier.TYPE_WECHAT_SERVICE_RUNNING, false);
	}

	@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
	@Override
	public void onAccessibilityEvent(AccessibilityEvent event) {
		if (event == null)
			return;
		if(SettingHelper.getREAutoMode()){
			handleNotificationChange(event);
		}
		AccessibilityNodeInfo nodeInfo = event.getSource();
		if (nodeInfo == null) {
			return;
		}

		AccessibilityNodeInfo rowNode = nodeInfo;// we can also use getRootInActiveWindow() instead;
		if (rowNode == null) {
			log( "noteInfo is　null");
			return;
		}

		// String currentActivityName =
		// ActivityHelper.getTopActivityName(RedEnvelopeApplication.getInstance());
		CharSequence currentActivityName = event.getClassName();
		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED) {
			// 检测聊天界面的变化
			if ("android.widget.ListView".equals(currentActivityName)
					|| "android.widget.FrameLayout".equals(currentActivityName)) {
				if (SettingHelper.getREChatOnlyMode()) {
					handleChatOnly(rowNode);
				}
			}
		}
		if (event.getEventType() == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED) {
			if ("com.tencent.mm.ui.LauncherUI".equals(currentActivityName)) {// 聊天以及主页 chat page and the main page
				log( "Chat page");
//				print(nodeInfo);
				if (SettingHelper.getREAutoMode()) {
					handleChatPage(rowNode);
				}
			} else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyReceiveUI"
					.equals(currentActivityName)) {//打开红包主页 red envelope open page
				log("LuckyMoneyReceiveUI page");
//				print(nodeInfo);
				if (SettingHelper.getREAutoMode()
						|| SettingHelper.getRESafeMode()
						|| SettingHelper.getREChatOnlyMode())
					handleLuckyMoneyReceivePage(rowNode);
			} else if ("com.tencent.mm.plugin.luckymoney.ui.LuckyMoneyDetailUI"
					.equals(currentActivityName)) {// 红包详情主页 red envelope detail page
				if (SettingHelper.getREAutoMode()
						|| SettingHelper.getREChatOnlyMode())
					handleLuckyMoneyDetailPage(rowNode);

			} else {
				log( currentActivityName + " page");
			}
		}
	}

	private void print(AccessibilityNodeInfo nodeInfo) {
		List<String> nodeInfoList = new ArrayList<String>() ;
		for ( int i = 0 ; i < nodeInfo.getChildCount(); i++) {
			String info = getNodeInfo(nodeInfo.getChild(i)) ;
			if (info != null ) {
//                      nodeInfoList.add(info);
				if (info.length() > 1024 ) {
					Log. d("wangchong", "node" + i + " start=====================") ;
					int size = info.length();
					int index = 0;
					int BUFFER = 1024;
					while (index < size) {
						if (index + BUFFER < size) {
							Log. d("wangchong", info.substring(index , index + BUFFER));
							index += BUFFER;
						} else {
							Log. d("wangchong", info.substring(index , size));
							break;
						}
					}

					Log. d("wangchong", "node" + i + " end=======================") ;
				} else {
					Log. d("wangchong", "node" + i + ", = " + info);
				}
			}
		}

//          Log.d("wangchong", "NODEINFO = " + nodeInfoList.toString());
	}

	private String getNodeInfo(AccessibilityNodeInfo info) {
		if (info == null) {
			return null;
		}

		Map<String, String> hashMap = new HashMap<>();
		hashMap.put( "ViewIdResourceName" , info.getViewIdResourceName()) ;
		hashMap.put( "ClassName", info.getClassName().toString()) ;
		if (TextUtils. equals(info.getClassName().toString(), "android.widget.TextView")) {
			if (info.getText() == null) {
				hashMap.put( "Text", "null" );
			} else {
				hashMap.put( "Text", info.getText().toString()) ;
			}

		}
		hashMap.put("count", String.valueOf(info.getChildCount()));

		List<String> list = new ArrayList<>();
		for ( int i = 0 ; i < info.getChildCount(); i++) {
			String node = getNodeInfo(info.getChild(i)) ;
			if (node != null ) {
				list.add(node) ;
			}
		}

		if (list.size() > 0) {
			hashMap.put( "Children", list.toString()) ;
		}

		return hashMap.toString();
	}

	/** handle notification notice */
	@TargetApi(Build.VERSION_CODES.KITKAT)
	public void handleNotificationChange(AccessibilityEvent event) {
		log( "eventtype:" + event.getEventType());
		if (event == null)
			return;
		
		if (!(event.getEventType() == AccessibilityEvent.TYPE_NOTIFICATION_STATE_CHANGED)) {
			return;
		}
		if (event.getParcelableData() instanceof Notification) {
			Notification notification = (Notification) event
					.getParcelableData();
			if (notification.tickerText != null
					&& notification.tickerText.toString().contains(getString(R.string.wechat_acc_service_red_envelope_notification_identification))) {
				log("来红包啦 get red envelope message");
				RedEnvelopeHelper.openNotification(event);
			}
		}
	}

	private static int mLastViewHashCode = 0;
	public void handleChatOnly(AccessibilityNodeInfo node) {
		if (node == null) {
			return;
		}

		// TODO 需要考虑版本兼容
//		AccessibilityNodeInfo tempNode=RedEnvelopeHelper.getLastWechatRedEnvelopeNodeById(node);
//		Log.d("wangchong", "mLastViewHashCode = " + mLastViewHashCode);
//		if (tempNode != null) {
//			Log.d("wangchong", "tempNode.hashCode() = " + tempNode.hashCode());
//			Log.d("wangchong", "tempNode = " + tempNode.toString());
//		}
//		if(tempNode!=null && mLastViewHashCode != tempNode.hashCode()) {
//			// 不重复打开红包
//			mLastViewHashCode = tempNode.hashCode();
//			tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//			tempNode.recycle();
//		}

		AccessibilityNodeInfo tempNode=RedEnvelopeHelper.getLastWechatRedEnvelopeNodeById(node);
		if(tempNode != null) {
			// 不重复打开红包
//			mLastViewHashCode = tempNode.hashCode();
			tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
			tempNode.recycle();
		}
	}

	public void handleChatPage(AccessibilityNodeInfo node) {
		if (node == null)
			return;
		if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.KITKAT){
			 AccessibilityNodeInfo tempNode=RedEnvelopeHelper.getLastWechatRedEnvelopeNodeById(node);
			 if(tempNode!=null){
				 tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				 tempNode.recycle();
			 }
		}else if(android.os.Build.VERSION.SDK_INT>=Build.VERSION_CODES.JELLY_BEAN){
			 AccessibilityNodeInfo tempNode=RedEnvelopeHelper.getLastWechatRedEnvelopeNodeByText(node,this);
			 if(tempNode!=null){
				 tempNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				 tempNode.recycle();
			 }
		}
	}

	public void handleLuckyMoneyReceivePage(AccessibilityNodeInfo node) {
		if (node == null)
			return;
		AccessibilityNodeInfo nodeDetail = RedEnvelopeHelper
				.getWechatRedEnvelopeOpenDetailNode(node);
		if (nodeDetail != null) {// the red envelope already opened
									// 红包已经被打开
			if (SettingHelper.getREAutoMode()) {
				ActivityHelper.goHome(this);
			} else if (SettingHelper.getREChatOnlyMode()) {
				// 返回到聊天页面
				AccessibilityNodeInfo closeView = RedEnvelopeHelper.getWechatLuckyMoneyReceivePageCloseImage(node);
				if (closeView != null) {
					closeView.performAction(AccessibilityNodeInfo.ACTION_CLICK);
					closeView.recycle();
				}
			}
		} else {
			AccessibilityNodeInfo nodeOpen = RedEnvelopeHelper
					.getWechatRedEnvelopeOpenNode(node);
			if (nodeOpen != null) {
				nodeOpen.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				nodeOpen.recycle();
			} else {// this page is loading red envelope data, no action
				if (SettingHelper.getREAutoMode()) {
					ActivityHelper.goHome(this);
				} else if (SettingHelper.getREChatOnlyMode()) {
					// 返回到聊天页面
					AccessibilityNodeInfo closeView = RedEnvelopeHelper.getWechatLuckyMoneyReceivePageCloseImage(node);
					if (closeView != null) {
						closeView.performAction(AccessibilityNodeInfo.ACTION_CLICK);
						closeView.recycle();
					}
				}
			}
		}
	}

	public void handleLuckyMoneyDetailPage(AccessibilityNodeInfo node) {
		if (node == null)
			return;
		if (SettingHelper.getREAutoMode()) {
			ActivityHelper.goHome(this);
		} else if (SettingHelper.getREChatOnlyMode()) {
			// 返回到聊天页面
			AccessibilityNodeInfo backView = RedEnvelopeHelper.getWechatLuckyMoneyDetailPageBackImage(node);
			if (backView != null) {
				backView.performAction(AccessibilityNodeInfo.ACTION_CLICK);
				backView.recycle();
			}
		}
	}

	

	@Override
	public void onInterrupt() {
		log("onInterrupt");
	}

	public void onDestroy() {
		super.onDestroy();
		Notifier.getInstance().cancelByType(
				Notifier.TYPE_WECHAT_SERVICE_RUNNING);
	}

}
