package su.j2e.rvjoiner.demo.model;

import su.j2e.rvjoiner.demo.Util;

public class Note extends DomainObject {

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
