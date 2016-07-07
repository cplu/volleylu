package com.luke.volley.callback;

import com.android.volley.VolleyError;

/**
 * Created by cplu on 2014/9/2.
 */
public interface RequestCallback<SuccessType> {
    void onSuccess(int code, SuccessType ret);
    void onFailed(int code, VolleyError reason);
}
