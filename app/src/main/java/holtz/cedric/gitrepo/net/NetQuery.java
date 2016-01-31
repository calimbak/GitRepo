package holtz.cedric.gitrepo.net;

import android.content.Context;
import android.util.Log;

import com.android.volley.Response;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import holtz.cedric.gitrepo.model.RepositoryList;
import holtz.cedric.gitrepo.model.UserList;

/**
 * Created by holtz_c on 1/28/16.
 * Helper class doing Network call
 * GitRepo
 */
public class NetQuery {

	private static final String TAG = "NetQuery";

	private static final String API_URL = "https://api.github.com/";
	// Maximum number of items return by page
	public static final int MAX_ITEM_PAGE = 30;

	/**
	 * Network query asking the list of users matching the username at the specified page
	 * @param username Username to be searched
	 * @param page Page to be searched
	 */
	public static void getUserList(Context context, Response.Listener<UserList> responseListener,
	                                   Response.ErrorListener errorListener, String username, int page) {
		// Url of the request
		String url = API_URL + "search/users?q=";
		try {
			url += URLEncoder.encode(username, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// If utf-8 isn't found, just send it as it is
			url += username;

			Log.e(TAG, "getUserList", e);
		}
		url += "&page=" + page + "&per_page=" + MAX_ITEM_PAGE;

		genericGsonRequest(context, responseListener, errorListener, UserList.class, url);
	}

	/**
	 * Network query asking the list of repositories for an user at the specified page
	 * @param user User to be searched
	 * @param page Page to be searched
	 */
	public static void getRepoForUser(Context context, Response.Listener<RepositoryList> responseListener,
	                               Response.ErrorListener errorListener, String user, int page) {
		// Url of the request
		String url = API_URL + "search/repositories?q=user:";
		try {
			url +=  URLEncoder.encode(user, "utf-8");
		} catch (UnsupportedEncodingException e) {
			// If utf-8 isn't found, just send it as it is
			url += user;

			Log.e(TAG, "getRepoForUser", e);
		}
		url += "&page=" + page + "&per_page=" + MAX_ITEM_PAGE;

		genericGsonRequest(context, responseListener, errorListener, RepositoryList.class, url);
	}

	/**
	 * Send a generic Gson request to the Volley RequestQueue
	 * @param url Url of the request
	 * @param clazz Class of the response
	 * @param <T> Type of the response
	 */
	private static <T> void genericGsonRequest(Context context, Response.Listener<T> responseListener,
	                                      Response.ErrorListener errorListener, Class<T> clazz, String url) {
		GsonRequest<T> gsonRequest = new GsonRequest<>(
				url,
				clazz,
				null,
				responseListener,
				errorListener);

		// Add the request to the Volley RequestQueue
		VolleyRequestQueue.getInstance(context).addToRequestQueue(gsonRequest);
	}

}
