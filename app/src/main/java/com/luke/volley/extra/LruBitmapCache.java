package com.luke.volley.extra;

import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.toolbox.ImageLoader;

/**
 * Created by cplu on 2015/10/29.
 */
public class LruBitmapCache extends LruCache<String, Bitmap> implements ImageLoader.ImageCache {
	// Get max available VM memory, exceeding this amount will throw an
	// OutOfMemory exception. Stored in kilobytes as LruCache takes an
	// int in its constructor.
	static final int MAX_MEMORY = (int) (Runtime.getRuntime().maxMemory() >> 10);
	// Use 1/8th of the available memory for this memory cache.
	static final int CACHE_SIZE = MAX_MEMORY / 8;

	public LruBitmapCache(){
		super(CACHE_SIZE);
	}

	/**
	 * @param maxSize for caches that do not override {@link #sizeOf}, this is
	 *                the maximum number of entries in the cache. For all other caches,
	 *                this is the maximum sum of the sizes of the entries in this cache.
	 */
	public LruBitmapCache(int maxSize) {
		super(maxSize);
	}

	@Override
	public Bitmap getBitmap(String url) {
		return get(url);
	}

	@Override
	public void putBitmap(String url, Bitmap bitmap) {
		if(bitmap != null) {
			put(url, bitmap);
		}
		else{
			remove(url);
		}
	}

	@Override
	protected int sizeOf(String key, Bitmap bitmap) {
		// The cache size will be measured in kilobytes rather than
		// number of items.
		return (bitmap.getRowBytes() * bitmap.getHeight()) >> 10;
	}
}
