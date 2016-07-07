package com.luke.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;

import java.util.Map;

/**
 * Created by cplu on 2015/10/26.
 */
public class StringGetRequest extends PatchedRequest<String> {
	/**
	 * Make a GET request and return a raw string.
	 *
	 * @param url      URL of the request to make
	 * @param callback
	 */
	public StringGetRequest(String url, RequestCallback callback) {
		super(Method.GET, url, String.class, null, callback);
	}

	public StringGetRequest(String url, Map<String, String> headers, RequestCallback callback) {
		this(url, callback);
		m_headers = headers;
	}

	@Override
	protected Response<String> parseNetworkResponse(NetworkResponse response) {
		try {
			super.parseNetworkResponse(response);
			String response_str = new String(
					response.data,
					HttpHeaderParser.parseCharset(response.headers, DEFAULT_NETWORK_ENCODING));
			return Response.success(response_str, HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError());
		}
	}
}
