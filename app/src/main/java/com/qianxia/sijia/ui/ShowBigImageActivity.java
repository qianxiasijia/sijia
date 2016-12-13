package com.qianxia.sijia.ui;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.PhotoViewPagerAdapter;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.util.BitmapCompressUtils;
import com.qianxia.sijia.view.PhotoViewPager;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import uk.co.senab.photoview.PhotoView;

public class ShowBigImageActivity extends BaseActivity {

    @Bind(R.id.viewpager_showbigimage)
    PhotoViewPager viewPager;
    @Bind(R.id.toolbar_showbigimage)
    Toolbar mToolbar;
    @Bind(R.id.tv_toolbar_showbigimage)
    TextView tvImgNum;

    private List<View> views;
    private ArrayList<String> imgPaths;
    private PhotoViewPagerAdapter adapter;
    private String from;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_show_big_image);
    }

    @Override
    protected void init() {
        from = getIntent().getStringExtra("from");
        imgPaths = getIntent().getStringArrayListExtra("imgPaths");
        views = new ArrayList<>();
        initToolbar();
        initViewpager();
    }

    private void initToolbar() {
        mToolbar.setTitle("图片");
        setSupportActionBar(mToolbar);
        mToolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (from.equals("postfood") || from.equals("postcomment")) {
                    Intent data = new Intent();
                    data.putExtra("imgPaths", imgPaths);
                    setResult(Constant.RESULT_SHOWBIGIMAGE, data);
                }
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
        tvImgNum.setText(viewPager.getCurrentItem() + 1 + "/" + imgPaths.size());

    }

    private void initViewpager() {
        for (String imgPath : imgPaths) {
            View view = getLayoutInflater().inflate(R.layout.item_showbigimage_viewpager, viewPager, false);
            PhotoView photoview = (PhotoView) view.findViewById(R.id.photoview_showbigimage_viewpager);
            if (from.equals("postfood") || from.equals("postcomment")) {
                Bitmap bitmap = BitmapCompressUtils.decodeImageFromPath(this, imgPath, 300, 300);
                photoview.setImageBitmap(bitmap);
            } else {
                ImageLoader.getInstance().displayImage(imgPath, photoview);
            }

            views.add(view);
        }
        adapter = new PhotoViewPagerAdapter(this, views);
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                tvImgNum.setText(viewPager.getCurrentItem() + 1 + "/" + imgPaths.size());
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (from.equals("postfood") || from.equals("postcomment")) {
            getMenuInflater().inflate(R.menu.menu_toolbar_showbigimage, menu);
            return true;
        } else {
            return super.onCreateOptionsMenu(menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.menu_toolbar_dele) {
            int position = viewPager.getCurrentItem();
            imgPaths.remove(position);
            views.remove(position);
//            viewPager.removeView(views.get(position));
            adapter.notifyDataSetChanged();
            tvImgNum.setText(viewPager.getCurrentItem() + 1 + "/" + imgPaths.size());
            if (imgPaths.size() == 0) {
                Intent data = new Intent();
                data.putExtra("imgPaths", imgPaths);
                setResult(Constant.RESULT_SHOWBIGIMAGE, data);
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }

            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
