//package com.qianxia.sijia.fragment;
//
//import android.os.Bundle;
//import android.support.v4.view.ViewPager;
//import android.util.DisplayMetrics;
//import android.util.Log;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.HorizontalScrollView;
//import android.widget.LinearLayout;
//import android.widget.ListView;
//import android.widget.RadioButton;
//import android.widget.RadioGroup;
//
//import com.qianxia.sijia.R;
//import com.qianxia.sijia.adapter.FoodRankPagerAdapter;
//import com.qianxia.sijia.entry.SubCategory;
////import com.qianxia.sijia.manager.CategroyManager;
//import com.qianxia.sijia.manager.PopupWindowManager;
//
//import java.util.List;
//
//
///**
// * Created by tarena on 2016/9/14.
// */
//public class FoodRankFragment extends BaseFragment implements PopupWindowManager.OnCityChangedListener {
//    private ViewPager viewPager;
//    private String mCityName;
//    private int mPosition;
//    private RadioGroup radioGroup;
//    private String mSubCategoryName;
//    private  List<SubCategory> subCategories;
//    private FoodRankPagerAdapter pagerAdapter;
//    public static final FoodRankFragment getInstance(String cityName,int mainCategroyPositon,String subCategoryName){
//        FoodRankFragment foodRankFragment = new FoodRankFragment();
//        Bundle bundle = new Bundle(3);
//        bundle.putString("cityName",cityName);
//        bundle.putInt("mainCategroyPositon",mainCategroyPositon);
//        bundle.putString("subCategoryName",subCategoryName);
//        foodRankFragment.setArguments(bundle);
//        return  foodRankFragment;
//    }
//
//    @Override
//    public void onCreate(Bundle savedInstanceState) {
//        mCityName = getArguments().getString("cityName");
//        mPosition = getArguments().getInt("mainCategroyPositon");
//        mSubCategoryName = getArguments().getString("subCategoryName");
//        super.onCreate(savedInstanceState);
//        Log.i("TAG","foodRankFragment:onCreate");
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
//        contentView = inflater.inflate(R.header1_fooddetail_refreshlsv.fragment_foodrank,container,false);
//        initialUI();
//        return contentView;
//    }
//
//    private void initialUI() {
//        initToobar(R.drawable.icon_my_photo,-1,true);
//        setRadioGroup();
//        setViewPager();
//        setToobarListener();
//        setRadioGroupListener();
//    }
//
//    private void setToobarListener() {
//        toolbarPhoto.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupWindowManager.showCityChangePopupWindow(getContext(),v,FoodRankFragment.this);
//            }
//        });
//    }
//
//    private void setRadioGroupListener() {
//        final HorizontalScrollView scrollView = (HorizontalScrollView) contentView.findViewById(R.id.foodRankScrollView);
//        DisplayMetrics metrics = new DisplayMetrics();
//        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metrics);
//        final int screenHalf = metrics.widthPixels/2;
//        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(RadioGroup group, int checkedId) {
//                RadioButton checkedRadioButton = (RadioButton) group.findViewById(checkedId);
//
//                int scrollX= scrollView.getScrollX();
//                int left = checkedRadioButton.getLeft();
//                int width = checkedRadioButton.getWidth()/2;
//                int leftScreen = left-scrollX;
//                scrollView.smoothScrollBy(leftScreen-screenHalf+width,0);
//                viewPager.setCurrentItem(group.indexOfChild(checkedRadioButton),true);
//
//            }
//        });
//    }
//
//    private void setViewPager() {
//        viewPager = (ViewPager) contentView.findViewById(R.id.foodRankViewPager);
//        List<ListView> listViews = CategroyManager.getListViews(getContext(),mSubCategoryName,mCityName);
//        pagerAdapter = new FoodRankPagerAdapter(getContext());
//        pagerAdapter.setDatas(listViews);
//        viewPager.setAdapter(pagerAdapter);
//    }
//
//
//    private void setRadioGroup() {
//        radioGroup = (RadioGroup) contentView.findViewById(R.id.categoryRadioGroupInFoodRank);
//        subCategories = CategroyManager.getSubCategroys(mPosition);
//        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.MATCH_PARENT);
//        layoutParams.gravity= Gravity.CENTER;
//        for(int i = 0; i< subCategories.size(); i++){
//            RadioButton radioButton = new RadioButton(getContext());
//            radioButton.setLayoutParams(layoutParams);
//            radioButton.setPadding(9,0,9,0);
//            radioButton.setButtonDrawable(null);
//            radioButton.setText(subCategories.get(i).getCategoryName());
//            radioButton.setTextColor(getResources().getColor(R.color.selector_radiobtn_foodrank));
//            radioGroup.addView(radioButton);
//        }
//        radioGroup.check(radioGroup.getChildAt(0).getId());
//    }
//
//
//    @Override
//    public void updateFoodRankAdapter(String cityName) {
//        List<ListView> listViews = CategroyManager.getListViews(getContext(),mSubCategoryName,cityName);
//        pagerAdapter.updateDatas(listViews);
//
//    }
//}
