package com.parivartree.helpers;

import java.util.ArrayList;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.customviews.CustomNode;
import com.parivartree.fragments.HomeFragment;
import com.parivartree.models.Node;
import com.parivartree.models.NodeUser;

public class CompleteTree implements OnClickListener{
	
	private final String TAG = "CompleteTree";
	public Context context;
	public Node user;
	public Fragment fragment;
	
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	
	// list to hold views for parents' row, user's row, offsprings' row
	public ArrayList<View> parentsRow = new ArrayList<View>();
	public ArrayList<View> parentsRowAdjustment = new ArrayList<View>();
	public ArrayList<View> userRow = new ArrayList<View>();
	public ArrayList<View> userRowAdjustment = new ArrayList<View>();
	public ArrayList<View> offSpringRow = new ArrayList<View>();
	public ArrayList<View> offSpringRowAdjustment = new ArrayList<View>();
	
	// stores positions for characters
	public ArrayList<Integer> parentsToUserConnectors = new ArrayList<Integer>();
	public ArrayList<Integer> userToParentsConnectors = new ArrayList<Integer>();
	public ArrayList<Integer> offspringsToUserConnectors = new ArrayList<Integer>();
	public ArrayList<Integer> userToOffspringConnectors = new ArrayList<Integer>();
	
	public View userView;
	public int connectorWidthFactor = 5;
	
	// userParentConnector width
	int userParentConnectorWidth = 0;
	
	// userOffSpringConnector width
	int userOffSpringConnector = 0;
	
	// parent row width
	int parentRowWidth = 0;
	
	// parent to user main connector
	int parentToUserMainConnector = 0;
	
	// parent to user join point
	int parentToUserJoinPoint = 0;
	
	// user to offSpring main connector
	int userToOffSpringMainConnector = 0;
	
	// user to offSpring join point
	int userToOffSpringJoinPoint = 0;
	
	// offsets
	int parentOffset = 0, offSpringOffset = 0, userOffset = 0;
	
	int largestRow = 1;
	int largestSize = userRow.size();
	
	// store the connector offset for current sibling node
	int siblingMainConnector = 0;
	
	//main user's is whose tree is being viewed
	int mainUserId = 0;
	
	// height of the connector
	int connectorHeight;
	
