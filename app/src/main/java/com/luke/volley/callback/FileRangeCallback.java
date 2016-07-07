package com.luke.volley.callback;

/**
 * Created by cplu on 2016/6/24.
 */
public interface FileRangeCallback<T> extends RequestCallback<T> {
	/**
	 * notify the current progress of downloading, and tells if we should continue downloading
	 * @param progress
	 * @param percent
	 * @return  true if downloading should be continued, false otherwise
	 */
	boolean onProgress(long progress, int percent);
}
