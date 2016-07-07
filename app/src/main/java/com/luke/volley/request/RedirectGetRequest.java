package com.luke.volley.request;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.data.RedirectData;

import org.pmw.tinylog.Logger;

import java.util.Map;

/**
 * Created by cplu on 2015/10/27.
 */
public class RedirectGetRequest extends PatchedRequest<RedirectData> {
	public static final String HEADER_LOCATION = "Location";

	//	protected static final Map<String, String> PING_HEADER = new HashMap<String, String>(){
//		{
//			put("Accept", "*/*");
//			put("Accept-Language", "zh");
//		}
//	};
	private static final int RETRY_TIMES = 5;   /// retry times is also used as redirect times in Request
	private static final int INITIAL_TIMEOUT = 20000;

	/**
	 * Make a Get request to "ping" the given url. A status code of 204 means that network is ok
	 * @param url      URL of the request to make
	 * @param callback
	 */
//	public RedirectGetRequest(String url, RequestCallback<RedirectData> callback) {
//		super(Method.GET, url, RedirectData.class, null, callback);
//		setShouldCache(false).setShouldReadCache(false);
//		m_headers = PING_HEADER;
//	}

	/**
	 * Make a Get request to "ping" the given url and expect a redirect on i-Shanghai/i-PudongFree/i-Guangdong... if {#allowRedirect} is true
	 * A status code of 204 means that network is ok
	 *
	 * @param url
	 * @param headers
	 * @param allowRedirect
	 * @param callback
	 */
	public RedirectGetRequest(String url, Map<String, String> headers, boolean allowRedirect, RequestCallback<RedirectData> callback) {
		super(Request.Method.GET, url, RedirectData.class, null, callback);
		setShouldCache(false);
		if (allowRedirect) {
			setRetryPolicy(new DefaultRetryPolicy(INITIAL_TIMEOUT, RETRY_TIMES, 1.0f));
		}
		m_headers = headers;
	}

	/**
	 * get status code here
	 *
	 * @param response Response from the network
	 * @return null as default, derived class should override to return valid Response
	 */
	@Override
	protected Response<RedirectData> parseNetworkResponse(NetworkResponse response) {
		RedirectData data = new RedirectData();
		if (getUrl() != null && !getUrl().equals(getOriginUrl())) {
			/// redirect url is not the same as origin url, set it to the returned redirectURL
			data.setRedirectURL(getUrl());
		}
		try {
			super.parseNetworkResponse(response);
			String response_str = new String(
					response.data,
					HttpHeaderParser.parseCharset(response.headers, DEFAULT_NETWORK_ENCODING));
			data.setContent(response_str);
			return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.success(data, HttpHeaderParser.parseCacheHeaders(response));
		}
	}

	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		Logger.info("RedirectGetRequest#parseNetworkError", volleyError);
		if (volleyError != null && volleyError.networkResponse != null) {
			m_statusCode = volleyError.networkResponse.statusCode;
			if (volleyError.networkResponse.headers != null) {
				volleyError.networkResponse.headers.put(HEADER_LOCATION, getUrl());
			}
		}
		return volleyError;
	}
}
