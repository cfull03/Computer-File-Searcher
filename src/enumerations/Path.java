package enumerations;

public enum Path {
	
	//Values
	USERS,
	CF,
	DESKTOP;
		
	//Methods
	public String toPath() {
		switch(this) {
		case CF:
			return "/Users/christianfullerton";
		case DESKTOP:
			return "/Users/christianfullerton/Desktop";
		case USERS:
			return "/Users";
		default:
			return null;
		}
	}
	
	@Override
	public String toString() {
		switch(this) {
		case CF:
			return "CF";
		case DESKTOP:
			return "Desktop";
		case USERS:
			return "Users";
		default:
			return null;
		
		}
	}
}
