package com.parivartree.fragments;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.helpers.CompleteTree;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.helpers.TreeView;
import com.parivartree.models.Node;
import com.parivartree.models.NodeUser;
import com.parivartree.zoom.DynamicZoomControl;
import com.parivartree.zoom.LayoutZoomView;
import com.parivartree.zoom.PinchZoomListener;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class HomeFragment extends Fragment implements OnClickListener {
	
	public HomeFragment() {
		
	}
	
	Activity activity;
	Context context;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	SharedPreferences sharedPreferences1;
	Editor sharedPreferencesEditor1;
	// LinearLayout contentHolder;
	//ScrollView parentView;
	//HorizontalScrollView subParentView;
	LayoutZoomView contentHolder;
	DynamicZoomControl mZoomControl;
	PinchZoomListener mPinchZoomListener;

	LinearLayout userTreeMessage;
	TextView textViewUserMessage;

	public RelativeLayout optionsLayout, helpLayout;
	CompleteTree nodeView;
	Node mainUser;
	String selectedNode = "0";
	String selectedName = "NA";
	String selectedGender = "1";
	RelativeLayout textViewAddRelation, textViewProfile, textViewImageGallery, textViewVideoGallery, textViewWall,
			textViewTree, textViewHide;

	private final String TAG = "HomeFragment";
	boolean helpDraw;

	@SuppressLint("ClickableViewAccessibility")
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		//this.setRetainInstance(true);
		
		activity = getActivity();
		context = getActivity().getApplicationContext();
		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferences1 = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCE), Context.MODE_PRIVATE);
		sharedPreferencesEditor1 = sharedPreferences1.edit();
		helpDraw = sharedPreferences1.getBoolean("helpdraw", false);

		View rootView = inflater.inflate(R.layout.fragment_home, container, false);
		// contentHolder = (LinearLayout)
		// rootView.findViewById(R.id.contentHolder);
		//parentView = (ScrollView) rootView.findViewById(R.id.topScrollView);
		//subParentView = (HorizontalScrollView) rootView.findViewById(R.id.horizontalScrollView1);

		//contentHolder = (ZoomableRelativeLayout) rootView.findViewById(R.id.contentHolder);

		/**
         * Set up zooming listner and control classes
         */
		
        mZoomControl = new DynamicZoomControl();
        
        mPinchZoomListener = new PinchZoomListener(this.getActivity());
        mPinchZoomListener.setZoomControl(mZoomControl);
        
        contentHolder = (LayoutZoomView) rootView.findViewById(R.id.contentHolder);
        contentHolder.setZoomState(mZoomControl.getZoomState());
        mZoomControl.setAspectQuotient(contentHolder.getAspectQuotient());
        resetZoomState();
        contentHolder.setOnTouchListener(mPinchZoomListener);
        
		userTreeMessage = (LinearLayout) rootView.findViewById(R.id.userTreeMessage);
		textViewUserMessage = (TextView) rootView.findViewById(R.id.textViewUserMessage);

		optionsLayout = (RelativeLayout) rootView.findViewById(R.id.selectOptions);
		helpLayout = (RelativeLayout) rootView.findViewById(R.id.helplayout);

		textViewAddRelation = (RelativeLayout) rootView.findViewById(R.id.addRelation);
		textViewProfile = (RelativeLayout) rootView.findViewById(R.id.viewProfile);
		textViewImageGallery = (RelativeLayout) rootView.findViewById(R.id.viewImageGallery);
		textViewVideoGallery = (RelativeLayout) rootView.findViewById(R.id.viewVideoGallery);
		textViewWall = (RelativeLayout) rootView.findViewById(R.id.viewWall);
		textViewTree = (RelativeLayout) rootView.findViewById(R.id.viewTree);
		textViewHide = (RelativeLayout) rootView.findViewById(R.id.viewHide);
		
		textViewAddRelation.setOnClickListener(this);
		textViewProfile.setOnClickListener(this);
		textViewImageGallery.setOnClickListener(this);
		textViewVideoGallery.setOnClickListener(this);
		//textViewWall.setOnClickListener(this);
		textViewTree.setOnClickListener(this);
		textViewHide.setOnClickListener(this);

		optionsLayout.setOnClickListener(this);

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final CompleteTreeTask tVT = new CompleteTreeTask();
			tVT.execute();
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (tVT.getStatus() == AsyncTask.Status.RUNNING){
						tVT.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
		if (!helpDraw) {
			
				// TODO add info layout for slide menu
				helpLayout.setBackgroundResource(R.color.pt_dark_overlay);
				
				ImageView homeClickImage = new ImageView(this.activity);
				homeClickImage.setImageDrawable(this.getResources().getDrawable(R.drawable.info_click_menus));
				
				RelativeLayout.LayoutParams homeClickImageLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				homeClickImageLayoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
				homeClickImage.setLayoutParams(homeClickImageLayoutParams);
				
				ImageView slideMenuImage = new ImageView(this.activity);
				slideMenuImage.setImageDrawable(this.getResources().getDrawable(R.drawable.info_image));
				
				RelativeLayout.LayoutParams slideMenuImageLayoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
				slideMenuImageLayoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
				slideMenuImage.setLayoutParams(slideMenuImageLayoutParams);
				
				helpLayout.addView(homeClickImage);
				helpLayout.addView(slideMenuImage);
				helpLayout.setVisibility(View.VISIBLE);
			
			sharedPreferencesEditor1.putBoolean("helpdraw", true);
			sharedPreferencesEditor1.commit();
		} else {
			helpLayout.setVisibility(View.INVISIBLE);
		}
		helpLayout.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				// TODO Auto-generated method stub
				helpLayout.setVisibility(View.INVISIBLE);
				return false;
			}
		});

		return rootView;
	}
	
	/**
     * Reset zoom state and notify observers
     */
    private void resetZoomState() {
        mZoomControl.getZoomState().setPanX(0.5f);
        mZoomControl.getZoomState().setPanY(0.5f);
        mZoomControl.getZoomState().setZoom(1f);
        mZoomControl.getZoomState().notifyObservers();
    }
	// private Node initializeNode(JSONObject data) {
	//
	// Node node = null;
	// try {
	// node = new Node(data);
	//
	// } catch (JSONException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	//
	//
	//
	// return node;
	// }

	private class TreeViewTask extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Fetching Tree details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String userId = sharedPreferences.getString("user_id", "0");
			String nodeId = sharedPreferences.getString("node_id", userId);
			// nodeId = (nodeId.equals("0")) ? userId : nodeId;
			return HttpConnectionUtils.getMyTreeResponse(
					userId,
					nodeId,
					getActivity().getResources().getString(R.string.hostname)
							+ getResources().getString(R.string.url_mytree));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("TreeView Fetch Response ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("status");
				if (responseResult.equals("success")) {

					// TODO store the login response and
					JSONObject data = loginResponseObject.getJSONObject("data");
					// JSONObject userProfileData = (JSONObject) data.get(0);
					// mainUser = initializeNode(data);
					// mainUser = new NodeUser(data,context);

					Log.d(TAG, "Node Data: -" + mainUser.getName());

					// TODO initialize node
					// TreeViewTask tVT = new TreeViewTask();
					// tVT.execute();

					// TODO generate node
					// nodeView = new TreeView(context, mainUser,
					// HomeFragment.this);

					// TODO add node view
					// contentHolder.addView(nodeView.generateView());

					/*
					 * GenerateTreeViewTask gTVT = new GenerateTreeViewTask();
					 * gTVT.equals(mainUser);
					 */
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: Treeview Fetch", "" + tempStack.getLineNumber() + " methodName: "
							+ tempStack.getClassName() + "-" + tempStack.getMethodName());
				}
				Log.d(TAG, "Invalid Server content from home!!");
			}
		}

	}

	private class GenerateTreeViewTask extends AsyncTask<NodeUser, String, TreeView> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Generating Tree view...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected TreeView doInBackground(NodeUser... params) {

			// TODO generate node
			// nodeView = new TreeView(context, params[0], HomeFragment.this);

			return null;
		}

		@Override
		protected void onPostExecute(TreeView response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();

			// TODO add node view
			contentHolder.addView(nodeView.generateView());

			/*
			 * Log.i("TreeView Fetch Response ", response); try {
			 * 
			 * JSONObject loginResponseObject = new JSONObject(response); String
			 * responseResult = loginResponseObject.getString("result");
			 * if(responseResult.equals("success")) {
			 * 
			 * // TODO store the login response and JSONObject data =
			 * loginResponseObject.getJSONObject("data"); //JSONObject
			 * userProfileData = (JSONObject) data.get(0); //mainUser =
			 * initializeNode(data); mainUser = new Node(data);
			 * 
			 * // TODO initialize node //TreeViewTask tVT = new TreeViewTask();
			 * //tVT.execute();
			 * 
			 * // TODO generate node nodeView = new GenerateTreeView(context,
			 * mainUser);
			 * 
			 * 
			 * 
			 * // TODO add node view
			 * contentHolder.addView(nodeView.generateView()); }
			 * 
			 * } catch (Exception e) { for(StackTraceElement tempStack :
			 * e.getStackTrace()) { Log.d("Exception thrown: ", "" +
			 * tempStack.getLineNumber()); } Toast.makeText(activity,
			 * "Invalid Server content!!", Toast.LENGTH_SHORT).show(); }
			 */
		}
	}

	public void showOptionsLayout(String nodeId,String gender) {
		optionsLayout.setVisibility(View.VISIBLE);
		selectedNode = nodeId;
		selectedGender = gender;
		Log.d(TAG + " find nodeid 1----", ""+selectedNode);
		if (nodeId.equals(sharedPreferences.getString("user_id", "0"))) {
			textViewHide.setVisibility(View.GONE);
		} else {
			textViewHide.setVisibility(View.VISIBLE);
		}
	}

	public void hideOptionsLayout() {
		optionsLayout.setVisibility(View.INVISIBLE);
		selectedNode = sharedPreferences.getString("user_id", "0");
		Log.d(TAG + " find nodeid 1----", ""+selectedNode);
		//selectedName = sharedPreferences.getString("sessionname", "NA");
		//selectedGender = sharedPreferences.getString("gender", "1");
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.addRelation) {
			// TODO get the id of the
			// ((MainActivity)this.getActivity()).changeFragment("SelectRelationFragment");
			sharedPreferencesEditor.putString("node_id", selectedNode);
			sharedPreferencesEditor.putString("selectgender", selectedGender);
			sharedPreferencesEditor.commit();
			((MainActivity) this.getActivity()).changeFragment("RelationFragment");
		} else if (v.getId() == R.id.viewProfile) {
			sharedPreferencesEditor.putString("node_id", selectedNode);
			sharedPreferencesEditor.commit();
			((MainActivity) this.getActivity()).changeFragment("ProfileFragment");
		} else if (v.getId() == R.id.viewTree) {
			// sharedPreferencesEditor.putString("current_user_id", "0");
			// String currentUser =
			// sharedPreferences.getString("current_user_id",
			// sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.putString("node_id", selectedNode);
			sharedPreferencesEditor.commit();
			((MainActivity) this.getActivity()).changeFragment("HomeFragment");
		} else if (v.getId() == R.id.viewImageGallery) {
		} else if (v.getId() == R.id.viewVideoGallery) {
		} else if (v.getId() == R.id.viewWall) {
		} else if (v.getId() == R.id.viewHide) {
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
			// Setting Dialog Title
			alertDialog.setTitle("Confirm Hide...");
			// Setting Dialog Message
			alertDialog.setMessage("    Are you sure to Hide");
			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.delete);
			// Setting Positive "Yes" Button
			alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to invoke YES event
					boolean bool = new ConDetect(getActivity()).isOnline();
					if (bool) {
						// Create object of AsycTask and execute
						final HideUserTask hideTask = new HideUserTask();
						hideTask.execute(selectedNode, sharedPreferences.getString("user_id", "0"));
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (hideTask.getStatus() == AsyncTask.Status.RUNNING){
									hideTask.cancel(true);
								}
							}
						}, 10000);
					} else {
						Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
					}
				}
			});
			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to invoke NO event
					dialog.cancel();
				}
			});
			// Showing Alert Message
			alertDialog.show();

		} else {
			hideOptionsLayout();
		}
	}

	private class CompleteTreeTask extends AsyncTask<Node, String, String> {

		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
		}

		@Override
		protected String doInBackground(Node... params) {

			// TODO generate node
			String userId = sharedPreferences.getString("user_id", "0");
			String nodeId = sharedPreferences.getString("node_id", userId);
			// nodeId = (nodeId.equals("0")) ? userId : nodeId;
			return HttpConnectionUtils.getMyTreeResponse(	
					userId,nodeId,
					getActivity().getResources().getString(R.string.hostname)
							+ getResources().getString(R.string.url_mytree));

		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			Log.d(TAG + " response", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("status");
				if (responseResult.equals("success")) {

					// TODO store the login response and
					JSONObject data = loginResponseObject.getJSONObject("data");
					// JSONObject userProfileData = (JSONObject) data.get(0);
					// mainUser = initializeNode(data);
					mainUser = new Node(data, context);

					sharedPreferencesEditor.putString("treeusername", mainUser.getName());
					sharedPreferencesEditor.commit();

					textViewUserMessage.setText("You are viewing "
				             + sharedPreferences.getString("treeusername", "user name") + "'s tree");

						     String userId = sharedPreferences.getString("user_id", "0");
						     String nodeId = sharedPreferences.getString("node_id", userId);

						     //if (!userId.equals(nodeId)) {
						      Animation fadeIn = new AlphaAnimation(0, 1);
						      fadeIn.setInterpolator(new DecelerateInterpolator()); // add
						                    // this
						      fadeIn.setDuration(1000);

						      Animation fadeOut = new AlphaAnimation(1, 0);
						      fadeOut.setInterpolator(new AccelerateInterpolator()); // and
						                    // this
						      fadeOut.setStartOffset(1000);
						      fadeOut.setDuration(1000);

						      AnimationSet animation = new AnimationSet(false); // change
						                   // to
						                   // false
						      animation.addAnimation(fadeIn);
						      //animation.addAnimation(fadeOut);
						      userTreeMessage.setAnimation(animation);
						      userTreeMessage.setVisibility(View.VISIBLE);
						     //}

					Log.d(TAG, "Node Data: -" + mainUser.getName());

					// TODO initialize node
					// TreeViewTask tVT = new TreeViewTask();
					// tVT.execute();

					// TODO generate node
					nodeView = new CompleteTree(context, mainUser, HomeFragment.this);

					// TODO add node view
					contentHolder.addView(nodeView.generateView());

					/*
					 * GenerateTreeViewTask gTVT = new GenerateTreeViewTask();
					 * gTVT.equals(mainUser);
					 */
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: Treeview Fetch", "" + tempStack.getLineNumber() + " methodName: "
							+ tempStack.getClassName() + "-" + tempStack.getMethodName());
				}
				Log.d(TAG, "Invalid Server content from home!!");
			}

		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}
	
	public class HideUserTask extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Hiding...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getHideUserResponse(params[0], params[1], getActivity().getResources()
					.getString(R.string.hostname) + getResources().getString(R.string.url_hide_user));
		}
		
		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("delete user Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					Log.i("hide user Fetch Response ", "user hide");
					String userId = sharedPreferences.getString("user_id", "0");
					sharedPreferencesEditor.putString("node_id", userId);
					sharedPreferencesEditor.commit();
					String nodeName = sharedPreferences.getString("node_first_name", "Fristname") + " " + sharedPreferences.getString("node_last_name", "Lastname");
				     Crouton.makeText(activity, "You have successfully hidden " + nodeName + " from your family tree", Style.INFO).show();
					((MainActivity) activity).changeFragment("HomeFragment");
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}
	}

}
