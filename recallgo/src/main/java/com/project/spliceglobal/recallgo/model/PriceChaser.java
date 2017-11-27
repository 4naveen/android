package com.project.spliceglobal.recallgo.model;

/**
 * Created by Personal on 9/21/2017.
 */

public class PriceChaser {
String product_name,product_date,product_url,target_price,Original_rice,current_price;
int id;

    boolean status;

    public String getProduct_date() {
        return product_date;
    }

    public void setProduct_date(String product_date) {
        this.product_date = product_date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProduct_name() {
        return product_name;
    }

    public void setProduct_name(String product_name) {
        this.product_name = product_name;
    }

    public String getProduct_url() {
        return product_url;
    }

    public void setProduct_url(String product_url) {
        this.product_url = product_url;
    }

    public String getTarget_price() {
        return target_price;
    }

    public void setTarget_price(String target_price) {
        this.target_price = target_price;
    }

    public String getOriginal_rice() {
        return Original_rice;
    }

    public void setOriginal_rice(String original_rice) {
        Original_rice = original_rice;
    }

    public String getCurrent_price() {
        return current_price;
    }

    public void setCurrent_price(String current_price) {
        this.current_price = current_price;
    }

    public boolean isStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }
}
