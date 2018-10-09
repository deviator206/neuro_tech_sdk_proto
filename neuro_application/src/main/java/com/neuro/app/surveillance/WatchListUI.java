package com.neuro.app.surveillance;

import java.awt.BorderLayout;
import java.awt.Component;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JTable;

import com.neuro.app.service.DBService;
import com.neuro.app.service.WatchListBioDataService;
import com.neuro.app.util.BasePanel;
import com.neurotec.biometrics.client.NBiometricClient;

public class WatchListUI extends JFrame {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4328755910661879713L;
	BasePanel panel1 = null;
	BasePanel panel2 = null;

	WatchListBioDataService watchListServicePanel1;
	WatchListBioDataService watchListServicePanel2;
	
	private NBiometricClient biometricClientPanel1;
	private NBiometricClient biometricClientPanel2;

	JTable oJTable = null;
	JTable iJTable = null;
	
	List<String> idsListPanel1 = null;
	List<String> idsListPanel2 = null;
	
	public static void main(String args[]) throws Throwable {
		WatchListUI service = new WatchListUI();
		service.init("surveillance");
	}

	@SuppressWarnings("static-access")
	public WatchListUI() throws Throwable {
		this.setDefaultCloseOperation(this.EXIT_ON_CLOSE);
		panel1 = new OUTWatchList(50);
		panel2 = new INWatchList(50);
		this.add(panel1, BorderLayout.EAST);
		this.add(panel2, BorderLayout.WEST);
		this.setExtendedState(this.MAXIMIZED_BOTH);
		this.setSize(1000, 1000);

	}

	public void init(String componentName) throws Throwable {
		obtainLicenses(panel1, panel1.getName(), "surveillance");
		obtainLicenses(panel2, panel2.getName(), "surveillance1");

		DBService dbService = new DBService();

		watchListServicePanel1 = new WatchListBioDataService("outimageStore");
		watchListServicePanel2 = new WatchListBioDataService("inimageStore");

//		biometricClientPanel1 = watchListServicePanel1.getBiometricClient();
//		biometricClientPanel2 = watchListServicePanel2.getBiometricClient();
//		
		oJTable = panel1.getJTable();
		iJTable = panel2.getJTable();

		watchListServicePanel1.addWatchSubject(oJTable, dbService,"OUT");
		watchListServicePanel2.addWatchSubject(iJTable, dbService,"IN");
		
		watchListServicePanel1.checkForUpdatesInWatchlist(oJTable, dbService,"OUT");
		watchListServicePanel2.checkForUpdatesInWatchlist(iJTable, dbService,"IN");
		
		

	}

	private void obtainLicenses(BasePanel panel, String watchListName, String componentName) {
		try {
			if (!panel.isObtained()) {

				boolean status = false;
				if (componentName != null) {
					if (componentName.equals("surveillance")) {
						status = SurveillanceTools.getInstance().obtainLicenses(panel.getRequiredLicenses());
						SurveillanceTools.getInstance().obtainLicenses(panel.getOptionalLicenses());
					} else if (componentName.equals("surveillance1")) {

						status = INSurveillanceTools.getInstance().obtainLicenses(panel.getRequiredLicenses());
						INSurveillanceTools.getInstance().obtainLicenses(panel.getOptionalLicenses());

					}

					panel.getLicensing().setRequiredComponents(panel.getRequiredLicenses(), componentName);
					panel.getLicensing().setOptionalComponents(panel.getOptionalLicenses(), componentName);
				}
				panel.updateLicensing(status);
			}
		} catch (Exception e) {
			showError(null, e);
		}
	}

	private void showError(Component parentComponent, Throwable e) {
		e.printStackTrace();
		String message;
		if (e.getMessage() != null) {
			message = e.getMessage();
		} else if (e.getCause() != null) {
			message = e.getCause().getMessage();
		} else {
			message = "An error occurred. Please see log for more details.";
		}
		JOptionPane.showMessageDialog(parentComponent, message, "Error", JOptionPane.ERROR_MESSAGE);
	}
}
