package com.luke.volley.request;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.google.gson.JsonSyntaxException;
import com.luke.gson.GsonUtil;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.extra.ResponseParser;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by cplu on 2015/10/24.
 */
public class GsonPostRequest<T> extends PatchedRequest<T> {
	private ResponseParser<T> m_parser = null;
//	private static final Map<String, String> POST_WITH_FORM_URLENCODED = new HashMap<String, String>(){
//		{
//			put("Accept", "application/vnd.windfindtech-v1+json");
//			put("Accept-Language", "zh");
//		}
//	};

	/**
	 * Make a POST request with application/x-www-form-urlencoded and return a parsed object from JSON.
	 *
	 * @param url URL of the request to make
	 * @param clazz Relevant class object, for Gson's reflection
	 */
	public GsonPostRequest(String url, Class<T> clazz, Map<String, String> params, RequestCallback callback) {
		super(Method.POST, url, clazz, params, callback);
	}

	public GsonPostRequest(String url, Class<T> clazz, Map<String, String> params, int retryTimes, RequestCallback callback) {
		super(Method.POST, url, clazz, params, callback);
		setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, retryTimes, DEFAULT_MULTIPLIER));
	}

	public GsonPostRequest(String url, Map<String, String> headers, Class<T> clazz, Map<String, String> params, RequestCallback callback) {
		this(url, clazz, params, callback);
		m_headers = headers;
	}

	public GsonPostRequest(String url, Class<T> clazz, Map<String, String> params, ResponseParser<T> parser, RequestCallback callback) {
		this(url, clazz, params, callback);
		m_parser = parser;
	}

//	@Override
//	public String getBodyContentType() {
//		return "application/x-www-form-urlencoded";
//	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			super.parseNetworkResponse(response);
			String json = new String(
					response.data,
					HttpHeaderParser.parseCharset(response.headers, DEFAULT_NETWORK_ENCODING));
			if(m_parser != null){
				return Response.success(
						m_parser.parseResponse(json),
						HttpHeaderParser.parseCacheHeaders(response));
			}
			else{
				return Response.success(
						GsonUtil.getGson().fromJson(json, m_clazz),
						HttpHeaderParser.parseCacheHeaders(response));
			}
		} catch (UnsupportedEncodingException e) {
			return Response.error(new ParseError());
		} catch (JsonSyntaxException e) {
			return Response.error(new ParseError());
		}
	}
}
