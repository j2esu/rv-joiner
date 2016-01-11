package ru.java2e.android.rvadapterjoiner;

import android.support.annotation.LayoutRes;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Wraps layout to use in {@link AdapterJoiner}.
 */
public class LayoutWrapper implements AdapterJoiner.JoinableWrapper {

	private static final int TYPE = 0;

	interface Callback {

		/**
		 * Runs after layout was inflated. You can use it to perform extra initialization,
		 * such as, for ex. setting onClick listener.
		 * @param view just inflated view
		 * @param parent view parent
		 */
		void onInflateComplete(View view, ViewGroup parent);

	}

	private LayoutWrapper.Adapter adapter;

	public LayoutWrapper(@LayoutRes int layoutResId, @Nullable Callback callback) {
		adapter = new Adapter(layoutResId, callback);
	}

	@Override
	public RecyclerView.Adapter getAdapter() {
		return adapter;
	}

	@Override
	public int getTypeCount() {
		return 1;
	}

	@Override
	public int getType(int typeIndex) {
		return TYPE;//doesn't matter index (we have only one)
	}

	@Override
	public int getTypeIndex(int type) {
		return 0;//the only type, so first index
	}

	private static class Adapter extends RecyclerView.Adapter<Adapter.LayoutVH> {

		private int layoutResId;

		//init default callback to avoid null checking
		private Callback callback = new Callback() {
			@Override
			public void onInflateComplete(View view, ViewGroup parent) {}
		};

		public Adapter(int layoutResId, Callback callback) {
			this.layoutResId = layoutResId;
			if (callback != null) {
				this.callback = callback;
			}
		}

		@Override
		public LayoutVH onCreateViewHolder(ViewGroup parent, int viewType) {
			View view = LayoutInflater.from(parent.getContext())
					.inflate(layoutResId, parent, false);
			callback.onInflateComplete(view, parent);
			return new LayoutVH(view);
		}

		@Override
		public void onBindViewHolder(LayoutVH holder, int position) {}

		@Override
		public int getItemCount() {
			return 1;
		}

		protected static class LayoutVH extends RecyclerView.ViewHolder {

			public LayoutVH(View itemView) {
				super(itemView);
			}

		}

	}

}
