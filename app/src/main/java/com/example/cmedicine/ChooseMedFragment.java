package com.example.cmedicine;

import android.app.ProgressDialog;
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
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmedicine.db.Initial;
import com.example.cmedicine.db.Medicine;
import com.example.cmedicine.db.Search;
import com.example.cmedicine.util.HttpUtil;
import com.example.cmedicine.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by macbookair on 2018/2/23.
 */

public class ChooseMedFragment extends Fragment {

    public static final int LEVEL_INITIAL = 0;

    public static final int LEVEL_MED = 1;

    private ProgressDialog progressDialog;

    private TextView titleText;

    private Button backButton;

    private ListView listView;

    private SearchView searchView;

    private ArrayAdapter<String> adapter;

    private List<String> dataList = new ArrayList<>();

    private List<Initial> initialList;

    private List<Medicine> medicineList;

    private Initial selectedInitial;

    private int currentLevel;

    private List<Search> searchList;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.choose_med, container, false);
        titleText = (TextView) view.findViewById(R.id.title_text);
        backButton = (Button) view.findViewById(R.id.back_Button);
        listView = (ListView) view.findViewById(R.id.list_view);
        searchView = (SearchView) view.findViewById(R.id.search_med);
        adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, dataList);
        listView.setAdapter(adapter);
        queryInitials();
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                if (currentLevel == LEVEL_INITIAL) {
                    selectedInitial = initialList.get(i);
                    queryMedicine();
                } else if (currentLevel == LEVEL_MED) {
                    String medicineCode = medicineList.get(i).getMcode();
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
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentLevel == LEVEL_MED) {
                    queryInitials();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                queryMedicineFromServer(s);
                String mCode = queryMedicineByName(s);
                closeProgressDialog();
                if (mCode != null) {
                    Intent intent = new Intent(getActivity(), MedicineActivity.class);
                    intent.putExtra("medicine_code", mCode);
                    startActivity(intent);
                    getActivity().finish();
                } else {
                    Toast.makeText(getContext(), "获取药材信息失败", Toast.LENGTH_SHORT).show();
                }
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return true;
            }
        });
    }

    /**
     * 首字母查询
     */
    private void queryInitials() {
        titleText.setText("通过首字母查询");
        backButton.setVisibility(View.GONE);
        initialList = DataSupport.findAll(Initial.class);
        if (initialList.size() > 0) {
            dataList.clear();
            for (Initial initial : initialList) {
                dataList.add(String.valueOf(initial.getName()));
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_INITIAL;
        } else {
            Utility.initInitial();
        }
    }

    /**
     * 药材名查询
     */
    private void queryMedicine() {
        titleText.setText(String.valueOf(selectedInitial.getName()));
        backButton.setVisibility(View.VISIBLE);
        medicineList = DataSupport.where("initialId = ?", String.valueOf(selectedInitial.getId())).find(Medicine.class);
        if (medicineList.size() > 0) {
            dataList.clear();
            for (Medicine medicine : medicineList) {
                dataList.add(medicine.getName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel = LEVEL_MED;
        } else {
            int initialName = selectedInitial.getName();
            String address = "http://www.bencao.com.cn/zhongcaoyao/daquan/index.html";
//            从服务器中查询药材
            queryFromServer(address, "medicine");
        }
    }

    /**
     * 从服务器查询
     */
    private void queryFromServer(String address, final String type) {
        HttpUtil.sendOkhttpRequest(address, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(getContext(), "加载失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseBytes = response.body().bytes();
                boolean result = false;
                if ("medicine".equals(type)) {
                    String newString = new String(responseBytes, "GBK");
                    result = Utility.handleMedicineResponse(newString, selectedInitial.getId());
                }
                if (result) {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("medicine".equals(type)) {
                                queryMedicine();
                            }
                        }
                    });
                }
            }
        });
    }

    /**
     * 显示进度条
     */
    private void showProgressDialog() {
        if (progressDialog == null) {
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("正在加载...");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }

    /**
     * 关闭进度条
     */
    private void closeProgressDialog() {
        if (progressDialog != null) {
            progressDialog.dismiss();
        }
    }

    private String queryMedicineByName(String mName) {
        int time = 0;
        searchList = DataSupport.select("code").where("name = ?", String.valueOf(mName)).find(Search.class);
        while (searchList.size() == 0) {
            time++;
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            searchList = DataSupport.select("code").where("name = ?", String.valueOf(mName)).find(Search.class);
            if (time > 40) {
                break;
            }
        }
        if (searchList.size() > 1) {
            return String.valueOf(searchList.get(0).getCode());
        } else {
            return null;
        }
    }

    private void queryMedicineFromServer(final String mName) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                showProgressDialog();
            }
        });
        HttpUtil.sendOkhttpRequest("http://www.bencao.com.cn/zhongcaoyao/daquan/index.html", new Callback() {
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
                Utility.handleNameResponse(newString, mName);
            }
        });
    }
}