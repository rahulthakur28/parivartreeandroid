package com.parivartree.models;

import android.os.Parcel;
import android.os.Parcelable;

public class SearchRecordRelation implements Parcelable {
String relationname = "",name = "";
int id,imageexists;

public String getRelationname() {
	return relationname;
}
public void setRelationname(String relationname) {
	this.relationname = relationname;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public int getId() {
	return id;
}
public void setId(int id) {
	this.id = id;
}
public int getImageexists() {
	return imageexists;
}
public void setImageexists(int imageexists) {
	this.imageexists = imageexists;
}
@Override
public int describeContents() {
	// TODO Auto-generated method stub
	return 0;
}
@Override
public void writeToParcel(Parcel parcel, int flags) {
	// TODO Auto-generated method stub
	parcel.writeString(relationname);
	parcel.writeString(name);
	parcel.writeInt(id);
	parcel.writeInt(imageexists);
}
}
