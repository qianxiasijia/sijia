package cn.bmob.im.inteface;

import cn.bmob.im.bean.BmobMsg;


/**
 * 上传回调
 *
 * @author smile
 * @ClassName: UploadListener
 * @Description: TODO
 * @date 2014-6-19 下午3:07:25
 */
public abstract interface UploadListener {

    /**
     * 上传开始前，用于先将图片显示出来，然后再上传成功之后再将msg发送出去
     *
     * @param msg :其中的content为拍照或者选择图库中得到的本地图片的地址
     * @return
     * @throws
     * @Title: onStart
     * @Description: TODO
     */
    public abstract void onStart(BmobMsg msg);

    /**
     * 上传成功
     *
     * @param path
     * @return
     * @throws
     * @Title: onSuccess
     * @Description: TODO
     */
    public abstract void onSuccess();


    /**
     * 上传失败
     *
     * @param arg0
     * @param arg1
     * @return
     * @throws
     * @Title: onFailure
     * @Description: TODO
     */
    public abstract void onFailure(int error, String arg1);


}
