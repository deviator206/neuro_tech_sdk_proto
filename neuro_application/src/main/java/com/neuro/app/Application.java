package com.neuro.app;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import com.neuro.app.security.LoginFrame;
import com.neuro.app.utility.NeuroApplication;

public class Application {

	// ===========================================================
	// Static constructor
	// ===========================================================

	static {
		try {
			javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			Logger.getLogger(Logger.getLogger("global").getName()).log(Level.FINE, e.getMessage(), e);
		}
	}

	public static void main(String[] a) throws Exception {

		SpalshScreen screen = new SpalshScreen();

		final LoginFrame frame = new LoginFrame();
		frame.setTitle("Inside Out Login Console!!!");
		frame.setBounds(10, 10, 370, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		screen.setVisible(true);

		try {
			for (int row = 0; row <= 100; row++) {
				Thread.sleep(100);
				screen.loadingnumber.setText(Integer.toString(row) + "%");
				screen.loadingprogress.setValue(row);
				if (row == 100) {
					screen.setVisible(false);
					frame.setVisible(true);
					frame.setFocusable(true);
					try {
						frame.loadNeuroApplication();
					} catch (Exception e) {
						e.printStackTrace();
					}

				}
			}
		} catch (Exception e) {
		}

	}
}
