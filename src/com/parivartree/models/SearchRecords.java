package com.parivartree.models;

import java.util.ArrayList;
import java.util.HashMap;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchRecords implements Parcelable {
int userid,gender,status,deceased,connected,imageexists,invite;
String city,state,firstname,lastname;
ArrayList<HashMap<String, String>> relationRecords;

public int getUserid() {
	return userid;
}
public void setUserid(int userid) {
	this.userid = userid;
}
public int getGender() {
	return gender;
}
public void setGender(int gender) {
	this.gender = gender;
}
public int getStatus() {
	return status;
}
public void setStatus(int status) {
	this.status = status;
}
public int getDeceased() {
	return deceased;
}
public void setDeceased(int deceased) {
	this.deceased = deceased;
}
public int getConnected() {
	return connected;
}
public void setConnected(int connected) {
	this.connected = connected;
}
public int getImageexists() {
	return imageexists;
}
public void setImageexists(int imageexists) {
	this.imageexists = imageexists;
}
public int getInvite() {
	return invite;
}
public void setInvite(int invite) {
	this.invite = invite;
}
public String getCity() {
	return city;
}
public void setCity(String city) {
	this.city = city;
}
public String getState() {
	return state;
}
public void setState(String state) {
	this.state = state;
}
public String getFirstname() {
	return firstname;
}
public void setFirstname(String firstname) {
	this.firstname = firstname;
}
public String getLastname() {
	return lastname;
}
public void setLastname(String lastname) {
	this.lastname = lastname;
}
public ArrayList<HashMap<String, String>> getRelationRecords() {
	return relationRecords;
}
public void setRelationRecords(ArrayList<HashMap<String, String>> relationRecords) {
	this.relationRecords = relationRecords;
}
public static final Parcelable.Creator<SearchRecords> CREATOR = new Creator<SearchRecords>() {  
	 public SearchRecords createFromParcel(Parcel source) {  
		 SearchRecords msearchRecords = new SearchRecords();  
		 msearchRecords.userid = source.readInt();
		 return msearchRecords;  
	 }  
	 public SearchRecords[] newArray(int size) {  
	     return new SearchRecords[size];  
	 }  
	    };  
@Override
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void writeToParcel(Parcel parcel, int flags) {
	// TODO Auto-generated method stub
	parcel.writeString(city);
	parcel.writeString(state);
	parcel.writeString(firstname);
	parcel.writeString(lastname);
	parcel.writeInt(userid);
	parcel.writeInt(status);
	parcel.writeInt(gender);
	parcel.writeInt(deceased);
	parcel.writeInt(connected);
	parcel.writeInt(imageexists);
	parcel.writeInt(invite);
	parcel.writeList(relationRecords);
}
}
