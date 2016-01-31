package holtz.cedric.gitrepo.adapter;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

import holtz.cedric.gitrepo.R;
import holtz.cedric.gitrepo.model.Repository;

/**
 * Created by holtz_c on 1/29/16.
 * Adapter displaying a list of repositories
 * GitRepo
 */
public class RepositoryAdapter  extends BaseAdapter {

	// List of Repositories
	private List<Repository> mList;
	// Parent activity
	private Activity mActivity;

	public RepositoryAdapter(Activity activity) {
		this.mActivity = activity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		// Re-instantiate only a layout if it wasn't used before
		if (convertView == null) {
			view = mActivity.getLayoutInflater().inflate(R.layout.row_repository_list, parent, false);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.name = (TextView) view.findViewById(R.id.row_repository_name);
			viewHolder.description = (TextView) view.findViewById(R.id.row_repository_description);

			// Save the ViewHolder to be used later
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		// Retrieve the saved ViewHolder
		ViewHolder holder = (ViewHolder) view.getTag();
		// Get the correct repository
		Repository repository = mList.get(position);

		holder.name.setText(repository.name);
		String description = repository.description;
		// Only display the description field if it isn't empty
		if (!TextUtils.isEmpty(description)) {
			holder.description.setVisibility(View.VISIBLE);
			holder.description.setText(description);
		} else {
			holder.description.setVisibility(View.GONE);
		}
		return view;
	}

	@Override
	public int getCount() {
		// Handle the case where the data can be null
		if (mList == null) {
			return 0;
		}
		return mList.size();
	}

	@Override
	public Repository getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Display a new list of repositories
	 * @param repositories The new list of repositories
	 */
	public void setRepositoryData(List<Repository> repositories) {
		mList = repositories;
		notifyDataSetChanged();
	}

	/**
	 * Remove the current list of displayed repositories
	 */
	public void clearRepositoryData() {
		mList = null;
		notifyDataSetChanged();
	}

	// Holds reference of view element to be re-used
	static class ViewHolder {
		protected TextView name;
		protected TextView description;
	}
}
