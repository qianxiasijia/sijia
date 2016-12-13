package com.qianxia.sijia.listener;

/**
 * Created by Administrator on 2016/11/12.
 */
public interface OnReplayListener<T> {
    void onReplay(T t, String title);
}
