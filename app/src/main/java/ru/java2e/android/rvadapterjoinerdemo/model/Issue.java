package ru.java2e.android.rvadapterjoinerdemo.model;

public interface Issue {

	int TYPE_TASK = 10;
	int TYPE_BUG = 11;

	String getDescription();

	void setDescription(String description);

	int getType();

}
