# java-thread-tester

## Interactive thread pool
Allows the users to specify a thread pool size and number of tasks to be performed. Each task lasts five seconds and is implemented via Thread.sleep(). The purpose of developing this program was to familiarize myself with coding multithreaded Swing applications.

The program takes two input values, `Pool Size` and `Task Count`. `Pool Size` can be no more than 25 and `Task Count` can be no more than 50. Both fields must be completed using positive integers.

The GUI is updated as each task completes. Once all tasks are completed, the total time required to process all the tasks is output. If the pool contains more than one thread, the percent improvement over processing the tasks with a single thread is also displayed.