package com.qianxia.sijia.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.EmoGridViewAdapter;
import com.qianxia.sijia.adapter.EmoPagerAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.FoodComment;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.ShopComment;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.util.BitmapCompressUtils;
import com.qianxia.sijia.util.EmoUtil;
import com.qianxia.sijia.view.NumberProgressBar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class PostCommentActivity extends BaseActivity {

    @Bind(R.id.et_postcomment_content)
    EditText etContent;
    @Bind(R.id.ll_postcomment_imgcontainer)
    LinearLayout llImgContainer;
    @Bind(R.id.iv_postcomment_commentimg0)
    ImageView ivCommentImg0;
    @Bind(R.id.iv_postcomment_commentimg1)
    ImageView ivCommentImg1;
    @Bind(R.id.iv_postcomment_commentimg2)
    ImageView ivCommentImg2;
    @Bind(R.id.iv_postcomment_commentimg3)
    ImageView ivCommentImg3;
    @Bind(R.id.iv_postcomment_commentimg4)
    ImageView ivCommentImg4;
    @Bind(R.id.iv_postcomment_commentimg5)
    ImageView ivCommentImg5;

    @Bind(R.id.tv_postcomment_imagenumber)
    TextView tvImgNumbers;
    @Bind(R.id.npb_postcomment_progressbar)
    NumberProgressBar npbSendImgs;

    @Bind(R.id.btn_postcomment_add)
    ImageButton btnAdd;
    @Bind(R.id.btn_postcomment_picture)
    ImageButton btnAddPicture;
    @Bind(R.id.btn_postcomment_camera)
    ImageButton btnAddCamera;

    @Bind(R.id.ll_postcomment_emocontainer)
    LinearLayout llEmoContainer;

    private LinearLayout emoLayout;
    private ViewPager emoViewPager;
    private ImageButton btnShowEmos;
    private ViewTreeObserver.OnGlobalLayoutListener mLayoutChangeListener;

    private Food food;
    private Shop shop;
    private String cameraPath;
    private ArrayList<ImageView> imgs;

    private boolean isExpanded;
    private boolean isPost;
    private ArrayList<String> imgPaths;
    private List<File> tempImgFiles;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_post_comment);
    }

    @Override
    protected void init() {
        tempImgFiles = new ArrayList<>();
        String from = getIntent().getStringExtra("from");
        log("from" + from);
        if ("food".equals(from)) {
            food = (Food) getIntent().getSerializableExtra("food");

        } else {
            shop = (Shop) getIntent().getSerializableExtra("shop");
        }
//        setSupportActionBar(toolbar);
        initToolbar();

//        setKeyboardListener();
        initImgsLayout();
        initEmoLayout();
    }

    private void initToolbar() {
        toolbar.setTitle("评论");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
            }
        });

        TextView btnSend = (TextView) toolbar.findViewById(R.id.btn_main_toolbar_cityselect);
        btnSend.setText("发送");
        Toolbar.LayoutParams params = (Toolbar.LayoutParams) btnSend.getLayoutParams();
        params.gravity = Gravity.END | Gravity.CENTER_VERTICAL;
        btnSend.setVisibility(View.VISIBLE);
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isPost) {
                    String content = etContent.getText().toString();
                    if (TextUtils.isEmpty(content) && imgs.get(0).getVisibility() != View.VISIBLE) {
                        toast("想说一点什么呢？");
                        return;
                    }
                    isPost = true;
                    PostImgs();
                }
            }
        });
//        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
//            @Override
//            public boolean onMenuItemClick(MenuItem item) {
//                if(item.getItemId()==R.id.menu_toolbar_comment){
//
//                }
//                return false;
//            }
//        });
    }

    private void setKeyboardListener() {
        mLayoutChangeListener = new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                //判断窗口可见区域大小
                Rect r = new Rect();
                getWindow().getDecorView().getWindowVisibleDisplayFrame(r);
                //如果屏幕高度和Window可见区域高度差值大于整个屏幕高度的1/3，则表示软键盘显示中，否则软键盘为隐藏状态。
                int screenHeight = getResources().getDisplayMetrics().heightPixels;
                int heightDifference = screenHeight - (r.bottom - r.top);
                boolean isKeyboardShowing = heightDifference > screenHeight / 3;
                if (isKeyboardShowing) {
                    llEmoContainer.addView(emoLayout);
                }
            }
        };
        //注册布局变化监听
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(mLayoutChangeListener);
    }

