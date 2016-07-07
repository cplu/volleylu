package com.luke.volley.toolbox;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.http.AndroidHttpClient;
import android.os.Build;

import com.android.volley.Network;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.luke.volley.PatchedRequestQueue;

import java.io.File;

public class PatchedVolley {

	/** Default on-disk cache directory. */
	private static final String DEFAULT_CACHE_DIR = "volley";

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 *
	 * @param context A {@link Context} to use for creating the cache dir.
	 * @param stack An {@link HttpStack} to use for the network, or null for default.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static PatchedRequestQueue newRequestQueue(Context context, HttpStack stack) {
		File cacheDir = new File(context.getCacheDir(), DEFAULT_CACHE_DIR);

		String userAgent = "volley/0";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (NameNotFoundException e) {
		}

		if (stack == null) {
			if (Build.VERSION.SDK_INT >= 9) {
				stack = new HurlStack();
			} else {
				// Prior to Gingerbread, HttpUrlConnection was unreliable.
				// See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
				stack = new HttpClientStack(AndroidHttpClient.newInstance(userAgent));
			}
		}

		Network network = new PatchedBasicNetwork(stack);

		PatchedRequestQueue queue = new PatchedRequestQueue(new DiskBasedCache(cacheDir), network);
		queue.start();

		return queue;
	}

	/**
	 * Creates a default instance of the worker pool and calls {@link RequestQueue#start()} on it.
	 *
	 * @param context A {@link Context} to use for creating the cache dir.
	 * @return A started {@link RequestQueue} instance.
	 */
	public static PatchedRequestQueue newRequestQueue(Context context) {
		return newRequestQueue(context, null);
	}
}
