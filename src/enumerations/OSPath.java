package enumerations;

public enum OSPath {

	// Enum values with associated paths
	USERS("/Users", "Users"),
	CF("/Users/christianfullerton", "CF"),
	DESKTOP("/Users/christianfullerton/Desktop", "Desktop");

	// Fields
	private final String path;
	private final String displayName;

	// Constructor
	OSPath(String path, String displayName) {
		this.path = path;
		this.displayName = displayName;
	}

	// Methods
	public String toPath() {
		return path;
	}

	@Override
	public String toString() {
		return displayName;
	}
}
