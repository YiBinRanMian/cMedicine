package com.example.cmedicine.db;

import org.litepal.crud.DataSupport;

/**
 * Created by macbookair on 2018/2/21.
 */

public class Initial extends DataSupport {

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public char getName() {
        return name;
    }

    public void setName(char name) {
        this.name = name;
    }

    private int id;
    private char name;
}
