package cn.bmob.im.inteface;


/**
 * 播放变化监听
 * 用于提供给开发者调用
 *
 * @author smile
 * @ClassName: VoiceListener
 * @Description: TODO
 * @date 2014-6-30 下午7:54:44
 */
public interface OnPlayChangeListener {

    /**
     * 播放录音开始
     *
     * @param fileName
     * @param isUserSpeaker 是否使用扬声器
     * @return
     * @throws
     * @Title: playGreeting
     * @Description: TODO
     */
    void onPlayStart();

    /**
     * 播放录音结束
     *
     * @param
     * @return
     * @throws
     * @Title: stopPlayback
     * @Description: TODO
     */
    void onPlayStop();


}
