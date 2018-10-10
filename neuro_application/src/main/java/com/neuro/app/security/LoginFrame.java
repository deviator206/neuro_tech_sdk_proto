package com.neuro.app.security;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;

import com.neuro.app.service.DBService;
import com.neuro.app.util.Utils;
import com.neuro.app.utility.NeuroApplication;

public class LoginFrame extends JFrame implements ActionListener {

	Container container = getContentPane();
	JLabel userLabel = new JLabel("USERNAME");
	JLabel passwordLabel = new JLabel("PASSWORD");
	JTextField userTextField = new JTextField();
	JPasswordField passwordField = new JPasswordField();
	JButton loginButton = new JButton("LOGIN");
	JButton resetButton = new JButton("RESET");
	JCheckBox showPassword = new JCheckBox("Show Password");
	private DBService dbService;
	private NeuroApplication application;

	public LoginFrame() throws Exception {
		setLayoutManager();
		setLocationAndSize();
		addComponentsToContainer();
		addActionEvent();
		dbService = new DBService();
		
	}

	public void setLayoutManager() {
		container.setLayout(null);
		setIconImage(Utils.createIconImage("images/logo.png"));
	}

	public void setLocationAndSize() {
		userLabel.setBounds(50, 150, 100, 30);
		passwordLabel.setBounds(50, 220, 100, 30);
		userTextField.setBounds(150, 150, 150, 30);
		passwordField.setBounds(150, 220, 150, 30);
		showPassword.setBounds(150, 250, 150, 30);
		loginButton.setBounds(50, 300, 100, 30);
		resetButton.setBounds(200, 300, 100, 30);

	}

	public void addComponentsToContainer() {
		container.add(userLabel);
		container.add(passwordLabel);
		container.add(userTextField);
		container.add(passwordField);
		container.add(showPassword);
		container.add(loginButton);
		container.add(resetButton);
	}

	public void addActionEvent() {
		loginButton.addActionListener(this);
		resetButton.addActionListener(this);
		showPassword.addActionListener(this);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getSource() == loginButton) {
			String userText;
			String pwdText;
			userText = userTextField.getText();
			pwdText = passwordField.getText();

			if (dbService.checkUserAuthentication(userText, pwdText)) {
				JOptionPane.showMessageDialog(this, "Login Successful");
				this.setVisible(false);
				application.setVisibility(true);

			} else {
				JOptionPane.showMessageDialog(this, "Invalid Username or Password");
			}

			/*
			 * if (userText.equalsIgnoreCase("mehtab") && pwdText.equalsIgnoreCase("12345"))
			 * { JOptionPane.showMessageDialog(this, "Login Successful"); } else {
			 * JOptionPane.showMessageDialog(this, "Invalid Username or Password"); }
			 */

		}
		if (e.getSource() == resetButton) {
			userTextField.setText("");
			passwordField.setText("");
		}
		if (e.getSource() == showPassword) {
			if (showPassword.isSelected()) {
				passwordField.setEchoChar((char) 0);
			} else {
				passwordField.setEchoChar('*');
			}

		}
	}
	
private boolean loaded = false;
	
	

	public boolean isLoaded() {
		return loaded;
	}

	public void setLoaded(boolean loadingStatus) {
		this.loaded = loadingStatus;
	}
	
	public void loadNeuroApplication() throws Exception{
		application = new NeuroApplication();
		setLoaded(true);
	}

}