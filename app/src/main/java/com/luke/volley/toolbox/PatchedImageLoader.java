package com.luke.volley.toolbox;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;

/**
 * Created by cplu on 2016/2/23.
 */
public class PatchedImageLoader extends ImageLoader {
	private final ImageCache m_cache;

	/**
	 * Constructs a new ImageLoader.
	 *
	 * @param queue      The RequestQueue to use for making image requests.
	 * @param imageCache The cache to use as an L1 cache.
	 */
	public PatchedImageLoader(RequestQueue queue, ImageCache imageCache) {
		super(queue, imageCache);
		m_cache = imageCache;
	}

	public void clearCache(String requestUrl, int maxWidth, int maxHeight) {
		final String cacheKey = getCacheKey(requestUrl, maxWidth, maxHeight, ImageView.ScaleType.CENTER_INSIDE);
		m_cache.putBitmap(cacheKey, null);
	}

	/**
	 * Creates a cache key for use with the L1 cache.
	 * @param url The URL of the request.
	 * @param maxWidth The max-width of the output.
	 * @param maxHeight The max-height of the output.
	 * @param scaleType The scaleType of the imageView.
	 */
	private static String getCacheKey(String url, int maxWidth, int maxHeight, ImageView.ScaleType scaleType) {
		return new StringBuilder(url.length() + 12).append("#W").append(maxWidth)
				.append("#H").append(maxHeight).append("#S").append(scaleType.ordinal()).append(url)
				.toString();
	}

	@Override
	protected Request<Bitmap> makeImageRequest(String requestUrl, int maxWidth, int maxHeight,
	                                           ImageView.ScaleType scaleType, final String cacheKey) {
		return new PatchedImageRequest(requestUrl, new Response.Listener<Bitmap>() {
			@Override
			public void onResponse(Bitmap response) {
				onGetImageSuccess(cacheKey, response);
			}
		}, maxWidth, maxHeight, scaleType, Bitmap.Config.RGB_565, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				onGetImageError(cacheKey, error);
			}
		});
	}
}
