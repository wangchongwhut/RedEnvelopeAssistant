package com.nearucenterplaza.redenvelopeassistant.service.core;

import java.util.ArrayList;
import java.util.List;

import com.nearucenterplaza.redenvelopeassistant.R;
import com.nearucenterplaza.redenvelopeassistant.utils.XLog;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.os.Build;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

@TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
public class RedEnvelopeHelper {
    /**
     * TIPS：目前兼容了微信6.3.8和6.3.9版本，其他版本还没有测试过。
     */
    // 微信红包：聊天页面红包id，类型：android.widget.LinearLayout
    private static final String[] WECHAT_LUCKY_MONEY_VIEW_IDS = new String[]{"com.tencent.mm:id/ye", "com.tencent.mm:id/b_"};
    // 微信红包：拆红包id，类型：android.widget.Button
    private static final String[] WECHAT_OPEN_LUCKY_MONEY_IDS = new String[]{"com.tencent.mm:id/b2m", "com.tencent.mm:id/b2c"};
    // 微信红包：拆红包页面关闭按钮id，类型：android.widget.ImageView
    private static final String[] WECHAT_LUCKY_MONEY_RECEIVE_CLOSE_IDS = new String[]{"com.tencent.mm:id/b2h", "com.tencent.mm:id/b2g"};
    // 微信红包：红包详情页返回按钮id，类型：android.widget.LinearLayout
    private static final String[] WECHAT_LUCKY_MONEY_DETAIL_BACK_IDS = new String[]{"com.tencent.mm:id/fb", "com.tencent.mm:id/c2m"};

