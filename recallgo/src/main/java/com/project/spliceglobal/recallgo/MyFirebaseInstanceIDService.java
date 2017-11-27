package com.project.spliceglobal.recallgo;

import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    private static final String TAG = "MyFirebaseIIDService";
    public MyFirebaseInstanceIDService() {

    }
    @Override
    public void onTokenRefresh() {
        //Getting registration token
        String refreshedToken = FirebaseInstanceId.getInstance().getToken();
        //Displaying token on logcat
        Log.d(TAG, "Refreshed toke: " + refreshedToken);

        //calling the method store token and passing token
        storeToken(refreshedToken);
    }
    private void storeToken(String token) {
        //we will save the token in sharedpreferences later
    }
}
