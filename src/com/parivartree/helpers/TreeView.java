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
import android.graphics.drawable.Drawable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.parivartree.R;
import com.parivartree.customviews.CustomNode;
import com.parivartree.fragments.HomeFragment;
import com.parivartree.models.NodeUser;

public class TreeView implements OnClickListener {

	private final String TAG = "TreeView";
	public Context context;
	public NodeUser user;
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

	public TreeView(Context context, NodeUser user, Fragment fragment) {

		this.fragment = fragment;
		this.context = context;
		this.user = user;

		sharedPreferences = context.getApplicationContext().getSharedPreferences(
				context.getPackageName() + context.getResources().getString(R.string.USER_PREFERENCES),
				Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
	}

	/*
	 * private void setNodeAttributes(CustomNode customNode, int id, String
	 * name, Drawable image) {
	 * 
	 * customNode.setUserId(id); //customNode.setUserName(name);
	 * customNode.setUserFirstName(name); customNode.setUserLastName(name);
	 * customNode.setGender(0); customNode.setDeceased(0);
	 * customNode.setRelationCount(0); customNode.setUserImage(); }
	 */

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
		Log.i(TAG, "firstName: " + firstName + ", lastName:" + lastName);
	}

	private void setNodeAttributes(CustomNode customNode, NodeUser nodeUser) {

		customNode.setUserId(nodeUser.getId());
		customNode.setUserFirstName(nodeUser.getFirstName());
		customNode.setUserLastName(nodeUser.getLastName());
		customNode.setUserImageString("http://www.parivartree.com/profileimages/thumbs/" + customNode.getUserId()
				+ "PROFILE.jpeg");
		customNode.setGender(nodeUser.getGender());
		customNode.setDeceased(nodeUser.getDeceased());
		customNode.setRelationCount(nodeUser.getRelationCount());
		customNode.setUserImage();

		Log.i(TAG, "Attributes for Custom node - " + " gender:" + customNode.getGender());
	}

	public TableLayout generateView() {

		TableLayout tl = new TableLayout(context);
		tl.setOrientation(LinearLayout.VERTICAL);
		tl.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		Log.d(TAG, "fathers count:" + user.getFathers().size());
		Log.d(TAG, "mothers count:" + user.getMothers().size());

		generateParentRow();
		generateUserRow();
		generateOffSpringRow();
		// if(offSpringRow.size() > 0) {generateOffSpringRow();}

		calculateAdjustments();

		if (user.getFathers().size() > 0 || user.getMothers().size() > 0) {

			// TODO add parents row
			TableLayout tlParent = new TableLayout(context);
			tlParent.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			tlParent.addView(generateParentRowLayout());

			tl.addView(tlParent);

			// TODO user Parent level connector must be put here if no
			// connection required if only siblings relation present
		}

		if (user.getFathers().size() > 0 || user.getMothers().size() > 0 || user.getBrothers().size() > 0
				|| user.getSisters().size() > 0) {
			// TODO add user parent level connecter
			TableLayout tlUserParentLevelConnector = new TableLayout(context);
			tlUserParentLevelConnector.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

			Log.v(TAG, "userParent level connector added");
			TableRow tr = new TableRow(context);
			tr.addView(generateUserParentLevelConnector());
			tlUserParentLevelConnector.addView(tr);

			tl.addView(tlUserParentLevelConnector);
		}

		// TODO add user row
		TableLayout tlUser = new TableLayout(context);
		tlUser.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		tlUser.addView(generateUserRowLayout());

		tl.addView(tlUser);

		if (user.getSons().size() > 0 || user.getDaughters().size() > 0) {
			// TODO add user offspring level connecter
			TableLayout tlUserOffspringLevelConnector = new TableLayout(context);
			tlUserOffspringLevelConnector.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
					LayoutParams.WRAP_CONTENT));

			Log.v(TAG, "userOffspring level connector added");
			TableRow tr = new TableRow(context);
			tr.addView(generateUserOffSpringLevelConnector());
			tlUserOffspringLevelConnector.addView(tr);

			tl.addView(tlUserOffspringLevelConnector);

			// TODO add offSpring layout
			TableLayout tlOffSpring = new TableLayout(context);
			tlOffSpring.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

			// Log.d(TAG, "offSpringViewCoutn" +
			// generateOffSpringRowLayout().getChildCount());
			tlOffSpring.addView(generateOffSpringRowLayout());

