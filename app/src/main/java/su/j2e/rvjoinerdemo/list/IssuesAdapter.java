package su.j2e.rvjoinerdemo.list;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.LinkedList;
import java.util.List;

import ru.java2e.android.rvadapterjoinerdemo.R;
import su.j2e.rvjoinerdemo.model.Bug;
import su.j2e.rvjoinerdemo.model.Issue;
import su.j2e.rvjoinerdemo.model.Task;

public class IssuesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

	public static final int VIEW_TYPE_TASK = 0;
	public static final int VIEW_TYPE_BUG = 1;

	private List<Issue> issues = new LinkedList<>();

	/**
	 * Used to update adapter data
	 */
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

	protected class TaskVh extends RecyclerView.ViewHolder {

		private final TextView descTv;

		private TaskVh(ViewGroup parent) {
			super(LayoutInflater.from(parent.getContext())
					.inflate(R.layout.task_item, parent, false));
			descTv = (TextView) itemView.findViewById(R.id.task_item_desc);
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
		}

		private void bind(Bug bug) {
			descTv.setText(bug.getDescription());
		}
	}

}
