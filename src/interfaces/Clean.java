package interfaces;

import java.io.File;
import java.util.*;

public interface Clean {
	
	//Default Methods
	default void PrintPath(File selectedFile) {
		System.out.println(selectedFile.getPath());
	}
	
	default String GetPath(File selectedFile) {
		return selectedFile.getPath();
	}
	
	//Static Methods
	static void Print(Collection<File> array) {
		if(array.size() != 0) {
			for(File i : array) {
				System.out.println(i.getName());
			}
		}else {
			System.err.println("Array is null");
		}
	}
	
	static void Print(File[] array) {
		if(array.length != 0) {
			for(File i : array) {
				System.out.println(i.getName());
			}
		}else {
			System.err.println("Array is null");
		}
	}
	
	
	//Abstract Methods
	public Boolean Edit();
	public void DisplayName();
	public void DisplayPath();
	public void DisplayFileSize();
	public void PrintType();

}
