package com.luke.volley.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.gson.GsonUtil;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;

import org.apache.http.entity.mime.MultipartEntity;
import org.pmw.tinylog.Logger;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by cplu on 2016/3/16.
 */
public class GsonMultiPartRequest<T> extends PatchedRequest<T> {
	private final MultipartEntity m_multipartEntity;

	/**
	 * make a http request (post/put) with MultipartEntity as body
	 *
	 * @param method
	 * @param url
	 * @param callback
	 */
	public GsonMultiPartRequest(int method, String url, MultipartEntity multipartEntity, Class<T> clazz, RequestCallback<T> callback) {
		super(method, url, clazz, null, callback);
		m_multipartEntity = multipartEntity;
	}

	public byte[] getBody() throws AuthFailureError {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		try {
			m_multipartEntity.writeTo(bos);
			return bos.toByteArray();
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public String getBodyContentType() {
		return m_multipartEntity.getContentType().getValue();
	}


	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		try {
			String json = new String(
					response.data,
					HttpHeaderParser.parseCharset(response.headers, DEFAULT_NETWORK_ENCODING));
			Logger.debug("response of url " + getUrl() + " : " + json);
			return Response.success(
					GsonUtil.getGson().fromJson(json, m_clazz),
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError());
		}
	}
}
