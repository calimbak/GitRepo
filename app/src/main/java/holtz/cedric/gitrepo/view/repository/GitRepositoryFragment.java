package holtz.cedric.gitrepo.view.repository;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import holtz.cedric.gitrepo.R;
import holtz.cedric.gitrepo.adapter.RepositoryAdapter;
import holtz.cedric.gitrepo.model.GitRetainedData;
import holtz.cedric.gitrepo.model.Repository;
import holtz.cedric.gitrepo.model.RepositoryList;
import holtz.cedric.gitrepo.model.User;
import holtz.cedric.gitrepo.net.NetQuery;
import holtz.cedric.gitrepo.net.VolleyRequestQueue;
import holtz.cedric.gitrepo.utils.NetworkUtils;
import holtz.cedric.gitrepo.view.LoadingListView;

/**
 * Created by holtz_c on 1/27/16.
 * Fragment handling data regarding the repositories
 * GitRepo
 */
public class GitRepositoryFragment extends Fragment {

	private TextView mUsername;
	private LoadingListView mRepositoryList;
	private ProgressBar mLoading;
	private TextView mCount;

	/*
		The avatar was meant to be displayed in a CollapsingToolbarLayout with a parallax NetworkImageView
		The only solution for the nested ListView to work was to use a minimal API of 21 or a Recycler View
		In this case, we will just display the avatar on the view
	 */
	private NetworkImageView mAvatar;

	// Current view
	private View mView;
	// Current User
	private User mUser;
	// Adapter populating the list
	private RepositoryAdapter mRepositoryAdapter;

	public GitRepositoryFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_git_repo, container, false);

		mAvatar = (NetworkImageView) mView.findViewById(R.id.repo_avatar);
		mUsername = (TextView) mView.findViewById(R.id.repo_username);
		mRepositoryList = (LoadingListView) mView.findViewById(R.id.repo_list);
		mLoading = (ProgressBar) mView.findViewById(R.id.repo_loading);
		mCount = (TextView) mView.findViewById(R.id.repo_count);

		mRepositoryAdapter = new RepositoryAdapter(getActivity());
		mRepositoryList.setAdapter(mRepositoryAdapter);

		mRepositoryList.setLoadingListener(new LoadingListView.OnLoadingListener() {
			@Override
			public void onLoading() {
				// If there is still repositories to be fetched, request another page
				Integer totalCount = GitRetainedData.getInstance().getTotalUserCount();
				if (totalCount != null && mRepositoryAdapter.getCount() < totalCount) {
					// Disable further loading call
					mRepositoryList.setLoadingEnabled(false);
					// Add the loading footer view
					mRepositoryList.addFooterView();
					// Request another page
					requestRepositoryData();
				}
			}
		});

		mRepositoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// On repository click, launch the repository URL
				Repository repository = mRepositoryAdapter.getItem(position);
				launchUrl(repository.url);
			}
		});

		return mView;
	}

	/**
	 * Launch the specified url on the Android system browser
	 * @param url Url to be launched
	 */
	private void launchUrl(String url) {
		Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
		startActivity(browserIntent);
	}

	/**
	 *  Send a request to the GitHub API searching repositories for an user
	 */
	private void requestRepositoryData() {
		// If the network isn't available, display immediately an error
		if (!NetworkUtils.inNetworkAvailable(getContext())) {
			displayError(null);
			return;
		}

		// Request the new page with the current search and the correct page
		// The pagination starts at page 1
		NetQuery.getRepoForUser(getContext(), new Response.Listener<RepositoryList>() {
			@Override
			public void onResponse(RepositoryList response) {
				// Restore view state
				mLoading.setVisibility(View.INVISIBLE);
				mRepositoryList.removeFooterView();

				// Save current search data
				GitRetainedData retainedData = GitRetainedData.getInstance();
				retainedData.addRepositories(response.repositories);
				retainedData.setTotalRepositoryCount(response.totalCount);

				// Load current search data
				List<Repository> repositories = GitRetainedData.getInstance().getRepositories();
				mRepositoryAdapter.setRepositoryData(repositories);
				mRepositoryList.setLoadingEnabled(response.totalCount != repositories.size());
				setResultCount(response.totalCount);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError error) {
				// Handle error response
				displayError(error);
			}
		}, mUser.login, (int) Math.floor(mRepositoryAdapter.getCount() / NetQuery.MAX_ITEM_PAGE) + 1);
	}

	/**
	 * Display an error message on the view
	 * @param error The error to be parsed, can be null
	 */
	private void displayError(VolleyError error) {
		// Restore view state
		mLoading.setVisibility(View.INVISIBLE);
		mRepositoryList.removeFooterView();
		mCount.setText("");

		// Display an error on the view
		Snackbar.make(mView, NetworkUtils.parseError(error, getContext()), Snackbar.LENGTH_LONG).show();
	}

	/**
	 * Update the view with user data, can force a new repository search
	 * @param user User to be loaded on the view
	 * @param needsSearch True if the repository list need to be fetched on the GitHub API, false otherwise
	 */
	public void loadUser(User user, boolean needsSearch) {
		if (user == null) {
			return;
		}

		mUser = user;
		// If we are on landscape orientation, set the username otherwise it will be displayed on the Toolbar
		if (getResources().getBoolean(R.bool.has_two_panes)) {
			mUsername.setText(mUser.login);
		}
		mAvatar.setImageUrl(mUser.avatarUrl,
				VolleyRequestQueue.getInstance(getContext()).getImageLoader());

		if (needsSearch) {
			// Request a new repository search for the current user
			loadRepoUser();
		} else {
			// Load saved repository data on the view
			loadRepoData();
		}
	}

	/**
	 * Clear the stored data and request the repository list for the current user
	 */
	private void loadRepoUser() {
		// Clear view and saved data state
		mCount.setText("");
		mLoading.setVisibility(View.VISIBLE);
		mRepositoryAdapter.clearRepositoryData();
		GitRetainedData.getInstance().clearSavedRepositories();

		// Request a new repository list for this user
		requestRepositoryData();
	}

	/**
	 * Load the saved data on the view
	 */
	public void loadRepoData() {
		// Restore view data
		GitRetainedData retainedData = GitRetainedData.getInstance();
		mRepositoryAdapter.setRepositoryData(retainedData.getRepositories());

		Integer totalCount = retainedData.getTotalRepositoryCount();
		if (totalCount != null) {
			mRepositoryList.setLoadingEnabled(mRepositoryAdapter.getCount() != totalCount);
			setResultCount(totalCount);
		}
	}

	/**
	 * Display the current repository count on the total repository count
	 * @param total Total result count
	 */
	private void setResultCount(int total) {
		String resultCount = String.format(getString(R.string.result_repository_count),
				mRepositoryAdapter.getCount(), total);
		mCount.setText(resultCount);
	}
}
