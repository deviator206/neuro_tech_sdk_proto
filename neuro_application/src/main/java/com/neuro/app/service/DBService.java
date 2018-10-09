package com.neuro.app.service;

import java.sql.Date;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import com.neuro.app.dao.DBConnection;
import com.neuro.app.util.NotificationStatus;
import com.neurotec.images.NImage;

public class DBService {
	private static DBConnection instance;

	public DBService() throws Exception {
		super();
		DBConnection.getInstance().connectDataBase();
		instance = DBConnection.getInstance();
	}

	public TreeMap<String, ArrayList<Object>> getImageList(String tableName) throws Exception {
		HashMap<String, ArrayList<Object>> imageDBList = DBConnection.getInstance().getImageDetailsFromDB(tableName);

		// TreeMap keeps all entries in sorted order
		TreeMap<String, ArrayList<Object>> sorted = new TreeMap<String, ArrayList<Object>>(imageDBList);
		System.out.println("HashMap after sorting by keys in ascending order ");
		return sorted;
	}

	public void saveInsideOutInfoToDB(String subjectId, int score, int age, String gender, int isLive,
			String deviceType) throws Exception {
		HashMap<String, String> subjectDBList = instance.checkIfSubjectPresentInInsideOutInfoInDB(subjectId, age,
				gender);
		if (subjectDBList.isEmpty()) {
			instance.insertInsideOutInfoInDB(subjectId, score, age, gender, isLive, deviceType);
		}
	}

	public String getAgeOfUser(String subjectId) throws ParseException {
		return instance.getUserInfoForAgeInDB(subjectId);
	}

	public Timestamp getTimestamp(Date date) throws ParseException {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		java.util.Date now = sdf.parse(date.toString());

		Timestamp nowTimestamp = new Timestamp(now.getTime());
		return nowTimestamp;
	}

	public void saveSubjectInfoForUnknownToDB(String subjectId, NImage image, String string, Timestamp timeStamp)
			throws Exception {
		String dbSubject = instance.checkIfSubjectPresentInfoInDB(subjectId, string);
		if (dbSubject == null) {
			instance.insertSubjectInfoForUnknownInDB(subjectId, image, string, timeStamp);
		}
	}

	public void saveSubjectInfoToDB(String subjectId, NImage image, String string, Timestamp timeStamp)
			throws Exception {
		String dbSubject = instance.checkIfSubjectPresentInfoInDB(subjectId, string);
		if (dbSubject == null) {
			instance.insertSubjectInfoInDB(subjectId, image, string, timeStamp);
		}
	}

	public void saveTheNotification(String target, String origin, String title, String matchedId,
			NotificationStatus status, Timestamp timeStampOut) {
		instance.insertNotificationInDB(target, origin, title, matchedId, status, timeStampOut);
	}

	public void markAttendanceInHistory(String cameratype, String name, Timestamp timeStampType, Timestamp timestamp) {
		instance.insertAttendanceInHistoryInDB(cameratype, name, timeStampType, timestamp);

	}

	public boolean checkUserAuthentication(String username, String password) {
		return instance.checkUserAuthentication(username, password);
	}

	public String getUniqueUnMatchedIdFromDB() {
		String subjectId = instance.getUniqueUnMatchedIdFromDB();
		return getAnonymousID(subjectId);
	}

	public String getAnonymousID(String subjectId) {
		String unMatchedId = null;
		if (subjectId != null) {
			subjectId = subjectId.replace("anonymous0", "").replace(".png", "");
			int num = Integer.parseInt(subjectId);
			num += 1;
			unMatchedId = "anonymous0" + num + ".png";
			System.out.println(unMatchedId);
		}
		return unMatchedId;
	}

	public String getCameraDeviceTypeFromDB(String cameraType) {
		String cameraDeviceType = instance.getUniqueUnMatchedIdFromDB(cameraType);
		return cameraDeviceType;
	}

}
