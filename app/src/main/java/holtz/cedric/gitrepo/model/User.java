package holtz.cedric.gitrepo.model;

import android.os.Bundle;

import com.google.gson.annotations.SerializedName;

/**
 * Created by holtz_c on 1/28/16.
 * Model defining the information we need in a user
 * GitRepo
 */
public class User {

	private static final String KEY_LOGIN = "login";
	private static final String KEY_AVATAR_URL = "avatar_url";

	@SerializedName("login")
	public String login;

	@SerializedName("avatar_url")
	public String avatarUrl;

	/**
	 * Save the current data to the bundle
	 * @param bundle Bundle to store the data
	 */
	public void serializeBundle(Bundle bundle) {
		bundle.putString(KEY_LOGIN, login);
		bundle.putString(KEY_AVATAR_URL, avatarUrl);
	}

	/**
	 * Extract data from the bundle
	 * @param bundle Bundle to extract data from
	 */
	public void deserializeBundle(Bundle bundle) {
		if (bundle != null) {
			login = bundle.getString(KEY_LOGIN);
			avatarUrl = bundle.getString(KEY_AVATAR_URL);
		}
	}

	/**
	 * Check if the bundle contains data regarding an user
	 * @param bundle Bundle containing the data
	 * @return True if the bundle contains an user, false otherwise
	 */
	public static boolean containsUserData(Bundle bundle) {
		return (bundle != null
				&& bundle.containsKey(KEY_LOGIN)
				&& bundle.containsKey(KEY_AVATAR_URL));
	}
}
