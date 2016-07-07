package com.luke.volley;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.volley.callback.RequestCallback;

import org.pmw.tinylog.Logger;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by cplu on 2015/10/26.<br>
 * Patch volley Request with the following issues:<br>
 * 1 set default headers<br>
 * 2 set default retry policy to timeout of 20000ms and no retry<br>
 * 3 store status code in {@link #parseNetworkResponse}, derived classes should call to super.parseNetworkResponse to get the status code from server<br>
 * 4 store and get redirection url in mRedirectUrl
 */
abstract public class PatchedRequest<T> extends Request<T> {
	protected static final int DEFAULT_TIMEOUT = 20000;
	protected static final int DEFAULT_RETRY_TIMES = 0;
	protected static final float DEFAULT_MULTIPLIER = 1.0f;
	//	private static final int DEFAULT_RETRY_TIMES = 5;
	protected final Class<T> m_clazz;
	protected final RequestCallback<T> m_callback;
	protected final Map<String, String> m_params;
	protected int m_statusCode = 0;
	public static final String DEFAULT_NETWORK_ENCODING = "UTF-8";
	public static final String HTTP_BODY_TYPE = "application/json";
	protected static final Map<String, String> DEFAULT_HEADER = new HashMap<String, String>(){
		{
			put("Accept", HTTP_BODY_TYPE);  // Warning: "application/vnd.windfindtech-v1+json" is not used to avoid strange error: unable to do http request at some site: "浦东市民中心"
			put("Accept-Language", "zh");
		}
	};
	protected Map<String, String> m_headers = DEFAULT_HEADER;

	/** The redirect url to use for 3xx http responses */
	private String mRedirectUrl;

	/** The unique identifier of the request */
	private String mIdentifier;

	/** Whether or not this request should try to retrieve data from cache. */
//	private boolean mShouldReadCache = true;

	/**
	 *
	 * @param method
	 * @param url
	 * @param clazz
	 * @param params
	 * @param callback
	 */
	public PatchedRequest(int method, String url, Class<T> clazz, Map<String, String> params, RequestCallback<T> callback) {
		super(method, url, null);
		mIdentifier = createIdentifier(method, url);
		m_clazz = clazz;
		m_callback = callback;
		m_params = params;
		/// default retry policy does not allow redirect (retry times is 0)
		setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, DEFAULT_RETRY_TIMES, DEFAULT_MULTIPLIER));
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		return m_headers != null ? m_headers : super.getHeaders();
	}

	@Override
	public Map<String, String> getParams() {
		return m_params;
	}

//	public int getStatusCode(){
//		return m_statusCode;
//	}

	@Override
	protected void deliverResponse(T response) {
//		Logger.debug("deliverResponse with url " + getUrl());
		m_callback.onSuccess(m_statusCode, response);
	}

	@Override
	public void deliverError(VolleyError error) {
		Logger.debug("deliverError with url " + getUrl(), error);
		if(error != null && error.networkResponse != null) {
			m_callback.onFailed(error.networkResponse.statusCode, error);
		}
		else{
			m_callback.onFailed(m_statusCode, error);
		}
	}

	/**
	 * get status code here
	 * @param response Response from the network
	 * @return  null as default, derived class should override to return valid Response
	 */
	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		m_statusCode = response.statusCode;
		return null;
	}

	/**
	 * try parse network error result as a ErrorResponse object
	 * derived class should consider other implementations if ErrorResponse is not expected
	 * @param volleyError the error retrieved from the network
	 * @return
	 */
	@Override
	protected VolleyError parseNetworkError(VolleyError volleyError) {
		try {
			String json = new String(
					volleyError.networkResponse.data,
					HttpHeaderParser.parseCharset(volleyError.networkResponse.headers, DEFAULT_NETWORK_ENCODING));
			return volleyError;
		} catch (Exception e) {
			return volleyError;
		}
	}

	/**
	 * Returns the URL of this request.
	 */
	@Override
	public String getUrl() {
		return (mRedirectUrl != null) ? mRedirectUrl : super.getUrl();
	}

	/**
	 * Returns the URL of the request before any redirects have occurred.
	 */
	public String getOriginUrl() {
		return super.getUrl();
	}

	/**
	 * Returns the identifier of the request.
	 */
	public String getIdentifier() {
		return mIdentifier;
	}

	/**
	 * Sets the redirect url to handle 3xx http responses.
	 */
	public void setRedirectUrl(String redirectUrl) {
		mRedirectUrl = redirectUrl;
	}

	/**
	 * Returns the cache key for this request.  By default, this is the URL.
	 */
	@Override
	public String getCacheKey() {
		return getMethod() + ":" + super.getUrl();
	}

//	/**
//	 * Set whether or not this request should try to retrieve data from cache
//	 *
//	 * @return This Request object to allow for chaining.
//	 */
//	public final Request<?> setShouldReadCache(boolean shouldReadCache) {
//		mShouldReadCache = shouldReadCache;
//		return this;
//	}
//
//	/**
//	 * Returns true if responses to this request should be read from cache before network request is sent
//	 */
//	public final boolean shouldReadCache() {
//		return mShouldReadCache;
//	}

	private static long sCounter;
	/**
	 *  sha1(Request:method:url:timestamp:counter)
	 * @param method http method
	 * @param url               http request url
	 * @return sha1 hash string
	 */
	private static String createIdentifier(final int method, final String url) {
		return PatchedUtils.sha1Hash("Request:" + method + ":" + url +
		                             ":" + System.currentTimeMillis() + ":" + (sCounter++));
	}
}
