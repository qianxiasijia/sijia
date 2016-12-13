package cn.bmob.im.inteface;

import cn.bmob.im.bean.BmobInvitation;
import cn.bmob.im.bean.BmobMsg;

/**
 * 事件监听
 *
 * @author smile
 * @ClassName: EventListener
 * @Description: TODO
 * @date 2014-6-6 上午10:02:41
 */
public abstract interface EventListener {

    /**
     * 接收到消息
     *
     * @param message
     * @return
     * @throws
     * @Title: onMessage
     * @Description: TODO
     */
    public abstract void onMessage(BmobMsg message);

    /**
     * 已读回执
     *
     * @param conversionId
     * @param msgTime
     * @return
     * @throws
     * @Title: onReaded
     * @Description: TODO
     */
    public abstract void onReaded(String conversionId, String msgTime);

    /**
     * 网络改变
     *
     * @param isNetConnected
     * @return
     * @throws
     * @Title: onNetChange
     * @Description: TODO
     */
    public abstract void onNetChange(boolean isNetConnected);

    /**
     * 好友请求
     *
     * @param message
     * @return
     * @throws
     * @Title: onAddUser
     * @Description: TODO
     */
    public abstract void onAddUser(BmobInvitation message);

    /**
     * 下线通知
     *
     * @param
     * @return
     * @throws
     * @Title: onOffline
     * @Description: TODO
     */
    public abstract void onOffline();
}
