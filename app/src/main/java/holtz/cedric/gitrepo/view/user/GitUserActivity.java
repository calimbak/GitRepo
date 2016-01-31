package holtz.cedric.gitrepo.view.user;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import holtz.cedric.gitrepo.R;
import holtz.cedric.gitrepo.model.User;
import holtz.cedric.gitrepo.view.repository.GitRepositoryActivity;
import holtz.cedric.gitrepo.view.repository.GitRepositoryFragment;

/**
 * Created by holtz_c on 1/27/16.
 * Activity handling the user fragment in portrait mode
 *                   the user and repository fragment in landscape mode
 * GitRepo
 */
public class GitUserActivity extends AppCompatActivity implements GitUserFragment.OnUserClickListener {

	private GitRepositoryFragment mRepositoryFragment;
	// If the layout is in landscape
	private boolean mIsDualPane;
	// Current clicked user
	private User mUser;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_layout);
		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
		setSupportActionBar(toolbar);

		// Set the current layout orientation
		mIsDualPane = getResources().getBoolean(R.bool.has_two_panes);

		// Retrieve the fragments references
		GitUserFragment userFragment = (GitUserFragment)
				getSupportFragmentManager().findFragmentById(R.id.user_fragment);
		mRepositoryFragment = (GitRepositoryFragment)
				getSupportFragmentManager().findFragmentById(R.id.repository_fragment);

		// Initialize our fragment
		userFragment.setOnUserClickListener(this);
		userFragment.loadUserData();

		// Restore the current intent on orientation change
		restoreState(savedInstanceState);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Serialize our current user to handle configuration change
		if (mUser != null) {
			mUser.serializeBundle(outState);
		}
		super.onSaveInstanceState(outState);
	}

	/**
	 * Try to get user data from the Bundle
	 * @param savedInstanceState Bundle containing an user
	 */
	private void restoreState(Bundle savedInstanceState) {
		// Return if there isn't user data in the bundle
		if (!User.containsUserData(savedInstanceState)) {
			return;
		}

		// Create an user if needed
		if (mUser == null) {
			mUser = new User();
		}
		// Load bundle data
		mUser.deserializeBundle(savedInstanceState);
		// If we are in landscape mode, update user info on the repository fragment
		if (mIsDualPane && mRepositoryFragment != null) {
			mRepositoryFragment.loadUser(mUser, false);
			mRepositoryFragment.loadRepoData();
		}
	}

	@Override
	public void onUserClicked(User user) {
		mUser = user;
		if (mIsDualPane) {
			// Update the user data in the repository fragment
			mRepositoryFragment.loadUser(user, true);
		} else {
			// Launch a new activity to display the repositories
			Intent intent = new Intent(this, GitRepositoryActivity.class);

			// Attach current user's data to the intent
			Bundle bundle = new Bundle();
			user.serializeBundle(bundle);
			intent.putExtras(bundle);

			startActivity(intent);
		}
	}

	@Override
	public void onResetUser() {
		mUser = null;
	}
}
