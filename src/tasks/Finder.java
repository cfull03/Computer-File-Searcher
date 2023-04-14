package tasks;

import java.io.File;
import java.util.*;
import java.util.regex.*;
import interfaces.Search;

public class Finder implements Search {
	
	//Variables
	private File path;
	private File[] foundfilesARRAY;
	
	//Constructors
	public Finder(Collection<File> files) {
		foundfilesARRAY = new File[files.size()];
		foundfilesARRAY = files.toArray(foundfilesARRAY);
	}
	
	public Finder(String finderpath) {
		this.path = new File(finderpath);
		foundfilesARRAY = this.path.listFiles();
	}
	
	//Methods
	public File[] getFoundFilesARRAY() {
		return foundfilesARRAY;
	}
	
	
	public List<File> getFoundFilesLIST() {
		List<File> foundfiles = new ArrayList<File>(Arrays.asList(foundfilesARRAY));
		return foundfiles;
	}
	
	public List<File> getSingleFile(File[] array, String searchName) {
		List<File> files = null;
		Pattern pattern = Pattern.compile(searchName, Pattern.CASE_INSENSITIVE);
		for(File i : array) {
			Matcher matcher = pattern.matcher((CharSequence) i.toString());
			Boolean match = matcher.find();
			if(match) {
				files = new ArrayList<File>();
				files.add((File) i);
			}else {
				continue;
			}
		}
		return files;
	}
	
	public List<File> newDirectories(List<File> array){
		List<File> directory = new ArrayList<File>();
		for(File i : array) {
			if(i.isDirectory()) {
				directory.add(i);
			}else{
				continue;
			}
		}
		return directory;
	}
	

	@Override
	public int count(File[] array) {
		// TODO Auto-generated method stub
		return array.length;
	}

	@Override
	public int count(List<File> array) {
		// TODO Auto-generated method stub
		return array.size();
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

}
