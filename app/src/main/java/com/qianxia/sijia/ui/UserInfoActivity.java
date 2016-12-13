package com.qianxia.sijia.ui;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.Image;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.LruCache;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.qianxia.sijia.R;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.fragment.SijiaDatePickerFragment;
import com.qianxia.sijia.listener.OnBitmapLoadedListener;
import com.qianxia.sijia.util.BitmapCompressUtils;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SiJiaImageLoader;
import com.qianxia.sijia.view.CircleImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;

public class UserInfoActivity extends BaseActivity implements View.OnClickListener, DatePickerDialog.OnDateSetListener {

    @Bind(R.id.iv_userinfo_avatar)
    CircleImageView ivAvatar;
    @Bind(R.id.tv_userinfo_username)
    TextView tvUserName;
    @Bind(R.id.tv_userinfo_nickname)
    TextView tvNickName;
    //    @Bind(R.id.tv_userinfo_birthdate)
//    TextView tvBirthDate;
    @Bind(R.id.tv_userinfo_gender)
    TextView tvGender;
    @Bind(R.id.tv_userinfo_homeregion)
    TextView tvHomeRegion;
    @Bind(R.id.tv_userinfo_signup)
    TextView tvSignUp;
    @Bind(R.id.btn_userinfo_edit)
    Button btnEdit;
    @Bind(R.id.et_userinfo_edit)
    EditText etContent;

    TextView tvBirthDate;

    @Bind(R.id.ll_userinfo_birthdate)
    LinearLayout llBirthdate;
    @Bind(R.id.ll_userinfo_nickname)
    LinearLayout llNickName;
    @Bind(R.id.ll_userinfo_gender)
    LinearLayout llGender;
    @Bind(R.id.ll_userinfo_signup)
    LinearLayout llSignUp;
    @Bind(R.id.ll_userinfo_homeregion)
    LinearLayout llRegion;

    @Bind(R.id.prb_userinfo)
    ProgressBar progressBar;
    @Bind(R.id.ll_userinfo_edit)
    LinearLayout llEdit;

    private SijiaUser user;
    private DBUtil dbUtil;
    private String cameraPath;
    private Uri imgUrl;

    private int position = 0;
    public static int EDIT_NIKENAME = 1;
    public static int EDIT_GENDER = 2;
    public static int EDIT_HOMEREGION = 3;
    public static int EDIT_SIGNUP = 4;
    private File file;
    private LruCache<String, Bitmap> memoryCache;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_user_info);
    }

    @Override
    protected void init() {

        memoryCache = new LruCache<String, Bitmap>(Constant.MEMORYCACHE_MAXSIZE) {
            @Override
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }
        };
        progressBar.setVisibility(View.INVISIBLE);
        initToolbar();
        tvBirthDate = (TextView) findViewById(R.id.tv_userinfo_birthdate);
        user = (SijiaUser) getIntent().getSerializableExtra("user");
        dbUtil = new DBUtil(this);
        initView();
        if (bmobUserManager.getCurrentUser().getObjectId().equals(user.getObjectId())) {
            setViewListeners();
        }
    }

    private void initToolbar() {
        toolbar.setTitle("用户资料");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
    }

    private void setViewListeners() {
        ivAvatar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent1 = new Intent(Intent.ACTION_PICK);
                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");

                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".jpg");
                cameraPath = file.getAbsolutePath();
                imgUrl = Uri.fromFile(file);
                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imgUrl);

                Intent chooser = Intent.createChooser(intent1, "选择头像...");
                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
                startActivityForResult(chooser, Constant.INTENT_CHOOSER_AVATAR);
                overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);


//                Intent intent1 = new Intent(Intent.ACTION_PICK);
//                intent1.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,"image/*");
//
//                Intent intent2 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),System.currentTimeMillis()+".jpg");
//                cameraPath = file.getAbsolutePath();
//                imageUri = Uri.fromFile(file);
//                intent2.putExtra(MediaStore.EXTRA_OUTPUT, imageUri );
//
//                Intent chooser = Intent.createChooser(intent1, "选择头像...");
//                chooser.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{intent2});
//                startActivityForResult(chooser, 101);
//
            }
        });

        llBirthdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (llEdit.getVisibility() == View.VISIBLE) {
                    llEdit.setVisibility(View.GONE);
                }
                new SijiaDatePickerFragment().show(getSupportFragmentManager(), "picke");
