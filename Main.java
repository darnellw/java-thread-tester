// Main.java
// Coded by darnellw
//
// Main contains a main() method that instantiates the GUI.

package threads;

import javax.swing.SwingUtilities;

public class Main {
	public static void main(String[] args) {
		SwingUtilities.invokeLater(new Runnable() {
			public void run() {
				new Window();
			}
		});
	}
}
