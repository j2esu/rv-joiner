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
	 *              or you can pass null for simplicity if your adapter have single type and you
	 *              have NOT overrided {@link RecyclerView.Adapter#getItemViewType(int)}
	 * @throws IllegalArgumentException if types array is empty
	 */
	public JoinableAdapter(@NonNull RecyclerView.Adapter adapter, @Nullable int... types) {
		this.types = (types != null ? types : new int[] {0});
		if (this.types.length <= 0) throw new IllegalArgumentException("Types array can't be empty");
		this.adapter = adapter;
		postConstruct();
	}

	/**
	 * The same as {@link JoinableAdapter#JoinableAdapter(RecyclerView.Adapter, int[])}, but
	 * you also can set whether adapter has stable ids
	 */
	public JoinableAdapter(@NonNull RecyclerView.Adapter adapter, boolean hasStableIds,
						   @Nullable int... types) {
		this(adapter, types);
		//setting this can cause IllegalStateException, so we shouldn't call it if redundant
		if (adapter.hasStableIds() != hasStableIds) {
			adapter.setHasStableIds(hasStableIds);
			System.out.println("sets " + hasStableIds);
		}
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
