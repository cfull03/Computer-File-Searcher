package GUIs;

import java.awt.*;
import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.*;
import javax.swing.*;
import enumerations.*;
import factory.*;
import interfaces.*;
import tasks.*;

public class FileDisplayer extends JFrame {
	
	//Serialization ID
	private static final long serialVersionUID = 1L;
	
	//Logger Initialization
	private static final Logger LOGGER = Logger.getLogger("Main Logger");
	
	//Variables
	private FileEditor DesktopEdit;
	private FileEditor CFEdit;
	private FileEditor UEdit;
	private LinkedList<Object[]> fileTable;
	private JTable table;
	private Future<Integer> assignment;
	private Long time;

	//Final Variables
	private final WorkingThreadFactory NFactory = new NormalFactory("Search Factory");
	private final ExecutorService processor = Executors.newCachedThreadPool(NFactory);
	private final HashSet<File> DesktopFiles = Start(Path.DESKTOP.toPath());
	private final HashSet<File> CFFiles = Start(Path.CF.toPath());
	private final HashSet<File> USERSFiles = Start(Path.USERS.toPath());
	private final String[] Titles = {"Name","Size","Path"};

	public FileDisplayer(Path selection, String pattern) {
		
		long start = System.currentTimeMillis();
		LOGGER.info(String.format("%s Path File Window Displaying Files Containing the Keyword [%s]\n", 
				selection.toString(),pattern));
		
		UEdit = new FileEditor(USERSFiles,pattern ,Path.USERS);
		CFEdit = new FileEditor(CFFiles,pattern ,Path.CF);
		DesktopEdit = new FileEditor(DesktopFiles,pattern ,Path.DESKTOP);
		
		fileTable = new LinkedList<Object[]>();
		
		switch(selection) {
		case CF:
			assignment = processor.submit(CFEdit);
			while(!assignment.isDone()) {
				time = (Time() != 0) ? Time() : 0;
			}
			LOGGER.info(String.format("Time: %d Milliseconds to execute %s Task\n"
					, time, CFEdit.getPathname()));
			
			HashSet<File> CFFiles = CFEdit.getFinalFiles();
			for(File i : CFFiles) {
				addData(i);
			}
			break;
			
		case DESKTOP:
			assignment = processor.submit(DesktopEdit);
			while(!assignment.isDone()) {
				time = (Time() != 0) ? Time() : 0;
			}
			LOGGER.info(String.format("Time: %d Milliseconds to execute %s Task\n"
					, time, DesktopEdit.getPathname()));
			
			HashSet<File> DEFiles = DesktopEdit.getFinalFiles();
			for(File i : DEFiles) {
				addData(i);
			}
			break;
			
		case USERS:
			assignment = processor.submit(UEdit);
			while(!assignment.isDone()) {
				time = (Time() != 0) ? Time() : 0;
			}
			LOGGER.info(String.format("Time: %d Milliseconds to execute %s Task\n"
					, time, UEdit.getPathname()));
			
			HashSet<File> UFiles = UEdit.getFinalFiles();
			for(File i : UFiles) {
				addData(i);
			}
			break;
			
		default:
			break;
		}
		
		ShutdownAndAwait(processor);
		String statement = (processor.isShutdown() && processor.isTerminated()) ? 
				"Thread Pool has successfully shutdown\n" :
				"Thread Pool has not successfully shutdown\n";	
		
		LOGGER.info(statement);

		
		String title = String.format("Found Files in %s", selection.toString());
		setTitle(title);
		setSize(500,500);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLayout(new BorderLayout());
		
		
		Object[][] rows = toDoubleColumes(fileTable);
		table = new JTable(rows,Titles);
		add(new JScrollPane(table), BorderLayout.CENTER);
		
		setVisible(true);
		
		long end = System.currentTimeMillis() - start;
		try {
			LOGGER.info(String.format("Thread Pool found %d Files containing keyword [%s]\n"
					,assignment.get(),pattern));
		} catch (InterruptedException | ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		LOGGER.info(String.format("Time to find files: %d Milliseconds\n", end));
	}
	
	private Object[][] toDoubleColumes(LinkedList<Object[]> data) {
		Object[][] dataRows = new Object[data.size()][3];
		int i = 0;
		for(Object[] filedata : data) {
			dataRows[i++] = filedata;
		}
		return dataRows;
	}
	
	private void addData(File file) {
		String size = String.format("%d BYTES", file.length());
		Object[] data = {file.getName(),size,file.getAbsolutePath()};
		fileTable.add(data);
	}
	
	private HashSet<File> Start(String finderPath) {
		Finder path = new Finder(finderPath);
		File[] initalFiles = path.getFoundFilesARRAY();
		HashSet<File> allFILES = InitalSearch(initalFiles);
		
		return allFILES;
	}
	
	private HashSet<File> InitalSearch(File[] file) {
		HashSet<File> finalfiles = new HashSet<File>();
		for(File i : file) {
			if(i.isFile()) {
				finalfiles.add(i);
			}else if(i.isDirectory()) {
				FolderSearch(i,finalfiles);
			}
		}
		return finalfiles;
	}
	
	private void FolderSearch(File folder, Collection<File> list) {
		File[] files = folder.listFiles();
		if(files != null) {
			for(File i : files) {
				if(i.isFile()) {
					list.add(i);
				}else if(i.isDirectory()) {
					FolderSearch(i,list);
				}
			}
		}
	}
	
	private final long Time() {
		long start = System.currentTimeMillis();
		long end = System.currentTimeMillis() - start;
		return end;
	}
	
	private final void ShutdownAndAwait(ExecutorService TPool) {
		TPool.shutdown();
		try {
			if(!TPool.awaitTermination(3500, TimeUnit.MILLISECONDS)) {
				TPool.shutdownNow();
			}
		}catch(InterruptedException e) {
			TPool.shutdownNow();
		}
	}
}
