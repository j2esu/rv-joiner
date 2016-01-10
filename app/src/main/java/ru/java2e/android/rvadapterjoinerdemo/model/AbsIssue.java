package ru.java2e.android.rvadapterjoinerdemo.model;

import ru.java2e.android.rvadapterjoinerdemo.Util;

public abstract class AbsIssue implements Issue {

	protected String description = Util.getRandomSentence(40);

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

}
