package su.j2e.rvjoiner;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Joins several {@link RecyclerView.Adapter}, layouts and other {@link RvJoiner.Joinable} into
 * single adapter. Basic usage:
 * <pre>
 * 1. Wrap your adapter with {@link JoinableAdapter}, or wrap your layout with {@link JoinableLayout}.
 * 2. Use {@link #add(Joinable)} to add it to {@link RvJoiner}.
 * 3. When you finished addition, use {@link #getAdapter()} to get adapter and set it to your
 * {@link RecyclerView}
 * </pre>
 * Don't directly use methods of an adapter you received via {@link #getAdapter()}, or use carefully
 * (correct work is not guaranteed). Use methods in {@link RvJoiner} as a public interface.
 */
public class RvJoiner {

	/**
	 * Interface required for object to be used in {@link RvJoiner}
	 */
	interface Joinable {

		RecyclerView.Adapter getAdapter();

		int getTypeCount();

		/**
		 * Get type constant by type index
		 */
		int getType(int typeIndex);

		/**
		 * Get type index by type constant
		 */
		int getTypeIndex(int type);

	}

	private RvJoiner.Adapter adapter;
	private boolean autoUpdate = true;

	//perform only basic updates by now
	RecyclerView.AdapterDataObserver adapterDataObserver = new RecyclerView.AdapterDataObserver() {

		@Override
		public void onChanged() {
			updateData();
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount) {
			updateData();
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			updateData();
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			updateData();
		}

		@Override
		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			updateData();
		}

	};

	/**
	 * @param autoUpdate if true, joiner will listen for data updates in joined adapters,
	 *                   otherwise you have to call notify method manually
	 *                   (ex. {@link #updateData()})
	 */
	public RvJoiner(boolean autoUpdate) {
		adapter = new Adapter();
		this.autoUpdate = autoUpdate;
	}

	/**
	 * The same as {@link #RvJoiner(boolean)} with auto update ON.
	 */
	public RvJoiner() {
		this(true);
	}

	public void add(Joinable joinable) {
		adapter.addToStructure(joinable);
		if (autoUpdate) {
			joinable.getAdapter().registerAdapterDataObserver(adapterDataObserver);
		}
	}

	/**
	 * Should be called after data in joined adapter was changed. Called automatically, if
	 * auto update ON.
	 */
	public void updateData() {
		adapter.onDataSetChanged();
		adapter.notifyDataSetChanged();
	}

	/**
	 * @return adapter, which you can set to RecyclerView.
	 */
	public RecyclerView.Adapter getAdapter() {
		return adapter;
	}

	/**
	 * @see RecyclerView.Adapter#getItemCount()
	 */
	public int getItemCount() {
		return adapter.getItemCount();
	}

	/**
	 * @see RecyclerView.Adapter#getItemId(int)
	 */
	public long getItemId(int position) {
		return adapter.getItemId(position);
	}

	/**
	 * @see RecyclerView.Adapter#setHasStableIds(boolean)
	 */
	public void setHasStableIds(boolean hasStableIds) {
		adapter.setHasStableIds(hasStableIds);
	}

	/*
	Actual joiner adapter implementation.
	Joined position and joined type - values in new composite adapter.
	Real position and real type - values in real sub adapters.
	So, to integrate it we need some mapping, but we don't actually need a maps, because
	joined types is in [0 .. total_types_count) and joined positions in [0 .. new_adapter_size).
	So, we cat use list to accomplish this.
	 */
	private static class Adapter extends RecyclerView.Adapter {

		private List<Joinable> joinables = new ArrayList<>();

		//update once in constructor
		//this lists "maps" joined type (position) on different values
		private List<Integer> joinedTypeToRealType = new ArrayList<>();
		private List<Joinable> joinedTypeToJoinable = new ArrayList<>();

		//update after every data set change
		//this lists "maps" joined position (position) different values
		private int itemCount = 0;
		private List<Integer> joinedPosToJoinedType = new ArrayList<>();
		private List<Integer> joinedPosToRealPos = new ArrayList<>();
		private List<Joinable> joinedPosToJoinable = new ArrayList<>();

		private void addToStructure(Joinable joinable) {
			joinables.add(joinable);
			onStructureChanged();
			onDataSetChanged();//new joinable adds new data
		}

		//todo remove + check onInflateComplete

		/**
		 * Should be called after structure changed
		 * (adding, removing {@link RvJoiner.Joinable} etc.).
		 */
		private void onStructureChanged() {
			joinedTypeToJoinable.clear();
			joinedTypeToRealType.clear();
			for (Joinable joinable : joinables) {
				for (int i = 0; i < joinable.getTypeCount(); i++) {
					joinedTypeToJoinable.add(joinable);
					joinedTypeToRealType.add(joinable.getType(i));
				}
			}
		}

		/**
		 * Should be called after total adapter data set changed.
		 */
		private void onDataSetChanged() {
			itemCount = 0;
			joinedPosToJoinedType.clear();
			joinedPosToRealPos.clear();
			joinedPosToJoinable.clear();
			int prevTypeCount = 0;
			for (Joinable joinable : joinables) {
				for (int i = 0; i < joinable.getAdapter().getItemCount(); i++) {
					itemCount++;
					joinedPosToJoinedType.add(prevTypeCount +
							joinable.getTypeIndex(joinable.getAdapter().getItemViewType(i)));
					joinedPosToRealPos.add(i);
					joinedPosToJoinable.add(joinable);
				}
				prevTypeCount += joinable.getTypeCount();
			}
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int joinedType) {
			return joinedTypeToJoinable.get(joinedType).getAdapter()
					.onCreateViewHolder(parent, joinedTypeToRealType.get(joinedType));
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onBindViewHolder(ViewHolder holder, int joinedPosition) {
			joinedPosToJoinable.get(joinedPosition).getAdapter()
					.onBindViewHolder(holder, joinedPosToRealPos.get(joinedPosition));
		}

		@Override
		public long getItemId(int joinedPosition) {
			return joinedPosToJoinable.get(joinedPosition).getAdapter()
					.getItemId(joinedPosToRealPos.get(joinedPosition));
		}

		@Override
		public int getItemCount() {
			return itemCount;
		}

		@Override
		public int getItemViewType(int joinedPosition) {
			return joinedPosToJoinedType.get(joinedPosition);
		}

	}

}
