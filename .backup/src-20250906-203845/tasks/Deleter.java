package tasks;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;
import java.util.stream.Stream;

import enumerations.OSPath;

/**
 * The {@code Deleter} class is responsible for deleting files from the user's
 * computer that match a specified pattern within a given path.
 * It implements the {@link Runnable} interface to allow execution in a thread.
 */
public class Deleter implements Runnable {

	private static final Logger LOGGER = Logger.getLogger(Deleter.class.getName());

	private final String pattern;
	private final String pathname;
	private final ExecutorService executorService;

	/**
	 * Constructs a {@code Deleter} instance.
	 *
	 * @param pattern          The pattern used to match file names.
	 * @param path             The base path where the search will be performed.
	 * @param executorService  The {@link ExecutorService} for managing thread execution.
	 */
	public Deleter(String pattern, OSPath path, ExecutorService executorService) {
		this.pattern = pattern;
		this.pathname = path.toPath();
		this.executorService = executorService;
	}

	/**
	 * Traverses the file system and deletes files matching the pattern using {@link Files#walk}.
	 * Logs each file deletion attempt and gracefully shuts down the executor service.
	 */
	@Override
	public void run() {
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Path startPath = Paths.get(pathname);

		try (Stream<Path> paths = Files.walk(startPath)) {
			paths
				.filter(Files::isRegularFile)
				.filter(path -> compiledPattern.matcher(path.getFileName().toString()).find())
				.forEach(path -> {
					try {
						Files.delete(path);
						LOGGER.info("Deleted file: " + path);
					} catch (IOException e) {
						LOGGER.log(Level.WARNING, "Failed to delete file: " + path, e);
					}
				});
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "Failed to traverse directory: " + pathname, e);
		} finally {
			executorService.shutdown();
		}
	}
}
