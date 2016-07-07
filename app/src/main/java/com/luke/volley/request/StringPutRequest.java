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
public class StringPutRequest extends PatchedRequest<String> {
	/**
	 * Make a PUT request and return a raw string.
	 *
	 * @param url      URL of the request to make
	 * @param params
	 * @param callback
	 */
	public StringPutRequest(String url, Map params, RequestCallback callback) {
		super(Method.PUT, url, String.class, params, callback);
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
