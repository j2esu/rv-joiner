package su.j2e.rvjoiner;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.SparseArray;

/**
 * Wraps {@link RecyclerView.Adapter} to use in {@link RvJoiner}.
 */
public class JoinableAdapter implements RvJoiner.Joinable {

	private RecyclerView.Adapter adapter;
	private int[] types;
	private SparseArray<Integer> typesToIndex = new SparseArray<>();

	/**
	 * @param adapter your adapter instance
	 * @param types array of all your type constants, which you return in
	 * 				{@link RecyclerView.Adapter#getItemViewType(int)},
	 *              or null if your adapter have single type
	 */
	public JoinableAdapter(@NonNull RecyclerView.Adapter adapter, @Nullable int[] types) {
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

	@Override
	public RecyclerView.Adapter getAdapter() {
		return adapter;
	}

	@Override
	public int getTypeCount() {
		return types.length;
	}

	@Override
	public int getType(int typeIndex) {
		return types[typeIndex];
	}

	@Override
	public int getTypeIndex(int type) {
		return typesToIndex.get(type);
	}

}
