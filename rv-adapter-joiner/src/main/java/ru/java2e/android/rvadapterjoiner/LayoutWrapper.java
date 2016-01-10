package ru.java2e.android.rvadapterjoiner;

import android.support.annotation.LayoutRes;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Wraps layout to use in {@link AdapterJoiner}.
 */
public class LayoutWrapper extends RecyclerView.Adapter {

	private int resourceId;

	public LayoutWrapper(@LayoutRes int layoutResId) {
		this.resourceId = layoutResId;
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new LayoutVH(LayoutInflater.from(parent.getContext())
				.inflate(resourceId, parent, false));
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {}

	@Override
	public int getItemCount() {
		return 1;
	}

	private class LayoutVH extends RecyclerView.ViewHolder {

		public LayoutVH(View itemView) {
			super(itemView);
		}

	}

}
