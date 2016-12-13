//package com.qianxia.sijia.fragment;
//
//
//import android.content.Context;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.v4.app.Fragment;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ListView;
//
//import com.qianxia.sijia.R;
//import com.qianxia.sijia.adapter.SijiaMainCategoryAdapter;
//import com.qianxia.sijia.entry.MainCategory;
//import com.qianxia.sijia.interfaces.OnFragmentSwitchListener;
//import com.qianxia.sijia.manager.CategroyManager;
//import com.qianxia.sijia.manager.PopupWindowManager;
//
//import java.util.List;
//
///**
// * A simple {@link Fragment} subclass.
// */
//public class HomePagerFragment extends BaseFragment {
//
//
//    public HomePagerFragment() {
//        // Required empty public constructor
//    }
//    private ListView categoryListView;
//    OnFragmentSwitchListener mFragmentSwitchListener;
//
//    @Override
//    public void onAttach(Context context) {
//        mFragmentSwitchListener=(OnFragmentSwitchListener)context;
//        super.onAttach(context);
//    }
//
//    @Override
//    public void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        Log.i("TAG","onCreate");
//    }
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        contentView = inflater.inflate(R.header1_fooddetail_refreshlsv.fragment_food, container, false);
//        initUI();
//        setListViewListener();
//        return contentView;
//    }
//
//    private void setListViewListener() {
//        categoryListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                PopupWindowManager.showSubCategoryPopupWindow(getContext(),view,position,mFragmentSwitchListener);
//            }
//        });
//    }
//
//    private void initUI() {
//        initToobar(R.drawable.icon_my_photo,20,true);
//        categoryListView= (ListView) contentView.findViewById(R.id.ListViewInFoodCategoryId);
//        SijiaMainCategoryAdapter adapter = new SijiaMainCategoryAdapter(getContext());
//        categoryListView.setAdapter(adapter);
//        List<MainCategory> categroys =CategroyManager.getMainCategroy();
//        adapter.setDatas(categroys);
//        adapter.notifyDataSetChanged();
//    }
//
//}
