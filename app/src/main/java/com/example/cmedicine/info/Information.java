package com.example.cmedicine.info;

/**
 * Created by macbookair on 2018/2/24.
 */

public class Information {

    public Basic basic = new Basic();

    public Detail detail = new Detail();

    public Distribution distribution = new Distribution();

    public Usage usage = new Usage();

    @Override
    public String toString() {
        return "Information{" +
                "basic=" + basic.toString() +
                ", detail=" + detail.toString() +
                ", distribution=" + distribution.toString() +
                ", usage=" + usage.toString() +
                '}';
    }
}
