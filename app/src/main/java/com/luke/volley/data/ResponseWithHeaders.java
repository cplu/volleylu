package com.luke.volley.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

/**
 * Created by cplu on 2016/3/29.
 */
public class ResponseWithHeaders {
	private transient Map<String, String> m_headers;

	public ResponseWithHeaders() {

	}

	public void setHeaders(Map<String, String> headers) {
		m_headers = headers;
	}

	public Date getResponseDate() {
		if(m_headers != null) {
			try {
				String date = m_headers.get("Date");
				SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH);
				return format.parse(date);
			} catch (ParseException e) {
				e.printStackTrace();
				return null;
			}
		}
		return null;
	}
}
