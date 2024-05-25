package com.example.imagepro;

import java.util.List;

public class DataModel {
    private List<String> nestedList;
    private List<String> nestedVideoList;
    private String itemText;
    private boolean isExpandable;
    private String checkAlph;
    private double count;

    public DataModel(List<String> itemList, String itemText, List<String> videoList, String checkAlph, double count) {
        this.nestedList = itemList;
        this.itemText = itemText;
        this.nestedVideoList = videoList;
        isExpandable = false;
        this.checkAlph = checkAlph;
        this.count = count;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public String getCheckAlph() {
        return checkAlph;
    }

    public void setCheckAlph(String checkAlph) {
        this.checkAlph = checkAlph;
    }

    public List<String> getNestedList() {
        return nestedList;
    }

    public void setNestedList(List<String> itemList) {
        this.nestedList = itemList;
    }

    public String getItemText() {
        return itemText;
    }

    public void setItemText(String itemText) {
        this.itemText = itemText;
    }

    public boolean isExpandable() {
        return isExpandable;
    }

    public void setExpandable(boolean expandable) {
        isExpandable = expandable;
    }

    public List<String> getNestedVideoList() {
        return nestedVideoList;
    }

    public void setNestedVideoList(List<String> nestedVideoList) {
        this.nestedVideoList = nestedVideoList;
    }
}
