package anticheat.detections;

/**
 * Created by XtasyCode on 11/08/2017.
 */

public enum ChecksType {

	COMBAT("COMBAT"), MOVEMENT("MOVING"), OTHER("OTHER");

	private String name;

	ChecksType(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}
}
