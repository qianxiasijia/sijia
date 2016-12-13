package com.qianxia.sijia.ui;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.MainCategorySpinnerAdapter;
import com.qianxia.sijia.adapter.RegionSpinnerAdapter;
import com.qianxia.sijia.adapter.StreetSpinnerAdapter;
import com.qianxia.sijia.adapter.SubCategorySpinnerAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.City;
import com.qianxia.sijia.entry.Food;
import com.qianxia.sijia.entry.MainCategoryTable;
import com.qianxia.sijia.entry.Region;
import com.qianxia.sijia.entry.Shop;
import com.qianxia.sijia.entry.SijiaUser;
import com.qianxia.sijia.entry.SubCategory;
import com.qianxia.sijia.entry.SubCategoryTable;
import com.qianxia.sijia.listener.OnRegionLoadedListener;
import com.qianxia.sijia.listener.OnSubCategoriesLoadedListener;
import com.qianxia.sijia.manager.CategoryManager;
import com.qianxia.sijia.manager.RegionManager;
import com.qianxia.sijia.manager.ShopManager;
import com.qianxia.sijia.util.BitmapCompressUtils;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.EditInputUtil;
import com.qianxia.sijia.util.NetUtil;
import com.qianxia.sijia.view.NumberProgressBar;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;
import butterknife.OnClick;
import cn.bmob.v3.BmobPushManager;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.datatype.BmobGeoPoint;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.PushListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UploadBatchListener;

public class PostFoodActivity extends BaseActivity {

    @Bind(R.id.et_postfood_foodname)
    EditText etFoodName;
    @Bind(R.id.tv_postfood_maincategory)
    TextView tvMainCategory;
    @Bind(R.id.tv_postfood_subcategory)
    TextView tvSubCategory;
    @Bind(R.id.tv_postfood_selectshop)
    TextView tvSelectShopName;
    @Bind(R.id.btn_postfood_addshop)
    TextView btnAddShop;
    @Bind(R.id.btn_postfood_selectshop)
    TextView btnSelectShop;
    @Bind(R.id.et_postfood_foodbrief)
    EditText etFoodBrief;

    @Bind(R.id.ll_postfood_foodimglayout)
    LinearLayout layoutFoodImags;
    @Bind(R.id.iv_postfood_foodimg0)
    ImageView foodImage0;
    @Bind(R.id.iv_postfood_foodimg1)
    ImageView foodImage1;
    @Bind(R.id.iv_postfood_foodimg2)
    ImageView foodImage2;
    @Bind(R.id.iv_postfood_foodimg3)
    ImageView foodImage3;
    @Bind(R.id.iv_postfood_foodimg4)
    ImageView foodImage4;
    @Bind(R.id.iv_postfood_foodimg5)
    ImageView foodImage5;
    @Bind(R.id.tv_postfood_foodimagenumber)
    TextView tvFoodImgNum;
    @Bind(R.id.et_postfood_foodprice)
    EditText etPrice;
    @Bind(R.id.npb_postfood_foodprogressbar)
    NumberProgressBar npbFoodProgressBar;

    @Bind(R.id.ll_postfood_layout_addshop)
    LinearLayout layoutAddShop;
    @Bind(R.id.et_postfood_shopname)
    EditText etShopName;
    @Bind(R.id.et_postfood_address)
    EditText etAddress;
    @Bind(R.id.et_postfood_opentime)
    EditText etOpenTime;
    @Bind(R.id.et_postfood_telephone)
    EditText etTelephone;
    @Bind(R.id.tv_postfood_district)
    TextView tvDistrict;
    @Bind(R.id.tv_postfood_street)
    TextView tvStreet;
    @Bind(R.id.et_postfood_shopbrief)
    EditText etShopBrief;


    @Bind(R.id.ll_postfood_shopimglayout)
    LinearLayout layoutShopImgs;
    @Bind(R.id.iv_postfood_shopimg0)
    ImageView shopImage0;
    @Bind(R.id.iv_postfood_shopimg1)
    ImageView shopImage1;
    @Bind(R.id.iv_postfood_shopimg2)
    ImageView shopImage2;
    @Bind(R.id.iv_postfood_shopimg3)
    ImageView shopImage3;
    @Bind(R.id.iv_postfood_shopimg4)
    ImageView shopImage4;
    @Bind(R.id.iv_postfood_shopimg5)
    ImageView shopImage5;
    @Bind(R.id.tv_postfood_shopimagenumber)
    TextView tvShopImgNum;
    @Bind(R.id.npb_postfood_shopprogressbar)
    NumberProgressBar npbShopProgressBar;

