package su.j2e.rvjoinerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import ru.java2e.android.rvadapterjoinerdemo.R;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;
import su.j2e.rvjoiner.RvJoiner;
import su.j2e.rvjoinerdemo.list.DataProvider;
import su.j2e.rvjoinerdemo.list.IssuesAdapter;
import su.j2e.rvjoinerdemo.list.NotesAdapter;

/**
 * Main activity for demo app.
 * Use menu to switch between linear and grid layout manager.
 * Button at bottom of an app tests addition and deleting of Joinables. <b>NOTE:</b> this is not a
 * way of adding data to the list, update data in adapter to do this! I hope you understand :)
 */
public class AMain extends AppCompatActivity implements View.OnClickListener, JoinableLayout.Callback {

	//we need unique (for all joiner) type constants only for extra grid layout manager customization
	private static final int NOTES_TITLE_TYPE = 21;
	private static final int ISSUES_TITLE_TYPE = 22;

	private RecyclerView recyclerView;
	private RvJoiner rvJoiner = new RvJoiner();//auto update ON, stable ids ON
	private NotesAdapter notesAdapter = new NotesAdapter();
	private IssuesAdapter issuesAdapter = new IssuesAdapter(new RvJoiner.RealPositionProvider(rvJoiner));
	private DataProvider dataProvider = new DataProvider();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);
		//basic recycler view initialization
		recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
		setLinearLayoutManager(recyclerView);
		//construct joiner
		rvJoiner.add(new JoinableLayout(R.layout.notes_title, NOTES_TITLE_TYPE));
		rvJoiner.add(new JoinableAdapter(notesAdapter));
		rvJoiner.add(new JoinableLayout(R.layout.issues_title, ISSUES_TITLE_TYPE));
		rvJoiner.add(new JoinableAdapter(issuesAdapter, IssuesAdapter.VIEW_TYPE_TASK,
				IssuesAdapter.VIEW_TYPE_BUG));
		//example of extra view initialization
		rvJoiner.add(new JoinableLayout(R.layout.clickable, new JoinableLayout.Callback() {
			@Override
			public void onInflateComplete(View view, ViewGroup parent) {
				view.findViewById(R.id.clickable_btn).setOnClickListener(AMain.this);
			}
		}));
		//set join adapter to recycler view
		recyclerView.setAdapter(rvJoiner.getAdapter());
		//update data in adapters at any time (right now in this example)
		updateData();
	}

	private void updateData() {
		notesAdapter.updateData(dataProvider.getAllNotes());
		issuesAdapter.updateData(dataProvider.getAllIssues());
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
			case R.id.action_linear:
				setLinearLayoutManager(recyclerView);
				return true;
			case R.id.action_grid:
				setGridLayoutManager(recyclerView, rvJoiner);
				return true;
			default:
				return super.onOptionsItemSelected(item);
		}
	}

	private void setLinearLayoutManager(RecyclerView recyclerView) {
		recyclerView.setLayoutManager(new LinearLayoutManager(this));
	}

	private void setGridLayoutManager(RecyclerView recyclerView, final RvJoiner rvJoiner) {
		GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
		gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
			@Override
			public int getSpanSize(int position) {
				int realType = rvJoiner.getPositionInfo(position).realType;
				return realType == NOTES_TITLE_TYPE || realType == ISSUES_TITLE_TYPE ? 2 : 1;
			}
		});
		recyclerView.setLayoutManager(gridLayoutManager);
	}

	@Override
	public void onClick(View v) {
		//add new removable joinable to structure (generate random id)
		rvJoiner.add(new JoinableLayout(R.layout.removable_item, 0, this, -System.currentTimeMillis()),
				rvJoiner.getJoinableCount() - 1);
	}

	@Override
	public void onInflateComplete(View view, ViewGroup parent) {
		view.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int viewPosition = recyclerView.getChildAdapterPosition(v);
				RvJoiner.PositionInfo positionInfo = rvJoiner.getPositionInfo(viewPosition);
				//remove joinable
				rvJoiner.remove(positionInfo.joinable);
			}
		});
	}

}
