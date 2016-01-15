package su.j2e.rvjoiner;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

/**
 * Wraps {@link RecyclerView.Adapter} to use in {@link RvJoiner}.
 * Be careful when detecting position of view or view holder in your adapters, you can get position
 * in joined adapter. This class can be helpful.
 * {@link su.j2e.rvjoiner.RvJoiner.RealPositionProvider}
 */
public class JoinableAdapter implements RvJoiner.Joinable {

	private RecyclerView.Adapter mAdapter;
	private int[] mTypes;

	/**
	 * @param adapter your adapter instance
	 * @param types array of all your type constants, which you return in
	 * 				{@link RecyclerView.Adapter#getItemViewType(int)},
	 *              or you can pass no parameters null for simplicity if your adapter have single
	 *              type and you have NOT overrode {@link RecyclerView.Adapter#getItemViewType(int)}
	 */
	public JoinableAdapter(@NonNull RecyclerView.Adapter adapter, @Nullable int... types) {
		mTypes = (types != null && types.length > 0 ? types : new int[] {0});
		mAdapter = adapter;
	}

	/**
	 * The same as {@link JoinableAdapter#JoinableAdapter(RecyclerView.Adapter, int[])}, but
	 * you also can set whether adapter has stable ids
	 */
	public JoinableAdapter(@NonNull RecyclerView.Adapter adapter, boolean hasStableIds,
						   @Nullable int... types) {
		this(adapter, types);
		//setting this can cause IllegalStateException, so we shouldn't call it if it's redundant
		if (adapter.hasStableIds() != hasStableIds) {
			adapter.setHasStableIds(hasStableIds);
		}
	}

	@Override
	public RecyclerView.Adapter getAdapter() {
		return mAdapter;
	}

	@Override
	public int getTypeCount() {
		return mTypes.length;
	}

	@Override
	public int getTypeByIndex(int typeIndex) {
		return mTypes[typeIndex];
	}

}
