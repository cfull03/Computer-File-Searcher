package tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.*;
import java.util.stream.Collectors;
import interfaces.Search;

/**
 * The {@code Finder} class provides functionality for searching files and directories
 * within a specified path or a collection of files. Implements the {@link Search} interface.
 */
public class Finder implements Search {

	private static final Logger LOGGER = Logger.getLogger(Finder.class.getName());

	private final File path;
	private final List<File> foundFilesList;

	/**
	 * Constructs a {@code Finder} instance using a collection of files.
	 *
	 * @param files A collection of {@link File} objects to initialize the search space.
	 */
	public Finder(Collection<File> files) {
		this.foundFilesList = new ArrayList<>(files);
		this.path = null;
	}

	/**
	 * Constructs a {@code Finder} instance using a directory path.
	 *
	 * @param finderPath The directory path to initialize the search space.
	 */
	public Finder(String finderPath) {
		this.path = new File(finderPath);
		List<File> result;
		try {
			result = Files.walk(Paths.get(finderPath))
				.filter(Files::isRegularFile)
				.map(Path::toFile)
				.collect(Collectors.toList());
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to read files from path: " + finderPath, e);
			result = Collections.emptyList();
		}
		this.foundFilesList = result;
	}

	/**
	 * Retrieves the array of files found in the search space.
	 *
	 * @return An array of {@link File} objects.
	 */
	public File[] getFoundFilesArray() {
		return foundFilesList.toArray(new File[0]);
	}

	/**
	 * Retrieves the list of files found in the search space.
	 *
	 * @return A {@link List} of {@link File} objects.
	 */
	public List<File> getFoundFilesList() {
		return foundFilesList;
	}

	/**
	 * Searches for a single file by name in the provided list of files.
	 * The search is case-insensitive and uses regular expressions.
	 *
	 * @param files      A list of {@link File} objects to search in.
	 * @param searchName The name or pattern of the file to search for.
	 * @return A {@link List} containing the matching file(s) or an empty list if none found.
	 */
	public List<File> getSingleFile(List<File> files, String searchName) {
		Pattern pattern = Pattern.compile(searchName, Pattern.CASE_INSENSITIVE);
		return files.stream()
			.filter(file -> pattern.matcher(file.toString()).find())
			.collect(Collectors.toList());
	}

	/**
	 * Filters a list of files to include only directories.
	 *
	 * @param files A {@link List} of {@link File} objects.
	 * @return A {@link List} containing only directories from the input list.
	 */
	public List<File> filterDirectories(List<File> files) {
		return files.stream()
			.filter(File::isDirectory)
			.collect(Collectors.toList());
	}

	/**
	 * Counts the number of files in the provided array.
	 *
	 * @param array An array of {@link File} objects.
	 * @return The count of files in the array.
	 */
	@Override
	public int count(File[] array) {
		return array.length;
	}

	/**
	 * Counts the number of files in the provided list.
	 *
	 * @param list A {@link List} of {@link File} objects.
	 * @return The count of files in the list.
	 */
	@Override
	public int count(List<File> list) {
		return list.size();
	}
}
