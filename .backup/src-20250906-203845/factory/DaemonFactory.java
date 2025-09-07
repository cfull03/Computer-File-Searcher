package factory;

import java.util.*;
import interfaces.*;

public class DaemonFactory implements WorkingThreadFactory {

	//Variables
	private int count;
	private String FactoryName;
	private List<String> info;
	
	private final String TYPE = this.getClass().getSimpleName();
	
	//Constructors
	public DaemonFactory(String FactoryName) {
		count = 1;
		this.FactoryName = FactoryName;
		info = new ArrayList<String>();
	}

	//Methods
	@Override
	public Thread newThread(Runnable r) {
		String name = String.format("%s: Thread [%d]", FactoryName, count);
		count++;
		Thread t = new Thread(r, name);
		t.setDaemon(true);
		String HEXI = Integer.toHexString(t.hashCode());
		info.add(String.format("INFO: TYPE[%s], NAME[%s], ID [%d], HASHCODE [%s], "
				+ "PRIORITY [%d], TASK [%s]\n"
				,TYPE, t.getName(), t.getId(), HEXI, t.getPriority(), 
				r.getClass().getSimpleName().toString()));
		return t;
	}
	
	@Override
	public void getInfo() {
		System.out.printf("\n\t\t\t******** %s INFORMATION ********\n\n\n%s\n",
				FactoryName.toUpperCase(), Info());
	}
	
	public String getName() {
		return FactoryName;
	}
	
	@Override
	public String Info() {
		StringBuffer Sbuffer = new StringBuffer();
		Iterator<String> it = info.iterator();
		if(it.hasNext()) {
			while(it.hasNext()) {
				Sbuffer.append(it.next());
			}
		}else{
			Sbuffer.append("\t\t\t\t\tNo Threads Made");
		}
		
		return Sbuffer.toString();
	}

}
