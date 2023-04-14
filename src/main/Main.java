package main;

import java.io.IOException;
import java.util.logging.*;

import GUIs.*;

public class Main {

	//Logger Variables
	private static final Logger LOGGER = Logger.getLogger("Main Logger");
	
	private static FileHandler LOGFILE;
	private static SimpleFormatter format;

	
	//Logger Initialization
	static {
		try {
			HandlerTest();
			if(LOGFILE == null) {
				LOGFILE = new FileHandler("FileSearcher.log");
				format = new SimpleFormatter();
				
				LOGFILE.setFormatter(format);
				LOGGER.addHandler(LOGFILE);
			}
		}catch (SecurityException | IOException e) {
			// TODO Auto-generated catch block
			LOGGER.log(Level.SEVERE, "File not created",e);
		}
	}
		
	//Driver Method
	public static void main(String[] args) {
		new MainGUI();
		LOGGER.info("Application Laucnhed\n");
		
	}
	
	private static final void HandlerTest() {
		Handler[] handlers = LOGGER.getHandlers();
		for(Handler h : handlers) {
			if(h instanceof FileHandler) {
				LOGFILE = (FileHandler) h;
				break;
			}
		}	
	}
}
