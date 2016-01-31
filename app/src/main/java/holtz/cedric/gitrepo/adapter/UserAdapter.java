package holtz.cedric.gitrepo.adapter;

import android.app.Activity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.List;

import holtz.cedric.gitrepo.R;
import holtz.cedric.gitrepo.model.User;
import holtz.cedric.gitrepo.net.VolleyRequestQueue;

/**
 * Created by holtz_c on 1/29/16.
 * Adapter displaying a list of uers
 * GitRepo
 */
public class UserAdapter extends BaseAdapter {

	// List of users
	private List<User> mList;
	// Parent activity
	private Activity mActivity;
	// ImageLoader used by NetworkImageView
	private ImageLoader mImageLoader;

	public UserAdapter(Activity activity) {
		this.mActivity = activity;
		mImageLoader = VolleyRequestQueue.getInstance(mActivity).getImageLoader();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view;
		// Re-instantiate only a layout if it wasn't used before
		if (convertView == null) {
			view = mActivity.getLayoutInflater().inflate(R.layout.row_user_list, parent, false);
			final ViewHolder viewHolder = new ViewHolder();
			viewHolder.username = (TextView) view.findViewById(R.id.row_user_name);
			viewHolder.avatar = (NetworkImageView) view.findViewById(R.id.row_user_avatar);

			// Save the ViewHolder to be used later
			view.setTag(viewHolder);
		} else {
			view = convertView;
		}

		// Retrieve the saved ViewHolder
		ViewHolder holder = (ViewHolder) view.getTag();
		// Get the correct user
		User user = mList.get(position);

		holder.username.setText(user.login);
		holder.avatar.setImageUrl(user.avatarUrl, mImageLoader);
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
	public User getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * Display a new list of users
	 * @param users The new list of users
	 */
	public void setUserData(List<User> users) {
		mList = users;
		notifyDataSetChanged();
	}

	/**
	 * Remove the current list of displayed users
	 */
	public void clearUserData() {
		mList = null;
		notifyDataSetChanged();
	}

	// Holds reference of view element to be re-used
	static class ViewHolder {
		protected TextView username;
		protected NetworkImageView avatar;
	}
}
