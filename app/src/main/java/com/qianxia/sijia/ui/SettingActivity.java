package com.qianxia.sijia.ui;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.j256.ormlite.table.DatabaseTable;
import com.qianxia.sijia.R;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.fragment.DeleteDialogFrament;
import com.qianxia.sijia.listener.OnDialogClickListener;
import com.qianxia.sijia.util.SPUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.listener.UpdateListener;

public class SettingActivity extends BaseActivity {

    @Bind(R.id.btn_find_setting)
    ImageButton btnFind;
    @Bind(R.id.btn_clear_setting)
    Button btnCacheClear;
    @Bind(R.id.btn_nititycation_setting)
    ImageButton btnNotify;
    @Bind(R.id.btn_zhendong_setting)
    ImageButton btnShake;
    @Bind(R.id.btn_voice_setting)
    ImageButton btnVoice;
    @Bind(R.id.tv_cache_setting)
    TextView tvCacheSize;

    private SPUtil spUtil;
    private SijiaUser currentUser;
    private List<File> cacheFiles;
    private File dbSijia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_setting);
    }

    @Override
    protected void init() {
        currentUser = bmobUserManager.getCurrentUser(SijiaUser.class);
        cacheFiles = new ArrayList<>();
        toolbar.setTitle("设置");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
        initSwitch();
    }

    @Override
    protected void onResume() {
        super.onResume();
        refreshCacheSize();
    }

    private void refreshCacheSize() {
        try {
            dbSijia = getDatabasePath("sijia.db");
            cacheFiles.add(dbSijia);
            long dbSize = getFileSize(dbSijia);
            tvCacheSize.setText("清除磁盘缓存:  " + FormatFileSize(dbSize));

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void initSwitch() {
        spUtil = new SPUtil(bmobUserManager.getCurrentUser().getObjectId());
        if (currentUser.isAllowFinded()) {
            btnFind.setBackgroundResource(R.drawable.ic_switch_on);
        } else {
            btnFind.setBackgroundResource(R.drawable.ic_switch_off);
        }

        if (spUtil.isAllowNotify()) {
            btnNotify.setBackgroundResource(R.drawable.ic_switch_on);
            btnVoice.setClickable(true);
            btnShake.setClickable(true);
        } else {
            btnNotify.setBackgroundResource(R.drawable.ic_switch_off);
            btnVoice.setClickable(false);
            btnShake.setClickable(false);
        }
        if (spUtil.isAllowVoice()) {
            btnVoice.setBackgroundResource(R.drawable.ic_switch_on);
        } else {
            btnVoice.setBackgroundResource(R.drawable.ic_switch_off);
        }
        if (spUtil.isAllowShake()) {
            btnShake.setBackgroundResource(R.drawable.ic_switch_on);
        } else {
            btnShake.setBackgroundResource(R.drawable.ic_switch_off);
        }

    }

    @OnClick(R.id.btn_nititycation_setting)
    public void setNotify(View v) {
        if (spUtil.isAllowNotify()) {
            spUtil.setAllowNotify(false);
            spUtil.setAllowShake(false);
            spUtil.setAllowVoice(false);
        } else {
            spUtil.setAllowNotify(true);
        }
        initSwitch();
    }

    @OnClick(R.id.btn_find_setting)
    public void setFind(View v) {
        if (currentUser.isAllowFinded()) {
            currentUser.setAllowFinded(false);
            currentUser.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    btnFind.setBackgroundResource(R.drawable.ic_switch_off);
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网路繁忙，请稍后再试", i, s);
                }
            });

        } else {
            currentUser.setAllowFinded(true);
            currentUser.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    btnFind.setBackgroundResource(R.drawable.ic_switch_on);
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网路繁忙，请稍后再试", i, s);
                }
            });
        }
    }

    @OnClick(R.id.btn_voice_setting)
    public void setVoice(View v) {
        if (spUtil.isAllowVoice()) {
            spUtil.setAllowVoice(false);
            btnVoice.setBackgroundResource(R.drawable.ic_switch_off);
        } else {
            spUtil.setAllowVoice(true);
            btnVoice.setBackgroundResource(R.drawable.ic_switch_on);
        }
    }

    @OnClick(R.id.btn_zhendong_setting)
    public void setShake(View v) {
        if (spUtil.isAllowShake()) {
            spUtil.setAllowShake(false);
            btnShake.setBackgroundResource(R.drawable.ic_switch_off);
        } else {
            spUtil.setAllowShake(true);
            btnShake.setBackgroundResource(R.drawable.ic_switch_on);
        }
    }

    //    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_toolbar_main,menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        switch (item.getItemId()) {
//            case android.support.v7.appcompat.R.id.home:
//                finish();
//                break;
//            case R.id.menu_toolbar_search:
//                jumpTo(SearchActivity.class,false);
//                break;
//        }
//        return true;
//    }
    @OnClick(R.id.btn_clear_setting)
    public void clearCache(View view) {
        DeleteDialogFrament dialogFrament = new DeleteDialogFrament();
        dialogFrament.setOnDialogClickListener(new OnDialogClickListener() {
            @Override
            public void onDialogPositiveClick(DialogFragment dialog) {
                deleteDatabase("sijia.db");
                refreshCacheSize();
            }

            @Override
            public void onDialogNegativeClick(DialogFragment dialog) {

            }
        });
        dialogFrament.show(getSupportFragmentManager(), "clearCache");
    }

    public static long getFileSize(File file) throws IOException {
        long size = 0;
        if (file.isFile() && file.exists()) {
            FileInputStream inputStream = new FileInputStream(file);
            size = inputStream.available();
        }
        return size;
    }


    /**
     * 转换文件大小
     *
     * @param fileS
     * @return
     */
    public static String FormatFileSize(long fileS) {
        DecimalFormat df = new DecimalFormat("#.00");
        String fileSizeString = "";
        String wrongSize = "0B";
        if (fileS == 0) {
            return wrongSize;
        }
        if (fileS < 1024) {
            fileSizeString = df.format((double) fileS) + "B";
        } else if (fileS < 1048576) {
            fileSizeString = df.format((double) fileS / 1024) + "KB";
        } else if (fileS < 1073741824) {
            fileSizeString = df.format((double) fileS / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileS / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
