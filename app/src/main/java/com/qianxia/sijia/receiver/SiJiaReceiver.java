package com.qianxia.sijia.receiver;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.ui.MainActivity;
import com.qianxia.sijia.util.SPUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.im.BmobUserManager;
import cn.bmob.im.inteface.EventListener;
import cn.bmob.push.PushConstants;

/**
 * Created by Administrator on 2016/11/3.
 */
public class SiJiaReceiver extends BroadcastReceiver {

    private static List<EventListener> listeners = new ArrayList<>();

    public static void regist(EventListener listener) {
        listeners.add(listener);
    }

    public static void unRegist(EventListener listener) {
        listeners.remove(listener);
    }

    private SPUtil spUtil = new SPUtil(BmobUserManager.getInstance(SijiaApplication.context).getCurrentUserObjectId());


    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (PushConstants.ACTION_MESSAGE.equals(action)) {
            String message = intent.getStringExtra(PushConstants.EXTRA_PUSH_MESSAGE_STRING);
            Log.i("SIJIADEBUG", "MyReceiver收到的内容：" + message);
            try {
                JSONObject jsonObject = new JSONObject(message);
                String tag = jsonObject.getString("tag");
                Log.i("SIJIADEBUG", "tag:" + tag);
                if ("offline".equals(tag)) {
                    if (listeners.size() > 0) {
                        for (EventListener listener : listeners) {
                            listener.onOffline();
                        }
                    } else {
                        SijiaApplication.logout();
                    }
                } else if ("notify".equals(tag)) {
                    String content = jsonObject.getString("content");
                    String city = jsonObject.getString("city");
                    String userId = jsonObject.getString("userId");
                    Log.i("SIJIADEBUG", "content:" + content + ",city:" + city + ",userId:" + userId);
                    if (spUtil.isAllowNotify() && city.equals(SijiaApplication.selectedCity.getCityName())) {
                        String currentUserId = BmobUserManager.getInstance(SijiaApplication.context).getCurrentUserObjectId();
                        if (currentUserId != null && userId.equals(currentUserId)) {
                            return;
                        }
                        NotificationManager manager = (NotificationManager) SijiaApplication.context.getSystemService(Context.NOTIFICATION_SERVICE);
                        Notification notification = new Notification.Builder(SijiaApplication.context)
                                .setSmallIcon(R.drawable.ic_back).setTicker(content).setContentTitle("新品发布").setContentText(content)
                                .setContentIntent(PendingIntent.getActivity(SijiaApplication.context, 0, new Intent(SijiaApplication.context, MainActivity.class).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),
                                        PendingIntent.FLAG_UPDATE_CURRENT)).setWhen(System.currentTimeMillis()).build();

//                        long when = System.currentTimeMillis();
//                        Intent intent = new Intent(globalContext, targetClass);
//                        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//                        PendingIntent contentIntent = PendingIntent.getActivity(globalContext, 0,intent, PendingIntent.FLAG_UPDATE_CURRENT);
//                        Notification notification = new Notification.Builder(globalContext).setSmallIcon(icon).setTicker(tickerText).setContentTitle(contentTitle).setContentText(contentText)
//                                .setWhen(when).setContentIntent(contentIntent).build();
////		Notification notification = new Notification(icon, tickerText, when);
                        notification.flags = Notification.FLAG_AUTO_CANCEL;
                        if (spUtil.isAllowVoice()) {
                            // 设置默认声音
                            notification.defaults |= Notification.DEFAULT_SOUND;
                        }
                        if (spUtil.isAllowShake()) {
                            // 设定震动(需加VIBRATE权限)
                            notification.defaults |= Notification.DEFAULT_VIBRATE;
                        }
                        manager.notify(101, notification);
//                        notification.contentView = null;
//
//
////		notification.setLatestEventInfo(globalContext,contentTitle,	contentText, contentIntent);
//                        mNotificationManager.notify(NOTIFY_ID, notification);// 通知一下才会生效哦
////                        BmobNotifyManager manager =BmobNotifyManager.getInstance(SijiaApplication.context);
////                        manager.showNotify(spUtil.isAllowVoice(),spUtil.isAllowShake(), R.drawable.ic_back,
////                                content,"新品发布",content, MainActivity.class);
                    }
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

    }
}