//                SijiaDatePickerFragment.getInstance(UserInfoActivity.this).show(getSupportFragmentManager(),"datePicker");
            }
        });
        llNickName.setOnClickListener(this);
        llGender.setOnClickListener(this);
        llSignUp.setOnClickListener(this);
        llRegion.setOnClickListener(this);

    }

    private void initView() {
        tvUserName.setText(user.getUsername());
        String nickName = user.getNick();
        if (nickName == null) {
            nickName = "";
        }
        tvNickName.setText(nickName);
        String birthDate = user.getBirthdate();
        if (birthDate == null) {
            birthDate = "";
        }
        tvBirthDate.setText(birthDate);
        String gender = "女";
        boolean flag = user.isGender();
        if (flag) {
            gender = "男";
        }
        tvGender.setText(gender);
        String homeRegion = user.getRegion();
        if (homeRegion == null) {
            homeRegion = "";
        }
        tvHomeRegion.setText(homeRegion);
        String signup = user.getSignature();
        if (signup == null) {
            signup = "";
        }
        tvSignUp.setText(signup);
        String imageUrl = user.getAvatar();
        if (imageUrl == null) {
            ivAvatar.setImageResource(R.drawable.icon_my_photo);
        } else {
            SiJiaImageLoader.loadImage(imageUrl, ivAvatar, dbUtil, memoryCache);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == Constant.INTENT_CHOOSER_AVATAR) {
            String filePath = "";
            if (data != null) {
                Uri uri = data.getData();
                if (uri == null) {
                    Bundle bundle = data.getExtras();
                    Bitmap bitmap = (Bitmap) bundle.get("data");
                    return;
                }
                if (uri.getPath().equals(imgUrl.getPath())) {
                    filePath = cameraPath;
                } else {
                    Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursor.moveToNext();
                    filePath = cursor.getString(0);
                    cursor.close();
                }

            } else {
                filePath = cameraPath;
            }

            Bitmap bitmap = BitmapCompressUtils.decodeImageFromPath(this, filePath, 320, 480);

            file = new File(SijiaApplication.tempImgDir, System.currentTimeMillis() + ".png");

            try {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }

            final BmobFile bmobFile = new BmobFile(file);
            progressBar.setVisibility(View.VISIBLE);
            bmobFile.uploadblock(this, new UploadFileListener() {
                @Override
                public void onSuccess() {
                    final String url = bmobFile.getUrl();
                    user.setAvatar(url);
                    user.update(UserInfoActivity.this, new UpdateListener() {
                        @Override
                        public void onSuccess() {
                            progressBar.setVisibility(View.INVISIBLE);
                            ImageLoader.getInstance().displayImage(url, ivAvatar, new ImageLoadingListener() {
                                @Override
                                public void onLoadingStarted(String s, View view) {

                                }

                                @Override
                                public void onLoadingFailed(String s, View view, FailReason failReason) {

                                }

                                @Override
                                public void onLoadingComplete(String s, View view, Bitmap bitmap) {
                                    dbUtil.saveBitmap(s, bitmap);
                                    user.setAvatar(s);
                                    file.delete();
                                }

                                @Override
                                public void onLoadingCancelled(String s, View view) {

                                }
                            });
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toastAndLog("网络繁忙，请稍后重试", i, s);
                        }
                    });

                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网络繁忙，请稍后重试", i, s);
                }
            });
//
//                if(arg2!=null){
//                    //图库选图
//                    //对于部分手机来说，在安卓原生的拍照程序基础上做了修改
//                    //导致拍照的照片会随着arg2返回到这里
//                    Uri uri = arg2.getData();
//
//                    if(uri!=null){
//                        if(!uri.getPath().equals(imageUri.getPath())){
//                            //图库
//                            Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null,null,null);
//                            cursor.moveToNext();
//                            filePath = cursor.getString(0);
//                            cursor.close();
//
//                        }else{
//                            //拍照
//                            //拍照的路径依然是cameraPath
//                            filePath = cameraPath;
//                        }
//                    }else{
//                        Bundle bundle = arg2.getExtras();
//                        //bitmap是拍照回来的照片
//                        Bitmap bitmap = (Bitmap) bundle.get("data");
//                        //TODO 将bitmap存储到SD卡
//                    }
//
//                }else{
//                    //相机拍照
//                    filePath = cameraPath;
//                }
//
//                final BmobFile bf = new BmobFile(new File(filePath));
//                bf.uploadblock(UserInfoActivity.this, new UploadFileListener() {
//
//                    @Override
//                    public void onSuccess() {
//                        avatarUrl = bf.getFileUrl(UserInfoActivity.this);
//                        ImageLoader.getInstance().displayImage(avatarUrl, ivAvatar);
//                    }
//
//                    @Override
//                    public void onFailure(int arg0, String arg1) {
//                        toastAndLog("上传头像失败",arg0,arg1);
//                    }
//                });
//            }

        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            llEdit.setVisibility(View.GONE);
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onClick(View v) {
        if (llEdit.getVisibility() == View.GONE) {
            llEdit.setVisibility(View.VISIBLE);
        } else {
            llEdit.setVisibility(View.GONE);
        }

        switch (v.getId()) {
            case R.id.ll_userinfo_nickname:
                position = EDIT_NIKENAME;
                String nickeName = tvNickName.getText().toString();
                if (nickeName != null) {
                    etContent.setText(nickeName);
                }
                break;
            case R.id.ll_userinfo_gender:
                position = EDIT_GENDER;
                String gender = tvGender.getText().toString();
                if (gender != null) {
                    etContent.setText(gender);
                }
                break;
            case R.id.ll_userinfo_homeregion:
                position = EDIT_HOMEREGION;
                String homeRegion = tvHomeRegion.getText().toString();
                if (homeRegion != null) {
                    etContent.setText(homeRegion);
                }
                break;
            case R.id.ll_userinfo_signup:
                position = EDIT_SIGNUP;
                String signup = tvSignUp.getText().toString();
                if (signup != null) {
                    etContent.setText(signup);
                }
                break;
        }
    }

    @OnClick(R.id.btn_userinfo_edit)
    public void Edit(View v) {
        final String content = etContent.getText().toString();
        if (content == null) {
            return;
        }
        if (position == EDIT_NIKENAME) {
            if (content.equals(user.getNick())) {
                llEdit.setVisibility(View.GONE);
                return;
            }
            user.setNick(content);
            user.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    tvNickName.setText(user.getNick());
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网络繁忙，请稍后再试", i, s);
                }
            });
        }
        if (position == EDIT_SIGNUP) {
            if (content.equals(user.getSignature())) {
                llEdit.setVisibility(View.GONE);
                return;
            }
            user.setSignature(content);
            user.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    tvSignUp.setText(user.getSignature());
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网络繁忙，请稍后再试", i, s);
                }
            });
        }
        if (position == EDIT_HOMEREGION) {
            if (content.equals(user.getRegion())) {
                llEdit.setVisibility(View.GONE);
                return;
            }
            user.setRegion(content);
            user.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    tvHomeRegion.setText(user.getRegion());
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网络繁忙，请稍后再试", i, s);
                }
            });
        }
        if (position == EDIT_GENDER) {
            String gender = "";
            if (user.isGender()) {
                gender = "男";
            } else {
                gender = "女";
            }
            if (content.equals(gender)) {
                llEdit.setVisibility(View.GONE);
                return;
            }
            if (content.equals("男")) {
                user.setGender(true);
            } else if (content.equals("女")) {
                user.setGender(false);
            } else {
                toast("只能填写\"男\"或者\"女\"");
                return;
            }
            user.update(this, new UpdateListener() {
                @Override
                public void onSuccess() {
                    tvGender.setText(content);
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("网络繁忙，请稍后再试", i, s);

                }
            });
        }
        llEdit.setVisibility(View.GONE);
    }


    @Override
    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

        final String birthDate = year + "-" + (monthOfYear + 1) + "-" + dayOfMonth;
        if (birthDate.equals(tvBirthDate.getText().toString())) {
            return;
        }
        user.setBirthdate(birthDate);
        user.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
                tvBirthDate.setText(user.getBirthdate());
            }

            @Override
            public void onFailure(int i, String s) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
