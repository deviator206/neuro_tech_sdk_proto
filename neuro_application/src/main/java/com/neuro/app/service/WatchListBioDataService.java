package com.neuro.app.service;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.Timer;
import javax.swing.table.DefaultTableModel;

import com.neuro.app.surveillance.INSurveillanceTools;
import com.neuro.app.surveillance.SurveillanceTools;
import com.neurotec.biometrics.NBiometricOperation;
import com.neurotec.biometrics.NBiometricStatus;
import com.neurotec.biometrics.NBiometricTask;
import com.neurotec.biometrics.NFace;
import com.neurotec.biometrics.NLivenessMode;
import com.neurotec.biometrics.NSubject;
import com.neurotec.biometrics.client.NBiometricClient;
import com.neurotec.images.NImage;
import com.neurotec.io.NBuffer;

public class WatchListBioDataService {

	private NBiometricClient biometricClient;
	
	public WatchListBioDataService(String storeName) {
		// set connection to Mysql database using SDK
				biometricClient = new NBiometricClient();
				biometricClient.setDatabaseConnectionToOdbc("Dsn=neurotechnology;UID=root;PWD=passw0rd", storeName);
				biometricClient.setFacesLivenessMode(NLivenessMode.PASSIVE_AND_ACTIVE);
				biometricClient.setFacesDetectAllFeaturePoints(true);
				biometricClient.setFacesDetectBaseFeaturePoints(true);
				biometricClient.setFacesRecognizeExpression(true);
				biometricClient.setFacesDetectProperties(true);
				biometricClient.setFacesDetermineGender(true);
				biometricClient.setFacesDetermineAge(true);
	}
	
	public void addUnknownSubjectToDb(String unMatchedId, NImage image) throws Throwable {
		NSubject subject = null;
		NBiometricTask task = null;
		try {

			subject = new NSubject();
			NFace face = new NFace();
			face.setImage(image);
			subject.getFaces().add(face);
			subject.setId(unMatchedId);

			task = biometricClient.createTask(EnumSet.of(NBiometricOperation.ENROLL_WITH_DUPLICATE_CHECK), subject);
			biometricClient.performTask(task);

			if (task.getStatus() != NBiometricStatus.OK) {
				System.out.format("addUnknownSubjectToDb :: Identification was unsuccessful. Status: {0}.",
						task.getStatus());
				if (task.getError() != null)
					throw task.getError();
			}
		} catch (IOException e) {
			e.printStackTrace();

		}
	}
	
	
	public void addWatchSubject(JTable tableResults, DBService dbService) throws Throwable {

		TreeMap<String, ArrayList<Object>> sorted = dbService.getImageList("user");

		List<String> idsList = null;
		for (Entry<String, ArrayList<Object>> mapping : sorted.entrySet()) {
			try {
				System.out.println(mapping.getKey() + " ==> " + mapping.getValue());
//				if (mapping.getValue().get(2).equals("OUT")) {
				final NSubject subject = new NSubject();
				NFace face = new NFace();
				face.setImage(NImage.fromMemory((ByteBuffer) mapping.getValue().get(1)));
				subject.getFaces().add(face);
				final String id = mapping.getKey();
				subject.setId(id);
				idsList = Arrays.asList(biometricClient.listIds());
				NBiometricStatus status = SurveillanceTools.getInstance().getEngine().createTemplate(subject);
				if (status == NBiometricStatus.OK) {
					NBuffer template = subject.getTemplateBuffer();
					try {
						SurveillanceTools.getInstance().getSurveillance().addTemplate(id, template);
						if (!idsList.contains(id)) {
							SwingUtilities.invokeLater(new Runnable() {

								public void run() {

									NBiometricTask task = biometricClient
											.createTask(EnumSet.of(NBiometricOperation.ENROLL), subject);
									biometricClient.performTask(task);

									if (task.getStatus() != NBiometricStatus.OK) {
										System.out.format(
												"addWatchSubject :: Identification was unsuccessful. Status: {0}.",
												task.getStatus());
										if (task.getError() != null)
											try {
												throw task.getError();
											} catch (Throwable e) {
												e.printStackTrace();
											}
									}
								}

							});
						}
					} finally {
						template.dispose();
					}

					if ((tableResults.getModel().getRowCount() != 0)) {
						tableResults.repaint();
					}
					((DefaultTableModel) tableResults.getModel()).addRow(new Object[] { id, 0, null, 0 });
				} else {
					System.out.println("Template creation failed: " + status);
				}
//				}
			} catch (Exception e) {
				System.out.println("Exception: " + e);
			}
		}
	}
	
	public void checkForUpdatesInWatchlist(final JTable tableResults, final DBService dbService) {
		// New timer which works!
		int delay = 300000; // milliseconds
		ActionListener loadSubjectFromDBtaskPerformer = new ActionListener() {
			public void actionPerformed(ActionEvent evt) {
				try {
					addWatchSubject(tableResults, dbService);
				} catch (Throwable e) {
					e.printStackTrace();
				}
			}
		};
		new Timer(delay, loadSubjectFromDBtaskPerformer).start();
	}

	public void clearWatchSubjects(JTable tableResults) {
		INSurveillanceTools.getInstance().getSurveillance().removeAllTemplates();
		((DefaultTableModel) tableResults.getModel()).setRowCount(0);
	}
	
	public void resetParameters() {
		biometricClient.reset();
	}

}
