package com.parivartree.models;

import java.util.List;

public class UserAlbum {
	
	private String mUserId;
	private String mUserName;
	private String mPhotoCount;
	private String mView;
	private String mThumFlag;
	private String mImageExists;
	private List<Albums>mAlbums;
	
	public String getmUserId() {
		return mUserId;
	}
	public void setmUserId(String mUserId) {
		this.mUserId = mUserId;
	}
	public String getmUserName() {
		return mUserName;
	}
	public void setmUserName(String mUserName) {
		this.mUserName = mUserName;
	}
	public String getmPhotoCount() {
		return mPhotoCount;
	}
	public void setmPhotoCount(String mPhotoCount) {
		this.mPhotoCount = mPhotoCount;
	}
	public String getmView() {
		return mView;
	}
	public void setmView(String mView) {
		this.mView = mView;
	}
	public String getmThumFlag() {
		return mThumFlag;
	}
	public void setmThumFlag(String mThumFlag) {
		this.mThumFlag = mThumFlag;
	}
	public String getmImageExists() {
		return mImageExists;
	}
	public void setmImageExists(String mImageExists) {
		this.mImageExists = mImageExists;
	}
	public List<Albums> getmAlbums() {
		return mAlbums;
	}
	public void setmAlbums(List<Albums> mAlbums) {
		this.mAlbums = mAlbums;
	}
	
	

}
