package com.example.cmedicine.util;

import android.text.TextUtils;
import android.util.Log;

import com.example.cmedicine.MedicineActivity;
import com.example.cmedicine.db.Favorites;
import com.example.cmedicine.db.Initial;
import com.example.cmedicine.db.Medicine;
import com.example.cmedicine.db.Search;
import com.example.cmedicine.info.Information;
import com.example.cmedicine.info.News;
import com.example.cmedicine.info.NewsDetail;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.litepal.crud.DataSupport;


import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by macbookair on 2018/2/21.
 */

public class Utility {
    private static final int BEGIN = 29;
    private static final int END = 35;

    /**
     * 初始化首字母
     */
    public static void initInitial(){
        for (char i = 'A'; i <= 'Z'; i++) {
            if (i != 'V' && i != 'I' && i != 'O' && i != 'U'){
                Initial initial = new Initial();
                initial.setName(i);
                initial.save();
            }

        }
    }

    /**
     * 解析和处理服务器返回的药名数据
     */
    public static boolean handleMedicineResponse(String response, int initialId){
        if (!TextUtils.isEmpty(response)){
            try {
                String mCode;
                String mName;
                char initialName = DataSupport.find(Initial.class,initialId).getName();
                Document doc = Jsoup.parse(response);
                Element els = doc.select("a[name="+initialName+"]").first().nextElementSibling().nextElementSibling();
                Elements elements = els.children();
                for (Element element:elements){
                    mCode = element.select("a").attr("href");
                    mCode = mCode.substring(BEGIN,END);
                    mName = element.select("a").text();
                    Medicine medicine = new Medicine();
                    medicine.setMcode(mCode);
                    medicine.setName(mName);
                    medicine.setInitialId(initialId);
                    medicine.save();
                }return true;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return false;
    }

    /**
     * 解析和处理服务器返回的药材具体信息数据
     */
    public static Information handleInformationResponse(String response){
        if (!TextUtils.isEmpty(response)){
            try {
                String st1 = "";
                Information information = new Information();
                Document doc = Jsoup.parse(response);
                Element els = doc.select("div[align=center]").first().nextElementSibling();
                while (els.tagName().equals("p")){
                    String textString = els.text();
                    String parttern1 = "【[^】]*】";
                    String st2 = textString.replaceAll(parttern1,"");
                    if (textString.contains("【")){
                        st1 = textString.substring(textString.indexOf("【")+1,textString.indexOf("】"));
                        SetInformation(information,st1,st2);
                    }
                    else{
                        UpdateInformation(information,st1,st2);
                    }
                    els = els.nextElementSibling();
                }
                return information;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的新闻具体信息数据
     */
    public static NewsDetail handleNewsDetailResponse(String response){
        if(!TextUtils.isEmpty(response)){
            try{
                NewsDetail newsDetail = new NewsDetail();
                Document doc = Jsoup.parse(response);
                Element els1 = doc.select("div[id=wzdh]").first().nextElementSibling();
                newsDetail.setTitle(els1.text());
                Log.d("test", newsDetail.getTitle());
                Element els2 = doc.select("div[class=webnr]").first();
                newsDetail.setContent(els2.text());
                return newsDetail;
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 解析和处理服务器返回的新闻列表数据
     */
    public static List<News> handleNewsResponse(String response){
        List<News> newsList = new ArrayList<>();
        if(!TextUtils.isEmpty(response)){
            try{
                Document doc = Jsoup.parse(response);
                Elements els = doc.select("div[class=ullist01]").first().children().first().children();
                for (Element element:els){
                    News news = new News();
                    news.setNewsName(element.text());
                    news.setNewsId("https://www.zhzyw.com/"+element.select("a").attr("href"));
                    Log.d("test", "handleNewsResponse: "+news.getNewsName());
                    newsList.add(news);
                }
                return newsList;
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return null;
    }
    /**
     * 设置Information对象的属性
     */
    private static void SetInformation(Information information,String string1,String string2){
        switch (string1){
            case "名称":
                information.basic.name = string2;
                break;
            case "拼音":
                information.basic.pinyin = string2;
                break;
            case "摘录":
                information.basic.extract = string2;
                break;
            case "出处":
                information.detail.source = string2;
                break;
            case "英文名":
                information.detail.english = string2;
                break;
            case "来源":
                information.detail.origin = string2;
                break;
            case "别名":
                information.detail.alias = string2;
                break;
            case "性味":
                information.detail.taste = string2;
                break;
            case "各家论述":
                information.detail.evaluation = string2;
                break;
            case "生境分布":
                information.distribution.Habitat = string2;
                break;
            case "功能主治":
                information.usage.function = string2;
                break;
            case "用法用量":
                information.usage.Dosage = string2;
                break;
            case "附方":
                information.usage.attachment = string2;
                break;
            case "注意":
                information.usage.attention = string2;
                break;
            case "备注":
                information.usage.attention = string2;
                break;
            default:
        }
    }


    private static void UpdateInformation(Information information,String string1,String string2){
        switch (string1){
            case "名称":
                information.basic.name += "\n" + string2;
                break;
            case "拼音":
                information.basic.pinyin += "\n" + string2;
                break;
            case "摘录":
                information.basic.extract += "\n" + string2;
                break;
            case "出处":
                information.detail.source += "\n" + string2;
                break;
            case "英文名":
                information.detail.english += "\n" + string2;
                break;
            case "来源":
                information.detail.origin += "\n" + string2;
                break;
            case "别名":
                information.detail.alias += "\n" + string2;
                break;
            case "性味":
                information.detail.taste += "\n" + string2;
                break;
            case "各家论述":
                information.detail.evaluation += "\n" + string2;
                break;
            case "生境分布":
                information.distribution.Habitat += "\n" + string2;
                break;
            case "功能主治":
                information.usage.function += "\n" + string2;
                break;
            case "用法用量":
                information.usage.Dosage += "\n" + string2;
                break;
            case "附方":
                information.usage.attachment += "\n" + string2;
                break;
            case "注意":
                information.usage.attention += "\n" + string2;
                break;
            case "备注":
                information.usage.attention += "\n" + string2;
                break;
            default:
        }
    }

    /**
     * 通过药材名解析服务器数据并存储数据库
     */
    public static void handleNameResponse(String response,String name){
        String code = "";
        if(!TextUtils.isEmpty(response)){
            try {
                Document doc = Jsoup.parse(response);
                Element els = doc.select("a[name="+getFirstPinyin(name)+"]").first().nextElementSibling().nextElementSibling();
                Elements elements = els.children();
                for (Element element:elements){
                    if (element.select("a").text().equals(name)){
                        code = element.select("a").attr("href").substring(BEGIN,END);
                        break;
                    }
                }
                Search search = new Search();
                search.setCode(code);
                search.setName(name);
                search.save();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
    /**
     * 返回输入药材名的首字母大写
     */
    private static char getFirstPinyin(String name){
        String pinyinName = "";
        char[] nameChar = name.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        if (nameChar[0] > 128) {
            try {
                pinyinName += PinyinHelper.toHanyuPinyinStringArray(nameChar[0], defaultFormat)[0].charAt(0);
            } catch (BadHanyuPinyinOutputFormatCombination e) {
                e.printStackTrace();
            }
        }else{
            pinyinName += nameChar[0];
        }
        return pinyinName.toUpperCase().toCharArray()[0];
    }

    /**
     * 存储收藏药材
     */
    public static void handleFavoritesSet(String code,String name){
        Favorites favorites = new Favorites();
        Log.d("test", code + " "+ name);
        if (DataSupport.where("mcode = ?",code).find(Favorites.class).size()==0){
            favorites.setMcode(code);
            favorites.setName(name);
            favorites.save();
        }else {
            DataSupport.deleteAll(Favorites.class,"mcode = ?",code);
        }
    }
}
