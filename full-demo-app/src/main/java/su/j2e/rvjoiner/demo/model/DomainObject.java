package su.j2e.rvjoiner.demo.model;

public class DomainObject {

	private static int currentId = 0;

	private long id = currentId++;

	public long getId() {
		return id;
	}

}
