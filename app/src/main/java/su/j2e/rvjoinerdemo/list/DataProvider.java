package su.j2e.rvjoinerdemo.list;

import java.util.LinkedList;
import java.util.List;

import su.j2e.rvjoinerdemo.model.Bug;
import su.j2e.rvjoinerdemo.model.Issue;
import su.j2e.rvjoinerdemo.model.Note;
import su.j2e.rvjoinerdemo.model.Task;


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