//    @OnClick(R.id.et_postcomment_content)
//    public void addEmoLayout(View view){
//        log("addEmoLayout");
//        if(llEmoContainer.getChildCount()==0){
//                llEmoContainer.addView(emoLayout);
//                llEmoContainer.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//                llEmoContainer.invalidate();
//        }

//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.showSoftInputFromInputMethod(etContent.getWindowToken(),0);
//            final int screenHeight = llEmoContainer.getRootView().getHeight();
//            Rect rect = new Rect();
//            getWindow().getDecorView().getWindowVisibleDisplayFrame(rect);
//            int heightDifference = screenHeight - (rect.bottom - rect.top);
//            if(heightDifference > screenHeight/3){
//                log("heightDifference:"+heightDifference);
//                llEmoContainer.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,heightDifference));
//                llEmoContainer.requestLayout();
//                llEmoContainer.addView(emoLayout);
//            }
//
//        }
//    }

    @OnClick(R.id.btn_postcomment_emolayout_emoshow)
    public void showEmoLayout(View view) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            imm.hideSoftInputFromWindow(etContent.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        if (llEmoContainer.getChildCount() == 0) {
            llEmoContainer.addView(emoLayout);
        } else {
            llEmoContainer.removeAllViews();
        }

    }

    private void initEmoLayout() {
        emoLayout = (LinearLayout) getLayoutInflater().inflate(R.layout.inflate_layout_emolayout_postcomment, llEmoContainer, false);
        emoViewPager = (ViewPager) emoLayout.findViewById(R.id.viewpager_postcomment_emolayout_emo);
        List<View> views = new ArrayList<>();
        int pageno = EmoUtil.emos.size() % 21 == 0 ? EmoUtil.emos.size() / 21 : EmoUtil.emos.size() / 21 + 1;
        for (int i = 0; i < pageno; i++) {
            View view = getLayoutInflater().inflate(R.layout.item_viewpager_postcomment_emogridview, emoLayout, false);
            GridView gridView = (GridView) view.findViewById(R.id.gv_postcomment_emo_gridview);
            int end = Math.min((i + 1) * 21, EmoUtil.emos.size());
            List<String> emos = EmoUtil.emos.subList(i * 21, end);
            EmoGridViewAdapter adapter = new EmoGridViewAdapter(this, emos);
            gridView.setAdapter(adapter);
            gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    String emoName = (String) parent.getAdapter().getItem(position);
                    etContent.append(EmoUtil.getSpannableString(emoName));
                }
            });
            views.add(view);
        }
        EmoPagerAdapter emoPagerAdapter = new EmoPagerAdapter(this, views);
        emoViewPager.setAdapter(emoPagerAdapter);


    }

    private void PostImgs() {
//        log("postimgs");

        if (imgs.get(0).getVisibility() != View.VISIBLE) {
            //没有图片
            postComment("");
            return;
        }
        ArrayList<String> list = getImgPaths();
        final String[] imgPaths = list.toArray(new String[list.size()]);
        if (imgPaths == null) {
            toast("未获得图片，请重试");
        }
        npbSendImgs.setVisibility(View.VISIBLE);
        BmobFile.uploadBatch(this, imgPaths, new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
//                log("uploadimgsonSuccess");
                if (list1 != null && list1.size() == imgPaths.length) {
                    StringBuilder builder = new StringBuilder();
                    for (String str : list1) {
                        builder.append(str).append("&");
                    }
                    npbSendImgs.setVisibility(View.INVISIBLE);
                    postComment(builder.substring(0, builder.length() - 1));
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                npbSendImgs.setProgress(i3);
//                tvImgNumbers.setText();
            }

            @Override
            public void onError(int i, String s) {

            }
        });

    }

    private void initImgsLayout() {
        imgs = new ArrayList<>();
        imgs.add(ivCommentImg0);
        imgs.add(ivCommentImg1);
        imgs.add(ivCommentImg2);
        imgs.add(ivCommentImg3);
        imgs.add(ivCommentImg4);
        imgs.add(ivCommentImg5);

        llImgContainer.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = llImgContainer.getWidth();
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                int size = (width - margin * 5) / 6;
                for (int i = 0; i < 6; i++) {
                    View view = llImgContainer.getChildAt(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                    if (i != 5) {
                        params.setMargins(0, 0, margin, 0);
                    } else {
                        params.setMargins(0, 0, 0, 0);
                    }
                    view.setLayoutParams(params);

                }
                llImgContainer.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                llImgContainer.requestLayout();
                llImgContainer.setVisibility(View.GONE);
            }
        });
    }

    private void postComment(String imgsUrl) {
        if (food != null) {
            FoodComment comment = new FoodComment();
            comment.setAuthor(bmobUserManager.getCurrentUser(SijiaUser.class));
            comment.setContent(etContent.getText().toString());
            comment.setFood(food);
            comment.setPics(imgsUrl);
            comment.setTime(System.currentTimeMillis());
            comment.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    toast("评论发布成功");
                    isPost = false;
                    etContent.setText("");
                    for (int i = 0; i < imgs.size(); i++) {
                        imgs.get(i).setVisibility(View.INVISIBLE);
                    }
                    tvImgNumbers.setText("");
                    updateCommentNum();
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("评论发布失败,请稍后再试", i, s);
                }
            });
        } else {
            ShopComment comment = new ShopComment();
            comment.setAuthor(bmobUserManager.getCurrentUser(SijiaUser.class));
            comment.setContent(etContent.getText().toString());
            comment.setShop(shop);
            comment.setPics(imgsUrl);
            comment.setTime(System.currentTimeMillis());
            comment.save(this, new SaveListener() {
                @Override
                public void onSuccess() {
                    toast("评论发布成功");
                    isPost = false;
                    etContent.setText("");
                    for (int i = 0; i < imgs.size(); i++) {
                        imgs.get(i).setVisibility(View.INVISIBLE);
                    }
                    tvImgNumbers.setText("");
                    updateShopCommentNum();
                }

                @Override
                public void onFailure(int i, String s) {
                    toastAndLog("评论发布失败,请稍后再试", i, s);
                }
            });
        }

    }

    private void updateShopCommentNum() {

        int num = 0;
        if (shop.getCommentNum() != null) {
            num = shop.getCommentNum();
        }
        shop.setCommentNum(num + 1);
        shop.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
//                log("num:"+shop.getCommentNum());
                finish();
            }

            @Override
            public void onFailure(int i, String s) {
                finish();
            }
        });

    }

    private void updateCommentNum() {
        int num = 0;
        if (food.getCommentNum() != null) {
            num = food.getCommentNum();
        }
        food.setCommentNum(num + 1);
        food.update(this, new UpdateListener() {
            @Override
            public void onSuccess() {
//                log("num:"+food.getCommentNum());
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
            }

            @Override
            public void onFailure(int i, String s) {
                finish();
            }
        });

    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_toolbar_main,menu);
