package com.parivartree.customviews;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.helpers.CircularImageView;
import com.parivartree.helpers.RectangularImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class CustomNode extends LinearLayout {
	
	private final String TAG = "CustomNode";
	private int userId, gender, deceased, relationCount, relationId;
	private String userName, userFirstName, userLastName, userImageString;
	
	Drawable userImage;
	// CircularImageView viewUserImage;
	RectangularImageView viewUserImage;
	TextView textViewRelationCount, textViewUserName, textViewRelation;
	TextView textViewUserFirstName, textViewUserLastName;
	
	public CustomNode(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
		
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CustomNode, 0, 0);
		
		userId = a.getInt(R.styleable.CustomNode_userid, 0);
		userName = a.getString(R.styleable.CustomNode_username);
		userImage = a.getDrawable(R.styleable.CustomNode_userimage);
		relationCount = a.getInt(R.styleable.CustomNode_relationcount, 0);
		
		a.recycle();
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		inflater.inflate(R.layout.view_custom_node, this, true);
		
		setOrientation(LinearLayout.VERTICAL);
		this.setBackgroundResource(R.drawable.background_custom_node);
		this.setPadding(10, 10, 10, 4);
		//this.setBackgroundResource(R.drawable.background_custom_node);
		// this.setBackgroundColor(getResources().getColor(android.R.color.background_light));
		// this.setBackgroundResource(android.R.drawable.dialog_holo_light_frame);
		// this.setBackgroundDrawable(getResources().getDrawable(android.R.drawable.dialog_holo_light_frame));
		
		textViewRelationCount = (TextView) getChildAt(0);
		
		// viewUserImage = (com.parivartree.helpers.CircularImageView)
		// getChildAt(0);
		viewUserImage = (com.parivartree.helpers.RectangularImageView) getChildAt(1);
		
		// viewUserImage.setMaxWidth(50);
		// viewUserImage.setMaxHeight(50);
		viewUserImage.setLayoutParams(new LayoutParams(180, 180));
		
		textViewUserName = (TextView) getChildAt(2);
		textViewRelation = (TextView) getChildAt(3);
		
		//textViewUserFirstName = (TextView) getChildAt(4);
		//textViewUserLastName = (TextView) getChildAt(5);
		
		if (viewUserImage != null) {
			
			// viewUserImage.setImageDrawable(getResources().getDrawable(R.drawable.male));
			// UrlImageViewHelper.setUrlDrawable(viewUserImage,
			// "http://www.parivartree.com/profileimages/thumbs/1384PROFILE.jpeg",
			// null, 60000);
		}
		
		// viewUserName = (TextView) getChildAt(1);
		// if(viewUserName != null) {viewUserName.setText(userName);}
		
	}

	public CustomNode(Context context) {
		this(context, null);
	}
	
	public int getUserId() {
		return this.userId;
	}

	public void setUserId(int userId) {

		this.userId = userId;
		Log.d(TAG, "" + this.userId);
	}

	public String getUserFirstName() {
		return userFirstName;
	}

	public void setUserFirstName(String userFirstName) {
		this.userFirstName = userFirstName;
		//textViewUserFirstName.setText(this.userFirstName);
		
		this.userName = this.userFirstName + " " + this.userLastName;
		setUserName(this.userName);
	}

	public String getUserLastName() {
		return userLastName;
	}

	public void setUserLastName(String userLastName) {
		this.userLastName = userLastName;
		//textViewUserLastName.setText(this.userLastName);
		
		this.userName = this.userFirstName + " " + this.userLastName;
		setUserName(this.userName);
	}
	
	public String getUserName() {
		return this.userName;
	}
	
	private void setUserName(String userName) {
		if(userName.length() > 12) {
			textViewUserName.setText(userName.substring(0, 9) + "...");
		}
		else { textViewUserName.setText(userName); }
	}

	/*
	 * public String getUserName() { return userName; }
	 * 
	 * public void setUserName(String userName) {
	 * 
	 * this.userName = userName; Log.d(TAG, ""+this.userName);
	 * this.viewUserName.setText(this.userName); requestLayout(); invalidate();
	 * }
	 */

	public Drawable getUserImage() {
		return userImage;
	}

	public void setUserImage(Drawable userImage) {
		this.userImage = userImage;
	}

	public void setUserImage() {

		if (this.gender == 1) {
			UrlImageViewHelper.setUrlDrawable(this.viewUserImage, this.getUserImageString(), getResources()
					.getDrawable(R.drawable.male), 0);
		} else {
			UrlImageViewHelper.setUrlDrawable(this.viewUserImage, this.getUserImageString(), getResources()
					.getDrawable(R.drawable.female), 0);
		}

		// requestLayout();
		// invalidate();
	}
	
	public int getGender() {
		return gender;
	}
	
	public void setGender(int gender) {
		this.gender = gender;
		if (this.gender == 1) {
			viewUserImage.setBorderColor(getResources().getColor(R.color.pt_blue));
			// viewUserImage.setBorderColor(Color.CYAN);
		} else {
			// viewUserImage.setBorderColor(getResources().getColor(R.color.pt_crimson));
			viewUserImage.setBorderColor(Color.MAGENTA);
		}
	}
	
	public int getDeceased() {
		return deceased;
	}
	
	public void setDeceased(int deceased) {
		this.deceased = deceased;
		if (this.deceased == 1) {
			viewUserImage.setBorderColor(getResources().getColor(R.color.pt_gold));
		}
	}
	
	public int getRelationCount() {
		return relationCount;
	}
	
	public void setRelationCount(int relationCount) {
		this.relationCount = relationCount;
		if(this.relationCount == 0) {
			textViewRelationCount.setText(" ");
		} else {
			
			textViewRelationCount.setText("+" + this.relationCount);
		}
	}
	
	public String getUserImageString() {
		return userImageString;
	}
	
	public void setUserImageString(String userImageString) {
		if (userImageString.equals("none")) {
			this.userImageString = "";
		} else {
			this.userImageString = userImageString;
		}
		Log.i(TAG, "userImageString:" + this.userImageString);
		
	}
	
	public int getRelationId() {
		return relationId;
	}
	
	public void setRelationId(int relationId) {
		this.relationId = relationId;
		
		switch (this.relationId) {
		case 0:
			textViewRelation.setText(" ");
			break;
		case 1:
			textViewRelation.setText("(Father)");
			break;
		case 2:
			textViewRelation.setText("(Mother)");
			break;
		case 3:
			textViewRelation.setText("(Wife)");
			break;
		case 4:
			textViewRelation.setText("(Brother)");
			break;
		case 5:
			textViewRelation.setText("(Sister)");
			break;
		case 6:
			textViewRelation.setText("(Son)");
			break;
		case 7:
			textViewRelation.setText("(Daughter)");
			break;
		case 8:
			textViewRelation.setText("(Husband)");
		}
	}
}
