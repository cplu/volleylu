package com.luke.volley;

import com.android.volley.Cache;
import com.android.volley.Network;
import com.android.volley.RequestQueue;

/**
 * Created by cplu on 2016/2/22.
 */
public class PatchedRequestQueue extends RequestQueue {
	public PatchedRequestQueue(Cache cache, Network network) {
		super(cache, network);
	}

	public <T> void removeCache(PatchedRequest<T> request) {
		String key = request.getCacheKey();
		getCache().remove(key);
	}
}
