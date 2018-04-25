package com.example.cmedicine;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmedicine.info.News;
import com.example.cmedicine.info.NewsDetail;
import com.example.cmedicine.util.HttpUtil;
import com.example.cmedicine.util.Utility;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class NewsActivity extends AppCompatActivity {
    private DrawerLayout drawerLayout;
    private TextView newsName;
    private TextView detailText;
    private Button navButton;
    private ScrollView newsLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        newsLayout = (ScrollView) findViewById(R.id.news_layout);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer1_layout);
        newsName = (TextView) findViewById(R.id.news_name);
        detailText = (TextView) findViewById(R.id.source_text);
        navButton = (Button) findViewById(R.id.nav1_button);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        String newsLink = getIntent().getStringExtra("news_link");
        String newsString = prefs.getString(newsLink,null);
        if(newsString!= null){
            NewsDetail newsDetail = Utility.handleNewsDetailResponse(newsString);
            showNewsInfo(newsDetail);
        }else{
            requestInformation(newsLink);
        }
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
                Intent intent = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void requestInformation(String newsUrl){
        HttpUtil.sendOkhttpRequest(newsUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(NewsActivity.this, "获取新闻失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseBytes = response.body().bytes();
                final String newString = new String(responseBytes,"gb2312");
                final NewsDetail newsDetail = Utility.handleNewsDetailResponse(newString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(newsDetail!=null){
                            showNewsInfo(newsDetail);
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(NewsActivity.this).edit();
                            editor.putString(newsUrl,newString);
                            editor.apply();

                        }else {
                            Toast.makeText(NewsActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    public void showNewsInfo(NewsDetail n){
        String newsTitle = n.getTitle();
        if (n.getTitle().length()>16)
            newsTitle = newsTitle.substring(0,16);
        String newsContent = n.getContent();
        newsName.setText(newsTitle);
        detailText.setText(newsContent);
    }
}
