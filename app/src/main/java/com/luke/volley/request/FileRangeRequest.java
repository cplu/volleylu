package com.luke.volley.request;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.data.FileResponse;
import com.luke.volley.toolbox.HttpHeaderParserEx;

import java.io.RandomAccessFile;
import java.util.Map;

/**
 * Created by cplu on 2016/6/24.
 */
public class FileRangeRequest extends PatchedRequest<FileResponse> {
	private final long m_start;
	private final long m_end;
//	private final long m_total;
	private RandomAccessFile m_file;    /// handler for storing local file

	/**
	 * @param url
	 * @param params
	 * @param callback
	 */
	public FileRangeRequest(String url, Map<String, String> params, RequestCallback<FileResponse> callback,
	                        RandomAccessFile localFile, long start, long end) {
		super(Request.Method.GET, url, FileResponse.class, params, callback);
		m_file = localFile;
		m_start = start;
		m_end = end;
		/// retry 2 times
		setRetryPolicy(new DefaultRetryPolicy(DEFAULT_TIMEOUT, 2, DEFAULT_MULTIPLIER));
		setShouldCache(false);
	}

	@Override
	public Map<String, String> getHeaders() throws AuthFailureError {
		Map<String, String> headers = super.getHeaders();
		String range = String.format("bytes=%d-%d", m_start, m_end);
		headers.put("Range", range);
		return headers;
	}

	@Override
	protected Response<FileResponse> parseNetworkResponse(NetworkResponse response) {
		try {
			super.parseNetworkResponse(response);
			byte[] data = response.data;
			m_file.seek(m_start);
			m_file.write(data);
			long length = HttpHeaderParserEx.parseContentRangeLength(response.headers);
			FileResponse fileResponse = new FileResponse();
			fileResponse.setSize(length);
			fileResponse.setByteStart(m_start);
			fileResponse.setBytesReceived(data.length);
			return Response.success(
					fileResponse,
					HttpHeaderParser.parseCacheHeaders(response));
		} catch (Exception e) {
			return Response.error(new ParseError());
		}
	}
}
