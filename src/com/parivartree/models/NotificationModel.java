package com.parivartree.models;

public class NotificationModel {
	int notifid, notificationtype, readstatus, imageexists;
	String entityname, event, post, relationname, addedby, date, ownerid, weddingdate, exactNotificationText, entityid,
			notificationstatus;

	public NotificationModel(){
		entityname = "";
		event = ""; 
		post = "";
		relationname = ""; 
		addedby = ""; 
		date = "";
		ownerid = "";
		weddingdate = "";
		exactNotificationText = ""; 
		entityid = "";
		notificationstatus = "";
	}

	public String getExactNotificationText() {
		return exactNotificationText;
	}

	public void setExactNotificationText(String exactNotificationText) {
		this.exactNotificationText = exactNotificationText;
	}

	public int getNotifid() {
		return notifid;
	}

	public void setNotifid(int notifid) {
		this.notifid = notifid;
	}

	public int getNotificationtype() {
		return notificationtype;
	}

	public void setNotificationtype(int notificationtype) {
		this.notificationtype = notificationtype;
	}

	public String getEntityid() {
		return entityid;
	}

	public void setEntityid(String entityid) {
		this.entityid = entityid;
	}

	public int getReadstatus() {
		return readstatus;
	}

	public void setReadstatus(int readstatus) {
		this.readstatus = readstatus;
	}

	public String getNotificationstatus() {
		return notificationstatus;
	}

	public void setNotificationstatus(String notificationstatus) {
		this.notificationstatus = notificationstatus;
	}

	public int getImageexists() {
		return imageexists;
	}

	public void setImageexists(int imageexists) {
		this.imageexists = imageexists;
	}

	public String getEntityname() {
		return entityname;
	}

	public void setEntityname(String entityname) {
		this.entityname = entityname;
	}

	public String getEvent() {
		return event;
	}

	public void setEvent(String event) {
		this.event = event;
	}

	public String getPost() {
		return post;
	}

	public void setPost(String post) {
		this.post = post;
	}

	public String getRelationname() {
		return relationname;
	}

	public void setRelationname(String relationname) {
		this.relationname = relationname;
	}

	public String getAddedby() {
		return addedby;
	}

	public void setAddedby(String addedby) {
		this.addedby = addedby;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getOwnerid() {
		return ownerid;
	}

	public void setOwnerid(String ownerid) {
		this.ownerid = ownerid;
	}

	public String getWeddingdate() {
		return weddingdate;
	}

	public void setWeddingdate(String weddingdate) {
		this.weddingdate = weddingdate;
	}

}
