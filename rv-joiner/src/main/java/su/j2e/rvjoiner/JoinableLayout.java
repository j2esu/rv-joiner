package su.j2e.rvjoiner;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Wraps layout to use in {@link RvJoiner}.
 */
public class JoinableLayout implements RvJoiner.Joinable {

	public interface Callback {

		/**
		 * Runs after layout was inflated. You can use it to perform extra initialization,
		 * such as, for ex. setting onClick listener.
		 * @param view just inflated view
		 * @param parent view parent
		 */
		void onInflateComplete(View view, ViewGroup parent);

	}

	private JoinableLayout.Adapter mAdapter;
	private int mItemType = 0;

	/**
	 * @param layoutResId layout resource to inflate view
	 * @param itemType type constant, or 0, or other value if you don't need it
	 * @param callback callback if you want to customize view after inflating
	 * @param stableId unique id if you need to use stable id feature (calls setHasStableIds(true)
	 *                  on adapter automatically), or {@link RecyclerView#NO_ID} if no stable ids
	 */
	public JoinableLayout(@LayoutRes int layoutResId, int itemType, @Nullable Callback callback,
						  long stableId) {
		mItemType = itemType;
		mAdapter = new Adapter(layoutResId, itemType, callback, stableId);
	}

	/**
	 * The same as {@link #JoinableLayout(int, int, Callback, long)} without stable id
	 */
	public JoinableLayout(@LayoutRes int layoutResId, int itemType, @Nullable Callback callback) {
		this(layoutResId, itemType, callback, RecyclerView.NO_ID);
	}

	/**
	 * The same as {@link #JoinableLayout(int, int, Callback, long)} with 0 type and without stable id
	 */
	public JoinableLayout(@LayoutRes int layoutResId, @Nullable Callback callback) {
		this(layoutResId, 0, callback, RecyclerView.NO_ID);
	}

	/**
	 * The same as {@link #JoinableLayout(int, int, Callback, long)} with null callback and without
	 * stable id
	 */
	public JoinableLayout(@LayoutRes int layoutResId, int itemType) {
		this(layoutResId, itemType, null, RecyclerView.NO_ID);
	}

	/**
	 * Simple constructor if you don't need any customization.
	 * The same as {@link #JoinableLayout(int, int, Callback, long)} with 0 type, null callback and
	 * without stable id
	 * @param layoutResId layout resource to inflate view
	 */
	public JoinableLayout(@LayoutRes int layoutResId) {
		this(layoutResId, 0, null, RecyclerView.NO_ID);
	}

	@Override
	public RecyclerView.Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public int getTypeCount() {
		return 1;
	}

	@Override
	public int getTypeByIndex(int typeIndex) {
		return mItemType;//doesn't matter index (we have only one)
	}

	private static class Adapter extends RecyclerView.Adapter<Adapter.LayoutVh> {

		private int mLayoutResId;
		private int mItemType;
		private long mStableId = RecyclerView.NO_ID;
		private Callback mCallback;

		//pass stableId == RecyclerView.NO_ID if stable ids not used
		private Adapter(int layoutResId, int itemType, Callback callback, long stableId) {
			mLayoutResId = layoutResId;
			mItemType = itemType;
			mCallback = callback;
			mStableId = stableId;
			setHasStableIds(stableId != RecyclerView.NO_ID);
		}

		@Override
		public LayoutVh onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(mLayoutResId, parent, false);
			if (mCallback != null) {
				mCallback.onInflateComplete(view, parent);
			}
			return new LayoutVh(view);
		}

		@Override
		public void onBindViewHolder(LayoutVh holder, int position) {}

		@Override
		public int getItemCount() {
			return 1;
		}

		@Override
		public long getItemId(int position) {
			return mStableId;
		}

		@Override
		public int getItemViewType(int position) {
			return mItemType;
		}

		protected static class LayoutVh extends RecyclerView.ViewHolder {

			public LayoutVh(View itemView) {
				super(itemView);
			}

		}

	}

}
