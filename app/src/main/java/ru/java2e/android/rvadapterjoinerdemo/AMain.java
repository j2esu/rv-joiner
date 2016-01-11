package ru.java2e.android.rvadapterjoinerdemo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import ru.java2e.android.rvadapterjoinerdemo.list.DataProvider;
import ru.java2e.android.rvadapterjoinerdemo.list.IssuesAdapter;
import ru.java2e.android.rvadapterjoinerdemo.list.NotesAdapter;
import ru.java2e.android.rvadapterjoiner.RvJoiner;
import ru.java2e.android.rvadapterjoiner.AdapterWrapper;
import ru.java2e.android.rvadapterjoiner.LayoutWrapper;


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
		rvJoiner.add(new LayoutWrapper(R.layout.notes_title, null));
		rvJoiner.add(new AdapterWrapper(notesAdapter, null));
		rvJoiner.add(new LayoutWrapper(R.layout.issues_title, null));
		rvJoiner.add(new AdapterWrapper(issuesAdapter, new int[]{
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
