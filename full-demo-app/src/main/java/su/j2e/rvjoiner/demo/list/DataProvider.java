package su.j2e.rvjoiner.demo.list;

import java.util.LinkedList;
import java.util.List;

import su.j2e.rvjoiner.demo.model.Bug;
import su.j2e.rvjoiner.demo.model.Issue;
import su.j2e.rvjoiner.demo.model.Note;
import su.j2e.rvjoiner.demo.model.Task;


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
