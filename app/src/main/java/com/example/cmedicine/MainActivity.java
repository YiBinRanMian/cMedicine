package com.example.cmedicine;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.hjm.bottomtabbar.BottomTabBar;

public class MainActivity extends AppCompatActivity{
    BottomTabBar mBottomTabBar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mBottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar);
        mBottomTabBar.init(getSupportFragmentManager())
                .setImgSize(40,40)
                //.setFontSize(1)
                .setChangeColor(Color.RED,Color.DKGRAY)
                .addTabItem("目录", R.drawable.ic_category4, ChooseMedFragment.class)
                .addTabItem("收藏",R.drawable.ic_category4, LikeListFragment.class)
                .addTabItem("测试", R.drawable.ic_category, TestFragment.class)
                .setTabBarBackgroundColor(Color.WHITE)
                .isShowDivider(false)
                .setOnTabChangeListener(new BottomTabBar.OnTabChangeListener() {
                    @Override
                    public void onTabChange(int position, String name) {

                    }
                });

    }
}
