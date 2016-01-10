package ru.java2e.android.rvadapterjoinerdemo.list;

import java.util.LinkedList;
import java.util.List;

import ru.java2e.android.rvadapterjoinerdemo.model.Bug;
import ru.java2e.android.rvadapterjoinerdemo.model.Issue;
import ru.java2e.android.rvadapterjoinerdemo.model.Note;
import ru.java2e.android.rvadapterjoinerdemo.model.Task;


public class DataProvider {

	private List<Note> notes = new LinkedList<>();
	{
		for (int i = 0; i < 10; i++) {
			notes.add(new Note());
		}
	}

	private List<Issue> issues = new LinkedList<>();
	{
		for (int i = 0; i < 10; i++) {
			issues.add(i % 2 == 0 ? new Task() : new Bug());
		}
	}

	public List<Note> getAllNotes() {
		return notes;
	}

	public List<Issue> getAllIssues() {
		return issues;
	}

}
