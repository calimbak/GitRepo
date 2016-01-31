package holtz.cedric.gitrepo.net;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.LruCache;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

/**
 * Created by holtz_c on 1/28/16.
 * Singleton used to store the Volley RequestQueue and the Volley ImageLoader
 * GitRepo
 */
public class VolleyRequestQueue {

	// Number of cached item in the cache
	private static final int MAX_SIZE_LRU = 20;

	private static VolleyRequestQueue mInstance;
	private RequestQueue mRequestQueue;
	private static Context mCtx;
	private ImageLoader mImageLoader;

	private VolleyRequestQueue(Context context) {
		mCtx = context;
		mRequestQueue = getRequestQueue();

		mImageLoader = new ImageLoader(mRequestQueue,
				new ImageLoader.ImageCache() {
					private final LruCache<String, Bitmap>
							cache = new LruCache<>(MAX_SIZE_LRU);

					@Override
					public Bitmap getBitmap(String url) {
						return cache.get(url);
					}

					@Override
					public void putBitmap(String url, Bitmap bitmap) {
						cache.put(url, bitmap);
					}
				});
	}

	/**
	 * Return the current instance, create one if needed
	 * @return Current instance of Volley RequestQueue
	 */
	public static synchronized VolleyRequestQueue getInstance(Context context) {
		if (mInstance == null) {
			mInstance = new VolleyRequestQueue(context);
		}
		return mInstance;
	}

	/**
	 * Get the Volley RequestQueue being used
	 * @return The current RequestQueue
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley.newRequestQueue(mCtx.getApplicationContext());
		}
		return mRequestQueue;
	}

	/**
	 * Add a request to the RequestQueue
	 * @param req Request to be added
	 */
	public <T> void addToRequestQueue(Request<T> req) {
		getRequestQueue().add(req);
	}

	/**
	 * Get the ImageLoader being used
	 * @return The current ImageLoader
	 */
	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

}