    @Bind(R.id.ll_postfood_foodprogress)
    LinearLayout layoutFoodProgress;
    @Bind(R.id.ll_postfood_shopprogress)
    LinearLayout layoutShopProgress;

    private ArrayList<ImageView> foodImgs;
    private ArrayList<ImageView> shopImgs;
    private Shop shop;
    private boolean isPost;
    private List<MainCategoryTable> mainCategories;
    private List<SubCategoryTable> subCategories;
    private List<Region> regions;
    private List<String> streets;

    private MainCategorySpinnerAdapter mainCategoryAdapter;
    private SubCategorySpinnerAdapter subCategoryAdapter;
    private RegionSpinnerAdapter regionAdapter;
    private StreetSpinnerAdapter streetAdapter;

    private DBUtil dbUtil;
    private String subCategoryId;
    private String mainCategoryTableId;
    private String street;
    private String cameraPath;
    private SijiaUser currentUser;
    private ArrayList<String> shopImgPaths;
    private ArrayList<String> foodImgPaths;
    private List<File> tempImgFiles;
    private Food newFood;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_post_food);
    }

    @Override
    protected void init() {
        dbUtil = new DBUtil(this);
        tempImgFiles = new ArrayList<>();
        currentUser = bmobUserManager.getCurrentUser(SijiaUser.class);
        mainCategories = new ArrayList<>();
        mainCategoryAdapter = new MainCategorySpinnerAdapter(this, mainCategories);
        subCategories = new ArrayList<>();
        subCategoryAdapter = new SubCategorySpinnerAdapter(this, subCategories);
        initToolbar();
        initFoodImgLayout();
        initShopImgLayout();
    }

    private void initToolbar() {
        toolbar.setTitle("佳食推荐");
        setSupportActionBar(toolbar);
        toolbar.setNavigationIcon(android.support.v7.appcompat.R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });
        TextView btnPost = (TextView) toolbar.findViewById(R.id.btn_main_toolbar_cityselect);
        btnPost.setText("发表");
        btnPost.setVisibility(View.VISIBLE);
    }


    private void initFoodImgLayout() {
        foodImgs = new ArrayList<>();
        foodImgs.add(foodImage0);
        foodImgs.add(foodImage1);
        foodImgs.add(foodImage2);
        foodImgs.add(foodImage3);
        foodImgs.add(foodImage4);
        foodImgs.add(foodImage5);


        layoutFoodImags.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = layoutFoodImags.getWidth() - layoutFoodImags.getPaddingStart() - layoutFoodImags.getPaddingEnd();
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                int size = (width - margin * 5) / 6;
                for (int i = 0; i < 6; i++) {
                    View view = layoutFoodImags.getChildAt(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                    if (i != 5) {
                        params.setMargins(0, 0, margin, 0);
                    } else {
                        params.setMargins(0, 0, 0, 0);
                    }
                    view.setLayoutParams(params);

                }
                layoutFoodImags.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutFoodImags.requestLayout();
                layoutFoodImags.setVisibility(View.INVISIBLE);
            }
        });
    }


    private void initShopImgLayout() {

        shopImgs = new ArrayList<>();
        shopImgs.add(shopImage0);
        shopImgs.add(shopImage1);
        shopImgs.add(shopImage2);
        shopImgs.add(shopImage3);
        shopImgs.add(shopImage4);
        shopImgs.add(shopImage5);


        layoutShopImgs.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int width = layoutFoodImags.getWidth() - layoutFoodImags.getPaddingStart() - layoutFoodImags.getPaddingEnd();
                int margin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());
                int size = (width - margin * 5) / 6;
                for (int i = 0; i < 6; i++) {
                    View view = layoutShopImgs.getChildAt(i);
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(size, size);
                    if (i != 5) {
                        params.setMargins(0, 0, margin, 0);
                    } else {
                        params.setMargins(0, 0, 0, 0);
                    }
                    view.setLayoutParams(params);

                }
                layoutShopImgs.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                layoutShopImgs.requestLayout();
                layoutShopImgs.setVisibility(View.INVISIBLE);
            }
        });
    }

    @OnClick(R.id.btn_postfood_foodpicture)
    public void selectFoodPic(View v) {
        getImgFromPic(Constant.REQUESTCODE_POSTFOOD_GETPICTURE);
    }

    private void getImgFromPic(int code) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
        startActivityForResult(intent, code);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

    }

    @OnClick(R.id.btn_postfood_foodcamera)
    public void selectFoodCamera(View v) {
        getImgFromCamera(Constant.REQUESTCODE_POSTFOOD_GETCAMERA);
    }

    private void getImgFromCamera(int code) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        File file = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), System.currentTimeMillis() + ".png");
        cameraPath = file.getAbsolutePath();
        Uri imgUri = Uri.fromFile(file);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imgUri);
        startActivityForResult(intent, code);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

    }

    @OnClick(R.id.btn_postfood_shoppicture)
    public void selectShopPic(View v) {
        getImgFromPic(Constant.REQUESTCODE_POSTSHOP_GETPICTURE);
    }

    @OnClick(R.id.btn_postfood_shopcamera)
    public void selectShopCamera(View v) {
        getImgFromCamera(Constant.REQUESTCODE_POSTSHOP_GETCAMERA);
    }

    @OnClick(R.id.ll_postfood_district)
    public void selectRegion(View v) {

        View view = getLayoutInflater().inflate(R.layout.inflate_spinner_layout, null);
        ListView regionListView = (ListView) view.findViewById(R.id.listview_spinner);
        regionListView.setAdapter(regionAdapter);
        final PopupWindow regionSpinnerWindow = new PopupWindow(view, tvDistrict.getWidth(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics()), true);
        regionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Region region = (Region) parent.getAdapter().getItem(position);
                tvDistrict.setText(region.getName());
                tvStreet.setText("");
                tvStreet.setHint("请选择街道...");
                streets = region.getNeighborhoods();
                regionSpinnerWindow.dismiss();
            }
        });

        regionSpinnerWindow.setBackgroundDrawable(new ColorDrawable(0x00));
        regionSpinnerWindow.showAsDropDown(tvDistrict);
