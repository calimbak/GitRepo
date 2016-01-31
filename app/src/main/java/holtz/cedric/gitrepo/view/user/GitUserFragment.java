package holtz.cedric.gitrepo.view.user;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.List;

import holtz.cedric.gitrepo.R;
import holtz.cedric.gitrepo.adapter.UserAdapter;
import holtz.cedric.gitrepo.model.GitRetainedData;
import holtz.cedric.gitrepo.model.User;
import holtz.cedric.gitrepo.model.UserList;
import holtz.cedric.gitrepo.net.NetQuery;
import holtz.cedric.gitrepo.utils.NetworkUtils;
import holtz.cedric.gitrepo.view.LoadingListView;

/**
 * Created by holtz_c on 1/27/16.
 * Fragment handling data regarding the users
 * GitRepo
 */
public class GitUserFragment extends Fragment {

	private static final String KEY_CURRENT_SEARCH = "current_search";

	private EditText mSearchText;
	private LoadingListView mUserList;
	private ProgressBar mLoading;
	private TextView mCount;

	// Current view
	private View mView;
	// Adapter populating the list of users
	private UserAdapter mUserAdapter;
	// Callback used on user click
	private OnUserClickListener mClickListener;
	// Store the last search to load pages in the future
	private String mCurrentSearch;

	/**
	 * Interface definition for:
	 *   - a callback to be invoked when an user is clicked.
	 *   - a callback to be invoked when a user is cleared.
	 */
	public interface OnUserClickListener {
		/**
		 * Callback called when an user is clicked
		 * @param user Clicked user
		 */
		void onUserClicked(User user);

		/**
		 * Callback called when the user is cleared
		 */
		void onResetUser();
	}

	public GitUserFragment() {

	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
	                         Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_git_user, container, false);

		Button searchButton = (Button) mView.findViewById(R.id.user_search_button);
		mSearchText = (EditText) mView.findViewById(R.id.user_search_text);
		mUserList = (LoadingListView) mView.findViewById(R.id.user_list);
		TextView emptyText = (TextView) mView.findViewById(R.id.user_empty_element);
		mLoading = (ProgressBar) mView.findViewById(R.id.user_loading);
		mCount = (TextView) mView.findViewById(R.id.user_count);

		mUserAdapter = new UserAdapter(getActivity());
		mUserList.setEmptyView(emptyText);
		mUserList.setAdapter(mUserAdapter);

		searchButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				// Clear the EditText focus and do the Network operation
				mSearchText.clearFocus();
				searchUser();
			}
		});

		mUserList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				// If there is a listener, send the clicked user
				if (mClickListener != null) {
					mClickListener.onUserClicked(mUserAdapter.getItem(position));
				}
			}
		});
		mUserList.setLoadingListener(new LoadingListView.OnLoadingListener() {
			@Override
			public void onLoading() {
				// If there is still users to be fetched, request another page
				Integer totalCount = GitRetainedData.getInstance().getTotalUserCount();
				if (totalCount != null && mUserAdapter.getCount() < totalCount) {
					// Disable further loading call
					mUserList.setLoadingEnabled(false);
					// Add the loading footer view
					mUserList.addFooterView();
					// Request another page
					requestUserSearch();
				}
			}
		});

		// Set the ListView choice mode regarding the orientation
		if (getResources().getBoolean(R.bool.has_two_panes)) {
			mUserList.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		} else {
			mUserList.setChoiceMode(ListView.CHOICE_MODE_NONE);
		}

		// Retrieve the current search
		if (savedInstanceState != null) {
			mCurrentSearch = savedInstanceState.getString(KEY_CURRENT_SEARCH);
		}

		return mView;
	}

	/**
	 * Send a request to the GitHub API searching a new user
	 */
	private void requestUserSearch() {
		// If the network isn't available, display immediately an error
		if (!NetworkUtils.inNetworkAvailable(getContext())) {
			displayError(null);
			return;
		}

		// Request the new page with the current search and the correct page
		// The pagination starts at page 1
		NetQuery.getUserList(getContext(), new Response.Listener<UserList>() {
					@Override
					public void onResponse(UserList response) {
						// Restore view state
						mUserList.removeFooterView();
						mLoading.setVisibility(View.INVISIBLE);

						// Save current search data
						GitRetainedData retainedData = GitRetainedData.getInstance();
						retainedData.addUser(response.users);
						retainedData.setTotalUserCount(response.totalCount);

						// Load current search data
						List<User> users = retainedData.getUsers();
						mUserList.setLoadingEnabled(response.totalCount != users.size());
						mUserAdapter.setUserData(users);
						setResultCount(response.totalCount);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						// Handle error response
						displayError(error);
					}
				}, mCurrentSearch,
				(int) Math.floor(mUserAdapter.getCount() / NetQuery.MAX_ITEM_PAGE) + 1);
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// Save the current search
		outState.putString(KEY_CURRENT_SEARCH, mCurrentSearch);
		super.onSaveInstanceState(outState);
	}

	/**
	 * Display an error message on the view
	 * @param error The error to be parsed, can be null
	 */
	private void displayError(VolleyError error) {
		// Restore view state
		mLoading.setVisibility(View.INVISIBLE);
		mUserList.removeFooterView();
		mCount.setText("");

		// Display an error on the view
		Snackbar.make(mView, NetworkUtils.parseError(error, getContext()), Snackbar.LENGTH_LONG).show();
	}

	/**
	 * Register a callback being called when an user is clicked
	 * @param listener The new listener
	 */
	public void setOnUserClickListener(OnUserClickListener listener) {
		mClickListener = listener;
	}

	/**
	 * Check if the data in the user field is correct, initiate the view and call a method to send the request
	 */
	private void searchUser() {
		// Make sure that the supplied data is valid
		if (!isDataValid()) {
			// Known issue where the pop-up may be misplaced with a Coordinator Layout, will be fix soon
			// https://code.google.com/p/android/issues/detail?id=193793
			mSearchText.setError(getString(R.string.error_empty_search));
			mSearchText.requestFocus();
			return;
		}

		// If a callback is registered, specify that we changed our search
		if (mClickListener != null) {
			mClickListener.onResetUser();
		}

		// Save the current search to handle future page request
		mCurrentSearch = mSearchText.getText().toString();

		// Clear current search data
		mLoading.setVisibility(View.VISIBLE);
		mUserAdapter.clearUserData();
		GitRetainedData.getInstance().clearSavedUser();

		// Call a request to the API
		requestUserSearch();
	}

	/**
	 * Load user stored data on the view
	 */
	public void loadUserData() {
		GitRetainedData retainedData = GitRetainedData.getInstance();
		mUserAdapter.setUserData(retainedData.getUsers());
		Integer totalCount = retainedData.getTotalUserCount();
		if (totalCount != null) {
			mUserList.setLoadingEnabled(mUserAdapter.getCount() != totalCount);
			setResultCount(totalCount);
		}
	}

	/**
	 * Display the current user count on the total user count
	 * @param total Total result count
	 */
	private void setResultCount(int total) {
		String resultCount = String.format(getString(R.string.result_user_count),
				mUserAdapter.getCount(), total);
		mCount.setText(resultCount);
	}

	/**
	 * Check if the supplied user is valid
	 * @return True if it is valid, false otherwise
	 */
	private boolean isDataValid() {
		String text = mSearchText.getText().toString();
		return !TextUtils.isEmpty(text.trim());
	}

}
