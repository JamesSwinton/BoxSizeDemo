package com.zebra.jamesswinton.boxsizedemo;

import android.app.Application;

import com.mysize.surfacesdk.SurfaceSDK;

public class App extends Application {

    // Debugging
    private static final String TAG = "ApplicationClass";

    // Credentials
    private static final String API_SECRET = "e5d5b4fca791b3849d24ffc9b364367c";
    private static final String API_KEY = "6e8447414c5c397e23b5fb04f1c4d38893d22d4e9aca09280ef25794f5958724";

    /**
     * Life cycle Methods
     */

    @Override
    public void onCreate() {
        super.onCreate();
        // Init SDK
        SurfaceSDK.getInstance().sdkInitialization(this, API_SECRET, API_KEY);
    }
}
