package com.luke.volleylu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.HurlStack;
import com.luke.volley.PatchedRequestQueue;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.data.RedirectData;
import com.luke.volley.extra.LruBitmapCache;
import com.luke.volley.request.RedirectGetRequest;
import com.luke.volley.toolbox.PatchedImageLoader;
import com.luke.volley.toolbox.PatchedVolley;

import java.net.CookieHandler;
import java.net.CookieManager;

public class MainActivity extends AppCompatActivity {

	private PatchedRequestQueue m_requestQueue;
	private PatchedImageLoader m_imageLoader;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		final TextView textView = (TextView) findViewById(R.id.result);

		m_requestQueue = PatchedVolley.newRequestQueue(this, new HurlStack());
		m_imageLoader = new PatchedImageLoader(m_requestQueue, new LruBitmapCache());

		/// set default cookie manager if you want
		CookieManager cookieManager = new CookieManager();
		CookieHandler.setDefault(cookieManager);

		/// example of auto redirection from http://github.com to https://github.com
		RedirectGetRequest request = new RedirectGetRequest("http://github.com", null, true, new RequestCallback<RedirectData>() {
			@Override
			public void onSuccess(int code, RedirectData ret) {
				textView.setText(ret.getContent());
			}

			@Override
			public void onFailed(int code, VolleyError reason) {

			}
		});
		m_requestQueue.add(request);
	}
}
