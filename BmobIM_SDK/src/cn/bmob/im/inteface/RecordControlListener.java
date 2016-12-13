package cn.bmob.im.inteface;

import android.media.MediaRecorder;

/**
 * 录音控制监听
 *
 * @author smile
 * @ClassName: VoiceListener
 * @Description: TODO
 * @date 2014-6-30 下午7:54:44
 */
public interface RecordControlListener {

    /**
     * 开始录音
     *
     * @param fileName
     * @return void
     * @throws
     * @Title: startRecord
     * @Description: TODO
     */
    public void startRecording(String chatObjectId);

    /**
     * 取消录音
     *
     * @param
     * @return void
     * @throws
     * @Title: giveUpRecordIng
     * @Description: TODO
     */
    public void cancelRecording();

    /**
     * 停止录音
     *
     * @param
     * @return int
     * @throws
     * @Title: stopRecording
     * @Description: TODO
     */
    public int stopRecording();

    /**
     * 当前是否是处于录音状态
     *
     * @param
     * @return boolean
     * @throws
     * @Title: isRecording
     * @Description: TODO
     */
    public boolean isRecording();

    /**
     * 获取录音器
     *
     * @param
     * @return MediaRecorder
     * @throws
     * @Title: getMediaRecorder
     * @Description: TODO
     */
    public MediaRecorder getMediaRecorder();

    /**
     * 获取此次录音文件的完整地址
     *
     * @param
     * @return String
     * @throws
     * @Title: getVoicePath
     * @Description: TODO
     */
    public String getRecordFilePath(String chatObjectId);

}
