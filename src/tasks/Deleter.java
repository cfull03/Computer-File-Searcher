package tasks;

import java.io.*;
import java.util.*;
import interfaces.*;

/**
 * The {@code Deleter} class provides functionality to delete files in a specified directory
 * based on a filename pattern. Implements the {@link Clean} and {@link Runnable} interfaces.
 */
public class Deleter implements Clean, Runnable {

	// Variables
	private final DeleternameFilter deleterPattern;
	private final File path;
	private final String pathname;
	private HashSet<File> finalFiles;
	private final List<File> deletedFiles;

	/**
	 * Constructs a {@code Deleter} object with the specified directory and filename pattern.
	 *
	 * @param pathname The path to the directory to search for files.
	 * @param pattern  The filename pattern to match files for deletion.
	 */
	public Deleter(String pathname, String pattern) {
		this.pathname = pathname;
		this.path = new File(pathname);
		this.deleterPattern = new DeleternameFilter(pattern);
		this.deletedFiles = new ArrayList<>();
	}

	/**
	 * Returns the path of the directory being processed.
	 *
	 * @return The directory path as a {@link String}.
	 */
	public String getPath() {
		return this.pathname;
	}

	/**
	 * Returns the deletion pattern used to match filenames.
	 *
	 * @return The filename pattern as a {@link String}.
	 */
	public String getPattern() {
		return this.deleterPattern.toString();
	}

	/**
	 * Executes the deletion process by invoking the {@code Edit} method.
	 */
	@Override
	public void run() {
		Edit();
	}

	/**
	 * Deletes files in the specified directory that match the filename pattern.
	 * The deleted files are stored in a list for further inspection or reporting.
	 */
	@Override
	public void Edit() {
		var files = Optional.ofNullable(path.listFiles(this.deleterPattern))
				.orElse(new File[0]);
		finalFiles = new HashSet<>(Arrays.asList(files));

		for (var file : finalFiles) {
			if (file.delete()) {
				deletedFiles.add(file);
			}
		}
	}

	/**
	 * Displays the names of the deleted files in the console.
	 */
	@Override
	public void DisplayName() {
		int count = 0;
		for (var file : deletedFiles) {
			System.out.printf("[*] File: %d --- Name: %s [*]%n", ++count, file.getName());
		}
	}

	/**
	 * Displays the names and paths of the deleted files in the console.
	 */
	@Override
	public void DisplayPath() {
		int count = 0;
		for (var file : deletedFiles) {
			System.out.printf("[*] File: %d --- Name: %s [*]%n[*] Path: %s [*]%n",
					++count, file.getName(), file.getPath());
		}
	}

	/**
	 * Displays the names and sizes of the deleted files in the console.
	 */
	@Override
	public void DisplayFileSize() {
		int count = 0;
		for (var file : deletedFiles) {
			System.out.printf("[*] File: %d --- Name: %s --- Size: %d Bytes [*]%n",
					++count, file.getName(), file.length());
		}
	}

	/**
	 * Prints the names of deleted files in the console.
	 */
	@Override
	public void PrintType() {
		deletedFiles.forEach(file -> System.out.println(file.getName()));
	}

	/**
	 * Helper method to remove an element from an array.
	 *
	 * @param array The array from which to remove the element.
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

	/**
	 * A {@code FilenameFilter} implementation to match filenames based on a specified pattern.
	 */
	record DeleternameFilter(String deleterPattern) implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			String lowerName = name.toLowerCase();
			return lowerName.contains(deleterPattern) ||
					lowerName.startsWith(deleterPattern) ||
					lowerName.endsWith(deleterPattern);
		}

		@Override
		public String toString() {
			return deleterPattern;
		}
	}
}