//        log(regions.toString());
    }

    @OnClick(R.id.ll_postfood_street)
    public void selectStreet(View v) {
        if (TextUtils.isEmpty(tvDistrict.getText())) {
            toast("请先选择区域");
            return;
        }
        View view = getLayoutInflater().inflate(R.layout.inflate_spinner_layout, null);
        ListView streetListView = (ListView) view.findViewById(R.id.listview_spinner);
        streetAdapter = new StreetSpinnerAdapter(PostFoodActivity.this, streets);
        streetListView.setAdapter(streetAdapter);
        final PopupWindow streetSpinnerWindow = new PopupWindow(view, tvStreet.getWidth(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics()), true);
        streetListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                street = streets.get(position);
                tvStreet.setText(street);
                streetSpinnerWindow.dismiss();
            }
        });

        streetSpinnerWindow.setBackgroundDrawable(new ColorDrawable(0x00));
        streetSpinnerWindow.showAsDropDown(tvStreet);
    }

    @OnClick(R.id.btn_postfood_addshop)
    public void addShopInfo(View v) {
        if (layoutAddShop.getVisibility() == View.GONE) {
            layoutAddShop.setVisibility(View.VISIBLE);
            tvSelectShopName.setVisibility(View.INVISIBLE);
            btnSelectShop.setVisibility(View.INVISIBLE);
            btnAddShop.setText("取消添加");
            regions = new ArrayList<>();
            regionAdapter = new RegionSpinnerAdapter(this, regions);
//            log("SijiaApplication.selectedCity"+SijiaApplication.selectedCity.toString());
            RegionManager.getDistrictsByCity(PostFoodActivity.this, SijiaApplication.selectedCity, new OnRegionLoadedListener() {
                @Override
                public void onRegionLoaded(List<Region> list) {
                    if (list != null && list.size() > 0) {
                        regionAdapter.addAll(list, true);
//                        log(list.toString());
                    }
                }
            });

        } else {
            layoutAddShop.setVisibility(View.GONE);
            btnAddShop.setText("添加佳店");
            tvSelectShopName.setVisibility(View.VISIBLE);
            tvSelectShopName.setText("");
            tvSelectShopName.setHint("请选择佳店");
            btnSelectShop.setVisibility(View.VISIBLE);
        }
    }

    @OnClick(R.id.ll_postfood_maincategory)
    public void selectMainCategory(View v) {
        View view = getLayoutInflater().inflate(R.layout.inflate_spinner_layout, null);
        ListView mainCategoryListView = (ListView) view.findViewById(R.id.listview_spinner);
        mainCategoryListView.setAdapter(mainCategoryAdapter);
        final PopupWindow mainCategorySpinnerWindow = new PopupWindow(view, tvMainCategory.getWidth(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics()), true);
        mainCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                MainCategoryTable mainCategoryTable = (MainCategoryTable) parent.getAdapter().getItem(position);
                tvMainCategory.setText(mainCategoryTable.getName());
                tvSubCategory.setText("");
                tvSubCategory.setHint("请选择次分类...");
                mainCategoryTableId = mainCategoryTable.getId();
                CategoryManager.getSubCategories(PostFoodActivity.this, mainCategoryTableId, new OnSubCategoriesLoadedListener() {
                    @Override
                    public void onSubCategoriesLoaded(List<SubCategoryTable> list) {
                        if (list != null && list.size() > 0) {
                            subCategoryAdapter.addAll(list, true);
                        }
                    }
                });
                mainCategorySpinnerWindow.dismiss();
            }
        });

        mainCategorySpinnerWindow.setBackgroundDrawable(new ColorDrawable(0x00));
        mainCategorySpinnerWindow.showAsDropDown(tvMainCategory);

    }

    @OnClick(R.id.ll_postfood_subcategory)
    public void selectSubCategory(View v) {
        if (TextUtils.isEmpty(tvMainCategory.getText())) {
            toast("请先选择主分类");
            return;
        }
//        log("selectSubCategory");
        View view = getLayoutInflater().inflate(R.layout.inflate_spinner_layout, null);
        ListView subCategoryListView = (ListView) view.findViewById(R.id.listview_spinner);
//        log(subCategories.toString());
        subCategoryListView.setAdapter(subCategoryAdapter);

        final PopupWindow subCategorySpinnerWindow = new PopupWindow(view, tvSubCategory.getWidth(),
                (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 240, getResources().getDisplayMetrics()), true);
        subCategoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                SubCategoryTable subCategoryTable = (SubCategoryTable) parent.getAdapter().getItem(position);
                tvSubCategory.setText(subCategoryTable.getName());
                subCategoryId = subCategoryTable.getId();
                subCategorySpinnerWindow.dismiss();
            }
        });
        subCategorySpinnerWindow.setBackgroundDrawable(new ColorDrawable(0x00));
        subCategorySpinnerWindow.showAsDropDown(tvSubCategory);
