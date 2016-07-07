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

import org.pmw.tinylog.Logger;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by cplu on 2015/10/24.
 */
public class GsonGetRequest<T> extends PatchedRequest<T> {
	private ResponseParser<T> m_parser = null;
	/**
	 * expire time in milliseconds
	 */

//	private static final Map<String, String> POST_WITH_FORM_URLENCODED = new HashMap<String, String>(){
//		{
//			put("Accept", "application/vnd.windfindtech-v1+json");
//			put("Accept-Language", "zh");
//		}
//	};

	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url   URL of the request to make
	 * @param clazz Relevant class object, for Gson's reflection
	 */
	public GsonGetRequest(String url, Class<T> clazz, ResponseParser<T> parser, RequestCallback callback) {
		super(Method.GET, url, clazz, null, callback);
		m_parser = parser;
	}

	public GsonGetRequest(String url, Class<T> clazz, RequestCallback callback) {
		this(url, clazz, null, callback);
	}

	/**
	 * Make a GET request and return a parsed object from JSON.
	 *
	 * @param url
	 * @param clazz
	 * @param timeoutInMs timeout of the request, in millisecond
	 * @param callback
	 */
	public GsonGetRequest(String url, Class<T> clazz, int timeoutInMs, RequestCallback callback) {
		this(url, clazz, null, callback);
		setRetryPolicy(new DefaultRetryPolicy(timeoutInMs, 0, DEFAULT_MULTIPLIER));
	}

	public GsonGetRequest(String url, Map<String, String> headers, ResponseParser<T> parser, Class<T> clazz, RequestCallback callback) {
		this(url, clazz, parser, callback);
		m_headers = headers;
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
			Logger.debug("response of url " + getUrl() + " : " + json);
			if (m_parser != null) {
				return Response.success(
						m_parser.parseResponse(json),
						HttpHeaderParser.parseCacheHeaders(response));
			} else {
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
