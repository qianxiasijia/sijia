package cn.bmob.im.inteface;

/**
 * 转换回调监听
 *
 * @author smile
 * @ClassName: DownloadListener
 * @Description: TODO
 * @date 2014-7-3 上午11:27:32
 */
public abstract interface SwitchListener {

    public abstract void onSuccess(String shortUrl);

    public abstract void onError(String error);

}
