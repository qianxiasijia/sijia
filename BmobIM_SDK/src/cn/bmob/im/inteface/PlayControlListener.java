package cn.bmob.im.inteface;

import android.media.MediaPlayer;

/**
 * 语音控制监听
 *
 * @author smile
 * @ClassName: VoiceListener
 * @Description: TODO
 * @date 2014-6-30 下午7:54:44
 */
public interface PlayControlListener {

    /**
     * 播放录音
     *
     * @param View:点击播放的那个View
     * @param fileName
     * @param isUserSpeaker    是否使用扬声器
     * @return
     * @throws
     * @Title: playGreeting
     * @Description: TODO
     */
    void playRecording(String fileName, boolean isUserSpeaker);

    /**
     * 停止播放
     *
     * @param
     * @return
     * @throws
     * @Title: stopPlayback
     * @Description: TODO
     */
    void stopPlayback();

    /**
     * 是否处于播放状态
     *
     * @param
     * @return
     * @throws
     * @Title: pausePlayback
     * @Description: TODO
     */
    boolean isPlaying();

    /**
     * 获取播放时长
     *
     * @param
     * @return int
     * @throws
     * @Title: getPlaybackDuration
     * @Description: TODO
     */
    int getPlaybackDuration();

    /**
     * 获取播放器
     *
     * @param
     * @return MediaPlayer
     * @throws
     * @Title: getMediaPlayer
     * @Description: TODO
     */
    MediaPlayer getMediaPlayer();

}