//        log("subCategorySpinnerWindow"+subCategorySpinnerWindow.toString());


    }

    @OnClick(R.id.btn_postfood_selectshop)
    public void selectShop(View v) {
        Intent intent = new Intent(PostFoodActivity.this, SearchActivity.class);
        intent.putExtra("from", "postfood");
        startActivityForResult(intent, Constant.REQUESTCODE_GET_POSTSHOP);
        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

    }

    @OnClick(R.id.btn_main_toolbar_cityselect)
    public void Post(View v) {
        if (isPost) {
            return;
        }
        if (layoutAddShop.getVisibility() != View.VISIBLE) {
            PostFoodWithOutAddShop();
        } else {
            PostFoodWithAddShop();
        }
    }

    private void PostFoodWithAddShop() {
        if (!isCorrectFoodInputInfo()) {
            return;
        }
        if (!isCorrectShopInputInfo()) {
            return;
        }
        if (!NetUtil.isNetworkAvailable(this)) {
            toast("网络状况不良，请稍后重试");
            return;
        }
        uploadShopImgs();
    }


    private void PostFoodWithOutAddShop() {
        if (!isCorrectFoodInputInfo()) {
            return;
        }
        if (!NetUtil.isNetworkAvailable(this)) {
            toast("网络状况不良，请稍后重试");
            return;
        }
        uploadFoodImgs();
    }


    private boolean isCorrectFoodInputInfo() {
//        log("CheckFoodInputInfo");
        if (EditInputUtil.isEmpty(etFoodBrief, etFoodName)) {
            return false;
        }
        if (TextUtils.isEmpty(tvMainCategory.getText().toString())) {
            toast("需选择佳食分类");
            return false;
        }
        if (TextUtils.isEmpty(tvSubCategory.getText().toString())) {
            toast("需选择佳食分类");
            return false;
        }
        if (tvSelectShopName.getVisibility() == View.VISIBLE && TextUtils.isEmpty(tvSelectShopName.getText().toString())) {
            toast("请选择佳店或者添加佳店");
            return false;
        }
        if (foodImgs.get(0).getVisibility() != View.VISIBLE) {
            toast("需要添加最少一张佳食的美照~");
            return false;
        }
        return true;
    }

    private boolean isCorrectShopInputInfo() {
        if (EditInputUtil.isEmpty(etShopName, etShopBrief, etAddress)) {
            return false;
        }
        if (TextUtils.isEmpty(tvDistrict.getText().toString())) {
            toast("需选择区域");
            return false;
        }
        if (TextUtils.isEmpty(tvStreet.getText().toString())) {
            toast("需选择街道");
            return false;
        }
        if (shopImgs.get(0).getVisibility() != View.VISIBLE) {
            toast("需要添加最少一张佳食的美照~");
            return false;
        }
        return true;
    }

    private void uploadShopImgs() {
        final List<String> paths = getShopImgPaths();
        npbShopProgressBar.setVisibility(View.VISIBLE);
        isPost = true;
        BmobFile.uploadBatch(this, paths.toArray(new String[paths.size()]), new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list != null && list.size() == paths.size()) {
                    npbShopProgressBar.setVisibility(View.INVISIBLE);
                    getLocationByAddressAndUploadShop(etAddress.getText().toString(), list);
                }
            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                npbShopProgressBar.setProgress(i3);
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
                isPost = false;
            }
        });
    }

    private void getLocationByAddressAndUploadShop(final String address, final List<BmobFile> list) {
        GeoCoder geoCode = GeoCoder.newInstance();
        geoCode.setOnGetGeoCodeResultListener(new OnGetGeoCoderResultListener() {
            @Override
            public void onGetGeoCodeResult(GeoCodeResult geoCodeResult) {
                BmobGeoPoint position = null;
                if (geoCodeResult != null && geoCodeResult.error == SearchResult.ERRORNO.NO_ERROR) {
                    LatLng location = geoCodeResult.getLocation();
                    position = new BmobGeoPoint(location.longitude, location.latitude);
                }
                Shop shopNew = new Shop();
                City city = new City();
                city.setObjectId(SijiaApplication.selectedCity.getCityId());
                final String time = String.valueOf(System.currentTimeMillis());
                shopNew.setLikerNum(0);
                shopNew.setRating(6.0f);
                shopNew.setCity(city);
                shopNew.setAddress(address);
                shopNew.setCommentNum(0);
                shopNew.setDescription(etShopBrief.getText().toString());
                shopNew.setName(etShopName.getText().toString());
                shopNew.setOpenTime(etOpenTime.getText().toString());
                shopNew.setStreet(tvStreet.getText().toString());
                shopNew.setLocationPoint(position);
                shopNew.setTelephone(etTelephone.getText().toString());
                shopNew.setAuthor(currentUser);
                shopNew.setTime(time);
                shopNew.addAllUnique("pics", list);
                shopNew.save(PostFoodActivity.this, new SaveListener() {
                    @Override
                    public void onSuccess() {
                        getUploadShop(time);
                    }

                    @Override
                    public void onFailure(int i, String s) {
                        toastAndLog("网络繁忙，请稍后重试", i, s);
                        isPost = false;
                    }
                });

            }

            @Override
            public void onGetReverseGeoCodeResult(ReverseGeoCodeResult reverseGeoCodeResult) {

            }
        });
        GeoCodeOption option = new GeoCodeOption();
        option.city(SijiaApplication.selectedCity.getCityName());
        option.address(address);
        geoCode.geocode(option);
    }

    private void getUploadShop(String time) {
        ShopManager.getShopByAuthorAndTime(this, time, currentUser, new FindListener<Shop>() {
            @Override
            public void onSuccess(List<Shop> list) {
                if (list != null && list.size() > 0) {
                    shop = list.get(0);
                    uploadFoodImgs();

                } else {
                    toast("网络繁忙，请稍后重试");
                    isPost = false;
                }
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
                isPost = false;
            }
        });
    }

    private void uploadFoodImgs() {
        final List<String> paths = getFoodImgPaths();
        npbFoodProgressBar.setVisibility(View.VISIBLE);
        isPost = true;
        BmobFile.uploadBatch(this, paths.toArray(new String[paths.size()]), new UploadBatchListener() {
            @Override
            public void onSuccess(List<BmobFile> list, List<String> list1) {
                if (list != null && list.size() == paths.size()) {
                    npbFoodProgressBar.setVisibility(View.VISIBLE);
                    newFood = new Food();
                    SubCategory subCategory = new SubCategory();
                    City city = new City();
                    city.setObjectId(SijiaApplication.selectedCity.getCityId());
                    subCategory.setObjectId(subCategoryId);
                    newFood.setLikerNum(0);
                    newFood.setName(etFoodName.getText().toString());
                    newFood.setCommentNum(0);
                    newFood.setShop(shop);
                    newFood.setSubCategory(subCategory);
                    newFood.setAuthor(bmobUserManager.getCurrentUser(SijiaUser.class));
                    newFood.setCity(city);
                    newFood.setDescription(etFoodBrief.getText().toString());
                    float price = 0.0f;
                    String priceStr = etPrice.getText().toString();
                    if (!TextUtils.isEmpty(priceStr)) {
                        price = Float.parseFloat(priceStr.trim());
                    }
                    newFood.setPrice(price);
                    newFood.setRating(6.0f);
                    log(list.toString());
                    newFood.addAllUnique("pics", list);
                    newFood.save(PostFoodActivity.this, new SaveListener() {
                        @Override
                        public void onSuccess() {
                            BmobPushManager bmobPushManager = new BmobPushManager(PostFoodActivity.this);
                            String content = newFood.getDescription();
                            String jsonStr = "{\"tag\":\"notify\",\"content\":\"" + content + "\",\"userId\":\"" + currentUser.getObjectId() + "\",\"city\":\"" + SijiaApplication.selectedCity.getCityName() + "\"}";
                            JSONObject data = null;
                            try {
                                data = new JSONObject(jsonStr);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            bmobPushManager.pushMessage(data, new PushListener() {
                                @Override
                                public void onSuccess() {
                                    log("推送成功");
                                }

                                @Override
                                public void onFailure(int i, String s) {

                                }
                            });
                            toast("发表成功");
                            isPost = false;
                            new Handler().postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    finish();
                                    overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

                                }
                            }, 1000);
                        }

                        @Override
                        public void onFailure(int i, String s) {
                            toastAndLog("网络繁忙，请稍后重试", i, s);
                            isPost = false;
                        }
                    });
                }

            }

            @Override
            public void onProgress(int i, int i1, int i2, int i3) {
                npbFoodProgressBar.setProgress(i3);
            }

            @Override
            public void onError(int i, String s) {
                toastAndLog("网络繁忙，请稍后重试", i, s);
                isPost = false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        List<MainCategoryTable> list = dbUtil.getMainCategories();
        mainCategoryAdapter.addAll(list, true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        log("resultCode:" + resultCode + "," + "requestCode:" + requestCode);
        if (resultCode == RESULT_OK) {
            String filePath = null;
            switch (requestCode) {
                case Constant.REQUESTCODE_POSTFOOD_GETPICTURE:
                    Uri uri = data.getData();
//                    log("uri:"+uri.toString());
                    Cursor cursor = getContentResolver().query(uri, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursor.moveToNext();
                    filePath = cursor.getString(0);
                    cursor.close();
                    showFoodImg(filePath);
                    break;
                case Constant.REQUESTCODE_POSTFOOD_GETCAMERA:
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
                    showFoodImg(filePath);
                    break;
                case Constant.REQUESTCODE_POSTSHOP_GETPICTURE:
                    Uri uriShop = data.getData();
//                    log("uri:"+uri.toString());
                    Cursor cursorShop = getContentResolver().query(uriShop, new String[]{MediaStore.Images.Media.DATA}, null, null, null);
                    cursorShop.moveToNext();
                    filePath = cursorShop.getString(0);
                    cursorShop.close();
                    showShopImg(filePath);
                    break;
                case Constant.REQUESTCODE_POSTSHOP_GETCAMERA:
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
                    showShopImg(filePath);
                    break;
            }
        } else if (resultCode == Constant.REQUESTCODE_SEARCHRESULTT && requestCode == Constant.REQUESTCODE_GET_POSTSHOP) {
            shop = (Shop) data.getSerializableExtra("shop");
//            log("REQUESTCODE_GET_POSTSHOP"+shop.getName());
            tvSelectShopName.setText(shop.getName());
        } else if (resultCode == Constant.RESULT_SHOWBIGIMAGE) {
            switch (requestCode) {
                case Constant.REQUESTCODE_SHOW_POSTFOOD_SHOPIMGS:
                    ArrayList<String> rImgShopPaths = data.getStringArrayListExtra("imgPaths");
//                    shopImgPaths = getShopImgPaths();
//                    log("imgPaths:"+rImgShopPaths.toString());
//                    log("foodImgPaths"+shopImgPaths.size());
                    if (rImgShopPaths.size() == shopImgPaths.size()) {
                        return;
                    }
                    for (ImageView iv : shopImgs) {
                        iv.setImageBitmap(null);
                        iv.setVisibility(View.GONE);
                    }
                    if (rImgShopPaths.size() == 0) {
                        layoutShopProgress.setVisibility(View.GONE);
                        return;
                    } else {
                        for (String imgPath : rImgShopPaths) {
                            showShopImg(imgPath);
                        }
                    }
                    break;
                case Constant.REQUESTCODE_SHOW_POSTFOOD_FOODIMGS:
                    ArrayList<String> rImgFoodPaths = data.getStringArrayListExtra("imgPaths");
//                    foodImgPaths = getFoodImgPaths();
//                    log("imgPaths:"+rImgFoodPaths.toString());
//                    log("foodImgPaths"+foodImgPaths.size());
                    if (rImgFoodPaths.size() == foodImgPaths.size()) {
                        return;
                    }
                    for (ImageView iv : foodImgs) {
                        iv.setImageBitmap(null);
                        iv.setVisibility(View.GONE);
                    }
                    if (rImgFoodPaths.size() == 0) {
                        layoutFoodProgress.setVisibility(View.GONE);
                        return;
                    } else {
                        for (String imgPath : rImgFoodPaths) {
                            showFoodImg(imgPath);
                        }
                    }
                    break;
            }
        }
    }

    private void showShopImg(String filePath) {
        layoutShopProgress.setVisibility(View.VISIBLE);
        for (int i = 0; i < shopImgs.size(); i++) {
            ImageView iv = shopImgs.get(i);
            if (iv.getVisibility() != View.VISIBLE) {
                layoutShopImgs.setVisibility(View.VISIBLE);
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
                tvShopImgNum.setText("图片：" + (i + 1) + "/6");
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        shopImgPaths = getShopImgPaths();
                        Intent intent = new Intent(PostFoodActivity.this, ShowBigImageActivity.class);
                        intent.putStringArrayListExtra("imgPaths", shopImgPaths);
                        intent.putExtra("from", "postfood");
                        startActivityForResult(intent, Constant.REQUESTCODE_SHOW_POSTFOOD_SHOPIMGS);
                        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

                    }
                });
                return;
            }
        }
        toast("最多可添加6张图片");

    }

    private ArrayList<String> getShopImgPaths() {
        ArrayList<String> imgPaths = new ArrayList<>();
        for (ImageView iv : shopImgs) {
            if (iv.getVisibility() == View.VISIBLE) {
                imgPaths.add((String) iv.getTag());
            }
        }
        return imgPaths;
    }

    private void showFoodImg(String filePath) {
        layoutFoodProgress.setVisibility(View.VISIBLE);
        for (int i = 0; i < foodImgs.size(); i++) {
            ImageView iv = foodImgs.get(i);
            if (iv.getVisibility() != View.VISIBLE) {
                layoutFoodImags.setVisibility(View.VISIBLE);
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
                tvFoodImgNum.setText("图片：" + (i + 1) + "/6");
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        foodImgPaths = getFoodImgPaths();
                        Intent intent = new Intent(PostFoodActivity.this, ShowBigImageActivity.class);
                        intent.putExtra("from", "postfood");
                        intent.putStringArrayListExtra("imgPaths", foodImgPaths);
                        startActivityForResult(intent, Constant.REQUESTCODE_SHOW_POSTFOOD_FOODIMGS);
                        overridePendingTransition(R.anim.activity_right_in, R.anim.activity_left_out);

                    }
                });
                return;
            }
        }
        toast("最多可添加6张图片");
    }

    private ArrayList<String> getFoodImgPaths() {
        ArrayList<String> imgPaths = new ArrayList<>();
        for (ImageView iv : foodImgs) {
            if (iv.getVisibility() == View.VISIBLE) {
                imgPaths.add((String) iv.getTag());
            }
        }
        return imgPaths;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        for (File file : tempImgFiles) {
            if (file.exists()) {
                file.delete();
            }
            tempImgFiles = null;
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
