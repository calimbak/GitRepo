package holtz.cedric.gitrepo.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;
import android.util.Log;

import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import holtz.cedric.gitrepo.R;

/**
 * Created by holtz_c on 1/30/16.
 * Utility class around network calls
 * GitRepo
 */
public class NetworkUtils {

	private static final String TAG = "NetworkUtils";
	private static final String KEY_MESSAGE = "message";

	/**
	 * Check if the network is currently available
	 * @return True if the network is available, false otherwise
	 */
	public static boolean inNetworkAvailable(Context context) {
		ConnectivityManager cm =
				(ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		return activeNetwork != null &&
				activeNetwork.isConnectedOrConnecting();
	}

	/**
	 * Parse a VolleyError object and return an error message
	 * @param error VolleyError object to be parsed, can be null
	 * @return Message to be display to the user
	 */
	public static String parseError(VolleyError error, Context context) {
		if (error == null) {
			// Only happens when there is no connection
			return context.getString(R.string.error_no_connection);
		}

		// If the VolleyError contains data, try to get the error message
		if (error.networkResponse != null
				&& error.networkResponse.data != null) {
			try {
				JSONObject jsonObject = new JSONObject(new String(error.networkResponse.data));
				String message = jsonObject.getString(KEY_MESSAGE);
				if (!TextUtils.isEmpty(message)) {
					return message;
				}
			} catch (JSONException e) {
				Log.e(TAG, "parseError", e);
			}
		}
		// Display a generic error message in there was a problem
		return context.getString(R.string.error_search);
	}
}
