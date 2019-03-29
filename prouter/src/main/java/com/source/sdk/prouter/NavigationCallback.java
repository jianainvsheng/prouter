package com.source.sdk.prouter;

/**
 * Created by yangjian on 2018/5/17.
 */

public interface NavigationCallback {
    void onFound(Postcard var1);

    void onLost(Postcard var1);

    void onArrival(Postcard var1);

    void onInterrupt(Postcard var1);
}
