package com.example.cmedicine.db;

import org.litepal.crud.DataSupport;

/**
 * Created by macbookair on 2018/2/21.
 */

public class Medicine extends DataSupport{
    private int id;
    private String name;
    private String mcode;
    private int initialId;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

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

    public int getInitialId() {
        return initialId;
    }

    public void setInitialId(int initialId) {
        this.initialId = initialId;
    }
}
