package su.j2e.rvjoiner.demo.list;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import ru.java2e.android.rvadapterjoinerdemo.R;
import su.j2e.rvjoiner.RvJoiner;
import su.j2e.rvjoiner.demo.model.Bug;
import su.j2e.rvjoiner.demo.model.Issue;
import su.j2e.rvjoiner.demo.model.Task;

/**
 * This adapter uses {@link su.j2e.rvjoiner.RvJoiner.RealPositionProvider} in conjunction with
 * {@link RecyclerView.ViewHolder#getAdapterPosition()} to detect position of clicked view.
 * If this adapter will be used without {@link RvJoiner}, you need just pass null to constructor
 * {@link su.j2e.rvjoiner.RvJoiner.RealPositionProvider#RealPositionProvider(RvJoiner)} and
 * no code changes needed
 */
public class IssuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public static final int VIEW_TYPE_TASK = 0;
	public static final int VIEW_TYPE_BUG = 1;
	private static final String TAG = IssuesAdapter.class.getName();

	private List<Issue> issues = new LinkedList<>();
	private RvJoiner.RealPositionProvider realPositionProvider;

	public IssuesAdapter(RvJoiner.RealPositionProvider realPositionProvider) {
		this.realPositionProvider = realPositionProvider;
		setHasStableIds(true);
	}

	public void updateData(List<Issue> issues) {
		this.issues = issues;
		notifyDataSetChanged();
	}

	@Override
	public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		switch (viewType) {
			case VIEW_TYPE_TASK:
				return new TaskVh(parent);
			case VIEW_TYPE_BUG:
				return new BugVh(parent);
			default:
				throw new RuntimeException("Unknown view type");
		}
	}

	@Override
	public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
		switch (getItemViewType(position)) {
			case VIEW_TYPE_TASK:
				((TaskVh) holder).bind((Task) issues.get(position));
				break;
			case VIEW_TYPE_BUG:
				((BugVh) holder).bind((Bug) issues.get(position));
				break;
			default:
				throw new RuntimeException("Unknown view type");
		}
	}

	@Override
	public int getItemCount() {
		return issues.size();
	}

	@Override
	public int getItemViewType(int position) {
		switch (issues.get(position).getType()) {
			case Issue.TYPE_TASK:
				return VIEW_TYPE_TASK;
			case Issue.TYPE_BUG:
				return VIEW_TYPE_BUG;
			default:
				throw new RuntimeException("Unknown issue type");
		}
	}

	@Override
	public long getItemId(int position) {
		return issues.get(position).getId();
	}

	private void onBugItemClick(int position) {
		issues.remove(position);
		notifyItemRemoved(position);
	}

	private void onTaskItemClick(int position) {
		issues.add(position, new Bug());//ok, i shouldn't do this here, it should do data provider
		notifyItemInserted(position);
	}

	protected class TaskVh extends RecyclerView.ViewHolder {

		private final TextView descTv;

		private TaskVh(ViewGroup parent) {
			super(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.task_item, parent, false));
			descTv = (TextView) itemView.findViewById(R.id.task_item_desc);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "onClick: id " + getItemId());
					onTaskItemClick(realPositionProvider.getRealPosition(getAdapterPosition()));
				}
			});
		}

		private void bind(Task task) {
			descTv.setText(task.getDescription());
		}

	}

	protected class BugVh extends RecyclerView.ViewHolder {

		private final TextView descTv;

		private BugVh(ViewGroup parent) {
			super(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.bug_item, parent, false));
			descTv = (TextView) itemView.findViewById(R.id.bug_item_desc);
			itemView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Log.d(TAG, "onClick: id" + getItemId());
					onBugItemClick(realPositionProvider.getRealPosition(getAdapterPosition()));
				}
			});
		}

		private void bind(Bug bug) {
			descTv.setText(bug.getDescription());
		}
	}

}
