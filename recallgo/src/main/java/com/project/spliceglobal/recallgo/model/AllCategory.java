package com.project.spliceglobal.recallgo.model;

/**
 * Created by Personal on 9/15/2017.
 */

public class AllCategory {

    String category_name,completed,uncompletd,color;
    int id;

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getUncompletd() {
        return uncompletd;
    }

    public void setUncompletd(String uncompletd) {
        this.uncompletd = uncompletd;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }
}
