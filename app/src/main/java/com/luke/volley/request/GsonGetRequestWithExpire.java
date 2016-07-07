package com.luke.volley.request;

import com.android.volley.Cache;
import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.extra.ResponseParser;

import org.pmw.tinylog.Logger;

import java.util.Map;

/**
 * Created by cplu on 2015/10/24.
 * 注意：这个request发起的请求只有两种情况：1 cache未到期，则使用本地cache；2 cache已过期或没有cache，则访问网络
 */
public class GsonGetRequestWithExpire<T> extends GsonGetRequest<T> {
	/**
	 * expire time in milliseconds
	 */
	private int m_expire = 0;

	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url   URL of the request to make
	 * @param clazz Relevant class object, for Gson's reflection
	 */
	public GsonGetRequestWithExpire(String url, Class<T> clazz, int expire, ResponseParser<T> parser, RequestCallback callback) {
		super(url, clazz, parser, callback);
		m_expire = expire;

	}

	public GsonGetRequestWithExpire(String url, Map<String, String> headers, Class<T> clazz, int expire, ResponseParser<T> parser, RequestCallback callback) {
		super(url, headers, parser, clazz, callback);
		m_expire = expire;
	}


//	@Override
//	public String getBodyContentType() {
//		return "application/x-www-form-urlencoded";
//	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		Response<T> tResponse = super.parseNetworkResponse(response);
		Cache.Entry entry = tResponse.cacheEntry;
		if (response.networkTimeMs > 0 && entry != null) {
			if (m_expire > 0 && entry.ttl == 0 && entry.softTtl == 0) {
				entry.ttl = entry.softTtl = System.currentTimeMillis() + m_expire;
			}
		} else {
			Logger.debug("response.networkTimeMs " + response.networkTimeMs);
		}
		return tResponse;
	}
}
