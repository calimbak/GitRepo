package holtz.cedric.gitrepo.model;

import com.google.gson.annotations.SerializedName;

/**
 * Created by holtz_c on 1/29/16.
 * Model defining the information we need in a repository search
 * GitRepo
 */
public class RepositoryList {

	@SerializedName("total_count")
	public Integer totalCount;

	@SerializedName("items")
	public Repository[] repositories;

}
