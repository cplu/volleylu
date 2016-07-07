package com.luke.volley.callback;

import android.graphics.drawable.BitmapDrawable;

/**
 * Created by cplu on 2014/9/2.
 */
public abstract class ImageCallback {
    /**
     * @param ret
     */
    public abstract void onSuccess(BitmapDrawable ret);
    public abstract void onFailed(String reason);
//    public void on_found_locally(int code, BitmapDrawable ret){}
}
