package com.qianxia.sijia.fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.qianxia.sijia.R;
import com.qianxia.sijia.adapter.CitySearchAdapter;
import com.qianxia.sijia.application.SijiaApplication;
import com.qianxia.sijia.entry.CityNameBean;
import com.qianxia.sijia.listener.OnCitySelectedListener;
import com.qianxia.sijia.manager.CityManager;
import com.qianxia.sijia.util.DBUtil;
import com.qianxia.sijia.view.SijiaLetterView;

import java.util.ArrayList;
import java.util.List;

//import com.qianxia.sijia.utils.CityUtil;

/**
 * Created by tarena on 2016/10/17.
 */
public class CitySearchDialogFragment extends DialogFragment implements View.OnClickListener, SijiaLetterView.OnTouchLetterListener {

    private Dialog mDialog;
    private ListView listview;
    private static CitySearchDialogFragment fragment;
    private EditText edSearch;
    private ProgressBar progressBar;
    private CitySearchAdapter adapter;
    private List<CityNameBean> datas;
    private List<CityNameBean> cities;
    private View view;
    private SijiaLetterView letterView;
    private TextView tvLetter;
    private OnCitySelectedListener citySelectedListener;
    private DBUtil dbUtil;


    public CitySearchDialogFragment() {
    }

    public static CitySearchDialogFragment getInstance(Context context) {
        if (fragment == null) {
            synchronized (CitySearchDialogFragment.class) {
                if (fragment == null) {
                    fragment = new CitySearchDialogFragment();
                }
            }
        }
        return fragment;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        citySelectedListener = (OnCitySelectedListener) activity;
        dbUtil = new DBUtil(getContext());

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        //获取城市列表数据
        List<CityNameBean> list = dbUtil.getCities();
//        if (list != null && list.size() > 0) {
        progressBar.setVisibility(View.INVISIBLE);
        cities = list;
        adapter.addAll(cities, true);
//        } else {
//            CityManager.getCities(getContext(), new OnLoadedAllCitiesListener() {
//                @Override
//                public void onLoadedAllCities(List<CityNameBean> list) {
//                    progressBar.setVisibility(View.INVISIBLE);
//                    cities = list;
//                    adapter.addAll(cities, true);
//                }
//            });
//        }
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        initView();

        //设置Dialog样式为全屏
        mDialog = new Dialog(getActivity(), R.style.style_dialog_citysearch);
        mDialog.setContentView(view);
        mDialog.show();

        Window window = mDialog.getWindow();
        window.setGravity(Gravity.BOTTOM); //可设置dialog的位置
        window.getDecorView().setPadding(0, 0, 0, 0); //消除边距

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(lp);

        return mDialog;
    }

    /**
     * 初始化Dialog的界面
     */
    private void initView() {

        LayoutInflater mInflater = getActivity().getLayoutInflater();
        view = mInflater.inflate(R.layout.dialog_fragment_citysearch, null);
        edSearch = (EditText) view.findViewById(R.id.et_citysearchdialogfragment_search);
        progressBar = (ProgressBar) view.findViewById(R.id.pb_citysearchdialogfragment);
        progressBar.setVisibility(View.VISIBLE);
        letterView = (SijiaLetterView) view.findViewById(R.id.slv_citysearchdialogfragment);
        letterView.setOnTouchLetterListener(this);
        tvLetter = (TextView) view.findViewById(R.id.tv_citysearchdialogfragment_letter);

        View headerView = mInflater.inflate(R.layout.header_citysearch_listview, listview, false);
        initHeaderView(headerView);

        listview = (ListView) view.findViewById(R.id.listview_citysearchdialogfragment_cities);
        datas = new ArrayList<>();
        adapter = new CitySearchAdapter(getActivity(), datas);
        listview.addHeaderView(headerView);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                CityNameBean cityNameBean = (CityNameBean) parent.getAdapter().getItem(position);
                SijiaApplication.selectedCity = cityNameBean;
                citySelectedListener.citySelected(cityNameBean);
                dismiss();
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

    @Override
    public void onClick(View v) {
        String cityName = ((TextView) v).getText().toString();
        CityNameBean citySelected = dbUtil.getCity(cityName);
        SijiaApplication.selectedCity = citySelected;
        citySelectedListener.citySelected(citySelected);
        dismiss();
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
}
