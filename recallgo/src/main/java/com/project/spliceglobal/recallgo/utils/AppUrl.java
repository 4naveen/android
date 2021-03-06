package com.project.spliceglobal.recallgo.utils;

/**
 * Created by Personal on 9/26/2017.
 */

public class AppUrl {
    public static String TOKEN;
    public static final String BASE_URL = "http://ec2-35-154-135-19.ap-south-1.compute.amazonaws.com:8001/";
    public static final String LOGIN_URL = BASE_URL+"auth/login/";
    public static final String REGISTRATION_URL =BASE_URL+"auth/registration/";
    public static final String GET_USER_PROFILE_URL = BASE_URL+"auth/user/";
    public static final String ALL_CATEGORY_URL = BASE_URL+"api/lists/";
    public static final String ALL_BRAND_URL = BASE_URL+"api/brands/";
    public static final String ALL_STORE_URL = BASE_URL+"api/stores/";
    public static final String SHARE_CATEGORY_URL = BASE_URL+"api/lists/share_list/";
    public static final String ITEM_LIST_URL = BASE_URL+"api/reminders/";
    public static final String TODAY_REMINDERS_URL = BASE_URL+"api/reminders/?date=";

    public static final String PRICE_CHASER_URL = BASE_URL+"api/price_chasers/";
    public static final String FEEDBACK_URL = BASE_URL+"api/feedbacks/";

    public static final String CATEGOTY_ITEM_LIST_URL = BASE_URL+"api/reminders/?list=";
    public static final String CHANGE_PASSWORD_URL =BASE_URL+"auth/password/change/";
    public static final String FORGET_PASSWORD_URL =BASE_URL+"auth/password/reset/";
    public static final String SITES_URL = BASE_URL+"api/sites/";

}