    /**
     * 微信不同版本View对应的ID都不一样，为了保证兼容性，遍历所有可能的ID
     *
     * @param info 节点信息
     * @param ids  View可能的id
     * @return id对应的一组View
     */
    private static List<AccessibilityNodeInfo> getListByViewIds(AccessibilityNodeInfo info, String[] ids) {
        if (info == null || ids == null || ids.length == 0) {
            return null;
        }
        List<AccessibilityNodeInfo> list = new ArrayList<>();
        for (int i = 0; i < ids.length; i++) {
            list = info.findAccessibilityNodeInfosByViewId(ids[i]);
            if (list != null && list.size() > 0) {
                break;
            }
        }
        return list;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static void openNotification(AccessibilityEvent event) {
        if (!(event.getParcelableData() instanceof Notification)) {
            return;
        }
        Notification notification = (Notification) event.getParcelableData();
        PendingIntent pendingIntent = notification.contentIntent;
        try {
            pendingIntent.send();
        } catch (PendingIntent.CanceledException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获得红包详情页面打开节点
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo getWechatRedEnvelopeOpenNode(AccessibilityNodeInfo info) {
        if (info == null)
            return null;
        List<AccessibilityNodeInfo> list = getListByViewIds(info, WECHAT_OPEN_LUCKY_MONEY_IDS);
        AccessibilityNodeInfo tempNode = null;
        for (int i = 0; i < list.size(); i++) {
            tempNode = list.get(i);
            XLog.e("WechatAccService", "e2ee" + tempNode.isVisibleToUser() + "-" + tempNode.isEnabled());
            if ("android.widget.Button".equals(tempNode.getClassName()) && tempNode.isVisibleToUser()) {
                return tempNode;
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo getWechatRedEnvelopeOpenDetailNode(AccessibilityNodeInfo info) {
        if (info == null)
            return null;
        // 红包已经被打开过了
        List<AccessibilityNodeInfo> list = info.findAccessibilityNodeInfosByViewId("com.tencent.mm:id/aqx");
        AccessibilityNodeInfo tempNode = null;
        for (int i = 0; i < list.size(); i++) {
            tempNode = list.get(i);
            XLog.e("WechatAccService", "eee" + tempNode.isVisibleToUser() + "-" + tempNode.isEnabled());
            if ("android.widget.TextView".equals(tempNode.getClassName()) && tempNode.isVisibleToUser()) {
                return tempNode;
            }
        }
        return null;
    }

    /**
     * 返回红包接收页面，关闭按钮
     * @param info
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo getWechatLuckyMoneyReceivePageCloseImage(AccessibilityNodeInfo info) {
        if (info == null)
            return null;
        List<AccessibilityNodeInfo> list = getListByViewIds(info, WECHAT_LUCKY_MONEY_RECEIVE_CLOSE_IDS);
        AccessibilityNodeInfo tempNode = null;
        for (int i = 0; i < list.size(); i++) {
            tempNode = list.get(i);
            if ("android.widget.ImageView".equals(tempNode.getClassName()) && tempNode.isVisibleToUser()) {
                return tempNode;
            }
        }
        return null;
    }

    /**
     * 返回红包详情页，返回按钮
     * @param info
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo getWechatLuckyMoneyDetailPageBackImage(AccessibilityNodeInfo info) {
        if (info == null)
            return null;
        List<AccessibilityNodeInfo> list = getListByViewIds(info, WECHAT_LUCKY_MONEY_DETAIL_BACK_IDS);
        AccessibilityNodeInfo tempNode = null;
        for (int i = 0; i < list.size(); i++) {
            tempNode = list.get(i);
            if ("android.widget.LinearLayout".equals(tempNode.getClassName()) && tempNode.isVisibleToUser()) {
                return tempNode;
            }
        }
        return null;
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isWechatRedEnvelopeOpenNode(AccessibilityNodeInfo info) {
        if (info == null)
            return false;
        String residName = info.getViewIdResourceName();
        if ("com.tencent.mm:id/amt".equals(residName)) {
            if ("android.widget.Button".equals(info.getClassName())) {
                return true;
                /*AccessibilityNodeInfo infoChild22 = info.getChild(0);
                XLog.e(TAG, "red main layout2 "+infoChild22.getChildCount());
				if (infoChild22 != null && infoChild22.getChildCount() == 2 && "android.widget.RelativeLayout".equals(infoChild22.getClassName())) {
					XLog.e(TAG, "red main layout3");
					AccessibilityNodeInfo infoChild30 = infoChild22.getChild(0);
					if (infoChild30 != null && "微信红包".equals(infoChild30.getText() == null ? "" : infoChild30.getText().toString())) {
						XLog.e(TAG, "red main layout4");
						return true;

					}
				}*/
            }
        }
        return false;
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static boolean isWechatRedEnvelopeNode(AccessibilityNodeInfo info) {
        if (info == null)
            return false;
        String residName = info.getViewIdResourceName();
        if ("com.tencent.mm:id/s_".equals(residName)) {
            if ("android.widget.LinearLayout".equals(info.getClassName())) {// 是3,与层次图显示的不一致
                return true;
                /*AccessibilityNodeInfo infoChild22 = info.getChild(0);
				XLog.e(TAG, "red main layout2 "+infoChild22.getChildCount());
				if (infoChild22 != null && infoChild22.getChildCount() == 2 && "android.widget.RelativeLayout".equals(infoChild22.getClassName())) {
					XLog.e(TAG, "red main layout3");
					AccessibilityNodeInfo infoChild30 = infoChild22.getChild(0);
					if (infoChild30 != null && "微信红包".equals(infoChild30.getText() == null ? "" : infoChild30.getText().toString())) {
						XLog.e(TAG, "red main layout4");
						return true;

					}
				}*/
            }
        }
        return false;
    }


    @TargetApi(Build.VERSION_CODES.ICE_CREAM_SANDWICH)
    public static AccessibilityNodeInfo getLastWechatRedEnvelopeNodeByText(AccessibilityNodeInfo info, Context context) {
        if (info == null)
            return null;
        List<AccessibilityNodeInfo> resultList = info.findAccessibilityNodeInfosByText(context.getString(R.string.wechat_acc_service_red_envelope_list_identification));
        if (resultList != null && resultList.isEmpty()) {
            for (int i = resultList.size() - 1; i >= 0; i--) {
                AccessibilityNodeInfo parent = resultList.get(i).getParent();
                if (parent != null) {
                    return parent;
                }
            }
        }
        return null;
    }

    /**
     * 获取最新的一个红包
     * @param info
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static AccessibilityNodeInfo getLastWechatRedEnvelopeNodeById(AccessibilityNodeInfo info) {
        if (info == null)
            return null;
        //TextView com.tencent.mm:id/v8 领取红包,parent:LinearLayout
        List<AccessibilityNodeInfo> list = getListByViewIds(info, WECHAT_LUCKY_MONEY_VIEW_IDS);
        if (list != null) {
            Log.d("wangchong", "lucky money size = " + list.size());
//            // TODO delete
//            StringBuffer sb = new StringBuffer();
//            for (int i = 0; i < list.size(); i++) {
//                sb.append(list.get(i).hashCode());
//                sb.append(" ");
//            }
//            Log.d("wangchong", "HASHCODE: " + sb.toString());
        }
        for (int i = list.size() - 1; i >= 0; i--) {
            if ("android.widget.LinearLayout".equals(list.get(i).getClassName()))
                return list.get(i);
        }
        return null;
    }

    /**
     * 获取显示页面中的所有红包列表
     * @param info
     * @return
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR2)
    public static List<AccessibilityNodeInfo> getLastWechatRedEnvelopeNodeListById(AccessibilityNodeInfo info) {
        if (info == null)
            return null;
        //TextView com.tencent.mm:id/v8 领取红包,parent:LinearLayout
        List<AccessibilityNodeInfo> list = getListByViewIds(info, WECHAT_LUCKY_MONEY_VIEW_IDS);

        List<AccessibilityNodeInfo> luckList = new ArrayList<>();
        for (int i = list.size() - 1; i >= 0; i--) {
            if ("android.widget.LinearLayout".equals(list.get(i).getClassName()))
                luckList.add(list.get(i));
        }
        return luckList;
    }
}