	public CompleteTree(Context context, Node user, Fragment fragment) {
		
		this.fragment = fragment;
		this.context = context;
		this.user = user;
		//this.context = context;
		
		sharedPreferences = context.getApplicationContext()
				.getSharedPreferences(
						context.getPackageName()
								+ context.getResources().getString(
										R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		
		
		// TODO generates a reference node for calculationg sizes
		CustomNode currentUser = new CustomNode(context);
		
		setNodeAttributes(currentUser, user);
		
		currentUser.setOnClickListener(this);
		//currentUser.setBackgroundColor(context.getResources().getColor(R.color.pt_user));
		//currentUser.setBackground(context.getResources().getDrawable(R.drawable.background_user_node));
		currentUser.setBackgroundResource(R.drawable.background_user_node);
		//userRow.add(currentUser);
		
		currentUser.measure(0, 0);
		
		// Add the view to be accesssed throughout
		userView = currentUser;
		mainUserId = user.getId();
		connectorHeight = 80;
		Log.d(TAG, "mainUserId:" + mainUserId);
	}
	
	private void setNodeAttributes(CustomNode customNode, int id, String firstName, String lastName,
			String userImageString, int gender, int deceased, int relationCount) {
		
		customNode.setUserId(id);
		customNode.setUserFirstName(firstName);
		customNode.setUserLastName(lastName);
		customNode.setUserImageString(userImageString);
		customNode.setGender(gender);
		customNode.setDeceased(deceased);
		customNode.setRelationCount(relationCount);
		customNode.setUserImage();
		customNode.setRelationId(0);
		
		Log.i(TAG, "firstName: " + firstName + ", lastName:" + lastName);
	}
	
	private void setNodeAttributes(CustomNode customNode, int id, String firstName, String lastName,
			String userImageString, int gender, int deceased, int relationCount, int relationId) {
		
		customNode.setUserId(id);
		customNode.setUserFirstName(firstName);
		customNode.setUserLastName(lastName);
		customNode.setUserImageString(userImageString);
		customNode.setGender(gender);
		customNode.setDeceased(deceased);
		customNode.setRelationCount(relationCount);
		customNode.setUserImage();
		customNode.setRelationId(relationId);
		
		Log.i(TAG, "firstName: " + firstName + ", lastName:" + lastName);
	}
	
	private void setNodeAttributes(CustomNode customNode, Node nodeUser) {
		
		customNode.setUserId(nodeUser.getId());
		customNode.setUserFirstName(nodeUser.getFirstName());
		customNode.setUserLastName(nodeUser.getLastName());
		customNode.setUserImageString("https://www.parivartree.com/profileimages/thumbs/" + customNode.getUserId() + "PROFILE.jpeg");
		customNode.setGender(nodeUser.getGender());
		customNode.setDeceased(nodeUser.getDeceased());
		customNode.setRelationCount(nodeUser.getRelationCount());
		customNode.setUserImage();
		customNode.setRelationId(nodeUser.getRelationId());
		
		Log.i(TAG, "Attributes for Custom node - " + " gender:" + customNode.getGender());
	}
	
	public TableLayout generateView() {
		
		TableLayout tl = new TableLayout(context);
		tl.setOrientation(LinearLayout.VERTICAL);
		tl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		
		Log.d(TAG, "fathers count:" + user.getFathers().size());
		Log.d(TAG, "mothers count:" + user.getMothers().size());
		
		generateParentRow();
		generateUserRow();
		//generateOffSpringRow();
		
		calculateAdjustments();
		
		if (user.getFathers().size() > 0 || user.getMothers().size() > 0) {
			
			// TODO add parents row
			TableLayout tlParent = new TableLayout(context);
			tlParent.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			tlParent.addView(generateParentRowLayout());
			
			tl.addView(tlParent);
			
			// TODO user Parent level connector must be put here if no connection required if only siblings relation present
		}
		
		if (user.getFathers().size() > 0 || user.getMothers().size() > 0 || user.getBrothers().size() > 0 || user.getSisters().size() >0) {
			// TODO add user parent level connecter
			TableLayout tlUserParentLevelConnector = new TableLayout(context);
			tlUserParentLevelConnector.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			Log.v(TAG, "userParent level connector added");
			TableRow tr = new TableRow(context);
			tr.addView(generateUserParentLevelConnector());
			tlUserParentLevelConnector.addView(tr);
			
			tl.addView(tlUserParentLevelConnector);
		}
		
		// TODO add user row
		TableLayout tlUser = new TableLayout(context);
		tlUser.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		
		tlUser.addView(generateUserRowLayout());
		
		tl.addView(tlUser);
		
		/*
		if (user.getSons().size() > 0 || user.getDaughters().size() > 0) {
			
			// TODO add user offspring level connecter
			TableLayout tlUserOffspringLevelConnector = new TableLayout(context);
			tlUserOffspringLevelConnector.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			Log.v(TAG, "userOffspring level connector added");
			TableRow tr = new TableRow(context);
			tr.addView(generateUserOffSpringLevelConnector());
			tlUserOffspringLevelConnector.addView(tr);
			
			tl.addView(tlUserOffspringLevelConnector);
			
			// TODO add offSpring layout
			TableLayout tlOffSpring = new TableLayout(context);
			tlOffSpring.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			
			// Log.d(TAG, "offSpringViewCoutn" +
			// generateOffSpringRowLayout().getChildCount());
			tlOffSpring.addView(generateOffSpringRowLayout());
			
			tl.addView(tlOffSpring);
		}
		*/
		
		return tl;
		
		
		/*
		TableLayout containerTableLayout = new TableLayout(context);
		//tl.setOrientation(LinearLayout.VERTICAL);
		containerTableLayout.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT));
		TableRow siblingsTr = new TableRow(context);
		//siblingsTr.setLayoutParams(new android.widget.TableRow.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		
		TableLayout siblingsTreeTl = new TableLayout(context);
		siblingsTreeTl.setOrientation(LinearLayout.VERTICAL);
		
		getSiblingsTree(user.getBrothers().get(0), siblingsTreeTl);
		//siblingsTr.addView(siblingsTreeTl);
		//containerTableLayout.addView(siblingsTr);
		//((ViewGroup)siblingsTr.getParent()).removeView(siblingsTr)
		siblingsTreeTl.measure(0, 0);
		
		//siblingsTr.invalidate();
		//siblingsTr.measure(0, 0);
		//siblingsTreeTl.requestLayout();
		
		//containerTableLayout.requestLayout();
		//containerTableLayout.measure(0, 0);
		
		//Log.d(TAG, "containerTableLayout width() : " + containerTableLayout.getMeasuredWidth());
		//Log.d(TAG, "siblingsTr width() : " + siblingsTr.getMeasuredWidth());
		Log.d(TAG, "siblingsTreeTl width() : " + siblingsTreeTl.getMeasuredWidth());
		return siblingsTreeTl;
		//return (TableLayout) getSiblingsTree(user.getBrothers().get(0), tl);
		*/
	}

	public void calculateAdjustments() {

		// Calculate largest row
		/*
		if (parentsRow.size() > largestSize) {
			largestRow = 0;
			largestSize = parentsRow.size();
		}
		if (userRow.size() > largestSize) {
			largestRow = 1;
			largestSize = userRow.size();
		}
		*/
		
		if (parentRowWidth > largestSize) {
			largestRow = 0;
			largestSize = parentRowWidth;
		}
		if (userParentConnectorWidth > largestSize) {
			largestRow = 1;
			largestSize = userParentConnectorWidth;
		}
		
		/*
		if (offSpringRow.size() > largestSize) {
			largestRow = 2;
			largestSize = offSpringRow.size();
		}
		*/
		
		Log.d(TAG, "calculateAdjustments called!");
		Log.d(TAG, "Parents Row size : " + parentsRow.size());
		Log.d(TAG, "User Row size : " + userRow.size());
		Log.d(TAG, "OffSprings Row size : " + offSpringRow.size());
		Log.d(TAG, "largest row : " + largestRow);
		
		//userToOffSpringJoinPoint = userOffSpringConnector / 2;
		
		if (largestRow == 0) {
			
			if(userRow.size() == 1) {
				userOffset = Math.abs(parentToUserMainConnector - userToParentsConnectors.get(0));
				Log.d(TAG, "Offset for Largest Row 1: userOffset" + userOffset);
				if(userOffset > 0) {
					ImageView userOffsetImage = new ImageView(context);
					userOffsetImage.setImageDrawable(generateOffsetDrawable(
							userView.getMeasuredHeight(), userOffset));
					userRow.add(0, userOffsetImage);
				}
			}
			else if(userRow.size() > 0) {
				userOffset = Math.abs(parentToUserMainConnector - parentToUserJoinPoint);
				if(userOffset > 0) {
					ImageView userOffsetImage = new ImageView(context);
					userOffsetImage.setImageDrawable(generateOffsetDrawable(
							userView.getMeasuredHeight(), userOffset));
					userRow.add(0, userOffsetImage);
				}
			}
			
			/*
			offSpringOffset = Math.abs(userToOffSpringJoinPoint
					- userToOffSpringMainConnector) + userOffset;
			Log.i(TAG, "userToOffSpringJoinPoint:" + userToOffSpringJoinPoint +", userToOffSpringMainConnector" + userToOffSpringMainConnector + ", userOffset" + userOffset + ", offSpringOffset" + offSpringOffset);
			if(offSpringRow.size() > 0 && offSpringOffset > 0) {
				ImageView offSpringOffsetImage = new ImageView(context);
				offSpringOffsetImage.setImageDrawable(generateOffsetDrawable(
						userView.getMeasuredHeight(), offSpringOffset));
				offSpringRow.add(0, offSpringOffsetImage);
			}
			*/
		}
		
		if (largestRow == 1) {
			parentOffset = Math.abs(parentToUserJoinPoint
					- parentToUserMainConnector);
			if (parentOffset > 0 && parentsRow.size() > 0) {
				ImageView parentOffsetImage = new ImageView(context);
				parentOffsetImage.setImageDrawable(generateOffsetDrawable(
						userView.getMeasuredHeight(), parentOffset));
				parentsRow.add(0, parentOffsetImage);
				Log.e(TAG, "Parent row Offset: " + parentOffset);
			}
			
			/*
			offSpringOffset = Math.abs(userToOffSpringMainConnector
					- userToOffSpringJoinPoint);
			if (offSpringOffset > 0 && offSpringRow.size() > 0) {
				ImageView offSpringOffsetImage = new ImageView(context);
				offSpringOffsetImage.setImageDrawable(generateOffsetDrawable(
						userView.getMeasuredHeight(), offSpringOffset));
				offSpringRow.add(0, offSpringOffsetImage);
				Log.e(TAG, "userParentConnectorWidth" + userParentConnectorWidth);
				Log.e(TAG, "userToOffSpringMainConnector" + userToOffSpringMainConnector + ", userToOffSpringJoinPoint" + userToOffSpringJoinPoint);
				Log.e(TAG, "connector width" + offspringsToUserConnectors);
				Log.e(TAG, "Offspring row Offset: " + offSpringOffset);
			}
			*/
		}
		
		if (largestRow == 2) {
			userOffset = Math.abs(userToOffSpringMainConnector - userToOffSpringJoinPoint);
			Log.v(TAG, "useroffset - " + userOffset);
			if(userOffset > 0) {
				ImageView userOffsetImage = new ImageView(context);
				userOffsetImage.setImageDrawable(generateOffsetDrawable(
						userView.getMeasuredHeight(), userOffset));
				userRow.add(0, userOffsetImage);
			}
			
			parentOffset = Math.abs(this.parentToUserJoinPoint
					- this.parentToUserMainConnector) + userOffset;
			if(parentOffset > 0) {
				ImageView parentOffsetImage = new ImageView(context);
				parentOffsetImage.setImageDrawable(generateOffsetDrawable(
						userView.getMeasuredHeight(), parentOffset));
				parentsRow.add(0, parentOffsetImage);
			}
		}
	}
	
	private void generateParentRow() {

		Log.i(TAG, "father.size() - " + user.getFathers().size()
				+ ", mother.size() - " + user.getMothers().size());
		if (user.getFathers().size() > 0 || user.getMothers().size() > 0) {
			if (user.getFathers().size() > 0) {
				int count = 0;
				for (Node tempFather : user.getFathers()) {
					CustomNode father = new CustomNode(context);
					Log.d(TAG, "parsed father ID - " + tempFather.getId()
							+ ", name - " + tempFather.getName());
					
					setNodeAttributes(father, tempFather);
					
					father.measure(0, 0);
					Log.d(TAG, "Father dimensions:" + father.getMeasuredHeight() + ", " + father.getMeasuredWidth());
					if (count > 0) {
						// if (true) {
						ImageView connector = new ImageView(context);
						
						connector.setImageDrawable(generateHorizontalConnector(
								father.getMeasuredHeight(),
								father.getMeasuredWidth()));
						
						connector.measure(0, 0);
						parentsRow.add(connector);
						
						// Add to the width of parent row
						parentRowWidth = parentRowWidth
								+ connector.getMeasuredWidth();
						Log.i(TAG, "father connector- parentRowWidth "
								+ parentRowWidth);
					}
					
					father.setOnClickListener(this);
					parentsRow.add(father);
					
					// Add to the width of parent row
					parentRowWidth = parentRowWidth + father.getMeasuredWidth();
					Log.i(TAG, "father - parentRowWidth " + parentRowWidth);
					
					Log.d(TAG,
							"father added - for custom node - parent view:  - "
									+ father.getParent() + ", childcount:"
									+ father.getChildCount());
					count++;
				}
			} else if (user.getMothers().size() > 0) {
				// TODO if no father present then add a dummy father with
				// "Add Father" name
				CustomNode father = new CustomNode(context);
				// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
				// ", name - " + tempFather.getName());
				/*
				setNodeAttributes(
						father,
						0,
						"Add Father",
						context.getResources().getDrawable(
								R.drawable.parivar_mobile_profile_image_vsmall));
				*/
				setNodeAttributes(father, 0, "Add", "Father", "none", 1, 0, 0);
				
				father.measure(0, 0);
				father.setOnClickListener(this);
				parentsRow.add(father);
				
				// Add to the width of parent row
				parentRowWidth = parentRowWidth + father.getMeasuredWidth();
				Log.i(TAG, "father dummy - parentRowWidth " + parentRowWidth);
			}
			
			// TODO add connector between father and mother
			ImageView motherFatherConnector = new ImageView(context);
			/*
			motherFatherConnector
					.setImageDrawable(generateBothParentsConnecter(parentsRow
							.get(0).getMeasuredHeight(), parentsRow.get(0)
							.getMeasuredWidth()));
							*/
			motherFatherConnector
			.setImageDrawable(generateHorizontalConnector(userView.getMeasuredHeight(), userView.getMeasuredWidth()));
			motherFatherConnector.measure(0, 0);
			parentsRow.add(motherFatherConnector);

			// Add to the width of parent row
			parentRowWidth = parentRowWidth
					+ motherFatherConnector.getMeasuredWidth();
			Log.i(TAG, "motherFatherConnector- parentRowWidth "
					+ parentRowWidth);
			
			// Add position of connectors from parent to user
			parentsToUserConnectors.add(parentRowWidth
					- (motherFatherConnector.getMeasuredWidth() / 2));
			
			// Add as main connector from parent to user
			parentToUserMainConnector = parentRowWidth
					- (motherFatherConnector.getMeasuredWidth() / 2);
			
			if (user.getMothers().size() > 0) {
				int count = 0;
				for (Node tempMother : user.getMothers()) {
					CustomNode mother = new CustomNode(context);
					Log.d(TAG, "parsed mother ID - " + tempMother.getId()
							+ ", name - " + tempMother.getName());
					/*
					setNodeAttributes(
							mother,
							tempMother.getId(),
							tempMother.getName(),
							context.getResources()
									.getDrawable(
											R.drawable.parivar_mobile_profile_image_vsmall));
					*/
					
					setNodeAttributes(mother, tempMother);
					
					mother.measure(0, 0);
					Log.d(TAG, "Mother dimensions:" + mother.getMeasuredHeight() + ", " + mother.getMeasuredWidth());
					if (count > 0) {
						// if (true) {
						ImageView connector = new ImageView(context);
						
						connector.setImageDrawable(generateHorizontalConnector(
								mother.getMeasuredHeight(),
								mother.getMeasuredWidth()));
						/*
						connector.setImageDrawable(generateBothParentsConnecter(
								mother.getMeasuredHeight(), 
								mother.getMeasuredWidth()));
						*/
						connector.measure(0, 0);
						parentsRow.add(connector);

						// Add to the width of parent row
						parentRowWidth = parentRowWidth
								+ connector.getMeasuredWidth();
					}

					mother.setOnClickListener(this);
					parentsRow.add(mother);

					// Add to the width of parent row
					parentRowWidth = parentRowWidth + mother.getMeasuredWidth();

					Log.d(TAG,
							"mother added - for custom node - parent view:  - "
									+ mother.getParent() + ", childcount:"
									+ mother.getChildCount());
					count++;
				}
			} else if (user.getFathers().size() > 0) {
				// TODO if no mother present then add a dummy mother with
				// "Add Mother" name
				CustomNode mother = new CustomNode(context);
				// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
				// ", name - " + tempFather.getName());
				/*
				setNodeAttributes(
						mother,
						0,
						"Add Mother",
						context.getResources().getDrawable(
								R.drawable.parivar_mobile_profile_image_vsmall));
				*/
				
				setNodeAttributes(mother, 0, "Add", "Mother", "none", 2, 0, 0);
				
				mother.measure(0, 0);
				mother.setOnClickListener(this);
				parentsRow.add(mother);

				// Add to the width of parent row
				parentRowWidth = parentRowWidth + mother.getMeasuredWidth();
			}
			
			
			// TODO if the parent is required to be connected to the center
			parentsToUserConnectors.clear();
			// Add position of connectors from parent to user
			parentsToUserConnectors.add(parentRowWidth/2);
			
			// Add as main connector from parent to user
			parentToUserMainConnector = parentRowWidth / 2;
			
			View centerView = parentsRow.get((parentsRow.size()/2));
			if(!(centerView instanceof CustomNode)) {
				Log.i(TAG, "entered on even number of items");
				int tempIndex = (parentsRow.size()/2);
				View connectorView = parentsRow.get(tempIndex - 1);
				
				ImageView connector = new ImageView(context);
				connector.setImageDrawable(generateBothParentsConnecter(
						connectorView.getMeasuredHeight(), 
						connectorView.getMeasuredWidth()));
				connector.measure(0, 0);
				
				parentsRow.remove( ((parentsRow.size()/2)) );
				parentsRow.add( ((parentsRow.size()/2)) , connector);
			}
			
			
		}

	}
	
	public void generateUserRow() {

		
		if (user.getBrothers().size() > 0) {
			
			int count = 0;
			for (Node brother : user.getBrothers()) {

				LinearLayout tempBrother = new LinearLayout(context);
				tempBrother = (LinearLayout) getSiblingsTree(brother);
				
				Log.d(TAG, "Brother id:" + brother.getId() + " Brother Name:"
						+ brother.getName());
				
				tempBrother.measure(0, 0);
				Log.d(TAG, "brother: siblingMainConnector - " + siblingMainConnector);
				userRow.add(tempBrother);
				
				tempBrother.setOnClickListener(this);
				
				Log.d(TAG, "Brother dimensions:" + tempBrother.getMeasuredHeight() + ", " + tempBrother.getMeasuredWidth());
				
				ImageView nextImage = new ImageView(context);
				
				nextImage.setImageDrawable(generateBlankDrawable(
						tempBrother.getMeasuredHeight(),
						tempBrother.getMeasuredWidth()));
				
				Log.d(TAG, "Brother dimensions:" + tempBrother.getMeasuredHeight() + ", " + tempBrother.getMeasuredWidth());
				nextImage.measure(0, 0);
				userRow.add(nextImage);
				
				// Add to the userParent Connector width
				userParentConnectorWidth = userParentConnectorWidth
						+ tempBrother.getMeasuredWidth()
						+ nextImage.getMeasuredWidth();
				
				// Add user to parent connectors
				userToParentsConnectors.add(userParentConnectorWidth - (tempBrother.getMeasuredWidth() + nextImage.getMeasuredWidth()) + siblingMainConnector);
				
				count++;
			}
			
		}
		
		
		// TODO add user
		{
			LinearLayout currentUser = new LinearLayout(context);
			currentUser = (LinearLayout) getSiblingsTree(user);
			
			userRow.add(currentUser);
			
			currentUser.measure(0, 0);
			
			// Add to the userParent Connector width
			userParentConnectorWidth = userParentConnectorWidth
					+ currentUser.getMeasuredWidth();
			
			// Add user to parent connectors
			if(user.getGender() == 1) {
				Log.i(TAG, "Parent to user connector added at - " + userParentConnectorWidth
						+ (currentUser.getMeasuredWidth() / (2*connectorWidthFactor)));
				
				// if user is male then connector is on right side
				userToOffspringConnectors.add(userParentConnectorWidth
						+ (currentUser.getMeasuredWidth() / (2*connectorWidthFactor)));
				
				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = userParentConnectorWidth
						+ (currentUser.getMeasuredWidth() / (2*connectorWidthFactor));
			}
			else {
				//Log.e(TAG, "For current user log message is ")
				// if user is female then connector is on left side
				userToOffspringConnectors.add(userParentConnectorWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / (2*connectorWidthFactor)));
				
				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = userParentConnectorWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / (2*connectorWidthFactor));
			}
			//userToParentsConnectors.add(userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2) + siblingMainConnector);
			userToParentsConnectors.add(userParentConnectorWidth - (currentUser.getMeasuredWidth()) + siblingMainConnector);
			
			// Actual/Required connecting point for the parent connector
			parentToUserJoinPoint = userParentConnectorWidth
					- (currentUser.getMeasuredWidth() / (2*connectorWidthFactor));
			
		}
		
		if (user.getSisters().size() > 0) {
			
			int count = 0;
			for (Node sister : user.getSisters()) {
				
				LinearLayout tempSister = new LinearLayout(context);
				tempSister = (LinearLayout) getSiblingsTree(sister);
				Log.d(TAG, "Sister id:" + sister.getId() + " Sister Name:"
						+ sister.getName());
				
				tempSister.measure(0, 0);
				ImageView prevImage = new ImageView(context);
				prevImage.setImageDrawable(generateBlankDrawable(
						tempSister.getMeasuredHeight(),
						tempSister.getMeasuredWidth()));
				
				prevImage.measure(0, 0);
				userRow.add(prevImage);
				
				tempSister.setOnClickListener(this);
				userRow.add(tempSister);
				
				// Add to the userParent Connector width
				userParentConnectorWidth = userParentConnectorWidth
						+ prevImage.getMeasuredWidth()
						+ tempSister.getMeasuredWidth();
				
				// Add user to parent connectors
				//userToParentsConnectors.add(userParentConnectorWidth - (tempSister.getMeasuredWidth() / 2) + siblingMainConnector);
				userToParentsConnectors.add(userParentConnectorWidth - tempSister.getMeasuredWidth() + siblingMainConnector);
				
				count++;
			}
			
		}
		
		
		// TODO to add parent row to the center of the user row
		// Actual/Required connecting point for the parent connector
		parentToUserJoinPoint = userParentConnectorWidth / 2;
		

	}
	
