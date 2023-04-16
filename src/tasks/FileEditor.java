package tasks;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import enumerations.*;
import interfaces.*;


public class FileEditor implements Clean, Callable<Integer> {
	
	//Variables
	private File[] files;
	private File[] filespath;
	private File filepath;
	private String pattern;
	private String path;
	private String pathname;
	private FindernameFilter stringpattern;
	private HashSet<File> finalfiles;
	
	//Constructors
	public FileEditor(HashSet<File> fileArray, String pattern, Path path) {
		this.path = path.toPath();
		this.pattern = pattern;
		this.pathname = path.toString();
		this.files = new File[fileArray.size()];
		this.files = fileArray.toArray(files);
		this.stringpattern = new FindernameFilter(pattern);
		this.filepath = new File(this.path);
		
	}
	
	public FileEditor(String pattern, Path path) {
		this.path = path.toPath();
		this.pathname = path.toString();
		this.stringpattern = new FindernameFilter(pattern);
		this.pattern = pattern;
		this.files = null;
		
	}
	
	//Methods
	public File[] getFoundFiles() {
		File filepath = new File(path);
		File[] pathfiles = filepath.listFiles(stringpattern);
		return pathfiles;
	}
	
	public HashSet<File> getFinalFiles(){
		return this.finalfiles;
	}
	
	public String getPathname() {
		return this.pathname;
	}
	
	@Override
	public Integer call() throws Exception {
		// TODO Auto-generated method stub
		Edit();
		FolderSearch(filepath,pattern);
		
		return finalfiles.size();
	}
	
	
	@Override
	public void Edit(){
		File filepath = new File(path);
		this.filespath = filepath.listFiles(stringpattern);
		this.finalfiles = new HashSet<File>(Arrays.asList(filespath));
	}

	@Override
	public void PrintType() {
		// TODO Auto-generated method stub
		Iterator<File> it = finalfiles.iterator();
		while(it.hasNext()) {
			it.next();
			System.out.println(it.next().getName());
		}
	}
	
	@Override
	public void DisplayName() {
		System.out.println("\n====================================================================\n");
		System.out.printf("Files in the diractory (NAME): %s\n\n",pathname);
		int count = 0;
		if(finalfiles.size() == 0) {
			System.out.println("NULL\n");
		}
		for(File i : finalfiles) {
			count++;
			System.out.printf("[*] File: %d --- Name: %s [*]\n",count,i.getName());
		}
		System.out.println("\n====================================================================\n");
	}
	
	@Override
	public void DisplayPath() {
		System.out.println("\n====================================================================\n");
		System.out.printf("Files in the diractory (PATH): %s\n\n",pathname);
		int count = 0;
		if(finalfiles.size() == 0) {
			System.out.println("NULL\n");
		}
		for(File i : finalfiles) {
			count++;
			System.out.printf("[*] File: %d --- Name: %s[*]\n[*]Path: %s [*]\n\n",count,i.getName(),i.getPath());
		}
		System.out.println("====================================================================\n");
	}
	
	@Override
	public void DisplayFileSize() {
		System.out.println("\n====================================================================\n");
		System.out.printf("Files in the diractory (FILESIZE): %s\n\n",pathname);
		int count = 0;
		if(finalfiles.size() == 0) {
			System.out.println("NULL\n");
		}
		for(File i : finalfiles) {
			count++;
			System.out.printf("[*] File: %d --- Name: %s --- Size: %d Bytes [*]\n", count, i.getName(), i.length());
		}
		System.out.println("\n====================================================================\n");
	}
	
	public void Print(Collection<File> files) {
		Clean.Print(files);
	}
	
	public void Print(File[] files) {
		Clean.Print(files);
	}
	

	private void FolderSearch(File path, String pattern) {
		File[] foundfiles = path.listFiles();
		
		if(foundfiles != null) {
			for(File i : foundfiles) {
				if(i.isDirectory()) {
					FolderSearch(i,pattern);
				}else if(i.getName().contains(pattern)) {
					finalfiles.add(i);
				}
			}
		}
	}

	@SuppressWarnings("unused")
	private File[] removeElement(File[] array, int index) {
		if (array == null || index < 0|| index >= array.length) {
			return array;
		}
		File[] anotherArray = new File[array.length - 1];
		for (int i = 0, k = 0; i < array.length; i++){
			if(i == index) {
				continue;
			}
			anotherArray[k++] = array[i];
		}
		return anotherArray;
	}
	
	@SuppressWarnings("unused")
	private void delay() {
		try {
			Thread.sleep(3000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			Thread.currentThread().interrupt();
		}
	}
	
	class FindernameFilter implements FilenameFilter{
		
		private String FinderPattern;
		
		public FindernameFilter(String FinderPattern) {
			this.FinderPattern = FinderPattern;
		}
		
		public String getFinderPattern() {
			return this.FinderPattern;
		}
		
		@Override
		public String toString() {
			return this.FinderPattern;
		}

		@Override
		public boolean accept(File dir, String name) {
			// TODO Auto-generated method stub
			return name.toLowerCase().contains(FinderPattern) || 
					name.toLowerCase().endsWith(FinderPattern)  || 
					name.toLowerCase().startsWith(FinderPattern) ||
					name.toUpperCase().contains(FinderPattern) ||
					name.toUpperCase().endsWith(FinderPattern) ||
					name.toUpperCase().startsWith(FinderPattern);
		}
	}
}
