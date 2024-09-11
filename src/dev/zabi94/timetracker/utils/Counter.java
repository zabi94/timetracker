package dev.zabi94.timetracker.utils;

public class Counter {

	private int nextItem = 0;
	
	public int next() {
		return nextItem++;
	}
	
}
