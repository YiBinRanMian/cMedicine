package com.example.cmedicine.db;

import org.litepal.crud.DataSupport;

/**
 * Created by macbookair on 2018/2/26.
 */

public class Search extends DataSupport {

    private String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    private String code;
}
