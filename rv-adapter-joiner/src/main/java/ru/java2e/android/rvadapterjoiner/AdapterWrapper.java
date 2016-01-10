package ru.java2e.android.rvadapterjoiner;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

/**
 * Wraps {@link RecyclerView.Adapter} to use in {@link AdapterJoiner}.
 */
public class AdapterWrapper {

	private RecyclerView.Adapter adapter;
	private int[] types;
	private SparseArray<Integer> typesToIndex = new SparseArray<>();

	/**
	 * @param adapter your adapter instance
	 * @param types array of all your type constants, which you return in
	 * 				{@link RecyclerView.Adapter#getItemViewType(int)},
	 *              or null if your adapter have single type
	 */
	public AdapterWrapper(@NonNull RecyclerView.Adapter adapter, @Nullable int[] types) {
		this.adapter = adapter;
		this.types = (types != null ? types : new int[] {0});
		postConstruct();
	}

	private void postConstruct() {
		//init types to index mapping
		for (int i = 0; i < types.length; i++) {
			typesToIndex.put(types[i], i);
		}
	}

	protected RecyclerView.Adapter getAdapter() {
		return adapter;
	}

	protected int getTypeCount() {
		return types.length;
	}

	/**
	 * Get type constant by type index
	 */
	protected int getType(int typeIndex) {
		return types[typeIndex];
	}

	/**
	 * Get type index by type constant
	 */
	protected int getTypeIndex(int type) {
		return typesToIndex.get(type);
	}

}
