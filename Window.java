// Window.java
// Coded by darnellw
//
// Window defines the contents and layout of the GUI and specifies event
// handlers to capture user input and run the program.

package threads;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingWorker;

@SuppressWarnings("serial")
public class Window extends JFrame {
	private long prgmStartTime;						// The time the program begins.
	private double prgmTotalTime;					// Total time elapsed.
	private int poolSize, taskCount;				// Pool size and task count.
	private Processor p;							// Processor object.
	private ArrayList<Future<Integer>> taskNums;	// List of task numbers.
	
	// Swing components.
	private JLabel lblPoolSize, lblTaskCount;
	private JTextField txtPoolSize, txtTaskCount;
	private JTextArea txtOutput;
	private JScrollPane scroller;
	private JButton btnRun, btnClear, btnHelp, btnClose;
	private JPanel panel;
	
	public Window() {
		setTitle("Thread Tester");
		setSize(380, 400);
		setResizable(false);
		setLocationRelativeTo(null);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		// Assign components.
		panel = new JPanel();
		lblPoolSize = new JLabel("Pool Size");
		lblTaskCount = new JLabel("Task Count");
		txtPoolSize = new JTextField("", 5);
		txtTaskCount = new JTextField("", 5);
		txtOutput = new JTextArea(18, 28);
		scroller = new JScrollPane(txtOutput);
		btnRun = new JButton("Run");
		btnClear = new JButton("Clear");
		btnHelp = new JButton("Help");
		btnClose = new JButton("Close");
		
		// Add components to the panel.
		panel.add(lblPoolSize);
		panel.add(txtPoolSize);
		panel.add(lblTaskCount);
		panel.add(txtTaskCount);
		panel.add(btnRun);
		panel.add(scroller);
		panel.add(btnClear);
		panel.add(btnHelp);
		panel.add(btnClose);
		
		txtOutput.setEditable(false);
		txtOutput.setWrapStyleWord(true);
		
		// Action listener for the "Run" button.
		btnRun.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				// If the fields are valid, capture input and begin processing.
				if (validateFields()) {
					poolSize = Integer.parseInt(txtPoolSize.getText());
					taskCount = Integer.parseInt(txtTaskCount.getText());
					String output;
					
					output = "Thread pool size: " + poolSize + "\n";
					output += "Task count: " + taskCount + "\n";
					output += "Processing...\n";
	
					disableButtons();
					
					txtPoolSize.setText("");
					txtTaskCount.setText("");
					txtOutput.setText(output);
					txtPoolSize.requestFocus();
					
					run(poolSize, taskCount);
				} else {
					// Error message.
					String error = "Pool Size and Task Count fields must contain\n";
					error += "positive whole numbers.\n\n";
					error += "Pool Size must be no larger than 25.\n";
					error += "Task Count must be no larger than 50.";
					
					// Clear fields.
					txtPoolSize.setText("");
					txtTaskCount.setText("");
					txtOutput.setText("");
					txtPoolSize.requestFocus();
					
					// Display error message.
					JOptionPane.showMessageDialog(Window.this, error, "Invalid Fields", JOptionPane.ERROR_MESSAGE);
				}
			}
		});
		
		// Action listener for the "Clear" button.
		btnClear.addActionListener(new ActionListener() {
			// Clear the text fields and give txtPoolSize the focus.
			public void actionPerformed(ActionEvent e) {
				txtPoolSize.setText("");
				txtTaskCount.setText("");
				txtOutput.setText("");
				txtPoolSize.requestFocus();
			}
		});
		
		// Action listener for the "Help" button.
		btnHelp.addActionListener(new ActionListener() {
			// Display help dialog.
			public void actionPerformed(ActionEvent e) {
				String about = "";
				
				about += "Thread Tester\n";
				about += "Coded by wdarnell\n\n";
				about += "This Java program showcases the benefits and behavior of\n";
				about += "multithreading by allowing the user to specify the quantity\n";
				about += "of threads in a thread pool along with the number of tasks to\n";
				about += "process. The interface is updated as individual threads complete\n";
				about += "their respective tasks. Once all tasks are complete, data about\n";
				about += "the processing is displayed.";
				
				JOptionPane.showMessageDialog(Window.this, about, "Help", JOptionPane.INFORMATION_MESSAGE);
			}
		});
		
		// Action listener for the "Close" button.
		btnClose.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.exit(0);
			}
		});
		
		add(panel);					// Add the panel to the window.
		setVisible(true);			// Set visible to true.
		txtPoolSize.requestFocus();	// Give txtPoolSize the focus.
	}
	
	// Use the specified values for pool size and task count and begin processing.
	private void run(int poolSize, int taskCount) {
		// SwingWorker will work in a thread separate from the GUI so the GUI won't hang.
		SwingWorker<Void, Integer> worker = new SwingWorker<Void, Integer>() {
			protected Void doInBackground() throws Exception {
				// Create a new processor with specified pool size and task count.
				p = new Processor(poolSize, taskCount);
				
				// Assign taskNums to a new ArrayList that holds Future objects.
				taskNums = new ArrayList<Future<Integer>>();
				
				// Capture the start time.
				prgmStartTime = System.nanoTime();
				
				// Begin processing and capture the list of task numbers.
				taskNums = p.begin();
				
				// Publish the list of task numbers.
				// The task numbers a collection of Future objects that each return once
				// the thread processing them is finished.
				for (int i = 0; i < taskNums.size(); i++) {
					publish(taskNums.get(i).get());
				}
				return null;
			}
			
			// The process() method works with the list of Future objects published in
			// the doInBackground() method.
			// After each task is processed by a thread, it returns its task number to
			// be displayed in the GUI.
			protected void process(List<Integer> taskNums) {
				String output = "";
				for (int i = 0; i < taskNums.size(); i++) {
					output += "Task " + taskNums.get(i) + " complete.\n";
				}
				txtOutput.append(output);
			}

			// The done() method runs once the doInBackground() method finishes.
			// In this case, done() mainly deals with displaying results.
			protected void done() {
				// DecimalFormat object to deal with rounding.
				DecimalFormat format = new DecimalFormat("#.##");
				format.setRoundingMode(RoundingMode.HALF_UP);
				
				// Calculate total program running time.
				prgmTotalTime = (System.nanoTime() - prgmStartTime) / 1000000D;
				
				String results = "";
				
				enableButtons();
				
				results += "All tasks complete.\n";
				results += "Total time elapsed: " + format.format(prgmTotalTime) + " ms (" +
						format.format(prgmTotalTime / 1000) + " seconds).\n";
				
				// Display percent improvement via multithreading, if using more than one thread.
				if (poolSize > 1) {
					double dblPoolSize = poolSize;
					double dblTaskCount = taskCount;
					double groups = Math.ceil(dblTaskCount / dblPoolSize);
					double improvement = ((dblTaskCount - groups) / groups) * 100;
					results += "Improvement over one thread: around " + format.format(improvement) + "%.\n";
				}
				
				txtOutput.append(results);
			}
		};
		// Execute the SwingWorker.
		worker.execute();
	}
	
	// Validate the input fields.
	private boolean validateFields() {
		int poolSize, taskCount;
		
		// Attempt to store the text box values.
		// If an exception occurs, return false to signify the fields are not valid.
		try {
			poolSize = Integer.parseInt(txtPoolSize.getText());
			taskCount = Integer.parseInt(txtTaskCount.getText());
		} catch (Exception e) {
			return false;
		}
		
		// Ensure the fields are greater than 0.
		if (poolSize <= 0 || taskCount <= 0) {
			return false;
		}
		
		// Ensure thread pool size is no larger than 25.
		if (poolSize > 25) {
			return false;
		}
		
		// Ensure number of tasks is no larger than 50.
		if (taskCount > 50) {
			return false;
		}
		
		// Return true if all checks pass.
		return true;
	}
	
	// Disable the "Run" and "Clear" buttons, if enabled.
	private void disableButtons() {
		if (btnRun.isEnabled()) {
			btnRun.setEnabled(false);
		}
		
		if (btnClear.isEnabled()) {
			btnClear.setEnabled(false);
		}
	}
	
	// Enable the "Run" and "Clear" buttons, if disabled.
	private void enableButtons() {
		if (!btnRun.isEnabled()) {
			btnRun.setEnabled(true);
		}
		
		if (!btnClear.isEnabled()) {
			btnClear.setEnabled(true);
		}
	}
}
