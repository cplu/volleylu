package com.luke.volley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageRequest;

/**
 * Created by cplu on 2016/3/8.
 */
public class PatchedImageRequest extends ImageRequest {
	public PatchedImageRequest(String url, Response.Listener<Bitmap> listener, int maxWidth, int maxHeight, ImageView.ScaleType scaleType, Bitmap.Config decodeConfig, Response.ErrorListener errorListener) {
		super(url, listener, maxWidth, maxHeight, scaleType, decodeConfig, errorListener);
	}

	@Override
	protected Response<Bitmap> parseNetworkResponse(NetworkResponse response) {
//		if (response != null && response.data != null) {
//			NetworkTester.instance().addTestCase(response.data.length, response.networkTimeMs);
//		}
		return super.parseNetworkResponse(response);
	}
}
