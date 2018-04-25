package com.example.cmedicine;

import android.content.Intent;
import android.os.Handler;
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
//import com.jwenfeng.library.pulltorefresh.BaseRefreshListener;
//import com.jwenfeng.library.pulltorefresh.PullToRefreshLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import in.srain.cube.views.ptr.PtrClassicDefaultFooter;
import in.srain.cube.views.ptr.PtrClassicDefaultHeader;
import in.srain.cube.views.ptr.PtrDefaultHandler;
import in.srain.cube.views.ptr.PtrDefaultHandler2;
import in.srain.cube.views.ptr.PtrFrameLayout;
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
    private List<String> dataList1 = new ArrayList<>();
    private List<News> newsList;
    private PtrFrameLayout ptrFrameLayout;
    private String page;
    private List<News> news;
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.infolayout,container,false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        listView = (ListView) view.findViewById(R.id.list_view);
        newsList = new ArrayList<>();
        news = new ArrayList<>();
        adapter = new ArrayAdapter<>(getContext(),android.R.layout.simple_list_item_1,dataList1);
        listView.setAdapter(adapter);
        ptrFrameLayout = (PtrFrameLayout) view.findViewById(R.id.ptr_frame_layout);
//配置头部参数,可以在xml中设置
// the following are default settings
        ptrFrameLayout.setResistance(1.7f);
        ptrFrameLayout.setRatioOfHeaderHeightToRefresh(1.2f);
        ptrFrameLayout.setDurationToClose(200);
        ptrFrameLayout.setDurationToCloseHeader(1000);
// default is false
        ptrFrameLayout.setPullToRefresh(false);
// default is true
        ptrFrameLayout.setKeepHeaderWhenRefresh(true);
        PtrClassicDefaultHeader ptrClassicDefaultHeader = new PtrClassicDefaultHeader(getActivity());
        ptrFrameLayout.setHeaderView(ptrClassicDefaultHeader);
        PtrClassicDefaultFooter ptrClassicDefaultFooter = new PtrClassicDefaultFooter(getActivity());
        ptrFrameLayout.setFooterView(ptrClassicDefaultFooter);
        ptrFrameLayout.addPtrUIHandler(ptrClassicDefaultHeader);
        ptrFrameLayout.addPtrUIHandler(ptrClassicDefaultFooter);
        page = "1";
        queryNews(page);
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
        ptrFrameLayout.setPtrHandler(new PtrDefaultHandler2() {
            @Override
            public void onLoadMoreBegin(PtrFrameLayout frame) {

                if(page.equals("1")){
                    page = "_2";
                }
                else{
                    int index = Integer.parseInt(page.substring(1));
                    index = index + 1;
                    page = "_"+index;
                }
                queryNews(page);
                frame.postDelayed(ptrFrameLayout::refreshComplete, 2000);

            }

            @Override
            public void onRefreshBegin(PtrFrameLayout frame) {
                page = "1";
                dataList1.clear();
                queryNews(page);
                frame.postDelayed(ptrFrameLayout::refreshComplete, 2000);
            }
        });
        ptrFrameLayout.setMode(PtrFrameLayout.Mode.BOTH);
    }

    private void queryNews(String page){
        titleText.setText("每日资讯");
        if(page.equals("1"))
            page = "";
        HttpUtil.sendOkhttpRequest("https://www.zhzyw.com/zyxx/zyxw/index"+page+".html", new Callback() {
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
                news = Utility.handleNewsResponse(newString);
                if (news != null) {
                    newsList.addAll(news);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        List<String> dataList2 = new ArrayList<>();
                        for (News newsList1:news){
                            dataList2.add(newsList1.getNewsName());
                        }
                        dataList1.addAll(dataList2);
                        adapter.notifyDataSetChanged();
                    }
                });
            }
        });
    }
}
