package su.j2e.rvjoiner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseArray;
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
 * Don't directly use methods of an adapter you received via {@link #getAdapter()},
 * or use carefully (correct work is not guaranteed). Use methods in {@link RvJoiner} as a public
 * interface when possible. <b>NOTE:</b> notifySomething() methods will NOT work correctly, use
 * automatic data update (enabled in default constructor), or {@link RvJoiner#updateData()} to
 * update data manually.
 */
public class RvJoiner {

	/**
	 * Interface required for object to be used in {@link RvJoiner}.
	 * @see JoinableAdapter
	 * @see JoinableLayout
	 */
	public interface Joinable {

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

	private static final String TAG = RvJoiner.class.getName();

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

	/**
	 * Adds joinable to the bottom of adapter.
	 * @param joinable joinable to add, not null
	 * @return false if already was added before
	 * @throws IllegalArgumentException if joinable is null
	 */
	public boolean add(Joinable joinable) {
		if (joinable == null) throw new IllegalArgumentException("Joinable can't be null");
		if (autoUpdate) {
			try {//avoid "observer was already registered" exception
				joinable.getAdapter().registerAdapterDataObserver(adapterDataObserver);
			} catch (IllegalStateException ex) {
				Log.d(TAG, "add: observer was already registered");
			}
		}
		return adapter.addJoinableToStructure(joinable);
	}

	/**
	 * Removes joinable from adapter.
	 * @param joinable joinable to remove, not null
	 * @return false if wasn't added
	 * @throws IllegalArgumentException if joinable is null
	 */
	public boolean remove(Joinable joinable) {
		if (joinable == null) throw new IllegalArgumentException("Joinable can't be null");
		if (autoUpdate) {
			try {//avoid "observer wasn't registered" exception
				joinable.getAdapter().unregisterAdapterDataObserver(adapterDataObserver);
			} catch (IllegalStateException ex) {
				Log.d(TAG, "remove: observer wasn't registered");
			}
		}
		return adapter.removeJoinableFromStructure(joinable);
	}

	/**
	 * Should be called after data in joined adapter was changed. Called automatically, if
	 * auto update ON.
	 */
	public void updateData() {
		adapter.onDataSetChanged();
	}

	//todo other notification methods like updateData(from, to)

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

	/**
	 * @param joinedPosition total joined position (form 0 to {@link #getItemCount()}  - 1)
	 * @return object which wraps info, or null if position doesn't exist
	 * @see su.j2e.rvjoiner.RvJoiner.ItemInfo
	 */
	public ItemInfo getItemInfo(int joinedPosition) {
		return adapter.getItemInfoObject(joinedPosition);
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

		private static final String TAG = Adapter.class.getName();

		private List<Joinable> joinables = new ArrayList<>();
		private SparseArray<ItemInfo> itemInfoCache = new SparseArray<>();

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

		private boolean addJoinableToStructure(@NonNull Joinable joinable) {
			boolean wasAdded = joinables.add(joinable);
			if (wasAdded) {
				onStructureChanged();
			}
			return wasAdded;
		}

		private boolean removeJoinableFromStructure(@NonNull Joinable joinable) {
			boolean wasRemoved = joinables.remove(joinable);
			if (wasRemoved) {
				onStructureChanged();
			}
			return wasRemoved;
		}

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
			onDataSetChanged();//new joinable adds new data
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
			itemInfoCache.clear();
			notifyDataSetChanged();
		}

		/**
		 * Can return null, if position doesn't exist.
		 */
		private ItemInfo getItemInfoObject(int joinedPosition) {
			ItemInfo itemInfo = itemInfoCache.get(joinedPosition);
			if (itemInfo == null) {
				try {
					itemInfo = new ItemInfo(
							joinedPosition,
							joinedPosToRealPos.get(joinedPosition),
							joinedPosToJoinable.get(joinedPosition),
							joinedPosToJoinedType.get(joinedPosition),
							joinedTypeToRealType.get(joinedPosToJoinedType.get(joinedPosition))
					);
					itemInfoCache.put(joinedPosition, itemInfo);
				} catch (IndexOutOfBoundsException ex) {
					Log.e(TAG, "getItemInfoObject: position doesn't exist: " + joinedPosition, ex);
				}
			}
			return itemInfo;
		}

	}

	/**
	 * Class to wrap together extra item info. You can access info using public final fields.
	 * <pre>
	 *     {@link RvJoiner.ItemInfo#joinedPosition} - total position in joiner
	 *     {@link RvJoiner.ItemInfo#realPosition} - position in real adapter which handles item
	 *     {@link RvJoiner.ItemInfo#joinable} - joinable which handles item
	 *     {@link RvJoiner.ItemInfo#joinedType} - total type in joiner
	 *     {@link RvJoiner.ItemInfo#realType} - type in real adapter which handles item
	 * </pre>
	 */
	public static class ItemInfo {

		public final int joinedPosition;
		public final int realPosition;
		public final Joinable joinable;
		public final int joinedType;
		public final int realType;

		private ItemInfo(int joinedPosition, int realPosition, Joinable joinable, int joinedType,
						 int realType) {
			this.joinedPosition = joinedPosition;
			this.realPosition = realPosition;
			this.joinable = joinable;
			this.joinedType = joinedType;
			this.realType = realType;
		}

	}

}
