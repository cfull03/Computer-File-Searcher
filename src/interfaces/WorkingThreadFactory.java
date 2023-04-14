package interfaces;

import java.util.concurrent.ThreadFactory;

public interface WorkingThreadFactory extends ThreadFactory {
	
	//Abstract Methods
	public void getInfo();
	public String Info();
	
}
