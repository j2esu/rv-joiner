package ru.java2e.android.rvadapterjoiner;

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

/**
 * Joins adapters ({@link RecyclerView.Adapter}) into single adapter via {@link #add(JoinableWrapper)}.
 * Use {@link #getAdapter()} to get adapter for {@link RecyclerView}, but don't use other methods
 * of this adapter directly, or use carefully (correct work is not guaranteed).
 * Use methods in joiner class as public interface.
 */
public class AdapterJoiner {

	/**
	 * Interface required to be used in {@link AdapterJoiner}
	 */
	interface JoinableWrapper {

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

	private AdapterJoiner.Adapter adapter;
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
	public AdapterJoiner(boolean autoUpdate) {
		adapter = new Adapter();
		this.autoUpdate = autoUpdate;
	}

	/**
	 * The same as {@link #AdapterJoiner(boolean)} with auto update ON.
	 */
	public AdapterJoiner() {
		this(true);
	}

	public void add(JoinableWrapper wrapper) {
		adapter.addToStructure(wrapper);
		if (autoUpdate) {
			wrapper.getAdapter().registerAdapterDataObserver(adapterDataObserver);
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

		private List<JoinableWrapper> wrappers = new ArrayList<>();

		//update once in constructor
		//this lists "maps" joined type (position) on different values
		private List<Integer> joinedTypeToRealType = new ArrayList<>();
		private List<JoinableWrapper> joinedTypeToWrapper = new ArrayList<>();

		//update after every data set change
		//this lists "maps" joined position (position) different values
		private int itemCount = 0;
		private List<Integer> joinedPosToJoinedType = new ArrayList<>();
		private List<Integer> joinedPosToRealPos = new ArrayList<>();
		private List<JoinableWrapper> joinedPosToWrapper = new ArrayList<>();

		private void addToStructure(JoinableWrapper wrapper) {
			wrappers.add(wrapper);
			onStructureChanged();
			onDataSetChanged();//new wrapper data added
		}

		//todo remove + check onInflateComplete

		private void onStructureChanged() {
			joinedTypeToWrapper.clear();
			joinedTypeToRealType.clear();
			for (JoinableWrapper wrapper : wrappers) {
				for (int i = 0; i < wrapper.getTypeCount(); i++) {
					joinedTypeToWrapper.add(wrapper);
					joinedTypeToRealType.add(wrapper.getType(i));
				}
			}
		}

		private void onDataSetChanged() {
			itemCount = 0;
			joinedPosToJoinedType.clear();
			joinedPosToRealPos.clear();
			joinedPosToWrapper.clear();
			int prevTypeCount = 0;
			for (JoinableWrapper wrapper : wrappers) {
				for (int i = 0; i < wrapper.getAdapter().getItemCount(); i++) {
					itemCount++;
					joinedPosToJoinedType.add(prevTypeCount +
							wrapper.getTypeIndex(wrapper.getAdapter().getItemViewType(i)));
					joinedPosToRealPos.add(i);
					joinedPosToWrapper.add(wrapper);
				}
				prevTypeCount += wrapper.getTypeCount();
			}
		}

		@Override
		public ViewHolder onCreateViewHolder(ViewGroup parent, int joinedType) {
			return joinedTypeToWrapper.get(joinedType).getAdapter()
					.onCreateViewHolder(parent, joinedTypeToRealType.get(joinedType));
		}

		@Override
		@SuppressWarnings("unchecked")
		public void onBindViewHolder(ViewHolder holder, int joinedPosition) {
			joinedPosToWrapper.get(joinedPosition).getAdapter()
					.onBindViewHolder(holder, joinedPosToRealPos.get(joinedPosition));
		}

		@Override
		public long getItemId(int joinedPosition) {
			return joinedPosToWrapper.get(joinedPosition).getAdapter()
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
