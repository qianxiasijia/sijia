package com.qianxia.sijia.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.CitySearchAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.constant.Constant;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.manager.CityManager;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.util.SPUtil;
import com.qianxia.sijia.view.SijiaLetterView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

public class CitySearchActivity extends BaseActivity implements View.OnClickListener, SijiaLetterView.OnTouchLetterListener {


    @Bind(R.id.listview_citysearch_cities)
    ListView listview;
    @Bind(R.id.et_citysearch_search)
    EditText edSearch;
    @Bind(R.id.tv_citysearch_letter)
    TextView tvLetter;
    @Bind(R.id.slv_citysearch)
    SijiaLetterView letterView;
    @Bind(R.id.pb_citysearch)
    ProgressBar progressBar;

    private CitySearchAdapter adapter;
    private List<CityNameBean> datas;
    private List<CityNameBean> cities;
    private DBUtil dbUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        log("CitySearchActivity.onCreate");

    }

    @Override
    protected void setMyContentView() {
        setContentView(R.layout.activity_city_search);
    }

    //
    @Override
    protected void init() {
        dbUtil = new DBUtil(this);
        initToolbar();
        initListView();
        letterView.setOnTouchLetterListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
//        log("CitySearchActivity.onResume");

        //获取城市列表数据
//        List<CityNameBean> list = dbUtil.getCities();
        cities = new ArrayList<>(dbUtil.getCities());
        progressBar.setVisibility(View.INVISIBLE);
//        log("list.toString:"+list.toString());
//        log("cities.toString:"+cities.toString());
        adapter.addAll(cities, true);
//        log("CitySearchActivity.onResumefinish");

    }

    private void initListView() {
        View headerView = getLayoutInflater().inflate(R.layout.header_citysearch_listview, listview, false);
        initHeaderView(headerView);

        datas = new ArrayList<>();
        adapter = new CitySearchAdapter(this, datas);
        listview.addHeaderView(headerView);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    return;
                }
                CityNameBean cityNameBean = (CityNameBean) parent.getAdapter().getItem(position);
                SijiaApplication.selectedCity = cityNameBean;
                String cityStr = cityNameBean.getCityName();
                new SPUtil(CitySearchActivity.this).setSelectedCity(cityStr);
                Intent data = new Intent();
                data.putExtra("city", cityStr);
                setResult(Constant.RESULT_CITYSELECTED, data);
                finish();
                overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

            }
        });

        edSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() < 1) {
                    //EditText中没有内容
                    //清空ListView中的显示
                    adapter.addAll(cities, true);
                } else {
                    String name = s.toString().toUpperCase();
                    List<CityNameBean> result = CityManager.getSearchCity(cities, name);
                    if (result.size() > 0) {
                        adapter.addAll(result, true);
                    }
                }

            }
        });


    }

    //
    private void initHeaderView(View headerView) {
        TextView tvBeijing = (TextView) headerView.findViewById(R.id.tv_dialogcitysearch_beijing);
        TextView tvShanghai = (TextView) headerView.findViewById(R.id.tv_dialogcitysearch_shanghai);
        TextView tvShenzhen = (TextView) headerView.findViewById(R.id.tv_dialogcitysearch_shenzhen);
        TextView tvGuangzhou = (TextView) headerView.findViewById(R.id.tv_dialogcitysearch_guangzhou);
        TextView tvYantai = (TextView) headerView.findViewById(R.id.tv_dialogcitysearch_yantai);
        TextView tvHefei = (TextView) headerView.findViewById(R.id.tv_dialogcitysearch_hefei);
        tvBeijing.setOnClickListener(this);
        tvShanghai.setOnClickListener(this);
        tvShenzhen.setOnClickListener(this);
        tvGuangzhou.setOnClickListener(this);
        tvYantai.setOnClickListener(this);
        tvHefei.setOnClickListener(this);
    }

    //
    private void initToolbar() {

        toolbar.setTitle("城市选择");
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


    @Override
    public void onClick(View v) {
        String cityName = ((TextView) v).getText().toString();
        SijiaApplication.updateSelectedCity(cityName);
//        CityNameBean citySelected = dbUtil.getCity(cityName);
//        SijiaApplication.selectedCity = citySelected;
        Intent data = new Intent();
        data.putExtra("city", cityName);
        setResult(Constant.RESULT_CITYSELECTED, data);
        finish();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);
    }

    @Override
    public void onTouchLetter(String letter) {
        listview.setSelection(adapter.getPositionFromSection(letter.charAt(0)) + 1);
        tvLetter.setVisibility(View.VISIBLE);
        tvLetter.setText(letter);
    }

    @Override
    public void onReleaseLetter() {
        tvLetter.setText("");
        tvLetter.setVisibility(View.INVISIBLE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.activity_left_in, R.anim.activity_right_out);

    }
}