	// TODO generate row layout
	public TableRow generateParentRowLayout() {
		TableRow tr = new TableRow(context);
		
		for (View item : parentsRow) {
			tr.addView(item);
		}
		
		return tr;
	}
	
	// TODO generate row layout
	public TableRow generateUserRowLayout() {
		TableRow tr = new TableRow(context);
		
		int count = 0;
		for (View item : userRow) {
			
			item.measure(0, 0);
			//Log.d(TAG, "View count : " + count + ", height: " + item.getMeasuredHeight() + ", width:" + item.getMeasuredWidth());
			
			tr.addView(item);
			
			Log.e(TAG, "View added: count - " + count + ", child width: " + tr.getChildAt(count).getMeasuredWidth());
			count++;
		}
		
		return tr;
	}
	
	// TODO generate drawables
	public BitmapDrawable generateBothParentsConnecter(int height, int width) {
		
		width = width/connectorWidthFactor;
		Bitmap connector = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		
		connectorCanvas.drawLine(0, height / 5, width, height / 5, paint);
		connectorCanvas.drawLine(width / 2, height, width / 2, height / 5, paint);
		
		Bitmap heart = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        Rect bitmapRect = new Rect(width/2 - (heart.getWidth()/2), height/5 - (heart.getHeight()/2), width/2 + (heart.getWidth()/2), height/5 + (heart.getHeight()/2));
        connectorCanvas.drawBitmap(heart, null, bitmapRect, new Paint());
		
		
		return new BitmapDrawable(context.getResources(), connector);
	}
	
