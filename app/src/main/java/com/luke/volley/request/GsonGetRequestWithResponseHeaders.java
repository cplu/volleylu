package com.luke.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.Response;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.data.ResponseWithHeaders;
import com.luke.volley.extra.ResponseParser;

import java.util.Map;

/**
 * Created by cplu on 2016/3/29.
 */
public class GsonGetRequestWithResponseHeaders<T extends ResponseWithHeaders> extends GsonGetRequest<T> {
	private Map<String, String> m_responseHeaders;

	public GsonGetRequestWithResponseHeaders(String url, Class<T> clazz, ResponseParser<T> parser, RequestCallback callback) {
		super(url, clazz, parser, callback);
	}

	public GsonGetRequestWithResponseHeaders(String url, Class<T> clazz, RequestCallback callback) {
		super(url, clazz, callback);
	}

	public GsonGetRequestWithResponseHeaders(String url, Map<String, String> headers, ResponseParser<T> parser, Class<T> clazz, RequestCallback callback) {
		super(url, headers, parser, clazz, callback);
	}

	@Override
	protected void deliverResponse(T response) {
		response.setHeaders(m_responseHeaders);
		super.deliverResponse(response);
	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		m_responseHeaders = response.headers;
		return super.parseNetworkResponse(response);
	}
}
