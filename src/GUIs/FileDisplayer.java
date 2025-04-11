package GUIs;

import java.awt.*;
import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import javax.swing.*;

import enumerations.OSPath;
import factory.*;
import interfaces.*;
import tasks.*;
import finder.Finder;
import editor.FileEditor;

/**
 * A Swing-based GUI that displays files matching a given pattern in a specific OS path.
 * Users can interactively delete files or initiate a batch deletion using a pattern.
 */
public class FileDisplayer extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final Logger LOGGER = Logger.getLogger(FileDisplayer.class.getName());

	private final LinkedList<Object[]> fileTable = new LinkedList<>();
	private JTable table;
	private final Future<Integer> assignment;
	private final JCheckBox useBatchDeleter = new JCheckBox("Use Batch Pattern Deleter");

	private static final WorkingThreadFactory NFactory = new NormalFactory("Search Factory");
	private static final ExecutorService processor = Executors.newCachedThreadPool(NFactory);
	private static final String[] TITLES = { "Name", "Size", "Path" };

	/**
	 * Constructs the FileDisplayer GUI and begins file scanning using FileEditor.
	 *
	 * @param selection The OSPath enum indicating which base directory to search.
	 * @param pattern   The file name pattern to search for.
	 */
	public FileDisplayer(OSPath selection, String pattern) {
		long startTime = System.currentTimeMillis();
		LOGGER.info(() -> String.format("Displaying files in [%s] containing the keyword [%s]", selection, pattern));

		HashSet<File> initialFiles = initializeFiles(selection.toPath());
		FileEditor editor = new FileEditor(initialFiles, pattern, selection);

		assignment = processSelection(editor);
		shutdownAndAwait(processor);

		LOGGER.info(() -> processor.isShutdown() && processor.isTerminated()
				? "Thread pool successfully shut down."
				: "Thread pool failed to shut down properly.");

		initializeGUI(selection, pattern, startTime);
	}

	/**
	 * Submits a file scanning task and collects results into the fileTable.
	 *
	 * @param editor A FileEditor instance that performs the file scan.
	 * @return Future representing the task.
	 */
	private Future<Integer> processSelection(FileEditor editor) {
		try {
			var start = System.currentTimeMillis();
			var future = processor.submit(editor);
			var result = future.get(3500, TimeUnit.MILLISECONDS);
			LOGGER.info(() -> String.format("Processed %s in %d ms, found %d files.", editor.getPathname(),
					System.currentTimeMillis() - start, result));
			editor.getFinalFiles().forEach(this::addData);
			return future;
		} catch (TimeoutException | InterruptedException | ExecutionException e) {
			LOGGER.log(Level.SEVERE, "Error processing files.", e);
			throw new RuntimeException(e);
		}
	}

	/**
	 * Builds and shows the GUI window, including the file table and delete controls.
	 *
	 * @param selection The OSPath enum used to show which directory was scanned.
	 * @param pattern   The pattern used for scanning.
	 * @param startTime The system timestamp when scanning started.
	 */
	private void initializeGUI(OSPath selection, String pattern, long startTime) {
		setTitle(String.format("Found Files in %s", selection));
		setSize(500, 500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());

		var rows = to2DArray(fileTable);
		table = new JTable(rows, TITLES);
		add(new JScrollPane(table), BorderLayout.CENTER);

		JPanel bottomPanel = new JPanel(new BorderLayout());
		bottomPanel.add(useBatchDeleter, BorderLayout.NORTH);

		JButton deleteButton = new JButton("Delete Selected");
		deleteButton.addActionListener(e -> deleteSelectedFiles());
		bottomPanel.add(deleteButton, BorderLayout.SOUTH);
		add(bottomPanel, BorderLayout.SOUTH);

		setVisible(true);
		LOGGER.info(() -> String.format("Total time to find files: %d milliseconds.",
				System.currentTimeMillis() - startTime));
	}

	/**
	 * Converts a list of file metadata arrays into a 2D array suitable for JTable.
	 *
	 * @param data The file metadata list.
	 * @return A 2D Object array representing table rows.
	 */
	private Object[][] to2DArray(LinkedList<Object[]> data) {
		return data.toArray(new Object[0][0]);
	}

	/**
	 * Adds a file's metadata to the table.
	 *
	 * @param file The file to include.
	 */
	private void addData(File file) {
		var size = String.format("%d BYTES", file.length());
		var data = new Object[] { file.getName(), size, file.getAbsolutePath() };
		fileTable.add(data);
	}

	/**
	 * Deletes files selected in the table. Can optionally trigger batch deletion with Deleter.
	 */
	private void deleteSelectedFiles() {
		int[] selectedRows = table.getSelectedRows();
		if (selectedRows.length == 0) {
			JOptionPane.showMessageDialog(this, "No files selected for deletion.");
			return;
		}

		if (useBatchDeleter.isSelected()) {
			String pattern = JOptionPane.showInputDialog(this, "Enter pattern to match for batch deletion:");
			if (pattern != null && !pattern.trim().isEmpty()) {
				ExecutorService exec = Executors.newSingleThreadExecutor();
				exec.submit(new Deleter(pattern, OSPath.USERS, exec));
				JOptionPane.showMessageDialog(this, "Batch deletion initiated using pattern: " + pattern);
			} else {
				JOptionPane.showMessageDialog(this, "No valid pattern provided.");
			}
			return;
		}

		List<Integer> rowsToRemove = new ArrayList<>();
		for (int row : selectedRows) {
			String path = (String) table.getValueAt(row, 2);
			try {
				Files.deleteIfExists(Path.of(path));
				LOGGER.info("Deleted file: " + path);
				rowsToRemove.add(row);
			} catch (IOException ex) {
				LOGGER.log(Level.WARNING, "Failed to delete: " + path, ex);
			}
		}

		rowsToRemove.stream().sorted(Comparator.reverseOrder()).forEach(index -> {
			fileTable.remove(index);
		});
		table.setModel(new javax.swing.table.DefaultTableModel(to2DArray(fileTable), TITLES));
		JOptionPane.showMessageDialog(this, "Selected files deleted.");
	}

	/**
	 * Scans a directory recursively to get all regular files.
	 *
	 * @param path A string path to the root directory.
	 * @return A set of discovered files.
	 */
	private static HashSet<File> initializeFiles(String path) {
		try {
			Path basePath = Paths.get(path);
			try (Stream<Path> stream = Files.walk(basePath)) {
				return stream.filter(Files::isRegularFile).map(Path::toFile)
						.collect(Collectors.toCollection(HashSet::new));
			}
		} catch (IOException e) {
			LOGGER.log(Level.WARNING, "Failed to load initial files from: " + path, e);
			return new HashSet<>();
		}
	}

	/**
	 * Gracefully shuts down the given ExecutorService.
	 *
	 * @param pool The executor to shutdown.
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
