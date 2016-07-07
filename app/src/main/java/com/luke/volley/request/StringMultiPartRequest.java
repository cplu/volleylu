package com.luke.volley.request;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;

import org.apache.http.entity.mime.MultipartEntity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Created by cplu on 2015/10/28.
 */
public class StringMultiPartRequest extends PatchedRequest<String> {
	//	private static final String DEFAULT_MULTIPART_TYPE = "multipart/form-data";
	private final MultipartEntity m_multipartEntity;

	/**
	 * make a http request (post/put) with MultipartEntity as body
	 *
	 * @param method
	 * @param url
	 * @param callback
	 */
	public StringMultiPartRequest(int method, String url, MultipartEntity multipartEntity, RequestCallback callback) {
		super(method, url, String.class, null, callback);
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
