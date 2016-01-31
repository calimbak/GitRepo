package holtz.cedric.gitrepo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by holtz_c on 1/28/16.
 * Model defining the information we need in a user search
 * GitRepo
 */
public class UserList {

	@SerializedName("total_count")
	public Integer totalCount;

	@SerializedName("items")
	public User[] users;

}
