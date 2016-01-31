package holtz.cedric.gitrepo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.ListView;

import holtz.cedric.gitrepo.R;

/**
 * Created by holtz_c on 1/30/16.
 * Custom ListView implementing endless scrolling callbacks
 * GitRepo
 */
public class LoadingListView extends ListView implements AbsListView.OnScrollListener {

	// Item number threshold describing when the list will try to load another page
	private static final int MIN_ITEM_LOADING = 3;

	// Listener being called when another page needs to be created
	private OnLoadingListener mListener;
	// Whether the list have the endless scrolling enabled or not
	private boolean mLoadingEnabled;
	// Current footer of the list
	private View mFooterView;

	public interface OnLoadingListener {
		void onLoading();
	}

	public LoadingListView(Context context) {
		super(context);
		init();
	}

	public LoadingListView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public LoadingListView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	private void init() {
		setOnScrollListener(this);
		mLoadingEnabled = false;
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		// If we are MIN_ITEM_LOADING away from the end, call the listener
		int threshold = totalItemCount - MIN_ITEM_LOADING;
		if (threshold > 0 && visibleItemCount + firstVisibleItem >= threshold) {
			if (mListener != null && mLoadingEnabled) {
				mListener.onLoading();
			}
		}
	}

	public void addFooterView() {
		// Assure that there is only one footer view displayed at the same time
		removeFooterView();

		// Create and add a footer view
		mFooterView = ((LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE))
				.inflate(R.layout.row_footer_loading, this, false);
		addFooterView(mFooterView);
	}

	public void removeFooterView() {
		// Removes the current footer view
		if (mFooterView != null) {
			removeFooterView(mFooterView);
			mFooterView = null;
		}
	}

	/**
	 * Register a callback to be called when we are @{code MIN_ITEM_LOADING} away from the end of the list
	 * @param listener The callback that will be invoked.
	 */
	public void setLoadingListener(OnLoadingListener listener) {
		mListener = listener;
	}

	/**
	 * If the listener should be called at the end of the list
	 * @param enabled True if the listener will be called, false otherwise
	 */
	public void setLoadingEnabled(boolean enabled) {
		mLoadingEnabled = enabled;
	}
}
