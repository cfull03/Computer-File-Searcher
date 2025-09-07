package main;

import java.io.IOException;
import java.util.logging.*;

import GUIs.*;

/**
 * The entry point for the File Searcher application.
 * Initializes logging and launches the main GUI.
 */
public class Main {

	// Logger Variables
	private static final Logger LOGGER = Logger.getLogger(Main.class.getName());
	private static FileHandler logFileHandler;
	private static final SimpleFormatter FORMATTER = new SimpleFormatter();

	// Logger Initialization
	static {
		try {
			initializeLogger();
		} catch (SecurityException | IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to initialize logger", e);
		}
	}

	/**
	 * The main driver method to launch the application.
	 *
	 * @param args Command-line arguments (not used).
	 */
	public static void main(String[] args) {
		new MainGUI();
		LOGGER.info("Application launched successfully.");
	}

	/**
	 * Initializes the logger by setting up a {@link FileHandler}.
	 *
	 * @throws SecurityException If a security manager denies access to logging.
	 * @throws IOException        If there is an error creating the log file.
	 */
	private static void initializeLogger() throws SecurityException, IOException {
		if (!isFileHandlerAttached()) {
			logFileHandler = new FileHandler("FileSearcher.log", true); // Append to existing log file
			logFileHandler.setFormatter(FORMATTER);
			LOGGER.addHandler(logFileHandler);
			LOGGER.setLevel(Level.INFO); // Set the desired logging level
		}
	}

	/**
	 * Checks if a {@link FileHandler} is already attached to the logger.
	 *
	 * @return {@code true} if a {@link FileHandler} is attached, {@code false} otherwise.
	 */
	private static boolean isFileHandlerAttached() {
		for (var handler : LOGGER.getHandlers()) {
			if (handler instanceof FileHandler) {
				logFileHandler = (FileHandler) handler;
				return true;
			}
		}
		return false;
	}
}