	public BitmapDrawable generateHorizontalConnector(int height, int width) {

		width = width/connectorWidthFactor;
		Bitmap connector = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		
		connectorCanvas.drawLine(0, height / 5, connector.getWidth(),
				height / 5, paint);
		
		Bitmap heart = BitmapFactory.decodeResource(context.getResources(), R.drawable.heart);
        Rect bitmapRect = new Rect(width/2 - (heart.getWidth()/2), height/5 - (heart.getHeight()/2), width/2 + (heart.getWidth()/2), height/5 + (heart.getHeight()/2));
        connectorCanvas.drawBitmap(heart, null, bitmapRect, new Paint());
        
		return new BitmapDrawable(context.getResources(), connector);
	}
	
	public BitmapDrawable generateBlankDrawable(int height, int width) {
		
		width = width/connectorWidthFactor;
		Log.d(TAG, "height:" + height + ", width:" + width);
		Bitmap connector = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		
		return new BitmapDrawable(context.getResources(), connector);
	}
	
	public BitmapDrawable generateOffsetDrawable(int height, int width) {
		
		Log.d(TAG, "height:" + height + ", width:" + width);
		Bitmap connector = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		
		return new BitmapDrawable(context.getResources(), connector);
	}
	
	public BitmapDrawable generateGenericDrawable(int height, int width, int position) {
		
		Log.d(TAG, "height:" + height + ", width:" + width);
		Bitmap connector = Bitmap.createBitmap(width, height,
				Bitmap.Config.ARGB_8888);
		
		Canvas connectorCanvas = new Canvas(connector);
		
		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);
		
		connectorCanvas.drawLine(position, height/2, position, height, paint);
		
