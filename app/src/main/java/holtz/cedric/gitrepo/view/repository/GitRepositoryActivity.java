package holtz.cedric.gitrepo.view.repository;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;

import holtz.cedric.gitrepo.R;
import holtz.cedric.gitrepo.model.User;

/**
 * Created by holtz_c on 1/27/16.
 * Activity handling the repository fragment in portrait mode
 * GitRepo
 */
public class GitRepositoryActivity extends AppCompatActivity {

	private User mUser;
	private GitRepositoryFragment mRepoFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// If we are in landscape mode, stop this activity, GitUserActivity will create another fragment
		if (getResources().getBoolean(R.bool.has_two_panes)) {
			finish();
			return;
		}

		setContentView(R.layout.activity_git_repo);

		Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

		mRepoFragment = (GitRepositoryFragment) getSupportFragmentManager()
				.findFragmentById(R.id.repository_fragment);

		// Get the bundle extras attached to this activity
		Intent intent = getIntent();
		if (intent != null) {
			Bundle bundle = intent.getExtras();
			// Load the view with sent data, true because we want the fragment to do a new search
			restoreState(bundle, true);

			// Set the toolbar name since we are in portrait orientation
			if (mUser != null) {
				toolbar.setTitle(mUser.login);
			}
		} else {
			mRepoFragment.loadRepoData();
		}

		setSupportActionBar(toolbar);

		toolbar.setNavigationIcon(R.drawable.ic_keyboard_backspace);
		// Pop this activity on navigation icon click
		toolbar.setNavigationOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				onBackPressed();
			}
		});

		// Restore the current data on orientation change, false we don't need a repository search
		restoreState(savedInstanceState, false);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// Serialize our current user to handle configuration change
		if (mUser != null) {
			mUser.serializeBundle(outState);
		}
		super.onSaveInstanceState(outState);
	}

	public void restoreState(Bundle savedInstanceState, boolean needsSearch) {
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
		mRepoFragment.loadUser(mUser, needsSearch);
	}
}
