package enumerations;

public enum Display {
	
	//Values
	Name,
	Path,
	Size,
	NamePath,
	SizePath,
	NameSize;
	
	//Methods
	@Override
	public String toString() {
		switch(this) {
		case Name:
			return "Name";
		case NamePath:
			return "Path & Name";
		case NameSize:
			return "Name & Size";
		case Path:
			return "Path";
		case Size:
			return "Size";
		case SizePath:
			return "Path & Size";
		default:
			return null;
		}
	}
}
