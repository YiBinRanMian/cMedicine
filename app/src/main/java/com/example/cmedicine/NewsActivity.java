package com.example.cmedicine;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer1_layout);
        newsName = (TextView) findViewById(R.id.news_name);
        detailText = (TextView) findViewById(R.id.source_text);
        navButton = (Button) findViewById(R.id.nav1_button);
        String newsLink = getIntent().getStringExtra("news_link");
        requestInformation(newsLink);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
                Intent intent = new Intent(NewsActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }


    public void requestInformation(String Link){
        final String newsUrl = Link;
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
                            String newsTitle = newsDetail.getTitle().substring(0,16);
                            String newsContent = newsDetail.getContent();
                            Log.d("test", "run: "+newsTitle.length());
                            newsName.setText(newsTitle);
                            detailText.setText(newsContent);
                        }else {
                            Toast.makeText(NewsActivity.this, "获取信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }
}
