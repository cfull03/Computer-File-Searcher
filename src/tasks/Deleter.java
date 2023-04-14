package tasks;

import java.io.*;
import java.util.*;
import interfaces.*;

public class Deleter implements Clean, Runnable {

	//Variables
	private DeleternameFilter Deleterpattern;
	private File path;
	private String pathname;
	private HashSet<File> finalfiles;
	private ArrayList<File> deletedfiles;
	
	//Constructors
	public Deleter(String pathname, String pattern) {
		// TODO Auto-generated constructor stub
		this.pathname = pathname;
		this.path = new File(pathname);
		this.Deleterpattern = new DeleternameFilter(pattern);
		this.deletedfiles = new ArrayList<File>();
	}
	
	//Methods
	public String gePath() {
		return this.pathname;
	}
	
	public String getPattern(){
		return this.Deleterpattern.toString();
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		Edit();
	}
	
	@Override
	public Boolean Edit(){
		File[] files = path.listFiles(this.Deleterpattern);
		finalfiles = new HashSet<File>(Arrays.asList(files));
		Iterator<File> it = finalfiles.iterator();
		while(it.hasNext()) {
			deletedfiles.add(it.next());
			it.next().delete();
		}
		
		return null;
	}
	
	@Override
	public void DisplayName() {
		int count = 0;
		for(File i : deletedfiles) {
			count++;
			System.out.printf("[*]File: %d---Name: %s[*]\n",count,i.getName());
		}
	}
	
	@Override
	public void DisplayPath() {
		int count = 0;
		for(File i : deletedfiles) {
			count++;
			System.out.printf("[*]File: %d --- Name: %s[*]\n[*]Path: %s[*]\n",count,i.getName(),i.getPath());
		}
	}
	
	@Override
	public void DisplayFileSize() {
		int count = 0;
		for(File i : deletedfiles) {
			count++;
			System.out.printf("[*]File %d --- Name %s --- Size %d Bytes[*]\n", count, i.getName(), i.length());
		}
	}
	
	@Override
	public void PrintType() {
		// TODO Auto-generated method stub
		Iterator<File> it = deletedfiles.iterator();
		File selectedFile = it.next();
		while(it.hasNext()) {
			System.out.println(selectedFile.getName());
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
	
	class DeleternameFilter implements FilenameFilter{
		
		private String DeleterPattern;
		
		public DeleternameFilter(String DeleterPattern) {
			this.DeleterPattern = DeleterPattern;
		}

		@Override
		public boolean accept(File dir, String name) {
			// TODO Auto-generated method stub
			return name.toLowerCase().contains(DeleterPattern) || 
					name.toLowerCase().endsWith(DeleterPattern)  || 
					name.toLowerCase().startsWith(DeleterPattern) ||
					name.toUpperCase().contains(DeleterPattern) ||
					name.toUpperCase().endsWith(DeleterPattern) ||
					name.toUpperCase().startsWith(DeleterPattern);
		}
	}
}
