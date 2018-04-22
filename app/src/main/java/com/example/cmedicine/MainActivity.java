package com.example.cmedicine;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.cmedicine.info.News;
import com.example.cmedicine.util.HttpUtil;
import com.example.cmedicine.util.Utility;
import com.hjm.bottomtabbar.BottomTabBar;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity{
    BottomTabBar mBottomTabBar;

    private List<News> newsList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


//        Button button = findViewById(R.id.test);
//        button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                HttpUtil.sendOkhttpRequest("https://www.zhzyw.com/zyxx/zyxw/index.html", new Callback() {
//                    @Override
//                    public void onFailure(Call call, IOException e) {
//
//                    }
//
//                    @Override
//                    public void onResponse(Call call, Response response) throws IOException {
//                        byte[] responseBytes = response.body().bytes();
//                        String newString = new String(responseBytes, "GBK");
//                        newsList = Utility.handleNewsResponse(newString);
//                        for (News newsList1:newsList){
//                            Log.d("test", newsList1.getNewsId() + " "+newsList1.getNewsName());
//                        }
//                    }
//                });
//            }
//        });
        mBottomTabBar = (BottomTabBar) findViewById(R.id.bottom_tab_bar);
        mBottomTabBar.init(getSupportFragmentManager())
                .setImgSize(40,40)
                //.setFontSize(1)
                .setChangeColor(Color.RED,Color.DKGRAY)
                .addTabItem("资讯",R.drawable.ic_category4, InfoFragment.class)
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
