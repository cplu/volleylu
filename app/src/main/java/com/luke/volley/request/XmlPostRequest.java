package com.luke.volley.request;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;
import com.luke.volley.PatchedRequest;
import com.luke.volley.callback.RequestCallback;
import com.luke.volley.extra.XmlDefaultHandler;

import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Map;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

/**
 * Created by cplu on 2015/10/30.
 */
public class XmlPostRequest<T, HandlerType extends XmlDefaultHandler<T>> extends PatchedRequest<T> {

	private final SAXParserFactory m_saxParserFactory;
	private HandlerType m_xmlHandler;

	/**
	 * Make a GET request and return a parsed object from xml.
	 *
	 * @param url         URL of the request to make
	 * @param sax_handler object for parsing xml and storing result
	 */
	public XmlPostRequest(String url, Map<String, String> params, HandlerType sax_handler, RequestCallback callback) {
		super(Method.POST, url, null, params, callback);
		m_saxParserFactory = SAXParserFactory.newInstance();
		m_xmlHandler = sax_handler;
	}

	public XmlPostRequest(String url, Map<String, String> headers, Map<String, String> params, HandlerType sax_handler, RequestCallback callback) {
		this(url, params, sax_handler, callback);
		m_headers = headers;
	}

//	@Override
//	public String getBodyContentType() {
//		return "application/x-www-form-urlencoded";
//	}

	@Override
	protected Response<T> parseNetworkResponse(NetworkResponse response) {
		super.parseNetworkResponse(response);
		InputStream instream = new ByteArrayInputStream(response.data);
		InputStreamReader inputStreamReader = null;
		try {
			if (instream != null) {
				SAXParser saxParser = m_saxParserFactory.newSAXParser();
				XMLReader rssReader = saxParser.getXMLReader();
				rssReader.setContentHandler(m_xmlHandler);
				inputStreamReader = new InputStreamReader(instream, "UTF-8");
				rssReader.parse(new InputSource(inputStreamReader));
				return Response.success(
						m_xmlHandler.getResult(),
						HttpHeaderParser.parseCacheHeaders(response));
			} else {
				return Response.error(null);
			}
		} catch (Exception e) {
			return Response.error(new ParseError());
		} finally {
			try {
				if (instream != null) {
					instream.close();
				}
			} catch (IOException e) {

			}
			if (inputStreamReader != null) {
				try {
					inputStreamReader.close();
				} catch (IOException e) {

				}
			}
		}
	}
}
