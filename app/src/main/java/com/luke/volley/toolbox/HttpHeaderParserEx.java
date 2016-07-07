package com.luke.volley.toolbox;

import org.pmw.tinylog.Logger;

import java.util.Map;

/**
 * Created by cplu on 2016/6/24.
 */
public class HttpHeaderParserEx {

	/**
	 * Retrieve a Content-Range from headers
	 *
	 * @param headers An {@link Map} of headers
	 * @return Returns the total length in Content-Range, which follows slash<br>
	 *     e.g. "Content-Range → bytes 0-1024/17412989" will return 17412989
	 */
	public static long parseContentRangeLength(Map<String, String> headers) {
		String contentRange = headers.get("Content-Range");
		if (contentRange != null) {
			String[] params = contentRange.split("/");
			for (int i = 1; i < params.length; i++) {
				String length = params[1];
				try {
					return Long.parseLong(length);
				} catch (Exception e) {
					Logger.error(e);
					return 0;
				}
			}
		}

		return 0;
	}
}
