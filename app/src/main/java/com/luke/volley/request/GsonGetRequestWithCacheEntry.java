package com.luke.volley.request;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.extra.ResponseParser;

import java.util.Map;

/**
 * Created by cplu on 2015/12/11.
 */
public class GsonGetRequestWithCacheEntry<T> extends GsonGetRequest<T> {
	/**
	 * store cache entry of response object
	 */
	private Cache.Entry m_entry;

	public GsonGetRequestWithCacheEntry(String url, Map<String, String> headers, ResponseParser<T> parser, Class<T> clazz, RequestCallback callback) {
		super(url, headers, parser, clazz, callback);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		Response<T> ret = super.parseNetworkResponse(response);
		if (ret != null && ret.cacheEntry != null) {
			m_entry = ret.cacheEntry;
		}
		return ret;
	}

	public Cache.Entry getResponseCacheEntry() {
		return m_entry;
	}
}
