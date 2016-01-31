package holtz.cedric.gitrepo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by holtz_c on 1/29/16.
 * Model defining the information we need in a repository
 * GitRepo
 */
public class Repository {

	@SerializedName("name")
	public String name;

	@SerializedName("description")
	public String description;

	@SerializedName("html_url")
	public String url;

}
