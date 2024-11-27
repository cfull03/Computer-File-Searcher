package GUIs;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.swing.*;

import enumerations.OSPath;
import factory.*;
import interfaces.*;
import tasks.*;

/**
 * A GUI application for displaying files matching a specific pattern in a selected directory.
 * This class uses {@link FileEditor} for file filtering and {@link JTable} to display the results.
 */
public class FileDisplayer extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Logger instance for logging information and errors.
	 */
	private static final Logger LOGGER = Logger.getLogger(FileDisplayer.class.getName());

	// Variables
	private final FileEditor desktopEdit;
	private final FileEditor cfEdit;
	private final FileEditor uEdit;
	private final LinkedList<Object[]> fileTable = new LinkedList<>();
	private JTable table;
	private final Future<Integer> assignment;

	// Final Variables
	private static final WorkingThreadFactory NFactory = new NormalFactory("Search Factory");
	private static final ExecutorService processor = Executors.newCachedThreadPool(NFactory);
	private static final HashSet<File> DesktopFiles = initializeFiles(OSPath.DESKTOP.toPath());
	private static final HashSet<File> CFFiles = initializeFiles(OSPath.CF.toPath());
	private static final HashSet<File> USERSFiles = initializeFiles(OSPath.USERS.toPath());
	private static final String[] TITLES = {"Name", "Size", "Path"};

	/**
	 * Constructs a new {@code FileDisplayer} instance, initializing the GUI and filtering files
	 * based on the provided directory and search pattern.
	 *
	 * @param selection The {@link OSPath} representing the directory to search.
	 * @param pattern   The keyword pattern to filter files.
	 */
	public FileDisplayer(OSPath selection, String pattern) {
		long startTime = System.currentTimeMillis();
		LOGGER.info(() -> String.format("Displaying files in [%s] containing the keyword [%s]", selection, pattern));

		// Initialize FileEditors
		uEdit = new FileEditor(USERSFiles, pattern, OSPath.USERS);
		cfEdit = new FileEditor(CFFiles, pattern, OSPath.CF);
		desktopEdit = new FileEditor(DesktopFiles, pattern, OSPath.DESKTOP);

		// Process the selected path and filter files
		switch (selection) {
			case CF -> assignment = processSelection(cfEdit);
			case DESKTOP -> assignment = processSelection(desktopEdit);
			case USERS -> assignment = processSelection(uEdit);
			default -> throw new IllegalArgumentException("Invalid selection: " + selection);
		}

		// Ensure thread pool shutdown
		shutdownAndAwait(processor);

		// Log shutdown state
		LOGGER.info(() -> processor.isShutdown() && processor.isTerminated() ?
				"Thread pool successfully shut down." :
				"Thread pool failed to shut down properly.");

		// Initialize and display GUI
		initializeGUI(selection, pattern, startTime);
	}

	/**
	 * Processes the files in the selected directory using a {@link FileEditor}.
	 *
	 * @param editor The {@link FileEditor} to process the files.
	 * @return A {@link Future} representing the result of the file processing task.
	 */
	private Future<Integer> processSelection(FileEditor editor) {
		try {
			var start = System.currentTimeMillis();
			var future = processor.submit(editor);

			var result = future.get(3500, TimeUnit.MILLISECONDS);
			LOGGER.info(() -> String.format("Processed %s in %d milliseconds, found %d files.",
					editor.getPathname(), System.currentTimeMillis() - start, result));

			editor.getFinalFiles().forEach(this::addData);
			return future;

		} catch (TimeoutException | InterruptedException | ExecutionException e) {
			LOGGER.log(Level.SEVERE, "Error processing files.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Initializes the GUI components and displays the results.
	 *
	 * @param selection  The selected directory to display.
	 * @param pattern    The search pattern used to filter files.
	 * @param startTime  The start time of the GUI initialization for logging purposes.
	 */
	private void initializeGUI(OSPath selection, String pattern, long startTime) {
		setTitle(String.format("Found Files in %s", selection));
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		var rows = to2DArray(fileTable);
		table = new JTable(rows, TITLES);
		add(new JScrollPane(table), BorderLayout.CENTER);

		setVisible(true);

		LOGGER.info(() -> String.format("Total time to find files: %d milliseconds.",
				System.currentTimeMillis() - startTime));
	}

	/**
	 * Converts the file data from a list into a two-dimensional array for displaying in a {@link JTable}.
	 *
	 * @param data The linked list containing file data as object arrays.
	 * @return A 2D array representing the file data.
	 */
	private Object[][] to2DArray(LinkedList<Object[]> data) {
		return data.toArray(new Object[0][0]);
	}

	/**
	 * Adds file data to the table.
	 *
	 * @param file The file whose data should be added.
	 */
	private void addData(File file) {
		var size = String.format("%d BYTES", file.length());
		var data = new Object[]{file.getName(), size, file.getAbsolutePath()};
		fileTable.add(data);
	}

	/**
	 * Initializes the files in the given directory path.
	 *
	 * @param path The directory path as a string.
	 * @return A {@link HashSet} containing all files in the directory and subdirectories.
	 */
	private static HashSet<File> initializeFiles(String path) {
		var finder = new Finder(path);
		return initialSearch(finder.getFoundFilesARRAY());
	}

	/**
	 * Performs an initial search for files in the given file array, including subdirectories.
	 *
	 * @param files The array of files to search.
	 * @return A {@link HashSet} containing all valid files found.
	 */
	private static HashSet<File> initialSearch(File[] files) {
		var finalFiles = new HashSet<File>();
		for (var file : files) {
			if (file.isFile()) {
				finalFiles.add(file);
			} else if (file.isDirectory()) {
				folderSearch(file, finalFiles);
			}
		}
		return finalFiles;
	}

	/**
	 * Recursively searches a folder and adds files to the provided collection.
	 *
	 * @param folder The folder to search.
	 * @param list   The collection to store found files.
	 */
	private static void folderSearch(File folder, Collection<File> list) {
		var files = Optional.ofNullable(folder.listFiles()).orElse(new File[0]);
		for (var file : files) {
			if (file.isFile()) {
				list.add(file);
			} else if (file.isDirectory()) {
				folderSearch(file, list);
			}
		}
	}

	/**
	 * Shuts down the executor service gracefully, ensuring all tasks are completed or terminated.
	 *
	 * @param pool The {@link ExecutorService} to shut down.
	 */
	private void shutdownAndAwait(ExecutorService pool) {
		pool.shutdown();
		try {
			if (!pool.awaitTermination(3500, TimeUnit.MILLISECONDS)) {
				pool.shutdownNow();
			}
		} catch (InterruptedException e) {
			pool.shutdownNow();
			Thread.currentThread().interrupt();
		}
	}
}