//        return true;
//    }

    @OnClick(R.id.btn_postcomment_add)
    public void showAddButtons(View v) {
        if (isExpanded) {
            closeButtons();
        } else {
            openButtons();
        }
    }

    private void openButtons() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_expand);
        btnAddPicture.startAnimation(animation);
        btnAddCamera.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnAddCamera.setVisibility(View.VISIBLE);
                btnAddPicture.setVisibility(View.VISIBLE);
                isExpanded = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
    }

    private void closeButtons() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.button_close);
        btnAddPicture.startAnimation(animation);
        btnAddCamera.startAnimation(animation);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                btnAddCamera.setVisibility(View.INVISIBLE);
                btnAddPicture.setVisibility(View.INVISIBLE);
                isExpanded = false;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

    }

    @OnClick(R.id.btn_postcomment_picture)
    public void selectImgFromPicture(View v) {

        if (Build.VERSION.SDK_INT < 23) {
            getImgFromPicture();
            return;
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUESTCODE_GETPICTURE);
        } else {
            getImgFromPicture();
        }
    }

    private void getImgFromPicture() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, Constant.REQUESTCODE_GETPICTURE);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

    }

    @OnClick(R.id.btn_postcomment_camera)
    public void selectImgFromCamera(View v) {
        if (Build.VERSION.SDK_INT < 23) {
            getImgFromCamera();
        }
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constant.REQUESTCODE_GETCAMERA);
        } else {
            getImgFromCamera();
        }
    }

    private void getImgFromCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
        cameraPath = file.getAbsolutePath();
        Uri imgUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, Constant.REQUESTCODE_GETCAMERA);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            String filePath = null;
            switch (requestCode) {
                case Constant.REQUESTCODE_GETPICTURE:
                    Uri uri = data.getData();
//                    log("uri:"+uri.toString());
                    Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursor.moveToNext();
                    filePath = cursor.getString(0);
                    cursor.close();
                    break;
                case Constant.REQUESTCODE_GETCAMERA:
                    if (data != null) {
                        Bundle bundle = data.getExtras();
                        Bitmap bitmap = (Bitmap) bundle.get("data");
//                        log("bitmap:"+bitmap.toString());
                        try {
                            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(new File(cameraPath)));
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        }
                    }
                    filePath = cameraPath;
                    break;
            }
            showCommentImg(filePath);
        } else if (resultCode == Constant.RESULT_SHOWBIGIMAGE && requestCode == Constant.REQUESTCODE_POSTCOMMENT_SHOWIMG) {
            ArrayList<String> rImgPaths = data.getStringArrayListExtra("imgPaths");
            if (rImgPaths.size() == imgPaths.size()) {
                return;
            }
            for (ImageView iv : imgs) {
                iv.setImageBitmap(null);
                iv.setVisibility(View.GONE);
            }
            if (rImgPaths.size() == 0) {
                tvImgNumbers.setText("");
                return;
            } else {
                for (String imgPath : rImgPaths) {
                    showCommentImg(imgPath);
                }
            }

        }
    }

    private void showCommentImg(String filePath) {
        for (int i = 0; i < imgs.size(); i++) {
            ImageView iv = imgs.get(i);
            if (iv.getVisibility() != View.VISIBLE) {
                llImgContainer.setVisibility(View.VISIBLE);
                Bitmap bitmap = BitmapCompressUtils.decodeImageFromPath(this, filePath, 320, 480);
                iv.setImageBitmap(bitmap);
                iv.setVisibility(View.VISIBLE);
                File file = new File(SijiaApplication.tempImgDir, System.currentTimeMillis() + ".png");
                tempImgFiles.add(file);
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
                iv.setTag(file.getAbsolutePath());
                tvImgNumbers.setText("图片：" + (i + 1) + "/6");
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        imgPaths = getImgPaths();
                        Intent intent = new Intent(PostCommentActivity.this, ShowBigImageActivity.class);
                        intent.putExtra("from", "postcomment");
                        intent.putStringArrayListExtra("imgPaths", imgPaths);
                        startActivityForResult(intent, Constant.REQUESTCODE_POSTCOMMENT_SHOWIMG);
                    }
                });
                return;
            }
        }
        toast("最多可添加6张图片");
    }

    private ArrayList<String> getImgPaths() {
        ArrayList<String> imgPaths = new ArrayList<>();
        for (ImageView iv : imgs) {
            if (iv.getVisibility() == View.VISIBLE) {
                imgPaths.add((String) iv.getTag());
            }
        }
        return imgPaths;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == Constant.REQUESTCODE_GETCAMERA) {
            boolean isPermissived = true;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    isPermissived = false;
                    toast("需要获取授权使用拍照功能");
                    return;
                }
            }
            if (isPermissived) {
                getImgFromCamera();
            }
            return;
        } else if (requestCode == Constant.REQUESTCODE_GETPICTURE) {
            boolean isPermissived = true;
            for (int i : grantResults) {
                if (i == PackageManager.PERMISSION_DENIED) {
                    isPermissived = false;
                    toast("需要获取授权读取存储卡");
                    return;
                }
            }
            if (isPermissived) {
                getImgFromPicture();
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        llImgContainer.setVisibility(View.GONE);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (llEmoContainer.getChildCount() > 0) {
                llEmoContainer.removeAllViews();
                return false;
            } else {
                return super.onKeyDown(keyCode, event);
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (File file : tempImgFiles) {
            if (file.exists()) {
                file.delete();
            }
        }
        tempImgFiles = null;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
