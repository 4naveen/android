package com.project.spliceglobal.recallgo.model;

/**
 * Created by Guest on 12/10/2016.
 */

public class Shared {
    String category_name,sharedby,date,completed,uncompletd;
    int id;

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

    public String getCategory_name() {
        return category_name;
    }

    public void setCategory_name(String category_name) {
        this.category_name = category_name;
    }

    public String getSharedby() {
        return sharedby;
    }

    public void setSharedby(String sharedby) {
        this.sharedby = sharedby;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