			tl.addView(tlOffSpring);
		}

		return tl;
	}

	public void calculateAdjustments() {

		// Calculate largest row
		if (parentsRow.size() > largestSize) {
			largestRow = 0;
			largestSize = parentsRow.size();
		}
		if (userRow.size() > largestSize) {
			largestRow = 1;
			largestSize = userRow.size();
		}
		if (offSpringRow.size() > largestSize) {
			largestRow = 2;
			largestSize = offSpringRow.size();
		}

		Log.d(TAG, "calculateAdjustments called!");
		Log.d(TAG, "Parents Row size : " + parentsRow.size());
		Log.d(TAG, "User Row size : " + userRow.size());
		Log.d(TAG, "OffSprings Row size : " + offSpringRow.size());
		Log.d(TAG, "largest row : " + largestRow);

		userToOffSpringJoinPoint = userOffSpringConnector / 2;
		if (largestRow == 0) {
			userOffset = Math.abs(parentToUserMainConnector - parentToUserJoinPoint);
			if (userRow.size() > 0 && userOffset > 0) {
				ImageView userOffsetImage = new ImageView(context);
				userOffsetImage.setImageDrawable(generateBlankDrawable(userView.getMeasuredHeight(), userOffset));
				userRow.add(0, userOffsetImage);
			}

			offSpringOffset = Math.abs(userToOffSpringJoinPoint - userToOffSpringMainConnector) + userOffset;
			Log.i(TAG, "userToOffSpringJoinPoint:" + userToOffSpringJoinPoint + ", userToOffSpringMainConnector"
					+ userToOffSpringMainConnector + ", userOffset" + userOffset + ", offSpringOffset"
					+ offSpringOffset);
			if (offSpringRow.size() > 0 && offSpringOffset > 0) {
				ImageView offSpringOffsetImage = new ImageView(context);
				offSpringOffsetImage.setImageDrawable(generateBlankDrawable(userView.getMeasuredHeight(),
						offSpringOffset));
				offSpringRow.add(0, offSpringOffsetImage);
			}

		}
		if (largestRow == 1) {
			parentOffset = Math.abs(parentToUserJoinPoint - parentToUserMainConnector);
			if (parentOffset > 0 && parentsRow.size() > 0) {
				ImageView parentOffsetImage = new ImageView(context);
				parentOffsetImage.setImageDrawable(generateBlankDrawable(userView.getMeasuredHeight(), parentOffset));
				parentsRow.add(0, parentOffsetImage);
			}

			offSpringOffset = Math.abs(userToOffSpringMainConnector - userToOffSpringJoinPoint);
			if (offSpringOffset > 0 && offSpringRow.size() > 0) {
				ImageView offSpringOffsetImage = new ImageView(context);
				offSpringOffsetImage.setImageDrawable(generateBlankDrawable(userView.getMeasuredHeight(),
						offSpringOffset));
				offSpringRow.add(0, offSpringOffsetImage);
			}

		}
		if (largestRow == 2) {
			userOffset = Math.abs(userToOffSpringMainConnector - userToOffSpringJoinPoint);
			Log.v(TAG, "useroffset - " + userOffset);
			if (userOffset > 0) {
				ImageView userOffsetImage = new ImageView(context);
				userOffsetImage.setImageDrawable(generateBlankDrawable(userView.getMeasuredHeight(), userOffset));
				userRow.add(0, userOffsetImage);
			}

			parentOffset = Math.abs(this.parentToUserJoinPoint - this.parentToUserMainConnector) + userOffset;
			if (parentOffset > 0) {
				ImageView parentOffsetImage = new ImageView(context);
				parentOffsetImage.setImageDrawable(generateBlankDrawable(userView.getMeasuredHeight(), parentOffset));
				parentsRow.add(0, parentOffsetImage);
			}

		}
	}

	private void generateParentRow() {

		Log.i(TAG, "father.size() - " + user.getFathers().size() + ", mother.size() - " + user.getMothers().size());
		if (user.getFathers().size() > 0 || user.getMothers().size() > 0) {
			if (user.getFathers().size() > 0) {
				int count = 0;
				for (NodeUser tempFather : user.getFathers()) {
					CustomNode father = new CustomNode(context);
					Log.d(TAG, "parsed father ID - " + tempFather.getId() + ", name - " + tempFather.getName());
					/*
					 * setNodeAttributes( father, tempFather.getId(),
					 * tempFather.getName(), context.getResources()
					 * .getDrawable(
					 * R.drawable.parivar_mobile_profile_image_vsmall));
					 */
					setNodeAttributes(father, tempFather);

					father.measure(0, 0);
					Log.d(TAG, "Father dimensions:" + father.getMeasuredHeight() + ", " + father.getMeasuredWidth());
					if (count > 0) {
						// if (true) {
						ImageView connector = new ImageView(context);

						connector.setImageDrawable(generateHorizontalConnector(father.getMeasuredHeight(),
								father.getMeasuredWidth()));
						/*
						 * connector.setImageDrawable(generateBothParentsConnecter
						 * ( father.getMeasuredHeight(),
						 * father.getMeasuredWidth()));
						 */
						connector.measure(0, 0);
						parentsRow.add(connector);

						// Add to the width of parent row
						parentRowWidth = parentRowWidth + connector.getMeasuredWidth();
						Log.i(TAG, "father connector- parentRowWidth " + parentRowWidth);
					}

					father.setOnClickListener(this);
					parentsRow.add(father);

					// Add to the width of parent row
					parentRowWidth = parentRowWidth + father.getMeasuredWidth();
					Log.i(TAG, "father - parentRowWidth " + parentRowWidth);

					Log.d(TAG, "father added - for custom node - parent view:  - " + father.getParent()
							+ ", childcount:" + father.getChildCount());
					count++;
				}
			} else if (user.getMothers().size() > 0) {
				// TODO if no father present then add a dummy father with
				// "Add Father" name
				CustomNode father = new CustomNode(context);
				// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
				// ", name - " + tempFather.getName());
				/*
				 * setNodeAttributes( father, 0, "Add Father",
				 * context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
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
			 * motherFatherConnector
			 * .setImageDrawable(generateBothParentsConnecter(parentsRow
			 * .get(0).getMeasuredHeight(), parentsRow.get(0)
			 * .getMeasuredWidth()));
			 */
			motherFatherConnector.setImageDrawable(generateHorizontalConnector(parentsRow.get(0).getMeasuredHeight(),
					parentsRow.get(0).getMeasuredWidth()));
			motherFatherConnector.measure(0, 0);
			parentsRow.add(motherFatherConnector);

			// Add to the width of parent row
			parentRowWidth = parentRowWidth + motherFatherConnector.getMeasuredWidth();
			Log.i(TAG, "motherFatherConnector- parentRowWidth " + parentRowWidth);

			// Add position of connectors from parent to user
			parentsToUserConnectors.add(parentRowWidth - (motherFatherConnector.getMeasuredWidth() / 2));

			// Add as main connector from parent to user
			parentToUserMainConnector = parentRowWidth - (motherFatherConnector.getMeasuredWidth() / 2);

			if (user.getMothers().size() > 0) {
				int count = 0;
				for (NodeUser tempMother : user.getMothers()) {
					CustomNode mother = new CustomNode(context);
					Log.d(TAG, "parsed mother ID - " + tempMother.getId() + ", name - " + tempMother.getName());
					/*
					 * setNodeAttributes( mother, tempMother.getId(),
					 * tempMother.getName(), context.getResources()
					 * .getDrawable(
					 * R.drawable.parivar_mobile_profile_image_vsmall));
					 */

					setNodeAttributes(mother, tempMother);

					mother.measure(0, 0);
					Log.d(TAG, "Mother dimensions:" + mother.getMeasuredHeight() + ", " + mother.getMeasuredWidth());
					if (count > 0) {
						// if (true) {
						ImageView connector = new ImageView(context);

						connector.setImageDrawable(generateHorizontalConnector(mother.getMeasuredHeight(),
								mother.getMeasuredWidth()));
						/*
						 * connector.setImageDrawable(generateBothParentsConnecter
						 * ( mother.getMeasuredHeight(),
						 * mother.getMeasuredWidth()));
						 */
						connector.measure(0, 0);
						parentsRow.add(connector);

						// Add to the width of parent row
						parentRowWidth = parentRowWidth + connector.getMeasuredWidth();
					}

					mother.setOnClickListener(this);
					parentsRow.add(mother);

					// Add to the width of parent row
					parentRowWidth = parentRowWidth + mother.getMeasuredWidth();

					Log.d(TAG, "mother added - for custom node - parent view:  - " + mother.getParent()
							+ ", childcount:" + mother.getChildCount());
					count++;
				}
			} else if (user.getFathers().size() > 0) {
				// TODO if no mother present then add a dummy mother with
				// "Add Mother" name
				CustomNode mother = new CustomNode(context);
				// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
				// ", name - " + tempFather.getName());
				/*
				 * setNodeAttributes( mother, 0, "Add Mother",
				 * context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
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
			parentsToUserConnectors.add(parentRowWidth / 2);

			// Add as main connector from parent to user
			parentToUserMainConnector = parentRowWidth / 2;

			View centerView = parentsRow.get((parentsRow.size() / 2));
			if (!(centerView instanceof CustomNode)) {
				Log.i(TAG, "entered on even number of items");
				int tempIndex = (parentsRow.size() / 2);
				View connectorView = parentsRow.get(tempIndex);

				ImageView connector = new ImageView(context);
				connector.setImageDrawable(generateBothParentsConnecter(connectorView.getMeasuredHeight(),
						connectorView.getMeasuredWidth()));
				connector.measure(0, 0);

				parentsRow.remove(((parentsRow.size() / 2)));
				parentsRow.add(((parentsRow.size() / 2)), connector);
			}

		}

	}

	public void generateUserRow() {

		if (user.getBrothers().size() > 0) {

			int count = 0;
			for (NodeUser brother : user.getBrothers()) {

				CustomNode tempBrother = new CustomNode(context);

				Log.d(TAG, "Brother id:" + brother.getId() + " Brother Name:" + brother.getName());
				/*
				 * setNodeAttributes( tempBrother, brother.getId(),
				 * brother.getName(), context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
				 */
				setNodeAttributes(tempBrother, brother);

				tempBrother.measure(0, 0);
				Log.d(TAG,
						"Brother dimensions:" + tempBrother.getMeasuredHeight() + ", " + tempBrother.getMeasuredWidth());
				tempBrother.setOnClickListener(this);
				userRow.add(tempBrother);
				// if(count < user.getBrothers().size() - 1) {
				ImageView nextImage = new ImageView(context);
				nextImage.setImageDrawable(generateBlankDrawable(tempBrother.getMeasuredHeight(),
						tempBrother.getMeasuredWidth()));

				nextImage.measure(0, 0);
				userRow.add(nextImage);
				// }

				// Add to the userParent Connector width
				userParentConnectorWidth = userParentConnectorWidth + tempBrother.getMeasuredWidth()
						+ nextImage.getMeasuredWidth();

				// Add user to parent connectors
				userToParentsConnectors.add(userParentConnectorWidth
						- (tempBrother.getMeasuredWidth() / 2 + nextImage.getMeasuredWidth()));

				count++;
			}
		}

		// TODO add husbands
		if (user.getHusbands().size() > 0) {
			int count = 0;
			for (NodeUser husband : user.getHusbands()) {

				CustomNode tempHusband = new CustomNode(context);

				Log.d(TAG, "husband id:" + husband.getId() + " husband Name:" + husband.getName());
				/*
				 * setNodeAttributes( tempHusband, husband.getId(),
				 * husband.getName(), context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
				 */

				setNodeAttributes(tempHusband, husband);

				tempHusband.measure(0, 0);

				tempHusband.setOnClickListener(this);
				userRow.add(tempHusband);
				// if(count < user.getHusbands().size() - 1) {
				ImageView nextImage = new ImageView(context);
				if (user.getSons().size() > 0 || user.getDaughters().size() > 0) {
					nextImage.setImageDrawable(this.generateBothParentsConnecter(tempHusband.getMeasuredHeight(),
							tempHusband.getMeasuredWidth()));
				} else {
					nextImage.setImageDrawable(generateHorizontalConnector(tempHusband.getMeasuredHeight(),
							tempHusband.getMeasuredWidth()));
				}

				nextImage.measure(0, 0);
				userRow.add(nextImage);
				// }

				if (user.getBrothers().size() > 0) {
					// Add to the userParent Connector width
					userParentConnectorWidth = userParentConnectorWidth + tempHusband.getMeasuredWidth()
							+ nextImage.getMeasuredWidth();
				}

				count++;
			}
		} else if ((user.getSons().size() > 0 || user.getDaughters().size() > 0) && user.getGender() == 2) {
			// TODO add dummy husband

			CustomNode husband = new CustomNode(context);
			// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
			// ", name - " + tempFather.getName());
			/*
			 * setNodeAttributes( husband, 0, "Add Husband",
			 * context.getResources().getDrawable(
			 * R.drawable.parivar_mobile_profile_image_vsmall));
			 */
			setNodeAttributes(husband, 0, "Add", "Husband", "none", 1, 0, 0);

			// husband.measure(0, 0);
			// parentsRow.add(husband);

			husband.measure(0, 0);

			husband.setOnClickListener(this);
			userRow.add(husband);
			// if(count < user.getHusbands().size() - 1) {
			ImageView nextImage = new ImageView(context);
			if (user.getSons().size() > 0 || user.getDaughters().size() > 0) {
				nextImage.setImageDrawable(this.generateBothParentsConnecter(husband.getMeasuredHeight(),
						husband.getMeasuredWidth()));
			} else {
				nextImage.setImageDrawable(generateHorizontalConnector(husband.getMeasuredHeight(),
						husband.getMeasuredWidth()));
			}

			nextImage.measure(0, 0);
			userRow.add(nextImage);
			// }

			if (user.getBrothers().size() > 0) {
				// Add to the userParent Connector width
				userParentConnectorWidth = userParentConnectorWidth + husband.getMeasuredWidth()
						+ nextImage.getMeasuredWidth();
			}

		}

		// TODO add user
		{
			CustomNode currentUser = new CustomNode(context);
			/*
			 * setNodeAttributes( currentUser, user.getId(), user.getName(),
			 * context.getResources().getDrawable(
			 * R.drawable.parivar_mobile_profile_image_vsmall));
			 */
			setNodeAttributes(currentUser, user);

			currentUser.setOnClickListener(this);
			currentUser.setBackgroundColor(context.getResources().getColor(R.color.pt_user));
			userRow.add(currentUser);

			currentUser.measure(0, 0);

			// Add the view to be accesssed throughout
			userView = currentUser;

			// Add to the userParent Connector width
			userParentConnectorWidth = userParentConnectorWidth + currentUser.getMeasuredWidth();

			// Add user to parent connectors
			if (user.getGender() == 1) {
				Log.i(TAG,
						"Parent to user connector added at - " + userParentConnectorWidth
								+ (currentUser.getMeasuredWidth() / 2));
				// if user is male then connector is on right side
				userToOffspringConnectors.add(userParentConnectorWidth + (currentUser.getMeasuredWidth() / 2));

				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = userParentConnectorWidth + (currentUser.getMeasuredWidth() / 2);
			} else {
				// if user is female then connector is on left side
				userToOffspringConnectors.add(userParentConnectorWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / 2));

				// get the point of the user to offSpring connector
				userToOffSpringMainConnector = userParentConnectorWidth
						- (currentUser.getMeasuredWidth() + currentUser.getMeasuredWidth() / 2);
			}
			userToParentsConnectors.add(userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2));

			// Actual/Required connecting point for the parent connector
			parentToUserJoinPoint = userParentConnectorWidth - (currentUser.getMeasuredWidth() / 2);

		}

		// TODO add wives
		if (user.getWives().size() > 0) {
			int count = 0;
			for (NodeUser wife : user.getWives()) {

				CustomNode tempWife = new CustomNode(context);

				Log.d(TAG, "wife id:" + wife.getId() + " wife Name:" + wife.getName());
				/*
				 * setNodeAttributes( tempWife, wife.getId(), wife.getName(),
				 * context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
				 */
				setNodeAttributes(tempWife, wife);

				tempWife.measure(0, 0);

				// if(count < user.getHusbands().size() - 1) {
				ImageView prevImage = new ImageView(context);
				if (user.getSons().size() > 0 || user.getDaughters().size() > 0) {
					prevImage.setImageDrawable(this.generateBothParentsConnecter(tempWife.getMeasuredHeight(),
							tempWife.getMeasuredWidth()));
				} else {
					prevImage.setImageDrawable(generateHorizontalConnector(tempWife.getMeasuredHeight(),
							tempWife.getMeasuredWidth()));
				}

				prevImage.measure(0, 0);
				userRow.add(prevImage);
				// }
				tempWife.setOnClickListener(this);
				userRow.add(tempWife);

				if (user.getSisters().size() > 0) {
					// Add to the userParent Connector
					userParentConnectorWidth = userParentConnectorWidth + prevImage.getMeasuredWidth()
							+ tempWife.getMeasuredWidth();
				}

				count++;
			}
		} else if ((user.getSons().size() > 0 || user.getDaughters().size() > 0) && user.getGender() == 1) {
			// TODO add dummy wife
			CustomNode wife = new CustomNode(context);
			// Log.d(TAG, "parsed father ID - " + tempFather.getId() +
			// ", name - " + tempFather.getName());
			/*
			 * setNodeAttributes( wife, 0, "Add Wife",
			 * context.getResources().getDrawable(
			 * R.drawable.parivar_mobile_profile_image_vsmall));
			 */
			setNodeAttributes(wife, 0, "Add", "Wife", "none", 2, 0, 0);

			wife.measure(0, 0);

			wife.setOnClickListener(this);
			// parentsRow.add(wife);

			wife.measure(0, 0);

			// if(count < user.getHusbands().size() - 1) {
			ImageView prevImage = new ImageView(context);
			if (user.getSons().size() > 0 || user.getDaughters().size() > 0) {
				prevImage.setImageDrawable(this.generateBothParentsConnecter(wife.getMeasuredHeight(),
						wife.getMeasuredWidth()));
			} else {
				prevImage.setImageDrawable(generateHorizontalConnector(wife.getMeasuredHeight(),
						wife.getMeasuredWidth()));
			}

			prevImage.measure(0, 0);
			userRow.add(prevImage);
			// }

			userRow.add(wife);

			if (user.getSisters().size() > 0) {
				// Add to the userParent Connector
				userParentConnectorWidth = userParentConnectorWidth + prevImage.getMeasuredWidth()
						+ wife.getMeasuredWidth();
			}
		}

		if (user.getSisters().size() > 0) {

			int count = 0;
			for (NodeUser sister : user.getSisters()) {

				CustomNode tempSister = new CustomNode(context);

				Log.d(TAG, "Sister id:" + sister.getId() + " Sister Name:" + sister.getName());
				/*
				 * setNodeAttributes( tempSister, sister.getId(),
				 * sister.getName(), context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
				 */
				setNodeAttributes(tempSister, sister);

				tempSister.measure(0, 0);
				// if(count < user.getSisters().size() - 1) {
				ImageView prevImage = new ImageView(context);
				prevImage.setImageDrawable(generateBlankDrawable(tempSister.getMeasuredHeight(),
						tempSister.getMeasuredWidth()));

				prevImage.measure(0, 0);
				userRow.add(prevImage);
				// }

				tempSister.setOnClickListener(this);
				userRow.add(tempSister);

				// Add to the userParent Connector width
				userParentConnectorWidth = userParentConnectorWidth + prevImage.getMeasuredWidth()
						+ tempSister.getMeasuredWidth();

				// Add user to parent connectors
				userToParentsConnectors.add(userParentConnectorWidth - (tempSister.getMeasuredWidth() / 2));

				count++;
			}
		}

		// TODO to add parent row to the center of the user row
		// Actual/Required connecting point for the parent connector
		parentToUserJoinPoint = userParentConnectorWidth / 2;

	}

	public void generateOffSpringRow() {

		if (user.getSons().size() > 0) {

			int count = 0;
			for (NodeUser son : user.getSons()) {

				CustomNode tempSon = new CustomNode(context);

				Log.d(TAG, "Son id:" + son.getId() + " Son Name:" + son.getName());
				/*
				 * setNodeAttributes( tempSon, son.getId(), son.getName(),
				 * context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
				 */
				setNodeAttributes(tempSon, son);

				tempSon.measure(0, 0);
				tempSon.setOnClickListener(this);
				offSpringRow.add(tempSon);

				// Add to the userOffSpring Connector width
				userOffSpringConnector = userOffSpringConnector + tempSon.getMeasuredWidth();
				Log.d(TAG, "off Srping - son: tempSon - userOffSpringConnector" + userOffSpringConnector);

				// Add offSpring to user connectors
				offspringsToUserConnectors.add(userOffSpringConnector - (tempSon.getMeasuredWidth() / 2));

				if (count < user.getSons().size() - 1) {
					ImageView nextImage = new ImageView(context);
					nextImage.setImageDrawable(generateBlankDrawable(tempSon.getMeasuredHeight(),
							tempSon.getMeasuredWidth()));

					nextImage.measure(0, 0);
					offSpringRow.add(nextImage);

					// Add to the userParent Connector
					userOffSpringConnector = userOffSpringConnector + nextImage.getMeasuredWidth();
					Log.d(TAG, "off Srping - son: nextImage - userOffSpringConnector" + userOffSpringConnector);
				}

				count++;
			}
		}

		if (offSpringRow.size() != 0 && user.getSons().size() > 0 && user.getDaughters().size() > 0) {
			ImageView nextImage = new ImageView(context);
			nextImage.setImageDrawable(generateBlankDrawable(offSpringRow.get(0).getMeasuredHeight(),
					offSpringRow.get(0).getMeasuredWidth()));

			nextImage.measure(0, 0);
			offSpringRow.add(nextImage);

			// Add to the userParent Connector
			userOffSpringConnector = userOffSpringConnector + nextImage.getMeasuredWidth();
			Log.d(TAG, "off Srping - middle: nextImage - userOffSpringConnector" + userOffSpringConnector);

		}

		if (user.getDaughters().size() > 0) {
			int count = 0;
			for (NodeUser daughter : user.getDaughters()) {

				CustomNode tempDaughter = new CustomNode(context);

				Log.d(TAG, "daughter id:" + daughter.getId() + " daughter Name:" + daughter.getName());
				/*
				 * setNodeAttributes( tempDaughter, daughter.getId(),
				 * daughter.getName(), context.getResources().getDrawable(
				 * R.drawable.parivar_mobile_profile_image_vsmall));
				 */
				setNodeAttributes(tempDaughter, daughter);

				tempDaughter.measure(0, 0);

				if (count > 0) {
					ImageView prevImage = new ImageView(context);
					prevImage.setImageDrawable(generateBlankDrawable(tempDaughter.getMeasuredHeight(),
							tempDaughter.getMeasuredWidth()));

					prevImage.measure(0, 0);
					offSpringRow.add(prevImage);

					// Add to the userParent Connector width
					userOffSpringConnector = userOffSpringConnector + prevImage.getMeasuredWidth();
					Log.d(TAG, "off Srping - daughter: prevImage - userOffSpringConnector" + userOffSpringConnector);
				}

				tempDaughter.setOnClickListener(this);
				offSpringRow.add(tempDaughter);

				// Add to the userParent Connector width
				userOffSpringConnector = userOffSpringConnector + tempDaughter.getMeasuredWidth();

				Log.d(TAG, "off Srping - daughter: tempDaughter - userOffSpringConnector" + userOffSpringConnector);

				// Add offSpring to user connectors
				offspringsToUserConnectors.add(userOffSpringConnector - (tempDaughter.getMeasuredWidth() / 2));

				count++;
			}
		}

		/*
		 * //calculate the join point for user row userToOffSpringJoinPoint =
		 * userOffSpringConnector/2; Log.d(TAG,
		 * "join point - userToOffSpringJoinPoint:" + userToOffSpringJoinPoint);
		 * Log.d(TAG, "main connector - userToOffSpringMainConnector:" +
		 * userToOffSpringMainConnector); // calculate the offset required
		 * offSpringOffset = userToOffSpringMainConnector -
		 * userToOffSpringJoinPoint; Log.d(TAG, "offSpringOffset: " +
		 * offSpringOffset);
		 */
	}

	// TODO generate row layout
	public TableRow generateParentRowLayout() {
		TableRow tr = new TableRow(context);
		/*
		 * Log.d(TAG, "generateParentLayout:" + parentsRow.size());
		 * tr.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT,
		 * LayoutParams.WRAP_CONTENT)); int count = 0; for(int i=0;
		 * i<parentsRow.size();i++) { Log.d(TAG + "generateParentsLayout",
		 * "view count: " + i + ", Parent View:" + parentsRow.get(i).getParent()
		 * + ", child count -" +
		 * "((ViewGroup)parentsRow.get(i).getParent()).getChildCount()");
		 * tr.addView(parentsRow.get(i)); count++; }
		 */

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
			Log.d(TAG, "View count : " + count);
			tr.addView(item);
			count++;
		}

		return tr;
	}

	// TODO generate off spring layout
	public TableRow generateOffSpringRowLayout() {
		TableRow tr = new TableRow(context);

		Log.d(TAG, "off springs parsed - " + offSpringRow.size());
		for (View item : offSpringRow) {
			tr.addView(item);
		}

		return tr;
	}

	// TODO generate drawables
	public BitmapDrawable generateBothParentsConnecter(int height, int width) {

		// width = width/5;
		Bitmap connector = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);

		// connectorCanvas.drawLine(0, height / 5, connector.getWidth(), height
		// / 5, paint);
		connectorCanvas.drawLine(0, height / 5, connector.getWidth(), height / 5, paint);
		connectorCanvas.drawLine(width / 2, height, width / 2, height / 5, paint);

		Bitmap heart = BitmapFactory.decodeResource(context.getResources(), R.drawable.spouse);
		// Rect source = new Rect(width/2 - (heart.getWidth()/2), height/5 -
		// (heart.getHeight()/2), width/2 + (heart.getWidth()/2), height/5 +
		// (heart.getHeight()/2));
		Rect bitmapRect = new Rect(width / 2 - (heart.getWidth() / 2), height / 5 - (heart.getHeight() / 2), width / 2
				+ (heart.getWidth() / 2), height / 5 + (heart.getHeight() / 2));
		connectorCanvas.drawBitmap(heart, null, bitmapRect, new Paint());

		return new BitmapDrawable(context.getResources(), connector);
	}

	public BitmapDrawable generateHorizontalConnector(int height, int width) {

		// width = width/5;
		Bitmap connector = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		paint.setColor(Color.BLACK);
		paint.setStrokeWidth(5);

		connectorCanvas.drawLine(0, height / 5, connector.getWidth(), height / 5, paint);

		Bitmap heart = BitmapFactory.decodeResource(context.getResources(), R.drawable.spouse);
		// Rect source = new Rect(width/2 - (heart.getWidth()/2), height/5 -
		// (heart.getHeight()/2), width/2 + (heart.getWidth()/2), height/5 +
		// (heart.getHeight()/2));
		Rect bitmapRect = new Rect(width / 2 - (heart.getWidth() / 2), height / 5 - (heart.getHeight() / 2), width / 2
				+ (heart.getWidth() / 2), height / 5 + (heart.getHeight() / 2));
		connectorCanvas.drawBitmap(heart, null, bitmapRect, new Paint());

		return new BitmapDrawable(context.getResources(), connector);
	}

	public BitmapDrawable generateBlankDrawable(int height, int width) {

		// width = width/5;
		Log.d(TAG, "height:" + height + ", width:" + width);
		Bitmap connector = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

		return new BitmapDrawable(context.getResources(), connector);
	}

	// TODO generate level connectors
	public ImageView generateUserParentLevelConnector() {

		Bitmap connector = null;
		if (this.largestRow == 0) {
			int height = 200;
			connector = Bitmap.createBitmap(userParentConnectorWidth + userOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userParentConnectorWidth - " + userParentConnectorWidth);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastUserRowPosition = userRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(userOffset + startOffset, height / 2, userOffset + userParentConnectorWidth
					- endOffset, height / 2, paint);

			for (int userConnector : userToParentsConnectors) {
				connectorCanvas.drawLine(userOffset + userConnector, height, userOffset + userConnector, height / 2,
						paint);
			}

			for (int parentConnector : parentsToUserConnectors) {
				connectorCanvas.drawLine(parentOffset + parentConnector, height / 2, parentOffset + parentConnector, 0,
						paint);
			}
		}
		if (this.largestRow == 1) {
			int height = 200;
			connector = Bitmap.createBitmap(userParentConnectorWidth + userOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userParentConnectorWidth - " + userParentConnectorWidth);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastUserRowPosition = userRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(userOffset + startOffset, height / 2, userOffset + userParentConnectorWidth
					- endOffset, height / 2, paint);

			for (int userConnector : userToParentsConnectors) {
				connectorCanvas.drawLine(userOffset + userConnector, height, userOffset + userConnector, height / 2,
						paint);
			}

			for (int parentConnector : parentsToUserConnectors) {
				connectorCanvas.drawLine(parentOffset + parentConnector, height / 2, parentOffset + parentConnector, 0,
						paint);
			}
		}

		if (largestRow == 2) {
			int height = 200;
			connector = Bitmap.createBitmap(userParentConnectorWidth + userOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userParentConnectorWidth - " + userParentConnectorWidth);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastUserRowPosition = userRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(userOffset + startOffset, height / 2, userOffset + userParentConnectorWidth
					- endOffset, height / 2, paint);

			for (int userConnector : userToParentsConnectors) {
				connectorCanvas.drawLine(userOffset + userConnector, height, userOffset + userConnector, height / 2,
						paint);
			}

			for (int parentConnector : parentsToUserConnectors) {
				connectorCanvas.drawLine(parentOffset + parentConnector, height / 2, parentOffset + parentConnector, 0,
						paint);
			}
		}

		ImageView connectorView = new ImageView(context);
		connectorView.setImageBitmap(connector);
		// return new BitmapDrawable(context.getResources(), connector);
		return connectorView;
	}

	public ImageView generateUserOffSpringLevelConnector() {

		Bitmap connector = null;
		if (largestRow == 0) {
			int height = 200;
			connector = Bitmap.createBitmap(userOffSpringConnector + offSpringOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastOffSpringPosition = offSpringRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(offSpringOffset + startOffset, height / 2, offSpringOffset
					+ userOffSpringConnector - endOffset, height / 2, paint);

			for (int userConnector : offspringsToUserConnectors) {
				connectorCanvas.drawLine(offSpringOffset + userConnector, height, offSpringOffset + userConnector,
						height / 2, paint);
			}
			for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(userOffset + offSpringConnector, height / 2, userOffset + offSpringConnector,
						0, paint);
			}
		}
		if (this.largestRow == 1) {
			int height = 200;
			connector = Bitmap.createBitmap(userOffSpringConnector + offSpringOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastOffSpringPosition = offSpringRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(offSpringOffset + startOffset, height / 2, offSpringOffset
					+ userOffSpringConnector - endOffset, height / 2, paint);

			for (int userConnector : offspringsToUserConnectors) {
				connectorCanvas.drawLine(offSpringOffset + userConnector, height, offSpringOffset + userConnector,
						height / 2, paint);
			}
			for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(userOffset + offSpringConnector, height / 2, userOffset + offSpringConnector,
						0, paint);
			}
		}
		if (largestRow == 2) {
			int height = 200;
			connector = Bitmap.createBitmap(userOffSpringConnector + offSpringOffset, height, Bitmap.Config.ARGB_8888);
			Canvas connectorCanvas = new Canvas(connector);
			Log.d(TAG, "userOffSpringConnector - " + userOffSpringConnector);
			Paint paint = new Paint();
			paint.setColor(Color.BLACK);
			paint.setStrokeWidth(5);

			int lastOffSpringPosition = offSpringRow.size() - 1;
			int startOffset = userView.getMeasuredWidth() / 2;
			int endOffset = userView.getMeasuredWidth() / 2;
			connectorCanvas.drawLine(offSpringOffset + startOffset, height / 2, offSpringOffset
					+ userOffSpringConnector - endOffset, height / 2, paint);

			for (int userConnector : offspringsToUserConnectors) {
				connectorCanvas.drawLine(offSpringOffset + userConnector, height, offSpringOffset + userConnector,
						height / 2, paint);
			}
			for (int offSpringConnector : userToOffspringConnectors) {
				connectorCanvas.drawLine(userOffset + offSpringConnector, height / 2, userOffset + offSpringConnector,
						0, paint);
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
//		if (v instanceof CustomNode) {
//
//			TextView nodeUserFirstName = (TextView) v.findViewById(R.id.userNodeFirstName);
//			TextView nodeUserLastName = (TextView) v.findViewById(R.id.userNodeLastName);
//
//			String nodeUserName = nodeUserFirstName.getText() + " " + nodeUserLastName.getText();
//
//			if (nodeUserName.equals("Add Mother")) {
//				// TODO go to add relation directly by setting relation id for
//				// mother
//			} else if (nodeUserName.equals("Add Father")) {
//				// TODO go to add relation directly by setting relation id for
//				// mother
//			} else if (nodeUserName.equals("Add Wife")) {
//				// TODO go to add relation directly by setting relation id for
//				// mother
//			} else if (nodeUserName.equals("Add Husband")) {
//				// TODO go to add relation directly by setting relation id for
//				// mother
//			}
//
//			CustomNode clickedNode = (CustomNode) v;
//
//			String nodeid = "" + clickedNode.getUserId();
//			Log.d(TAG, nodeid);
//
//			// TODO implement an interface to communicate with HomeFragment
//			// sharedPreferencesEditor.putString("nodeid", nodeid);
//			// sharedPreferencesEditor.putString("current_user_id", nodeid);
//			//((HomeFragment) this.fragment).showOptionsLayout(nodeid);
//		}
	}
}
