package com.parivartree.models;

public class Comment {

	private int id;
	private String userName, date, profilePic, commentText;
	
	Comment() {}
	
	Comment(int id, String userName, String date, String profilePic, String commentText) {
		this.id = id;
		this.userName = userName;
		this.date = date;
		this.profilePic = profilePic;
		this.commentText = commentText;
	}
	
	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	public String getDate() {
		return date;
	}
	
	public void setDate(String date) {
		this.date = date;
	}
	
	public String getProfilePic() {
		return profilePic;
	}
	
	public void setProfilePic(String profilePic) {
		this.profilePic = profilePic;
	}
	
	public String getCommentText() {
		return commentText;
	}
	
	public void setCommentText(String commentText) {
		this.commentText = commentText;
	}
	
}
