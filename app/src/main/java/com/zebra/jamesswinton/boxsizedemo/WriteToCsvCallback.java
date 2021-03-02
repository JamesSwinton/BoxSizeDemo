package com.zebra.jamesswinton.boxsizedemo;

public interface WriteToCsvCallback {
    void onComplete();
    void onError(String e);
}
