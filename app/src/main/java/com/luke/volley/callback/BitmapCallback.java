package com.luke.volley.callback;

import android.graphics.Bitmap;

/**
 * Created by cplu on 2016/4/8.
 */
public interface BitmapCallback {
	void onSuccess(Bitmap ret);
	void onFailed(String reason);
}
