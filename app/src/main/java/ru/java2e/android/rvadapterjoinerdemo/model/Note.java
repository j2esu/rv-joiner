package ru.java2e.android.rvadapterjoinerdemo.model;

import ru.java2e.android.rvadapterjoinerdemo.Util;

public class Note {

	private String text = Util.getRandomSentence(40);
	private int color = Util.getRandomColor();

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

}
