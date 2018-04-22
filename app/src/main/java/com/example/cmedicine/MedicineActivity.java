package com.example.cmedicine;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cmedicine.db.Favorites;
import com.example.cmedicine.info.Information;
import com.example.cmedicine.util.HttpUtil;
import com.example.cmedicine.util.Utility;

import org.litepal.crud.DataSupport;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class MedicineActivity extends AppCompatActivity{

    private ScrollView medicineLayout;

    private TextView titleMedicine;

    private TextView extractText;

    private TextView pinyinText;

    private TextView sourceText;

    private TextView englishText;

    private TextView originText;

    private TextView aliasText;

    private TextView tasteText;

    private TextView evaluationText;

    private TextView habitatTitle;

    private TextView habitatText;

    private TextView functionText;

    private TextView dosageText;

    private TextView attachmentText;

    private TextView attentionText;

    public DrawerLayout drawerLayout;

    private String code;

    private String name;

    private Button likeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicine);
        medicineLayout = (ScrollView) findViewById(R.id.medicine_layout);
        titleMedicine = (TextView) findViewById(R.id.title_name);
        extractText = (TextView) findViewById(R.id.extract_text);
        pinyinText = (TextView) findViewById(R.id.pinyin_text);
        sourceText = (TextView) findViewById(R.id.source_text);
        englishText = (TextView) findViewById(R.id.english_text);
        originText = (TextView) findViewById(R.id.origin_text);
        aliasText = (TextView) findViewById(R.id.alias_text);
        tasteText = (TextView) findViewById(R.id.taste_text);
        evaluationText = (TextView) findViewById(R.id.evalution_text);
        habitatText = (TextView) findViewById(R.id.habitat_text);
        functionText = (TextView) findViewById(R.id.function_text);
        dosageText = (TextView) findViewById(R.id.dosage_text);
        attachmentText = (TextView) findViewById(R.id.attachment_text);
        attentionText = (TextView) findViewById(R.id.attention_text);
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Button navButton = (Button) findViewById(R.id.nav_button);
        habitatTitle = (TextView) findViewById(R.id.habitat);
        likeButton = (Button) findViewById(R.id.like_button);
        String medicineCode = getIntent().getStringExtra("medicine_code");
        medicineLayout.setVisibility(View.INVISIBLE);
        requestInformetion(medicineCode);
        navButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                drawerLayout.openDrawer(GravityCompat.START);
                Intent intent = new Intent(MedicineActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
        likeButton.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                Utility.handleFavoritesSet(code,name);
                setIcon(code);
            }
        });
    }

    /*
     * 获取药材具体信息
     */
    public void requestInformetion(String Code){
        code = Code;
        setIcon(Code);
        final String infoUrl = "http://www.bencao.com.cn/zhongcaoyao/html/"+Code+".html";
        HttpUtil.sendOkhttpRequest(infoUrl, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MedicineActivity.this, "获取药材信息失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                byte[] responseBytes = response.body().bytes();
                final String newString = new String(responseBytes,"gb2312");
                final Information information = Utility.handleInformationResponse(newString);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (information != null){
                            name = information.basic.name;
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(MedicineActivity.this).edit();
                            editor.putString("information",newString);
                            editor.apply();
                            showMedicineInfo(information);
                        }else {
                            Toast.makeText(MedicineActivity.this, "获取药材信息失败", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    /*
     *显示药材信息
     */
    private void showMedicineInfo(Information information){
        String medName = information.basic.name;
        setInfoText(titleMedicine,medName);
        String extract = "摘录: " + information.basic.extract;
        setInfoText(extractText,extract);
        String pinyin = "拼音: " + information.basic.pinyin;
        setInfoText(pinyinText,pinyin);
        String source = "出处: " + information.detail.source;
        setInfoText(sourceText,source);
        String english = "英文名: " + information.detail.english;
        setInfoText(englishText,english);
        String origin = "来源: " + information.detail.origin;
        setInfoText(originText,origin);
        String alias = "别名: " + information.detail.alias;
        setInfoText(aliasText,alias);
        String taste = "性味: " + information.detail.taste;
        setInfoText(tasteText,taste);
        String evaluation = "各家论述: " + information.detail.evaluation;
        setInfoText(evaluationText,evaluation);
        String habitat = "生境分布: " + information.distribution.Habitat;
        if (!habitat.substring(habitat.indexOf(":")+2).equals("null")){
            habitatText.setText(habitat);
        }else {
            habitatTitle.setVisibility(View.GONE);
            habitatText.setVisibility(View.GONE);
        }
        setInfoText(habitatText,habitat);
        String function = "功能主治: " + information.usage.function;
        setInfoText(functionText,function);
        String dosage = "用法用量: " + information.usage.Dosage;
        setInfoText(dosageText,dosage);
        String attachment = "附方: " + information.usage.attachment;
        setInfoText(attachmentText,attachment);
        String attention = "注意: " + information.usage.attention;
        setInfoText(attentionText,attention);
        medicineLayout.setVisibility(View.VISIBLE);
    }

    /**
     * 设置文本
     */
    private void setInfoText(TextView textView,String st){
        String text = st.substring(st.indexOf(":")+2);
        if (!text.equals("null")){
            textView.setText(st);
        }else {
            textView.setVisibility(View.GONE);
        }
    }

    /**
     * 设置收藏按钮颜色
     */
    private void setIcon(String Code){
        likeButton = (Button) findViewById(R.id.like_button);
        int state = DataSupport.where("mcode = ?", Code).find(Favorites.class).size();
        switch (state){
            case 0:
                likeButton.setBackgroundResource(R.drawable.favourites);
                break;
            case 1:
                likeButton.setBackgroundResource(R.drawable.favourites1);
                break;
        }
    }


}
