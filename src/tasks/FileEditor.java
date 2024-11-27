package tasks;

import java.io.*;
import java.util.*;
import java.util.concurrent.Callable;

import enumerations.OSPath;
import interfaces.Clean;

public class FileEditor implements Clean, Callable<Integer> {

	// Variables
	private final File[] files;
	private final File filepath;
	private final String pattern;
	private final String pathname;
	private final FindernameFilter stringpattern;
	private HashSet<File> finalfiles;

	// Constructors
	/**
	 * Creates a FileEditor with a set of initial files and a pattern to filter.
	 * @param fileArray The initial set of files.
	 * @param pattern The pattern to search for.
	 * @param path The OSPath enum representing the root directory.
	 */
	public FileEditor(HashSet<File> fileArray, String pattern, OSPath path) {
		this.pathname = path.toPath();
		this.pattern = pattern;
		this.files = fileArray.toArray(new File[0]);
		this.stringpattern = new FindernameFilter(pattern);
		this.filepath = new File(path.toPath());
	}

	/**
	 * Creates a FileEditor with no initial files and a pattern to filter.
	 * @param pattern The pattern to search for.
	 * @param path The OSPath enum representing the root directory.
	 */
	public FileEditor(String pattern, OSPath path) {
		this.pathname = path.toPath();
		this.pattern = pattern;
		this.files = null;
		this.stringpattern = new FindernameFilter(pattern);
		this.filepath = new File(path.toPath());
	}

	// Methods
	/**
	 * Gets the files in the specified directory that match the pattern.
	 * @return An array of matching files, or an empty array if none found.
	 */
	public File[] getFoundFiles() {
		return Optional.ofNullable(filepath.listFiles(stringpattern))
				.orElse(new File[0]);
	}

	/**
	 * Retrieves the final set of files after filtering and searching.
	 * @return A HashSet of matching files.
	 */
	public HashSet<File> getFinalFiles() {
		return finalfiles;
	}

	/**
	 * Retrieves the pathname of the current directory.
	 * @return The pathname as a String.
	 */
	public String getPathname() {
		return pathname;
	}

	@Override
	public Integer call() throws Exception {
		Edit();
		FolderSearch(filepath, pattern);
		return finalfiles.size();
	}

	@Override
	public void Edit() {
		this.finalfiles = new HashSet<>(Arrays.asList(getFoundFiles()));
	}

	@Override
	public void PrintType() {
		if (finalfiles == null || finalfiles.isEmpty()) {
			System.out.println("No files found.");
			return;
		}
		finalfiles.forEach(file -> System.out.println(file.getName()));
	}

	@Override
	public void DisplayName() {
		displayFiles("NAME", File::getName);
	}

	@Override
	public void DisplayPath() {
		displayFiles("PATH", file -> String.format("%s\n[*]Path: %s", file.getName(), file.getPath()));
	}

	@Override
	public void DisplayFileSize() {
		displayFiles("FILESIZE", file -> String.format("%s --- Size: %d Bytes", file.getName(), file.length()));
	}

	public void Print(Collection<File> files) {
		Clean.Print(files);
	}

	public void Print(File[] files) {
		Clean.Print(files);
	}

	private void FolderSearch(File path, String pattern) {
		var foundFiles = Optional.ofNullable(path.listFiles()).orElse(new File[0]);
		Arrays.stream(foundFiles).forEach(file -> {
			if (file.isDirectory()) {
				FolderSearch(file, pattern);
			} else if (file.getName().contains(pattern)) {
				finalfiles.add(file);
			}
		});
	}

	@SuppressWarnings("unused")
	private File[] removeElement(File[] array, int index) {
		if (array == null || index < 0 || index >= array.length) {
			return array;
		}
		return Arrays.stream(array)
				.filter(file -> !Objects.equals(file, array[index]))
				.toArray(File[]::new);
	}

	@SuppressWarnings("unused")
	private void delay() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void displayFiles(String displayType, DisplayFormatter formatter) {
		System.out.printf("\n====================================================================\n");
		System.out.printf("Files in the directory (%s): %s\n\n", displayType, pathname);
		if (finalfiles == null || finalfiles.isEmpty()) {
			System.out.println("NULL\n");
			return;
		}
		int count = 0;
		for (var file : finalfiles) {
			System.out.printf("[*] File: %d --- %s [*]\n", ++count, formatter.format(file));
		}
		System.out.println("====================================================================\n");
	}

	@FunctionalInterface
	private interface DisplayFormatter {
		String format(File file);
	}

	/**
	 * FindernameFilter record to filter files by pattern.
	 */
	record FindernameFilter(String finderPattern) implements FilenameFilter {

		@Override
		public boolean accept(File dir, String name) {
			var lowerName = name.toLowerCase();
			var upperName = name.toUpperCase();
			return lowerName.contains(finderPattern) ||
					lowerName.endsWith(finderPattern) ||
					lowerName.startsWith(finderPattern) ||
					upperName.contains(finderPattern) ||
					upperName.endsWith(finderPattern) ||
					upperName.startsWith(finderPattern);
		}

		@Override
		public String toString() {
			return finderPattern;
		}
	}
}
