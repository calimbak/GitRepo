package holtz.cedric.gitrepo.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by holtz_c on 1/30/16.
 * Singleton used to retain data during orientation change
 * GitRepo
 */
public class GitRetainedData {

	private static GitRetainedData mInstance;

	// Current list of users
	private List<User> mUsers;
	// Current number of users
	private Integer mTotalUserCount;

	// Current list of repositories
	private List<Repository> mRepositories;
	// Current number of repositories
	private Integer mTotalRepositoryCount;

	/**
	 * Return the current instance, create one if needed
	 * @return Current instance of GitRetainedData
	 */
	public static synchronized GitRetainedData getInstance() {
		if (mInstance == null) {
			mInstance = new GitRetainedData();
		}
		return mInstance;
	}

	/**
	 * Return the current list of users
	 * @return Current list of users
	 */
	public List<User> getUsers() {
		return mUsers;
	}

	/**
	 * Return the current list of repositories
	 * @return Current list of repositories
	 */
	public List<Repository> getRepositories() {
		return mRepositories;
	}

	/**
	 * Clear the saved repositories and repository count
	 */
	public void clearSavedRepositories() {
		mRepositories = null;
		mTotalRepositoryCount = null;
	}

	/**
	 * Add repositories to the current list, create one if needed
	 * @param repositories Repositories to be added
	 */
	public void addRepositories(Repository[] repositories) {
		if (mRepositories == null) {
			mRepositories = new ArrayList<>();
		}
		Collections.addAll(mRepositories, repositories);
	}

	/**
	 * Clear the saved users and user count
	 */
	public void clearSavedUser() {
		mUsers = null;
		mTotalUserCount = null;
	}

	/**
	 * Add users to the current list, create one if needed
	 * @param users Users to be added
	 */
	public void addUser(User[] users) {
		if (mUsers == null) {
			mUsers = new ArrayList<>();
		}
		Collections.addAll(mUsers, users);
	}

	/**
	 * Return the current numbers of users
	 * @return Current number of users or null
	 */
	public Integer getTotalUserCount() {
		return mTotalUserCount;
	}

	/**
	 * Set the current number of users
	 * @param totalUserCount Number of users
	 */
	public void setTotalUserCount(Integer totalUserCount) {
		this.mTotalUserCount = totalUserCount;
	}

	/**
	 * Return the current numbers of repositories
	 * @return Current number of repositories or null
	 */
	public Integer getTotalRepositoryCount() {
		return mTotalRepositoryCount;
	}

	/**
	 * Set the current number of repositories
	 * @param totalRepositoryCount Number of repositories
	 */
	public void setTotalRepositoryCount(Integer totalRepositoryCount) {
		this.mTotalRepositoryCount = totalRepositoryCount;
	}
}
