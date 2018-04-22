package com.example.cmedicine;

import android.content.Intent;
import android.support.v4.app.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmedicine.info.News;
import com.example.cmedicine.util.HttpUtil;
import com.example.cmedicine.util.Utility;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by macbookair on 2018/4/22.
 */

public class InfoFragment extends Fragment{
    private TextView titleText;
    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> dataList = new ArrayList<>();
    private List<News> newsList;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.infolayout,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        listView = (ListView) view.findViewById(R.id.list_view);
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList);
        listView.setAdapter(adapter);
        queryNews();
        return view;

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String newsLink = newsList.get(position).getNewsId();
                Intent intent = new Intent(getActivity(),NewsActivity.class);
                intent.putExtra("news_link",newsLink);
                startActivity(intent);
                getActivity().finish();
            }
        });
    }

    private void queryNews(){
        titleText.setText("今日资讯");
        HttpUtil.sendOkhttpRequest("https://www.zhzyw.com/zyxx/zyxw/index.html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseBytes = response.body().bytes();
                String newString = new String(responseBytes, "GBK");
                newsList = Utility.handleNewsResponse(newString);
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        for (News newsList1:newsList){
                            dataList.add(newsList1.getNewsName());
                        }
                        adapter.notifyDataSetChanged();
                        listView.setSelection(0);
                    }
                });
            }
        });

    }
}
