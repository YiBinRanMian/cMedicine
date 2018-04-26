package com.example.cmedicine;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.example.cmedicine.db.Favorites;

import org.litepal.crud.DataSupport;

import java.io.BufferedReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookair on 2018/2/27.
 */


public class LikeListFragment extends Fragment{

    private TextView likeText;

    private ListView likeList;


    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private Favorites selectedFavorites;

    private List<Favorites> favoritesList;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.like, container, false);
        likeText = (TextView) view.findViewById(R.id.like_title);
        likeList = (ListView) view.findViewById(R.id.like_list);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        likeList.setAdapter(adapter);
        Log.d("test", "onCreateView: 1");
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
//        Log.d("test", "onActivityCreated: "+dataList.size());
        super.onActivityCreated(savedInstanceState);
        queryFavorites();
        likeList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String medicineCode = favoritesList.get(position).getMcode();
                if (getActivity() instanceof MainActivity) {
                    Intent intent = new Intent(getActivity(), MedicineActivity.class);
                    intent.putExtra("medicine_code", medicineCode);
                    startActivity(intent);
                    getActivity().finish();
                } else if (getActivity() instanceof MedicineActivity) {
                    MedicineActivity activity = (MedicineActivity) getActivity();
                    activity.drawerLayout.closeDrawers();
                    activity.requestInformetion(medicineCode);
                }
            }
        });
    }

    private void queryFavorites(){
        favoritesList = DataSupport.findAll(Favorites.class);
        if (favoritesList.size()>0){
            Log.d("test", "onCreateView: 5");

            dataList.clear();
            for (Favorites favorites:favoritesList){
                dataList.add(favorites.getName());
            }
            adapter.notifyDataSetChanged();
            likeList.setSelection(0);
        }
    }
}
