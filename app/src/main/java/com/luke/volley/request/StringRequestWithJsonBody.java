package com.luke.volley.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.gson.GsonUtil;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by cplu on 2015/10/26.
 */
public class StringRequestWithJsonBody<BodyType> extends PatchedRequest<String> {
	//	private static final String DEFAULT_JSON_TYPE = "application/json";
	//"application/vnd.windfindtech-v1+json";
	private BodyType m_body;
	private Class<BodyType> m_bodyClazz;

	/**
	 * Make a POST/PUT request with Json as body and return a raw string.
	 *
	 * @param url      URL of the request to make
	 * @param params
	 * @param callback
	 */
	public StringRequestWithJsonBody(int method, String url, BodyType body, Class<BodyType> bodyClazz, Map params, RequestCallback callback) {
		super(method, url, String.class, params, callback);
		m_body = body;
		m_bodyClazz = bodyClazz;
	}

	public byte[] getBody() throws AuthFailureError {
		if (m_body == null) {
			return super.getBody();
		}
		StringBuilder encodedParams = new StringBuilder();
		try {
			String body_str = GsonUtil.getGson().toJson(m_body, m_bodyClazz);
			encodedParams.append(body_str);
			return encodedParams.toString().getBytes(getParamsEncoding());
		} catch (UnsupportedEncodingException uee) {
			throw new RuntimeException("Encoding not supported: " + getParamsEncoding(), uee);
		}
	}

	@Override
	public String getBodyContentType() {
		return HTTP_BODY_TYPE;
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
