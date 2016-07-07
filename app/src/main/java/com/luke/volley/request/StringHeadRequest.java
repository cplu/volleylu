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
public class StringHeadRequest extends PatchedRequest<String> {
	/**
	 * Make a HEAD request.
	 * Warning: HEAD request cannot be used to check for 304(NOT MODIFIED) since volley will also store response headers of HEAD request
	 * Thus, we should never use cache in head request
	 *
	 * @param url      URL of the request to make
	 * @param params
	 * @param callback
	 */
	public StringHeadRequest(String url, Map<String, String> params, RequestCallback callback) {
		super(Method.HEAD, url, String.class, params, callback);
		setShouldCache(false);
	}

	public StringHeadRequest(String url, Map<String, String> headers, Map<String, String> params, RequestCallback callback) {
		this(url, params, callback);
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
