package com.qianxia.sijia.listener;

import com.qianxia.sijia.entry.SearchResult;

import java.util.List;

/**
 * Created by Administrator on 2016/11/24.
 */
public interface OnFindResultListener {
    void findResult(List<SearchResult> list);
}
