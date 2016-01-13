package su.j2e.rvjoiner;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Joins several {@link RecyclerView.Adapter}, layouts and other {@link RvJoiner.Joinable} into
 * single hostAdapter. Basic usage:
 * <pre>
 * 1. Wrap your hostAdapter with {@link JoinableAdapter}, or wrap your layout with {@link JoinableLayout}.
 * 2. Use {@link #add(Joinable)} to add it to {@link RvJoiner}.
 * 3. When you finished addition, use {@link #getAdapter()} to get hostAdapter and set it to your
 * {@link RecyclerView}
 * 4. Update data in your adapters (and notify about this using data change notification methods)
 * 5. If auto update ON (default constructor) join hostAdapter reflects all your data updates (or you
 * can use notify methods on it to update data manually if using {@link RvJoiner#RvJoiner(boolean)})
 * </pre>
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

	private HostAdapter hostAdapter;
	private boolean autoUpdate = true;

	//to find correspond observer for unregister
	private Map<Joinable, DataObserver> joinableToObserver = new HashMap<>();

	/**
	 * @param autoUpdate if true, joiner will listen for data updates in joined adapters,
	 *                   otherwise you have to call notify methods manually
	 */
	public RvJoiner(boolean autoUpdate) {
		hostAdapter = new HostAdapter();
		this.autoUpdate = autoUpdate;
	}

	/**
	 * The same as {@link #RvJoiner(boolean)} with auto update ON.
	 */
	public RvJoiner() {
		this(true);
	}

	/**
	 * Adds joinable to the bottom of joiner.
	 * @param joinable joinable to add
	 * @return false if already added
	 * @throws IllegalArgumentException if joinable is null
	 */
	public boolean add(Joinable joinable) {
		if (joinable == null) throw new IllegalArgumentException("Joinable can't be null");
		boolean wasAdded = hostAdapter.addJoinableToStructure(joinable);
		if (wasAdded && autoUpdate) {
			try {//avoid "observer was already registered" exception
				if (joinableToObserver.get(joinable) == null) {//if no current observer
					joinableToObserver.put(joinable, new DataObserver(joinable, this));//put it
				}
				joinable.getAdapter().registerAdapterDataObserver(joinableToObserver.get(joinable));
			} catch (IllegalStateException ex) {
				Log.d(TAG, "add: observer was already registered");
			}
		}
		return wasAdded;
	}

	/**
	 * Removes joinable from joiner.
	 * @param joinable joinable to remove, not null
	 * @return false if joiner wasn't modified (joinable not exist or already removed)
	 */
	public boolean remove(Joinable joinable) {
		if (joinable == null) return false;
		if (autoUpdate) {
			try {//avoid "observer wasn't registered" exception
				joinable.getAdapter().unregisterAdapterDataObserver(
						joinableToObserver.get(joinable));
			} catch (IllegalStateException|IllegalArgumentException ex) {
				Log.d(TAG, "remove: observer not registered");
			}
		}
		return hostAdapter.removeJoinableFromStructure(joinable);
	}

	/**
	 * @return hostAdapter, which you can set to RecyclerView.
	 */
	public RecyclerView.Adapter getAdapter() {
		return hostAdapter;
	}

	/**
	 * @param joinedPosition total joined position (form 0 to item count  - 1)
	 * @return object which wraps info, or null if position doesn't exist
	 * @see su.j2e.rvjoiner.RvJoiner.ItemInfo
	 */
	public ItemInfo getItemInfo(int joinedPosition) {
		return hostAdapter.getItemInfoInternal(joinedPosition);
	}

	/**
	 * @see su.j2e.rvjoiner.RvJoiner.ItemInfo
	 * @return total joined position in joiner, or -1 if not exist
	 */
	public int getJoinedPosition(int realPosition, Joinable joinable) {
		return hostAdapter.getJoinedPositionInternal(realPosition, joinable);
	}

	/**
	 * Class to wrap together extra item info. You can access info using public final fields.
	 * <pre>
	 *     {@link RvJoiner.ItemInfo#joinedPosition} - total position in joiner
	 *     {@link RvJoiner.ItemInfo#realPosition} - position in real hostAdapter which handles item
	 *     {@link RvJoiner.ItemInfo#joinable} - joinable which handles item
	 *     {@link RvJoiner.ItemInfo#joinedType} - total type in joiner
	 *     {@link RvJoiner.ItemInfo#realType} - type in real hostAdapter which handles item
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

	/*
	Actual joiner hostAdapter implementation.
	Joined position and joined type - values in new composite hostAdapter.
	Real position and real type - values in real sub adapters.
	So, to integrate it we need some mapping, but we don't actually need a maps, because
	joined types is in [0 .. total_types_count) and joined positions in [0 .. new_adapter_size).
	So, we cat use list to accomplish this.
	 */
	private static class HostAdapter extends RecyclerView.Adapter {

		private static final String TAG = HostAdapter.class.getName();

		//todo should be unique and sorted (maybe sored set)
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

		private Map<Joinable, int[]> joinableToJoinedPosArray = new HashMap<>();

		private HostAdapter() {
			registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
				@Override
				public void onChanged() {
					postDataSetChanged();
				}

				@Override
				public void onItemRangeChanged(int positionStart, int itemCount) {
					onChanged();
				}

				@Override
				public void onItemRangeInserted(int positionStart, int itemCount) {
					onChanged();
				}

				@Override
				public void onItemRangeRemoved(int positionStart, int itemCount) {
					onChanged();
				}

				@Override
				public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
					onChanged();
				}
			});
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

		//todo check other hostAdapter methods

		private boolean addJoinableToStructure(@NonNull Joinable joinable) {
			boolean alreadyExist = joinables.contains(joinable);
			if (!alreadyExist) {
				joinables.add(joinable);
				postStructureChanged();
			}
			return !alreadyExist;
		}

		private boolean removeJoinableFromStructure(@NonNull Joinable joinable) {
			boolean wasRemoved = joinables.remove(joinable);
			if (wasRemoved) {
				postStructureChanged();
			}
			return wasRemoved;
		}

		/**
		 * Should be called after any structure changing (changes in {@link #joinables}).
		 */
		private void postStructureChanged() {
			joinedTypeToJoinable.clear();
			joinedTypeToRealType.clear();
			for (Joinable joinable : joinables) {
				for (int i = 0; i < joinable.getTypeCount(); i++) {
					joinedTypeToJoinable.add(joinable);
					joinedTypeToRealType.add(joinable.getType(i));
				}
			}
			//new joinable adds new data, so we need to notify
			notifyDataSetChanged();
		}

		/**
		 * Should be called after any {@link #hostAdapter} data set changing.
		 */
		private void postDataSetChanged() {
			itemCount = 0;
			joinedPosToJoinedType.clear();
			joinedPosToRealPos.clear();
			joinedPosToJoinable.clear();
			int prevTypeCount = 0;
			for (Joinable joinable : joinables) {
				int[] joinedPosArray = new int[joinable.getAdapter().getItemCount()];
				for (int i = 0; i < joinable.getAdapter().getItemCount(); i++) {
					joinedPosArray[i] = itemCount;
					itemCount++;
					joinedPosToJoinedType.add(prevTypeCount +
							joinable.getTypeIndex(joinable.getAdapter().getItemViewType(i)));
					joinedPosToRealPos.add(i);
					joinedPosToJoinable.add(joinable);
				}
				prevTypeCount += joinable.getTypeCount();
				joinableToJoinedPosArray.put(joinable, joinedPosArray);
			}
			itemInfoCache.clear();
		}

		//return null, if position doesn't exist.
		private ItemInfo getItemInfoInternal(int joinedPosition) {
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
					Log.e(TAG, "getItemInfoInternal: position doesn't exist: " + joinedPosition, ex);
				}
			}
			return itemInfo;
		}

		//return -1 if position doesn't exist
		private int getJoinedPositionInternal(int realPosition, Joinable joinable) {
			int[] joinedPosArray = joinableToJoinedPosArray.get(joinable);
			if (joinedPosArray != null && realPosition >= 0 && realPosition < joinedPosArray.length) {
				return joinableToJoinedPosArray.get(joinable)[realPosition];
			} else {
				return -1;
			}
		}

	}

	/**
	 * An observer to set in joinable adapters (needed for auto update implementation)
	 */
	private static class DataObserver extends RecyclerView.AdapterDataObserver {

		private Joinable joinable;
		private RvJoiner rvJoiner;
		private RecyclerView.Adapter adapter;

		private DataObserver(Joinable joinable, RvJoiner rvJoiner) {
			this.joinable = joinable;
			this.rvJoiner = rvJoiner;
			this.adapter = rvJoiner.getAdapter();
		}

		@Override
		public void onChanged() {
			adapter.notifyDataSetChanged();
		}

		@Override
		public void onItemRangeChanged(int positionStart, int itemCount) {
			adapter.notifyItemRangeChanged(getJoinedPosition(positionStart), itemCount);
		}

		@Override
		public void onItemRangeInserted(int positionStart, int itemCount) {
			adapter.notifyItemRangeInserted(getJoinedPosition(positionStart), itemCount);
		}

		@Override
		public void onItemRangeRemoved(int positionStart, int itemCount) {
			adapter.notifyItemRangeRemoved(getJoinedPosition(positionStart), itemCount);
		}

		@Override
		public void onItemRangeMoved(int fromPosition, int toPosition, int itemCount) {
			if (itemCount == 1) {
				adapter.notifyItemMoved(getJoinedPosition(fromPosition), getJoinedPosition(toPosition));
			} else if (itemCount > 1) {
				onChanged();//no notifyItemRangeMoved method by now
			}
		}

		//just a wrapper to be short
		private int getJoinedPosition(int realPosition) {
			return rvJoiner.getJoinedPosition(realPosition, joinable);
		}

	}

}
