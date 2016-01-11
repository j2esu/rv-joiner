package su.j2e.rvjoinerdemo.model;

public class Bug extends AbsIssue {

	@Override
	public int getType() {
		return Issue.TYPE_BUG;
	}

}
