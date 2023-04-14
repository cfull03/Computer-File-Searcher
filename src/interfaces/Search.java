package interfaces;

import java.io.File;
import java.util.*;

public interface Search {
	
	//Enumeration
	public enum ListType{
		LinkedList,
		ArrayList,
		Vector,
		Stack,
	}
	
	//Default Methods
	default void printFoundFiles(List<File> list) {
		for(File i : list) {
			if(i.isFile()) {
				System.out.println(i.getName());
			}
		}
	}
	
	default void printFoundDirectories(List<File> list) {
		for(File i : list) {
			if(i.isDirectory()) {
				System.out.println(i.getName());
			}
		}
	}
	
	default void Print(List<File> array) {
		for(File i : array) {
			System.out.println(i.getName());
		}
	}
	
	default void Print(File[] array) {
		for(File i : array) {
			System.out.println(i.getName());
		}
	}
	
	default void printFoundFiles(File[] array) {
		for(File i : array) {
			if(i.isFile()) {
				System.out.println(i.getName());
			}
		}
	}
	default void printDirectories(List<File> array) {
		for(File i : array) {
			if(i.isDirectory()) {
				System.out.println(i.getName());
			}
		}
	}
	default void printDirectories(File[] array) {
		for(File i : array) {
			if(i.isDirectory()) {
				System.out.println(i.getName());
			}
		}
	}
	
	default List<File> toList(List<File> list, ListType typeList){
		List<File> newList = null;
		switch(typeList) {
		case ArrayList:
			newList = new ArrayList<>();
			newList.addAll(list);
			return newList;
		case LinkedList:
			newList = new LinkedList<>();
			newList.addAll(list);
			return newList;
		case Stack:
			newList = new Stack<>();
			newList.addAll(list);
			return newList;
		case Vector:
			newList = new Vector<>();
			newList.addAll(list);
			return newList;
		default:
			return newList;
		
		}
	}
	
	default List<File> toList(Collection<? extends File> array, ListType typeList){
		List<File> newList = null;
		switch(typeList) {
		case ArrayList:
			newList = new ArrayList<>();
			newList.addAll(array);
			return newList;
		case LinkedList:
			newList = new LinkedList<>();
			newList.addAll(array);
			return newList;
		case Stack:
			newList = new Stack<>();
			newList.addAll(array);
			return newList;
		case Vector:
			newList = new Vector<>();
			newList.addAll(array);
			return newList;
		default:
			return newList;
		
		}
	}
	
	//Abstract Methods
	public int count(File[] array);
	public int count(List<File> array);
}
