package com.parivartree.models;

public class MyObject {
	public String objectName, objectStatus, objectId, relationid, nodeid;

	// constructor for adding sample data
	public MyObject(String objectName, String objectId, String objectStatus) {

		this.objectName = objectName;
		this.objectId = objectId;
		this.objectStatus = objectStatus;
	}

	public MyObject(String objectName, String objectId, String objectStatus, String relationid, String nodeid) {

		this.objectName = objectName;
		this.objectId = objectId;
		this.objectStatus = objectStatus;
		this.relationid = relationid;
		this.nodeid = nodeid;
	}
}
