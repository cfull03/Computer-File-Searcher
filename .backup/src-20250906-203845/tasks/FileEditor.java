package tasks;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import enumerations.OSPath;
import interfaces.Clean;

/**
 * The {@code FileEditor} class is responsible for locating files within a specified
 * directory (and all its subdirectories) that match a given keyword pattern.
 * 
 * It implements the {@link Clean} interface for output display functionality,
 * and {@link Callable} to allow concurrent execution and return the count of matched files.
 * 
 * This class is optimized with Java NIO {@code Files.walk()} for efficient traversal.
 */
public class FileEditor implements Clean, Callable<Integer> {

	/** The keyword or pattern to match against file names. */
	private final String pattern;

	/** String representation of the base directory path. */
	private final String pathname;

	/** The base directory path as a {@link Path} object. */
	private final Path basePath;

	/** Final collection of matched files. */
	private Set<File> finalFiles;

	/**
	 * Constructs a {@code FileEditor} using an existing set of files.
	 *
	 * @param fileArray Initial file collection to populate matched files.
	 * @param pattern   Keyword used to filter files by name.
	 * @param path      Enumeration defining the root directory.
	 */
	public FileEditor(HashSet<File> fileArray, String pattern, OSPath path) {
		this.pathname = path.toPath();
		this.pattern = pattern;
		this.basePath = Paths.get(this.pathname);
		this.finalFiles = new HashSet<>(fileArray);
	}

	/**
	 * Constructs a {@code FileEditor} and performs pattern matching from scratch
	 * using directory traversal.
	 *
	 * @param pattern Keyword used to filter files.
	 * @param path    Enumeration for the root search path.
	 */
	public FileEditor(String pattern, OSPath path) {
		this.pathname = path.toPath();
		this.pattern = pattern;
		this.basePath = Paths.get(this.pathname);
		this.finalFiles = new HashSet<>();
	}

	/**
	 * Executes the filtering operation by calling {@link #Edit()} and returns the
	 * number of matched files.
	 *
	 * @return the number of files that matched the search criteria.
	 */
	@Override
	public Integer call() {
		Edit();
		return finalFiles.size();
	}

	/**
	 * Performs the core logic: walks through the directory structure and
	 * filters files whose names match the specified pattern.
	 */
	@Override
	public void Edit() {
		try {
			Predicate<Path> matcher = p -> {
				String name = p.getFileName().toString().toLowerCase();
				String lowerPattern = pattern.toLowerCase();
				return name.contains(lowerPattern) ||
						name.startsWith(lowerPattern) ||
						name.endsWith(lowerPattern);
			};

			finalFiles = Files.walk(basePath)
				.filter(Files::isRegularFile)
				.filter(p -> matcher.test(p))
				.map(Path::toFile)
				.collect(Collectors.toSet());

		} catch (IOException e) {
			e.printStackTrace();
			finalFiles = Collections.emptySet();
		}
	}

	/**
	 * Displays file names of the filtered result set.
	 */
	@Override
	public void PrintType() {
		if (finalFiles.isEmpty()) {
			System.out.println("No files found.");
			return;
		}
		finalFiles.forEach(file -> System.out.println(file.getName()));
	}

	/**
	 * Displays the names of all matched files.
	 */
	@Override
	public void DisplayName() {
		displayFiles("NAME", File::getName);
	}

	/**
	 * Displays file paths alongside file names.
	 */
	@Override
	public void DisplayPath() {
		displayFiles("PATH", file -> String.format("%s\n[*]Path: %s", file.getName(), file.getPath()));
	}

	/**
	 * Displays file sizes alongside file names.
	 */
	@Override
	public void DisplayFileSize() {
		displayFiles("FILESIZE", file -> String.format("%s --- Size: %d Bytes", file.getName(), file.length()));
	}

	/**
	 * Returns the final collection of matched files.
	 *
	 * @return a set of files matching the search criteria.
	 */
	public Set<File> getFinalFiles() {
		return finalFiles;
	}

	/**
	 * Returns the path where the file search was initiated.
	 *
	 * @return the string path of the base directory.
	 */
	public String getPathname() {
		return pathname;
	}

	/**
	 * Displays formatted output of matched files based on a given formatter.
	 *
	 * @param displayType Label for the output section.
	 * @param formatter   Lambda expression to define file display formatting.
	 */
	private void displayFiles(String displayType, DisplayFormatter formatter) {
		System.out.printf("\n====================================================================\n");
		System.out.printf("Files in the directory (%s): %s\n\n", displayType, pathname);
		if (finalFiles == null || finalFiles.isEmpty()) {
			System.out.println("NULL\n");
			return;
		}
		int count = 0;
		for (var file : finalFiles) {
			System.out.printf("[*] File: %d --- %s [*]\n", ++count, formatter.format(file));
		}
		System.out.println("====================================================================\n");
	}

	/**
	 * Functional interface to format a {@link File} object as a string.
	 */
	@FunctionalInterface
	private interface DisplayFormatter {
		String format(File file);
	}
}
