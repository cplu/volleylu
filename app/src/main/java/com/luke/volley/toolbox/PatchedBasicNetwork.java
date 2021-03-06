package com.luke.volley.toolbox;

import android.os.SystemClock;

import com.android.volley.Cache;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.Request;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.PoolingByteArrayOutputStream;
import com.luke.volley.PatchedRequest;
import com.luke.volley.RedirectError;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.StatusLine;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.cookie.DateUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by cplu on 2016/2/23.
 */
public class PatchedBasicNetwork extends BasicNetwork {

	private static int SLOW_REQUEST_THRESHOLD_MS = 3000;

	public PatchedBasicNetwork(HttpStack httpStack) {
		super(httpStack);
	}

	@Override
	public NetworkResponse performRequest(Request<?> request) throws VolleyError {
		long requestStart = SystemClock.elapsedRealtime();
		while (true) {
			HttpResponse httpResponse = null;
			byte[] responseContents = null;
			Map<String, String> responseHeaders = Collections.emptyMap();
			try {
				// Gather headers.
				Map<String, String> headers = new HashMap<String, String>();
				addCacheHeaders(headers, request.getCacheEntry());
				httpResponse = mHttpStack.performRequest(request, headers);
				StatusLine statusLine = httpResponse.getStatusLine();
				int statusCode = statusLine.getStatusCode();

				responseHeaders = convertHeaders(httpResponse.getAllHeaders());
				// Handle cache validation.
				if (statusCode == HttpStatus.SC_NOT_MODIFIED) {

					Cache.Entry entry = request.getCacheEntry();
					if (entry == null) {
						return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, null,
								responseHeaders, true,
								SystemClock.elapsedRealtime() - requestStart);
					}

					// A HTTP 304 response does not have all header fields. We
					// have to use the header fields from the cache entry plus
					// the new ones from the response.
					// http://www.w3.org/Protocols/rfc2616/rfc2616-sec10.html#sec10.3.5
					entry.responseHeaders.putAll(responseHeaders);
					return new NetworkResponse(HttpStatus.SC_NOT_MODIFIED, entry.data,
							entry.responseHeaders, true,
							SystemClock.elapsedRealtime() - requestStart);
				}

				// Handle moved resources
				if (is_30x(statusCode)) {
					String newUrl = responseHeaders.get("Location");
					((PatchedRequest)request).setRedirectUrl(newUrl);
					responseContents = new byte[0];
				}

				// Some responses such as 204s do not have content.  We must check.
				else if (httpResponse.getEntity() != null) {
					responseContents = entityToBytes(httpResponse.getEntity());
				} else {
					// Add 0 byte response as a way of honestly representing a
					// no-content request.
					responseContents = new byte[0];
				}

				// if the request is slow, log it.
				long requestLifetime = SystemClock.elapsedRealtime() - requestStart;
				logSlowRequests(requestLifetime, request, responseContents, statusLine);

				if (statusCode < 200 || statusCode > 299) {
					throw new IOException();
				}
				return new NetworkResponse(statusCode, responseContents, responseHeaders, false,
						SystemClock.elapsedRealtime() - requestStart);
			} catch (SocketTimeoutException e) {
				attemptRetryOnException("socket", request, new TimeoutError());
			} catch (ConnectTimeoutException e) {
				attemptRetryOnException("connection", request, new TimeoutError());
			} catch (MalformedURLException e) {
				throw new RuntimeException("Bad URL " + request.getUrl(), e);
			} catch (IOException e) {
				int statusCode = 0;
				NetworkResponse networkResponse = null;
				if (httpResponse != null) {
					statusCode = httpResponse.getStatusLine().getStatusCode();
				} else {
					throw new NoConnectionError(e);
				}
				if (is_30x(statusCode)) {
					VolleyLog.e("Request at %s has been redirected to %s", ((PatchedRequest)request).getOriginUrl(), request.getUrl());
				} else {
					VolleyLog.e("Unexpected response code %d for %s", statusCode, request.getUrl());
				}
				if (responseContents != null) {
					networkResponse = new NetworkResponse(statusCode, responseContents,
							responseHeaders, false, SystemClock.elapsedRealtime() - requestStart);
//                    if (statusCode == HttpStatus.SC_UNAUTHORIZED ||
//                            statusCode == HttpStatus.SC_FORBIDDEN) {
//                        attemptRetryOnException("auth",
//                                request, new AuthFailureError(networkResponse));
//                    } else
					if (is_30x(statusCode)) {
						attemptRetryOnException("redirect",
								request, new RedirectError(networkResponse));
					} else {
						// TODO: Only throw ServerError for 5xx status codes.
						throw new ServerError(networkResponse);
					}
				} else {
					throw new NetworkError(e);
				}
			}
		}
	}

	/**
	 * Logs requests that took over SLOW_REQUEST_THRESHOLD_MS to complete.
	 */
	private void logSlowRequests(long requestLifetime, Request<?> request,
	                             byte[] responseContents, StatusLine statusLine) {
		if (DEBUG || requestLifetime > SLOW_REQUEST_THRESHOLD_MS) {
			VolleyLog.d("HTTP response for request=<%s> [lifetime=%d], [size=%s], " +
			            "[rc=%d], [retryCount=%s]", request, requestLifetime,
					responseContents != null ? responseContents.length : "null",
					statusLine.getStatusCode(), request.getRetryPolicy().getCurrentRetryCount());
		}
	}

	/**
	 * Attempts to prepare the request for a retry. If there are no more attempts remaining in the
	 * request's retry policy, a timeout exception is thrown.
	 * @param request The request to use.
	 */
	private static void attemptRetryOnException(String logPrefix, Request<?> request,
	                                            VolleyError exception) throws VolleyError {
		RetryPolicy retryPolicy = request.getRetryPolicy();
		int oldTimeout = request.getTimeoutMs();

		try {
			retryPolicy.retry(exception);
		} catch (VolleyError e) {
			request.addMarker(
					String.format("%s-timeout-giveup [timeout=%s]", logPrefix, oldTimeout));
			throw e;
		}
		request.addMarker(String.format("%s-retry [timeout=%s]", logPrefix, oldTimeout));
	}

	private void addCacheHeaders(Map<String, String> headers, Cache.Entry entry) {
		// If there's no cache entry, we're done.
		if (entry == null) {
			return;
		}

		if (entry.etag != null) {
			headers.put("If-None-Match", entry.etag);
		}

		if (entry.lastModified > 0) {
			Date refTime = new Date(entry.lastModified);
			headers.put("If-Modified-Since", DateUtils.formatDate(refTime));
		}
	}

	/** Reads the contents of HttpEntity into a byte[]. */
	private byte[] entityToBytes(HttpEntity entity) throws IOException, ServerError {
		PoolingByteArrayOutputStream bytes =
				new PoolingByteArrayOutputStream(mPool, (int) entity.getContentLength());
		byte[] buffer = null;
		try {
			InputStream in = entity.getContent();
			if (in == null) {
				throw new ServerError();
			}
			buffer = mPool.getBuf(1024);
			int count;
			while ((count = in.read(buffer)) != -1) {
				bytes.write(buffer, 0, count);
			}
			return bytes.toByteArray();
		} finally {
			try {
				// Close the InputStream and release the resources by "consuming the content".
				entity.consumeContent();
			} catch (IOException e) {
				// This can happen if there was an exception above that left the entity in
				// an invalid state.
				VolleyLog.v("Error occured when calling consumingContent");
			}
			mPool.returnBuf(buffer);
			bytes.close();
		}
	}

	/**
	 * Modified by Luke at 2015/10/28: support 303/307 for redirection
	 * @param statusCode
	 * @return
	 */
	private boolean is_30x(int statusCode){
		return (statusCode == HttpStatus.SC_MOVED_PERMANENTLY || statusCode == HttpStatus.SC_MOVED_TEMPORARILY
		        /// Modified by Luke at 2015/10/28: support 303/307 for redirection
		        || statusCode == HttpStatus.SC_SEE_OTHER || statusCode == HttpStatus.SC_TEMPORARY_REDIRECT);
	}
}
