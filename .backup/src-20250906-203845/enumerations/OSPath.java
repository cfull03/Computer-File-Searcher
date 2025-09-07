package enumerations;

import java.nio.file.Paths;

public enum OSPath {

	USERS("", "Users"),
	CF("", "Home"),
	DESKTOP("Desktop", "Desktop");

	private final String relativePath;
	private final String displayName;

	OSPath(String relativePath, String displayName) {
		this.relativePath = relativePath;
		this.displayName = displayName;
	}

	public String toPath() {
		return Paths.get(System.getProperty("user.home"), relativePath).toString();
	}

	@Override
	public String toString() {
		return displayName;
	}
}
