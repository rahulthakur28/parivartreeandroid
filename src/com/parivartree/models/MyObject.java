package com.parivartree.models;

public class MyObject {
	public String objectName, objectStatus, objectId, relationid, nodeid;
	public int gender,deceased;

	// constructor for adding sample data
	public MyObject(String objectName, String objectId, String objectStatus, int gender , int deceased) {

		this.objectName = objectName;
		this.objectId = objectId;
		this.objectStatus = objectStatus;
		this.gender = gender;
		this.deceased = deceased; 
		
	}

	public MyObject(String objectName, String objectId, String objectStatus, String relationid, String nodeid, int gender , int deceased) {

		this.objectName = objectName;
		this.objectId = objectId;
		this.objectStatus = objectStatus;
		this.relationid = relationid;
		this.nodeid = nodeid;
		this.gender = gender;
		this.deceased = deceased; 
	}
}
