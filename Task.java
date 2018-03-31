// Task.java
// Coded by darnellw
//
// Task implements the Callable interface in order to return the task number
// upon completion of the call() method. call() imitates a 5-second task
// being performed (50 ms sleep * 100 iterations = 5000 ms) and is intended
// to be run inside a thread.

package threads;

import java.util.concurrent.Callable;

public class Task implements Callable<Integer> {
	private int number;		// The task number.
	
	public Task(int number) {
		this.number = number;
	}
	
	// Simulate a task being performed.
	public Integer call() {
		for (int j = 0; j < 100; j++) {
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		// Return the task number upon completion. 
		return number;
	}
}
