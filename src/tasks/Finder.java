package tasks;

import java.io.File;
import java.util.*;
import java.util.regex.*;
import interfaces.Search;

/**
 * The {@code Finder} class provides functionality for searching files and directories
 * within a specified path or a collection of files. Implements the {@link Search} interface.
 */
public class Finder implements Search {

	// Variables
	private final File path;
	private final File[] foundFilesArray;

	/**
	 * Constructs a {@code Finder} instance using a collection of files.
	 *
	 * @param files A collection of {@link File} objects to initialize the search space.
	 */
	public Finder(Collection<File> files) {
		this.foundFilesArray = files.toArray(new File[0]);
		this.path = null;
	}

	/**
	 * Constructs a {@code Finder} instance using a directory path.
	 *
	 * @param finderPath The directory path to initialize the search space.
	 */
	public Finder(String finderPath) {
		this.path = new File(finderPath);
		this.foundFilesArray = Optional.ofNullable(this.path.listFiles())
				.orElse(new File[0]);
	}

	/**
	 * Retrieves the array of files found in the search space.
	 *
	 * @return An array of {@link File} objects.
	 */
	public File[] getFoundFilesARRAY() {
		return foundFilesArray;
	}

	/**
	 * Retrieves the list of files found in the search space.
	 *
	 * @return A {@link List} of {@link File} objects.
	 */
	public List<File> getFoundFilesLIST() {
		return new ArrayList<>(Arrays.asList(foundFilesArray));
	}

	/**
	 * Searches for a single file by name in the provided array of files.
	 * The search is case-insensitive and uses regular expressions.
	 *
	 * @param array      An array of {@link File} objects to search in.
	 * @param searchName The name or pattern of the file to search for.
	 * @return A {@link List} containing the matching file(s) or an empty list if none found.
	 */
	public List<File> getSingleFile(File[] array, String searchName) {
		List<File> files = new ArrayList<>();
		Pattern pattern = Pattern.compile(searchName, Pattern.CASE_INSENSITIVE);

		for (var file : array) {
			Matcher matcher = pattern.matcher(file.toString());
			if (matcher.find()) {
				files.add(file);
			}
		}
		return files;
	}

	/**
	 * Filters a list of files to include only directories.
	 *
	 * @param array A {@link List} of {@link File} objects.
	 * @return A {@link List} containing only directories from the input list.
	 */
	public List<File> newDirectories(List<File> array) {
		var directories = new ArrayList<File>();
		for (var file : array) {
			if (file.isDirectory()) {
				directories.add(file);
			}
		}
		return directories;
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
	 * @param array A {@link List} of {@link File} objects.
	 * @return The count of files in the list.
	 */
	@Override
	public int count(List<File> array) {
		return array.size();
	}

	/**
	 * Removes a specific element from a file array by index.
	 *
	 * @param array The original array of {@link File} objects.
	 * @param index The index of the element to remove.
	 * @return A new array with the specified element removed.
	 */
	@SuppressWarnings("unused")
	private File[] removeElement(File[] array, int index) {
		if (array == null || index < 0 || index >= array.length) {
			return array;
		}
		return Arrays.stream(array)
				.filter(file -> !Objects.equals(file, array[index]))
				.toArray(File[]::new);
	}
}
