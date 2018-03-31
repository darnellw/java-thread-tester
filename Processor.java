// Processor.java
// Coded by darnellw
//
// Processor creates a thread pool and captures the number of tasks to
// process. It also submits these tasks to the ExecutorService, capturing
// an ArrayList of Futures which it returns upon completion of begin().

package threads;

import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class Processor {
	// The number of tasks to process.
	private int taskCount;
	
	// An ArrayList of Future objects.
	// A task number is returned once a thread is finished with that task. 
	private ArrayList<Future<Integer>> taskNums;
	
	// An ExecutorService that will contain the thread pool. 
	private ExecutorService exe;
	
	public Processor(int poolSize, int taskCount) {
		exe = Executors.newFixedThreadPool(poolSize);	// Create the pool.
		this.taskCount = taskCount;					
		taskNums = new ArrayList<Future<Integer>>();
	}
	
	// Submit the tasks to the pool, shutdown the ExecutorService, and
	// return the list of task numbers.
	public ArrayList<Future<Integer>> begin() {
		for (int i = 0; i < taskCount; i++) {
			taskNums.add(exe.submit(new Task(i + 1)));
		}
		exe.shutdown();
		return taskNums;
	}
}
