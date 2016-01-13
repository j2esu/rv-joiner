package su.j2e.rvjoinerdemo.model;

import su.j2e.rvjoinerdemo.Util;

public abstract class Issue extends DomainObject {

	public static final int TYPE_TASK = 10;
	public static final int TYPE_BUG = 11;

	private String description = Util.getRandomSentence(40);

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public abstract int getType();

}
