package com.parivartree.models;

import java.util.ArrayList;

public class PostItem {

	private int id;
	private String userName, date, profilePic, postType, postText;
	private ArrayList<String> imageUrls;
	private String videoUrl;
	private ArrayList<Comment> comments;

	PostItem() {}
	
	PostItem(int id, String userName, String date, String profilePic, String postType, String postText) {
		this.userName = userName;
		this.date = date;
		this.profilePic = profilePic;
		this.postType = postType;
		this.postText = postText;
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

	public String getPostType() {
		return postType;
	}

	public void setPostType(String postType) {
		this.postType = postType;
	}

	public String getPostText() {
		return postText;
	}

	public void setPostText(String postText) {
		this.postText = postText;
	}
	
	public String getVideoUrl() {
		return videoUrl;
	}

	public void setVideoUrl(String videoUrl) {
		this.videoUrl = videoUrl;
	}
	
	public ArrayList<String> getImageUrls() {
		return imageUrls;
	}

	public void setImageUrls(ArrayList<String> imageUrls) {
		this.imageUrls = imageUrls;
	}
	
	public boolean addImageUrl(String imageUrl) {
		return this.imageUrls.add(imageUrl);
	}
	
	public String removeImageUrl(int position) {
		return this.imageUrls.remove(position);
	}

	public ArrayList<Comment> getComment() {
		return comments;
	}

	public void setComment(ArrayList<Comment> comment) {
		this.comments = comment;
	}
	
	public boolean addComment(Comment comment) {
		return this.comments.add(comment);
	}
	
	public Comment removeComment(int position) {
		return this.comments.remove(position);
	}
}
