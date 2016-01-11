package su.j2e.rvjoinerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.java2e.android.rvadapterjoinerdemo.R;
import su.j2e.rvjoinerdemo.list.DataProvider;
import su.j2e.rvjoinerdemo.list.IssuesAdapter;
import su.j2e.rvjoinerdemo.list.NotesAdapter;
import su.j2e.rvjoiner.RvJoiner;
import su.j2e.rvjoiner.JoinableAdapter;
import su.j2e.rvjoiner.JoinableLayout;


public class AMain extends AppCompatActivity {

	private NotesAdapter notesAdapter;
	private IssuesAdapter issuesAdapter;

	private DataProvider dataProvider = new DataProvider();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.a_main);
		RecyclerView rv = (RecyclerView) findViewById(R.id.rv);
		notesAdapter = new NotesAdapter();
		issuesAdapter = new IssuesAdapter();
		RvJoiner rvJoiner = new RvJoiner();
		rvJoiner.add(new JoinableLayout(R.layout.notes_title, null));
		rvJoiner.add(new JoinableAdapter(notesAdapter, null));
		rvJoiner.add(new JoinableLayout(R.layout.issues_title, null));
		rvJoiner.add(new JoinableAdapter(issuesAdapter, new int[]{
				IssuesAdapter.VIEW_TYPE_TASK, IssuesAdapter.VIEW_TYPE_BUG
		}));
		rv.setAdapter(rvJoiner.getAdapter());
		rv.setLayoutManager(new LinearLayoutManager(this));
		updateData();
	}

	private void updateData() {
		notesAdapter.updateData(dataProvider.getAllNotes());
		issuesAdapter.updateData(dataProvider.getAllIssues());
	}
}