		return new BitmapDrawable(context.getResources(), connector);
	}
	
	// TODO generate level connectors
	public ImageView generateUserParentLevelConnector() {
		
		Bitmap connector = null;
		if (this.largestRow == 0) {
			int height = connectorHeight;
			connector = Bitmap.createBitmap(userParentConnectorWidth
					+ userOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userParentConnectorWidth - " + userParentConnectorWidth);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastUserRowPosition = userRow.size() - 1;
			int startOffset = userToParentsConnectors.get(0);	//userView.getMeasuredWidth() / 2;
			int endOffset = userParentConnectorWidth - userToParentsConnectors.get(userToParentsConnectors.size() - 1); //userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(userOffset + startOffset, height / 2,
					userOffset + userParentConnectorWidth - endOffset,
					height / 2, paint);

			for (int userConnector : userToParentsConnectors) {
				connectorCanvas.drawLine(userOffset + userConnector, height,
						userOffset + userConnector, height / 2, paint);
			}

			for (int parentConnector : parentsToUserConnectors) {
				connectorCanvas.drawLine(parentOffset + parentConnector,
						height / 2, parentOffset + parentConnector, 0, paint);
			}
		}
		if (this.largestRow == 1) {
			int height = connectorHeight;
			connector = Bitmap.createBitmap(userParentConnectorWidth
					+ userOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userParentConnectorWidth - " + userParentConnectorWidth);
			Log.d(TAG, "userOffset:" + userOffset);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastUserRowPosition = userRow.size() - 1;
			int startOffset = userToParentsConnectors.get(0);	//userView.getMeasuredWidth() / 2;
			int endOffset = userParentConnectorWidth - userToParentsConnectors.get(userToParentsConnectors.size() - 1); //userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(userOffset + startOffset, height / 2,
					userOffset + userParentConnectorWidth - endOffset,
					height / 2, paint);

			for (int userConnector : userToParentsConnectors) {
				connectorCanvas.drawLine(userOffset + userConnector, height,
						userOffset + userConnector, height / 2, paint);
				//Log.d(TAG, "")
			}

			for (int parentConnector : parentsToUserConnectors) {
				connectorCanvas.drawLine(parentOffset + parentConnector,
						height / 2, parentOffset + parentConnector, 0, paint);
			}
		}
		
		if (largestRow == 2) {
			int height = connectorHeight;
			connector = Bitmap.createBitmap(userParentConnectorWidth
					+ userOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userParentConnectorWidth - " + userParentConnectorWidth);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastUserRowPosition = userRow.size() - 1;
			int startOffset = userToParentsConnectors.get(0);
			int endOffset = userParentConnectorWidth - userToParentsConnectors.get(userToParentsConnectors.size() - 1); //userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(userOffset + startOffset, height / 2,
					userOffset + userParentConnectorWidth - endOffset,
					height / 2, paint);

			for (int userConnector : userToParentsConnectors) {
				connectorCanvas.drawLine(userOffset + userConnector, height,
						userOffset + userConnector, height / 2, paint);
			}

			for (int parentConnector : parentsToUserConnectors) {
				connectorCanvas.drawLine(parentOffset + parentConnector,
						height / 2, parentOffset + parentConnector, 0, paint);
			}
		}

		ImageView connectorView = new ImageView(context);
		connectorView.setImageBitmap(connector);
		return connectorView;
	}

	public ImageView generateUserOffSpringLevelConnector() {

		Bitmap connector = null;
		if (largestRow == 0) {
			int height = connectorHeight;
			connector = Bitmap.createBitmap(userOffSpringConnector
					+ offSpringOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastOffSpringPosition = offSpringRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(offSpringOffset + startOffset, height / 2,
					offSpringOffset + userOffSpringConnector - endOffset,
					height / 2, paint);

			for (int userConnector : offspringsToUserConnectors) {
				connectorCanvas.drawLine(offSpringOffset + userConnector,
						height, offSpringOffset + userConnector, height / 2,
						paint);
			}
			for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(userOffset + offSpringConnector,
						height / 2, userOffset + offSpringConnector, 0, paint);
			}
		}
		if (this.largestRow == 1) {
			int height = connectorHeight;
			connector = Bitmap.createBitmap(userOffSpringConnector
					+ offSpringOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);
			
			int lastOffSpringPosition = offSpringRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(offSpringOffset + startOffset, height / 2,
					offSpringOffset + userOffSpringConnector - endOffset,
					height / 2, paint);
			
			for (int userConnector : offspringsToUserConnectors) {
				connectorCanvas.drawLine(offSpringOffset + userConnector,
						height, offSpringOffset + userConnector, height / 2,
						paint);
			}
			for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(userOffset + offSpringConnector,
						height / 2, userOffset + offSpringConnector, 0, paint);
			}
		}
		if (largestRow == 2) {
			int height = connectorHeight;
			connector = Bitmap.createBitmap(userOffSpringConnector
					+ offSpringOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);
			
			int lastOffSpringPosition = offSpringRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(offSpringOffset + startOffset, height / 2,
					offSpringOffset + userOffSpringConnector - endOffset,
					height / 2, paint);
			
			for (int userConnector : offspringsToUserConnectors) {
				connectorCanvas.drawLine(offSpringOffset + userConnector,
						height, offSpringOffset + userConnector, height / 2,
						paint);
			}
			for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(userOffset + offSpringConnector,
						height / 2, userOffset + offSpringConnector, 0, paint);
			}
		}

		ImageView connectorView = new ImageView(context);
		connectorView.setImageBitmap(connector);
		// return new BitmapDrawable(context.getResources(), connector);
		return connectorView;
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v instanceof CustomNode) {
			
			//TextView nodeUserFirstName = (TextView) v.findViewById(R.id.userNodeFirstName);
			//TextView nodeUserLastName = (TextView) v.findViewById(R.id.userNodeLastName);
			
			//String nodeUserName = nodeUserFirstName.getText() + " " + nodeUserLastName.getText();
			
			CustomNode clickedNode = (CustomNode) v;
			String nodeUserName = clickedNode.getUserFirstName() + " " + clickedNode.getUserLastName();
			
			Log.e(TAG, "nodeUserName" + nodeUserName);
			if(nodeUserName.equals("Add Mother")) {
				// TODO go to add relation directly by setting relation id for mother
				sharedPreferencesEditor.putString("node_id", ""+user.getId());
				sharedPreferencesEditor.putString("node_first_name", ""+user.getFirstName());
				sharedPreferencesEditor.putString("node_last_name", ""+user.getLastName());
				sharedPreferencesEditor.commit();
				((MainActivity)fragment.getActivity()).createRelation("2", ""+user.getId());
			}
			else if(nodeUserName.equals("Add Father")) {
				// TODO go to add relation directly by setting relation id for mother
				sharedPreferencesEditor.putString("node_id", ""+user.getId());
				sharedPreferencesEditor.putString("node_first_name", ""+user.getFirstName());
				sharedPreferencesEditor.putString("node_last_name", ""+user.getLastName());
				sharedPreferencesEditor.commit();
				((MainActivity)fragment.getActivity()).createRelation("1", ""+user.getId());
			}
			else if(nodeUserName.equals("Add Wife")) {
				// TODO go to add relation directly by setting relation id for mother
				sharedPreferencesEditor.putString("node_id", ""+user.getId());
				sharedPreferencesEditor.putString("node_first_name", ""+user.getFirstName());
				sharedPreferencesEditor.putString("node_last_name", ""+user.getLastName());
				sharedPreferencesEditor.commit();
				((MainActivity)fragment.getActivity()).createRelation("3", ""+user.getId());
			}
			else if(nodeUserName.equals("Add Husband")) {
				// TODO go to add relation directly by setting relation id for mother
				sharedPreferencesEditor.putString("node_id", ""+user.getId());
				sharedPreferencesEditor.putString("node_first_name", ""+user.getFirstName());
				sharedPreferencesEditor.putString("node_last_name", ""+user.getLastName());
				sharedPreferencesEditor.commit();
				((MainActivity)fragment.getActivity()).createRelation("3", ""+user.getId());
			}
			else {
				String nodeid = "" + clickedNode.getUserId();
			    String gender="" +clickedNode.getGender();
				Log.d(TAG, nodeid);
				
				// TODO implement an interface to communicate with HomeFragment
				sharedPreferencesEditor.putString("node_first_name", ""+clickedNode.getUserFirstName());
				sharedPreferencesEditor.putString("node_last_name", ""+clickedNode.getUserLastName());
				sharedPreferencesEditor.commit();
				
				Log.d(TAG, "node name: firstname - " +clickedNode.getUserFirstName()+ ", lastname - " + clickedNode.getUserLastName());
				((HomeFragment)this.fragment).showOptionsLayout(nodeid,gender);
			}
		}
	}
	
	public View generateSiblingsTree(Node sibling, TableLayout tl) {
		
		int mainConnector = 0;
		//ArrayList<View> siblingTree = new ArrayList<View>();
		ArrayList<View> siblingParentRow = new ArrayList<View>();
		ArrayList<View> siblingOffspringsRow = new ArrayList<View>();
		
		int siblingTreeLargestRow = 0;
		int siblingTreeLargestRowSize = 0;
		
		// Connectors
		int topLevelConnector = 0;
		ArrayList<Integer> siblingParentToOffspringConnector = new ArrayList<Integer>();
		ArrayList<Integer> offspringToSiblingParentConnector = new ArrayList<Integer>();
		
		ViewGroup vg; 
		
		String TAG = "SiblingsTree";
		
		int siblingTreeWidth = 0;
		int siblingParentWidth = 0;
		int siblingOffspringWidth = 0;
		
		Log.d(TAG, "id:" + sibling.getId() + ", wives:" + sibling.getWives().size());
		
		//LinearLayout tl = new LinearLayout(context);
		tl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TableRow trSiblingParent = new TableRow(context);
		TableRow trSiblingOffspring = new TableRow(context);
		//trSiblingParent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//ViewGroup siblingParentView = null;
		//siblingParentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		if(sibling.getHusbands().size() > 0) {
			// TODO add husbands
			if (sibling.getHusbands().size() > 0) {
				int count = 0;
				for (Node husband : sibling.getHusbands()) {
					
					CustomNode tempHusband = new CustomNode(context);
					
					Log.d(TAG, "husband id:" + husband.getId() + " husband Name:"
							+ husband.getName());
					
					setNodeAttributes(tempHusband, husband);
					
					tempHusband.measure(0, 0);
					
					tempHusband.setOnClickListener(this);
					siblingParentRow.add(tempHusband);
					
					ImageView nextImage = new ImageView(context);
					if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
						nextImage.setImageDrawable(generateBothParentsConnecter(
										tempHusband.getMeasuredHeight(),
										tempHusband.getMeasuredWidth()));
					} else {
						nextImage.setImageDrawable(generateHorizontalConnector(
								tempHusband.getMeasuredHeight(),
								tempHusband.getMeasuredWidth()));
					}
					
					nextImage.measure(0, 0);
					siblingParentRow.add(nextImage);
					
					
					siblingParentWidth = siblingParentWidth + tempHusband.getMeasuredWidth() + nextImage.getMeasuredWidth();
					/*
					if (sibling.getBrothers().size() > 0) {
						// Add to the userParent Connector width
						userParentConnectorWidth = userParentConnectorWidth
								+ tempHusband.getMeasuredWidth()
								+ nextImage.getMeasuredWidth();
					}
					*/
					count++;
				}
			} else if ((sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) && sibling.getGender() == 2) {
				// TODO add dummy husband
				
				CustomNode husband = new CustomNode(context);
				// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
				// ", name - " + tempFather.getName());
				
				setNodeAttributes(husband, 0, "Add", "Husband", "none", 1, 0, 0);
				
				//husband.measure(0, 0);
				//parentsRow.add(husband);
				
				husband.measure(0, 0);
				
				husband.setOnClickListener(this);
				siblingParentRow.add(husband);
				// if(count < user.getHusbands().size() - 1) {
				ImageView nextImage = new ImageView(context);
				if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
					nextImage
							.setImageDrawable(this.generateBothParentsConnecter(
									husband.getMeasuredHeight(),
									husband.getMeasuredWidth()));
				} else {
					nextImage
							.setImageDrawable(generateHorizontalConnector(
									husband.getMeasuredHeight(),
									husband.getMeasuredWidth()));
				}
				
				nextImage.measure(0, 0);
				siblingParentRow.add(nextImage);
				// }
				
				//siblingTreeWidth = siblingTreeWidth + husband.getMeasuredWidth() + nextImage.getMeasuredWidth();
				siblingParentWidth = siblingParentWidth + husband.getMeasuredWidth() + nextImage.getMeasuredWidth();
				/*
				if (sibling.getBrothers().size() > 0) {
					// Add to the userParent Connector width
					userParentConnectorWidth = userParentConnectorWidth
							+ husband.getMeasuredWidth()
							+ nextImage.getMeasuredWidth();
				}
				*/
			}
		}
		
		// TODO add user
		{
			CustomNode currentUser = new CustomNode(context);
			
			setNodeAttributes(currentUser, sibling);
			
			currentUser.setOnClickListener(this);
			//currentUser.setBackgroundColor(context.getResources().getColor(R.color.pt_user));
			currentUser.setBackgroundResource(R.drawable.background_user_node);
			siblingParentRow.add(currentUser);
			
			currentUser.measure(0, 0);
			
			siblingParentWidth = siblingParentWidth + currentUser.getMeasuredWidth();
			
			// Add the view to be accesssed throughout
			//userView = currentUser;
			
			
			/*
			// Add to the userParent Connector width
			userParentConnectorWidth = userParentConnectorWidth
					+ currentUser.getMeasuredWidth();
			*/
			// Add user to parent connectors
			if(sibling.getGender() == 1) {
				Log.i(TAG, "Parent to user connector added at - " + userParentConnectorWidth
						+ (currentUser.getMeasuredWidth() / 2));
				// if user is male then connector is on right side
				siblingParentToOffspringConnector.add(siblingParentWidth
						+ (currentUser.getMeasuredWidth() / 2));
				
				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = siblingParentWidth
						+ (currentUser.getMeasuredWidth() / 2);
			}
			else {
				// if user is female then connector is on left side
				userToOffspringConnectors.add(siblingParentWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / 2));
				
				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = siblingParentWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / 2);
			}
			userToParentsConnectors.add(userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2));
			
			// Actual/Required connecting point for the parent connector
			//topLevelConnector.add(siblingParentWidth - currentUser.getMeasuredWidth()/2);
			topLevelConnector = siblingParentWidth - (currentUser.getMeasuredWidth()/2);
			//parentToUserJoinPoint = userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2);
			
			
			
		}
		
		if(sibling.getWives().size()>0) {
			// TODO add wives
			int count = 0;
			for (Node wife : sibling.getWives()) {
				
				CustomNode tempWife = new CustomNode(context);
				
				Log.d(TAG,
						"wife id:" + wife.getId() + " wife Name:"
								+ wife.getName());
				
				setNodeAttributes(tempWife, wife);
				
				tempWife.measure(0, 0);
				
				// if(count < user.getHusbands().size() - 1) {
				ImageView prevImage = new ImageView(context);
				if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
					prevImage.setImageDrawable(this
							.generateBothParentsConnecter(
									tempWife.getMeasuredHeight(),
									tempWife.getMeasuredWidth()));
				} else {
					prevImage.setImageDrawable(generateHorizontalConnector(
							tempWife.getMeasuredHeight(),
							tempWife.getMeasuredWidth()));
				}
				
				prevImage.measure(0, 0);
				siblingParentRow.add(prevImage);
				// }
				tempWife.setOnClickListener(this);
				siblingParentRow.add(tempWife);
				
				siblingParentWidth = siblingParentWidth 
						+ prevImage.getMeasuredWidth()
						+ tempWife.getMeasuredWidth();
				/*
				if (sibling.getSisters().size() > 0) {
					// Add to the userParent Connector
					userParentConnectorWidth = userParentConnectorWidth
							+ prevImage.getMeasuredWidth()
							+ tempWife.getMeasuredWidth();
				}
				*/
				count++;
			}
		} else if ((sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0)
				&& sibling.getGender() == 1) {
			// TODO add dummy wife
			CustomNode wife = new CustomNode(context);
			// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
			// ", name - " + tempFather.getName());
			
			setNodeAttributes(wife, 0, "Add", "Wife", "none", 2, 0, 0);
			
			wife.measure(0, 0);
			
			wife.setOnClickListener(this);
			//parentsRow.add(wife);
			
			wife.measure(0, 0);
			
			// if(count < user.getHusbands().size() - 1) {
			ImageView prevImage = new ImageView(context);
			if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
				prevImage.setImageDrawable(this.generateBothParentsConnecter(
						wife.getMeasuredHeight(), wife.getMeasuredWidth()));
			} else {
				prevImage.setImageDrawable(generateHorizontalConnector(
						wife.getMeasuredHeight(), wife.getMeasuredWidth()));
			}
			
			prevImage.measure(0, 0);
			siblingParentRow.add(prevImage);
			// }

			siblingParentRow.add(wife);
			/*
			if (sibling.getSisters().size() > 0) {
				// Add to the userParent Connector
				userParentConnectorWidth = userParentConnectorWidth
						+ prevImage.getMeasuredWidth()
						+ wife.getMeasuredWidth();
			}
			*/
		}
		
		
		if (sibling.getSons().size() > 0) {
			
			int count = 0;
			for (Node son : sibling.getSons()) {
				
				CustomNode tempSon = new CustomNode(context);
				
				Log.d(TAG,
						"Son id:" + son.getId() + " Son Name:" + son.getName());
				
				setNodeAttributes(tempSon, son);
				
				tempSon.measure(0, 0);
				tempSon.setOnClickListener(this);
				siblingOffspringsRow.add(tempSon);
				
				// Add to the userOffSpring Connector width
				siblingOffspringWidth = siblingOffspringWidth
						+ tempSon.getMeasuredWidth();
				Log.d(TAG, "off Srping - son: tempSon - siblingOffspringWidth"
						+ siblingOffspringWidth);
				
				// Add offSpring to user connectors
				offspringToSiblingParentConnector.add(siblingOffspringWidth
						- (tempSon.getMeasuredWidth() / 2));
				
				if (count < sibling.getSons().size() - 1) {
					ImageView nextImage = new ImageView(context);
					nextImage.setImageDrawable(generateBlankDrawable(
							tempSon.getMeasuredHeight(),
							tempSon.getMeasuredWidth()));
					
					nextImage.measure(0, 0);
					siblingOffspringsRow.add(nextImage);
					
					// Add to the userParent Connector
					siblingOffspringWidth = siblingOffspringWidth
							+ nextImage.getMeasuredWidth();
					Log.d(TAG,
							"off Srping - son: nextImage - siblingOffspringWidth"
									+ siblingOffspringWidth);
				}
				
				count++;
			}
		}
		
		if (siblingOffspringsRow.size() != 0 && sibling.getSons().size() > 0 && sibling.getDaughters().size() > 0) {
			ImageView nextImage = new ImageView(context);
			nextImage.setImageDrawable(generateBlankDrawable(siblingOffspringsRow
					.get(0).getMeasuredHeight(), siblingOffspringsRow.get(0)
					.getMeasuredWidth()));
			
			nextImage.measure(0, 0);
			siblingOffspringsRow.add(nextImage);
			
			// Add to the userParent Connector
			siblingOffspringWidth = siblingOffspringWidth
					+ nextImage.getMeasuredWidth();
			Log.d(TAG,
					"off Srping - middle: nextImage - userOffSpringConnector"
							+ userOffSpringConnector);
			
		}
		
		if (sibling.getDaughters().size() > 0) {
			int count = 0;
			for (Node daughter : sibling.getDaughters()) {
				
				CustomNode tempDaughter = new CustomNode(context);
				
				Log.d(TAG, "daughter id:" + daughter.getId()
						+ " daughter Name:" + daughter.getName());
				
				setNodeAttributes(tempDaughter, daughter);
				
				tempDaughter.measure(0, 0);
				
				if (count > 0) {
					ImageView prevImage = new ImageView(context);
					prevImage.setImageDrawable(generateBlankDrawable(
							tempDaughter.getMeasuredHeight(),
							tempDaughter.getMeasuredWidth()));
					
					prevImage.measure(0, 0);
					siblingOffspringsRow.add(prevImage);
					
					// Add to the userParent Connector width
					siblingOffspringWidth = siblingOffspringWidth
							+ prevImage.getMeasuredWidth();
					Log.d(TAG,
							"off Srping - daughter: prevImage - siblingOffspringWidth"
									+ siblingOffspringWidth);
				}
				
				tempDaughter.setOnClickListener(this);
				siblingOffspringsRow.add(tempDaughter);
				
				
				// Add to the userParent Connector width
				siblingOffspringWidth = siblingOffspringWidth
						+ tempDaughter.getMeasuredWidth();
				
				Log.d(TAG,
						"off Srping - daughter: tempDaughter - siblingOffspringWidth"
								+ siblingOffspringWidth);
				
				// Add offSpring to user connectors
				offspringToSiblingParentConnector.add(siblingOffspringWidth
						- (tempDaughter.getMeasuredWidth() / 2));
				
				count++;
			}
		}
		
		// TODO calculate adjustments
		{
			// Calculate largest row
			if (siblingParentRow.size() > siblingTreeLargestRowSize) {
				siblingTreeLargestRow = 0;
				siblingTreeLargestRowSize = siblingOffspringsRow.size();
			}
			if (siblingOffspringsRow.size() > siblingTreeLargestRowSize) {
				siblingTreeLargestRow = 1;
				siblingTreeLargestRowSize = siblingOffspringsRow.size();
			}
			
			/*
			if (siblingTreeLargestRow == 0) {
				userOffset = Math.abs(parentToUserMainConnector - parentToUserJoinPoint);
				if(siblingParentRow.size() > 0 && userOffset > 0) {
					ImageView userOffsetImage = new ImageView(context);
					userOffsetImage.setImageDrawable(generateBlankDrawable(
							userView.getMeasuredHeight(), userOffset));
					siblingParentRow.add(0, userOffsetImage);
				}
				
				offSpringOffset = Math.abs(userToOffSpringJoinPoint
						- userToOffSpringMainConnector) + userOffset;
				Log.i(TAG, "userToOffSpringJoinPoint:" + userToOffSpringJoinPoint +", userToOffSpringMainConnector" + userToOffSpringMainConnector + ", userOffset" + userOffset + ", offSpringOffset" + offSpringOffset);
				if(offSpringRow.size() > 0 && offSpringOffset > 0) {
					ImageView offSpringOffsetImage = new ImageView(context);
					offSpringOffsetImage.setImageDrawable(generateBlankDrawable(
							userView.getMeasuredHeight(), offSpringOffset));
					offSpringRow.add(0, offSpringOffsetImage);
				}
				
			}
			*/
		}
		
		
		int siblingParentCount = 0;
		for(View tempView: siblingParentRow) {
			trSiblingParent.addView(tempView);
			Log.d(TAG, "siblingParentCount - " + siblingParentCount);
			siblingParentCount++;
		}
		
		// TODO generate sibling Parent to Offspring connector
		{
			
		}
		
		int siblingOffspringCount = 0;
		for(View tempView: siblingOffspringsRow) {
			trSiblingOffspring.addView(tempView);
			Log.d(TAG, "siblingOffspringCount - " + siblingOffspringCount);
			siblingOffspringCount++;
		}
		
		tl.addView(trSiblingParent);
		tl.addView(trSiblingOffspring);
		
		return tl;
		
		/*
		ImageView returnImage = new ImageView(context);
		returnImage.setImageDrawable(this.generateBothParentsConnecter(tl.getMeasuredHeight(), tl.getMeasuredWidth()));
		return returnImage;
		*/
	}
	
	public View getSiblingsTree(Node sibling) {
		
		
		int mainConnector = 0;
		siblingMainConnector = 0;
		//ArrayList<View> siblingTree = new ArrayList<View>();
		ArrayList<View> siblingParentRow = new ArrayList<View>();
		ArrayList<View> siblingOffspringsRow = new ArrayList<View>();
		
		int siblingTreeLargestRow = 0;
		int siblingTreeLargestRowSize = 0;
		
		// connector positions
		int siblingOffspringToSiblingParentJoinPoint = 0;
		
		// offsets
		int siblingOffset = 0;
		int siblingParentOffset = 0;
		
		// Connectors
		//int topLevelConnector = 0;
		ArrayList<Integer> siblingParentToOffspringConnector = new ArrayList<Integer>();
		ArrayList<Integer> offspringToSiblingParentConnector = new ArrayList<Integer>();
		
		ViewGroup vg;
		
		String TAG = "SiblingsTree";
		
		int siblingTreeWidth = 0;
		int siblingParentWidth = 0;
		int siblingOffspringWidth = 0;
		
		Log.d(TAG, "id:" + sibling.getId() + ", wives:" + sibling.getWives().size());
		
		LinearLayout ll = new LinearLayout(context);
		
		TableLayout tl = new TableLayout(context);
		tl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		TableRow trSiblingParent = new TableRow(context);
		TableRow trSiblingOffspring = new TableRow(context);
		//trSiblingParent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//ViewGroup siblingParentView = null;
		//siblingParentView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		if(sibling.getHusbands().size() > 0) {
			// TODO add husbands
			if (sibling.getHusbands().size() > 0) {
				int count = 0;
				for (Node husband : sibling.getHusbands()) {
					
					CustomNode tempHusband = new CustomNode(context);
					
					Log.d(TAG, "husband id:" + husband.getId() + " husband Name:"
							+ husband.getName());
					
					setNodeAttributes(tempHusband, husband);
					
					tempHusband.measure(0, 0);
					
					tempHusband.setOnClickListener(this);
					siblingParentRow.add(tempHusband);
					
					ImageView nextImage = new ImageView(context);
					if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
						nextImage.setImageDrawable(generateBothParentsConnecter(
										tempHusband.getMeasuredHeight(),
										tempHusband.getMeasuredWidth()));
					} else {
						nextImage.setImageDrawable(generateHorizontalConnector(
								tempHusband.getMeasuredHeight(),
								tempHusband.getMeasuredWidth()));
					}
					
					nextImage.measure(0, 0);
					siblingParentRow.add(nextImage);
					
					siblingParentWidth = siblingParentWidth + tempHusband.getMeasuredWidth() + nextImage.getMeasuredWidth();
					
					count++;
				}
			
			} 
		}	else if ((sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) && sibling.getGender() == 2) {
			// TODO add dummy husband
			
			CustomNode husband = new CustomNode(context);
			// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
			// ", name - " + tempFather.getName());
			
			setNodeAttributes(husband, 0, "Add", "Husband", "none", 1, 0, 0);
			
			//husband.measure(0, 0);
			//parentsRow.add(husband);
			
			husband.measure(0, 0);
			
			husband.setOnClickListener(this);
			siblingParentRow.add(husband);
			// if(count < user.getHusbands().size() - 1) {
			ImageView nextImage = new ImageView(context);
			if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
				nextImage
						.setImageDrawable(this.generateBothParentsConnecter(
								husband.getMeasuredHeight(),
								husband.getMeasuredWidth()));
			} else {
				nextImage
						.setImageDrawable(generateHorizontalConnector(
								husband.getMeasuredHeight(),
								husband.getMeasuredWidth()));
			}
			
			nextImage.measure(0, 0);
			siblingParentRow.add(nextImage);
			
			//siblingTreeWidth = siblingTreeWidth + husband.getMeasuredWidth() + nextImage.getMeasuredWidth();
			siblingParentWidth = siblingParentWidth + husband.getMeasuredWidth() + nextImage.getMeasuredWidth();
		}
		
		// TODO add user
		{
			CustomNode currentUser = new CustomNode(context);
			
			setNodeAttributes(currentUser, sibling);
			
			currentUser.setOnClickListener(this);
			//Log.d(TAG, "mainUserId:" + mainUserId + ", currentUser.id:" + sibling.getId());
			if(this.mainUserId == sibling.getId()) {
				//currentUser.setBackgroundColor(context.getResources().getColor(R.color.pt_user));
				currentUser.setBackgroundResource(R.drawable.background_user_node);
			}
			siblingParentRow.add(currentUser);
			
			currentUser.measure(0, 0);
			
			siblingParentWidth = siblingParentWidth + currentUser.getMeasuredWidth();
			
			mainConnector = siblingParentWidth
					- (currentUser.getMeasuredWidth() / 2);
			// Add the view to be accesssed throughout
			//userView = currentUser;
			
			/*
			// Add to the userParent Connector width
			userParentConnectorWidth = userParentConnectorWidth
					+ currentUser.getMeasuredWidth();
			*/
			// Add user to parent connectors
			if(sibling.getGender() == 1) {
				Log.i(TAG, "Parent to user connector added at - " + userParentConnectorWidth
						+ (currentUser.getMeasuredWidth() / 2));
				/*
				// if user is male then connector is on right side
				siblingParentToOffspringConnector.add(siblingParentWidth
						+ (currentUser.getMeasuredWidth() / 2));
				*/
				
				/*
				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = siblingParentWidth
						+ (currentUser.getMeasuredWidth() / 2);
				*/
			}
			else {
				/*
				// if user is female then connector is on left side
				userToOffspringConnectors.add(siblingParentWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / 2));
				*/
				
				/*
				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = siblingParentWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / 2);
				*/
			}
			
			
			//userToParentsConnectors.add(userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2));
			
			// Actual/Required connecting point for the parent connector
			//topLevelConnector.add(siblingParentWidth - currentUser.getMeasuredWidth()/2);
			//topLevelConnector = siblingParentWidth - (currentUser.getMeasuredWidth()/2);
			//parentToUserJoinPoint = userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2);
			
		}
		
		if(sibling.getWives().size()>0) {
			// TODO add wives
			int count = 0;
			for (Node wife : sibling.getWives()) {
				
				CustomNode tempWife = new CustomNode(context);
				
				Log.d(TAG,
						"wife id:" + wife.getId() + " wife Name:"
								+ wife.getName());
				
				setNodeAttributes(tempWife, wife);
				
				tempWife.measure(0, 0);
				
				// if(count < user.getHusbands().size() - 1) {
				ImageView prevImage = new ImageView(context);
				if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
					prevImage.setImageDrawable(this
							.generateBothParentsConnecter(
									tempWife.getMeasuredHeight(),
									tempWife.getMeasuredWidth()));
				} else {
					prevImage.setImageDrawable(generateHorizontalConnector(
							tempWife.getMeasuredHeight(),
							tempWife.getMeasuredWidth()));
				}
				
				prevImage.measure(0, 0);
				siblingParentRow.add(prevImage);
				// }
				tempWife.setOnClickListener(this);
				siblingParentRow.add(tempWife);
				
				siblingParentWidth = siblingParentWidth 
						+ prevImage.getMeasuredWidth()
						+ tempWife.getMeasuredWidth();
				/*
				if (sibling.getSisters().size() > 0) {
					// Add to the userParent Connector
					userParentConnectorWidth = userParentConnectorWidth
							+ prevImage.getMeasuredWidth()
							+ tempWife.getMeasuredWidth();
				}
				*/
				count++;
			}
		} else if ((sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0)
				&& sibling.getGender() == 1) {
			// TODO add dummy wife
			CustomNode wife = new CustomNode(context);
			// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
			// ", name - " + tempFather.getName());
			
			setNodeAttributes(wife, 0, "Add", "Wife", "none", 2, 0, 0);
			
			wife.measure(0, 0);
			
			wife.setOnClickListener(this);
			//parentsRow.add(wife);
			
			wife.measure(0, 0);
			
			// if(count < user.getHusbands().size() - 1) {
			ImageView prevImage = new ImageView(context);
			if (sibling.getSons().size() > 0 || sibling.getDaughters().size() > 0) {
				prevImage.setImageDrawable(this.generateBothParentsConnecter(
						wife.getMeasuredHeight(), wife.getMeasuredWidth()));
			} else {
				prevImage.setImageDrawable(generateHorizontalConnector(
						wife.getMeasuredHeight(), wife.getMeasuredWidth()));
			}
			
			prevImage.measure(0, 0);
			siblingParentRow.add(prevImage);
			
			siblingParentRow.add(wife);
			
			siblingParentWidth = siblingParentWidth 
					+ prevImage.getMeasuredWidth()
					+ wife.getMeasuredWidth();
			/*
			if (sibling.getSisters().size() > 0) {
				// Add to the userParent Connector
				userParentConnectorWidth = userParentConnectorWidth
						+ prevImage.getMeasuredWidth()
						+ wife.getMeasuredWidth();
			}
			*/
		}
		
		// TODO add sibling parent to sibling Offspring join point
		int siblingParentToSiblingOffspringMainConnector = siblingParentWidth/2;
		
		if (sibling.getSons().size() > 0) {
			
			int count = 0;
			for (Node son : sibling.getSons()) {
				
				CustomNode tempSon = new CustomNode(context);
				
				Log.d(TAG,
						"Son id:" + son.getId() + " Son Name:" + son.getName());
				
				setNodeAttributes(tempSon, son);
				
				tempSon.measure(0, 0);
				tempSon.setOnClickListener(this);
				siblingOffspringsRow.add(tempSon);
				
				// Add to the userOffSpring Connector width
				siblingOffspringWidth = siblingOffspringWidth
						+ tempSon.getMeasuredWidth();
				Log.d(TAG, "off Srping - son: tempSon - siblingOffspringWidth"
						+ siblingOffspringWidth);
				
				// Add offSpring to user connectors
				offspringToSiblingParentConnector.add(siblingOffspringWidth
						- (tempSon.getMeasuredWidth() / 2));
				
				if (count < sibling.getSons().size() - 1) {
					ImageView nextImage = new ImageView(context);
					nextImage.setImageDrawable(generateBlankDrawable(
							tempSon.getMeasuredHeight(),
							tempSon.getMeasuredWidth()));
					
					nextImage.measure(0, 0);
					siblingOffspringsRow.add(nextImage);
					
					// Add to the userParent Connector
					siblingOffspringWidth = siblingOffspringWidth
							+ nextImage.getMeasuredWidth();
					Log.d(TAG,
							"off Srping - son: nextImage - siblingOffspringWidth"
									+ siblingOffspringWidth);
				}
				
				count++;
			}
		}
		
		if (siblingOffspringsRow.size() != 0 && sibling.getSons().size() > 0 && sibling.getDaughters().size() > 0) {
			ImageView nextImage = new ImageView(context);
			nextImage.setImageDrawable(generateBlankDrawable(siblingOffspringsRow
					.get(0).getMeasuredHeight(), siblingOffspringsRow.get(0)
					.getMeasuredWidth()));
			
			nextImage.measure(0, 0);
			siblingOffspringsRow.add(nextImage);
			
			// Add to the userParent Connector
			siblingOffspringWidth = siblingOffspringWidth
					+ nextImage.getMeasuredWidth();
			Log.d(TAG,
					"off Srping - middle: nextImage - userOffSpringConnector"
							+ userOffSpringConnector);
			
		}
		
		if (sibling.getDaughters().size() > 0) {
			int count = 0;
			for (Node daughter : sibling.getDaughters()) {
				
				CustomNode tempDaughter = new CustomNode(context);
				
				Log.d(TAG, "daughter id:" + daughter.getId()
						+ " daughter Name:" + daughter.getName());
				
				setNodeAttributes(tempDaughter, daughter);
				
				tempDaughter.measure(0, 0);
				
				if (count > 0) {
					ImageView prevImage = new ImageView(context);
					prevImage.setImageDrawable(generateBlankDrawable(
							tempDaughter.getMeasuredHeight(),
							tempDaughter.getMeasuredWidth()));
					
					prevImage.measure(0, 0);
					siblingOffspringsRow.add(prevImage);
					
					// Add to the userParent Connector width
					siblingOffspringWidth = siblingOffspringWidth
							+ prevImage.getMeasuredWidth();
					Log.d(TAG,
							"off Srping - daughter: prevImage - siblingOffspringWidth"
									+ siblingOffspringWidth);
				}
				
				tempDaughter.setOnClickListener(this);
				siblingOffspringsRow.add(tempDaughter);
				
				// Add to the userParent Connector width
				siblingOffspringWidth = siblingOffspringWidth
						+ tempDaughter.getMeasuredWidth();
				
				Log.d(TAG,
						"off Srping - daughter: tempDaughter - siblingOffspringWidth"
								+ siblingOffspringWidth);
				
				// Add offSpring to user connectors
				offspringToSiblingParentConnector.add(siblingOffspringWidth
						- (tempDaughter.getMeasuredWidth() / 2));
				
				count++;
			}
		}
		
		// TODO calculate and add sibling offSpring to sibling parent joint point
		siblingOffspringToSiblingParentJoinPoint = siblingOffspringWidth/2;
		
		// TODO calculate adjustments
		{
			// Calculate largest row
			if (siblingParentRow.size() > siblingTreeLargestRowSize) {
				siblingTreeLargestRow = 0;
				siblingTreeLargestRowSize = siblingParentRow.size();
			}
			if (siblingOffspringsRow.size() > siblingTreeLargestRowSize) {
				siblingTreeLargestRow = 1;
				siblingTreeLargestRowSize = siblingOffspringsRow.size();
			}
			
			if (siblingTreeLargestRow == 0) {
				siblingOffset = Math.abs(siblingParentToSiblingOffspringMainConnector - siblingOffspringToSiblingParentJoinPoint);
				Log.d(TAG, "offset calculated from - " + siblingParentToSiblingOffspringMainConnector + ", " + siblingOffspringToSiblingParentJoinPoint);
				if(siblingOffspringsRow.size() > 0 && siblingOffset > 0) {
					Log.d(TAG, "Size of offset - " + siblingOffset);
					ImageView siblingOffspringOffsetImage = new ImageView(context);
					siblingOffspringOffsetImage.setImageDrawable(generateOffsetDrawable(
							userView.getMeasuredHeight(), siblingOffset));
					siblingOffspringOffsetImage.measure(0, 0);
					Log.d(TAG, "offset size = " + siblingOffspringOffsetImage.getMeasuredWidth());
					siblingOffspringsRow.add(0, siblingOffspringOffsetImage);
				}
				Log.d(TAG, "mainConnector: " + mainConnector + " siblingParentWidth:" + siblingParentWidth/2);
				siblingMainConnector = mainConnector;// - siblingParentWidth/2 + siblingOffset;
				//siblingMainConnector = mainConnector + siblingOffset;
			}
			
			if (siblingTreeLargestRow == 1) {
				siblingParentOffset = Math.abs(siblingParentToSiblingOffspringMainConnector - siblingOffspringToSiblingParentJoinPoint);
				Log.d(TAG, "offset calculated from - " + siblingParentToSiblingOffspringMainConnector + ", " + siblingOffspringToSiblingParentJoinPoint + ": siblingOffspringWidth - " + siblingOffspringWidth);
				if(siblingParentRow.size() > 0 && siblingParentOffset > 0) {
					Log.d(TAG, "Size of offset - " + siblingParentOffset);
					ImageView siblingParentOffsetImage = new ImageView(context);
					siblingParentOffsetImage.setImageDrawable(generateOffsetDrawable(
							userView.getMeasuredHeight(), siblingParentOffset));
					siblingParentRow.add(0, siblingParentOffsetImage);
				}
				Log.d(TAG, "mainConnector:" + mainConnector + " siblingOffspringWidth:" + siblingOffspringWidth/2);
				siblingMainConnector = mainConnector + siblingParentOffset;
				//siblingMainConnector = mainConnector;// + siblingParentOffset;
			}
		}
		
		
		TableLayout tlSiblingParent = new TableLayout(context);
		tlSiblingParent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		
		int siblingParentCount = 0;
		for(View tempView: siblingParentRow) {
			trSiblingParent.addView(tempView);
			Log.d(TAG, "siblingParentCount - " + siblingParentCount);
			siblingParentCount++;
		}
		
		tlSiblingParent.addView(trSiblingParent);
		
		
		TableLayout tlOffspringSiblingParentConnector = new TableLayout(context);
		tlOffspringSiblingParentConnector.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		//TableRow trtemp = new TableRow(context);
		//trtemp.addView(generateUserParentLevelConnector());
		
		
		TableRow troffspringToSiblingRow = new TableRow(context);
		// TODO generate sibling Parent to Offspring connector
		if(siblingOffspringsRow.size() > 0) {
			
			Bitmap connector;
			int height = connectorHeight;
			connector = Bitmap.createBitmap(siblingOffspringWidth
					+ siblingOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			//Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);
			
			int lastOffSpringPosition = siblingOffspringsRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(siblingOffset + startOffset, height / 2,
					siblingOffset + siblingOffspringWidth - endOffset,
					height / 2, paint);
			
			//for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(siblingParentOffset + siblingParentToSiblingOffspringMainConnector,
						height / 2, siblingParentOffset + siblingParentToSiblingOffspringMainConnector, 0, paint);
			//}
			for (int siblingParentConnector : offspringToSiblingParentConnector) {
				connectorCanvas.drawLine(siblingOffset + siblingParentConnector,
						height, siblingOffset + siblingParentConnector, height / 2,
						paint);
			}
			
			ImageView connectorImage = new ImageView(context);
			connectorImage.setImageBitmap(connector);
			troffspringToSiblingRow.addView(connectorImage);
			
			
			
			Log.v(TAG, "userParent level connector added");
			
			
			//tltemp.addView(trtemp);
			
			//tl.addView(tltemp);
		}
		
		tlOffspringSiblingParentConnector.addView(troffspringToSiblingRow);
		
		
		TableLayout tlSiblingOffspring = new TableLayout(context);
		tlSiblingOffspring.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		
		int siblingOffspringCount = 0;
		for(View tempView: siblingOffspringsRow) {
			trSiblingOffspring.addView(tempView);
			Log.d(TAG, "siblingOffspringCount - " + siblingOffspringCount);
			siblingOffspringCount++;
		}
		tlSiblingOffspring.addView(trSiblingOffspring);
		
		tl.addView(tlSiblingParent);
		tl.addView(tlOffspringSiblingParentConnector);
		tl.addView(tlSiblingOffspring);
		
		ll.addView(tl);
		
		return ll;
		
		/*
		ImageView returnImage = new ImageView(context);
		returnImage.setImageDrawable(this.generateBothParentsConnecter(tl.getMeasuredHeight(), tl.getMeasuredWidth()));
		return returnImage;
		*/
	}
	
	/*
	public View generateSiblingsTree(ArrayList<View> siblingsTree) {
		
		
		return null;
	}
	*/
	
}
