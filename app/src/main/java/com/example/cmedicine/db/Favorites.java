package com.example.cmedicine.db;

import org.litepal.crud.DataSupport;

/**
 * Created by macbookair on 2018/2/27.
 */

public class Favorites extends DataSupport{

    private String mcode;

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getMcode() {
        return mcode;
    }

    public void setMcode(String mcode) {
        this.mcode = mcode;
    }
}
