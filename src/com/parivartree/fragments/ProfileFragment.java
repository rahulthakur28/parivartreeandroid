package com.parivartree.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.ActivityInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.ImmediateFamilyAdapter;
import com.parivartree.crop.Crop;
import com.parivartree.customviews.HorizontalListView;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.helpers.RectangularImageView;
import com.parivartree.models.ProfilePrivacyDetails;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

//use optimization of dialog
public class ProfileFragment extends Fragment implements OnClickListener {
 
	private boolean set = false;
	private int year;
	private int month;
	private int day;
	int flag = 0;
	String date = "";
	ProfilePrivacyDetails profilePrivacyDetails;
	//dobprivacy,localityprivacy,pincodeprivacy,hometownprivacy,mobileprivacy,maritalStatusprivacy,weddingDateprivacy,religionprivacy,communityprivacy,gothraprivacy,professionprivacy;
	
	int relation = 0;
	ImageView imageViewCamera, imageViewGallery;
	RectangularImageView imageViewProfilePic;
	Button btndeceased, btndeleteuser;
	TextView textViewName1, textViewEmail1, textViewDob1, textViewGender1, textViewLocality1, textViewPincode1,
			textViewHometown1, textViewMobile1, textViewMaritalStatus1, textViewWeddingDate1, textViewReligion1,
			textViewCommunity1, textViewGothra1, textViewProfession1, textViewDeceasedDate, textViewUpdatedBy;
	ImageView imageViewDob, imageViewLocation, imageViewPincode, imageViewHomeTown, imageViewMob, imageViewMarital,
			imageViewWeddate, imageViewReligion, imageViewCommunity, imageViewGothra, imageViewProfession;
	Activity activity;
	Context context;
	
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	String nodeId;
	String userId;
	String sessionname;
	String view="";
	View viewEditProfile, viewEditfamily, viewEditAlbum ,viewImmediate;
	LinearLayout linearLayoutOverlay, linearDeceased, linearDeleteUser,linearMobile1, linearDob1, linearGender1,
			linearRelation1, linearWedDate1, linearLocation1, linearHomeTown1, linearProfession1, linearReligion1,
			linearPincode1, linearCommunity1, linearGothra1;
	ArrayList<String> listFamilyId;
	static final int IMAGE_PICKER_SELECT = 100;
	HorizontalListView horizontialListView;
	ImmediateFamilyAdapter ifa;
	
	ArrayList<HashMap<String, String>> immediateFamily;
	Bitmap userImage = null;
	private String[] PRIVACY = { "Private", "Family", "Public" };
	View rootView;
	
	private ProgressDialog pDialog;
	
	public ProfileFragment(Bitmap userImage) {
		this.userImage = userImage;
	}

	public ProfileFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		rootView = inflater.inflate(R.layout.fragment_profile, container, false);
		
		activity = getActivity();
		context = getActivity().getApplicationContext();
		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		profilePrivacyDetails = new ProfilePrivacyDetails();
		
		Bundle bndle = getArguments();
		if (bndle != null) {
			relation = bndle.getInt("relation", 0);
		}
		listFamilyId = new ArrayList<String>();
		linearMobile1 = (LinearLayout) rootView.findViewById(R.id.linearmobile1);
		linearDob1 = (LinearLayout) rootView.findViewById(R.id.lineardob1);
		linearGender1 = (LinearLayout) rootView.findViewById(R.id.lineargender1);
		linearRelation1 = (LinearLayout) rootView.findViewById(R.id.linearrelation1);
		linearWedDate1 = (LinearLayout) rootView.findViewById(R.id.linearweddate1);
		linearLocation1 = (LinearLayout) rootView.findViewById(R.id.linearlocation1);
		linearHomeTown1 = (LinearLayout) rootView.findViewById(R.id.linearhometown1);
		linearProfession1 = (LinearLayout) rootView.findViewById(R.id.linearprofession1);
		linearReligion1 = (LinearLayout) rootView.findViewById(R.id.linearreligion1);
		linearPincode1 = (LinearLayout) rootView.findViewById(R.id.linearpincode1);
		linearCommunity1 = (LinearLayout) rootView.findViewById(R.id.linearcommunity1);
		linearGothra1 = (LinearLayout) rootView.findViewById(R.id.lineargothra1);
		
		imageViewProfilePic = (RectangularImageView) rootView.findViewById(R.id.imageView1);
		imageViewCamera = (ImageView) rootView.findViewById(R.id.camera);
		imageViewGallery = (ImageView) rootView.findViewById(R.id.gallery);

		imageViewProfilePic.setOnClickListener(this);
		imageViewCamera.setOnClickListener(this);
		imageViewGallery.setOnClickListener(this);

		textViewName1 = (TextView) rootView.findViewById(R.id.textView1);
		textViewEmail1 = (TextView) rootView.findViewById(R.id.textView2);
		textViewDob1 = (TextView) rootView.findViewById(R.id.textViewdob);
		textViewGender1 = (TextView) rootView.findViewById(R.id.textViewgender);
		textViewMaritalStatus1 = (TextView) rootView.findViewById(R.id.textViewrelation);
		textViewLocality1 = (TextView) rootView.findViewById(R.id.textViewlocation);
		textViewProfession1 = (TextView) rootView.findViewById(R.id.textViewprofession);
		textViewMobile1 = (TextView) rootView.findViewById(R.id.textViewmobile);
		textViewHometown1 = (TextView) rootView.findViewById(R.id.textViewhometown1);
		textViewWeddingDate1 = (TextView) rootView.findViewById(R.id.textViewweddate1);
		textViewReligion1 = (TextView) rootView.findViewById(R.id.textViewreligion1);
		textViewPincode1 = (TextView) rootView.findViewById(R.id.textViewpincode);
		textViewCommunity1 = (TextView) rootView.findViewById(R.id.textViewcommunity);
		textViewGothra1 = (TextView) rootView.findViewById(R.id.textViewgothra);
		textViewDeceasedDate = (TextView) rootView.findViewById(R.id.textdeceaseddate);
		textViewUpdatedBy = (TextView) rootView.findViewById(R.id.textupdateby);

		imageViewDob = (ImageView) rootView.findViewById(R.id.imageviewdob);
		imageViewLocation = (ImageView) rootView.findViewById(R.id.imageviewlocation);
		imageViewPincode = (ImageView) rootView.findViewById(R.id.imageviewpincode);
		imageViewHomeTown = (ImageView) rootView.findViewById(R.id.imageviewhometown);
		imageViewMob = (ImageView) rootView.findViewById(R.id.imageviewmobile);
		imageViewMarital = (ImageView) rootView.findViewById(R.id.imageviewrelation);
		imageViewWeddate = (ImageView) rootView.findViewById(R.id.imageviewweddate);
		imageViewReligion = (ImageView) rootView.findViewById(R.id.imageviewreligion);
		imageViewCommunity = (ImageView) rootView.findViewById(R.id.imageviewcommunity);
		imageViewGothra = (ImageView) rootView.findViewById(R.id.imageviewgothra);
		imageViewProfession = (ImageView) rootView.findViewById(R.id.imageviewprofession);
		
		imageViewDob.setOnClickListener(this);
		imageViewLocation.setOnClickListener(this);
		imageViewPincode.setOnClickListener(this);
		imageViewHomeTown.setOnClickListener(this);
		imageViewMob.setOnClickListener(this);
		imageViewMarital.setOnClickListener(this);
		imageViewWeddate.setOnClickListener(this);
		imageViewReligion.setOnClickListener(this);
		imageViewCommunity.setOnClickListener(this);
		imageViewGothra.setOnClickListener(this);
		imageViewProfession.setOnClickListener(this);

		btndeceased = (Button) rootView.findViewById(R.id.buttondeceased);
		btndeleteuser = (Button) rootView.findViewById(R.id.buttondeleteuser);

		viewEditProfile = rootView.findViewById(R.id.view2);
		viewImmediate = rootView.findViewById(R.id.view3);
		//viewEditfamily = rootView.findViewById(R.id.imageView3);
		//viewEditAlbum = rootView.findViewById(R.id.imageView25);

		viewEditProfile.setOnClickListener(this);

		linearLayoutOverlay = (LinearLayout) rootView.findViewById(R.id.overlay);
		linearLayoutOverlay.setOnClickListener(this);

		// TODO show profile upload layout

		userId = sharedPreferences.getString("user_id", "0");
		nodeId = sharedPreferences.getString("node_id", userId);
		sessionname = sharedPreferences.getString("sessionname", "Unknown");
		
		
		horizontialListView = (HorizontalListView) rootView.findViewById(R.id.horizontalScrollViewFamilyMembers);
		immediateFamily = new ArrayList<HashMap<String, String>>();
		ifa = new ImmediateFamilyAdapter(context, immediateFamily);
		horizontialListView.setAdapter(ifa);

		
		btndeceased.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				flag = 0;
				// TODO Auto-generated method stub
				Button btn = (Button) v;
				Log.d("button text", btn.getText().toString().trim());
				if ((btn.getText().toString().trim()).equals("Make Me Alive")) {
					boolean bool = new ConDetect(getActivity()).isOnline();
					if (bool) {
						// Create object of AsycTask and execute
						final MakeAliveTask MakeAliveTask = new MakeAliveTask();
						MakeAliveTask.execute(nodeId,userId);
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (MakeAliveTask.getStatus() == AsyncTask.Status.RUNNING){
									MakeAliveTask.cancel(true);
								}
							}
						}, 10000);
					} else {
						Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
					}
				} else {
					Log.d("profile", "deceased button!!");
					Calendar cal = Calendar.getInstance();
					day = cal.get(Calendar.DAY_OF_MONTH);
					month = cal.get(Calendar.MONTH);
					year = cal.get(Calendar.YEAR);

					Log.d("profile", "deceased button1111!!" + day + "," + month + "," + year);
					final DatePickerDialog dpd = new DatePickerDialog(getActivity(),
							new DatePickerDialog.OnDateSetListener() {
								@Override
								public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
									if (set) {
									date = dayOfMonth + "-" + (1 + monthOfYear) + "-" + year;
									boolean bool = new ConDetect(getActivity()).isOnline();
									if (bool) {
										// Create object of AsycTask and execute
										Log.d("profile", "deceased button5555!!" + userId + "," + nodeId + "," + date
												+ "," + sessionname);
										if (flag == 0) {
											flag = 1;
											final DeceasedUserTask deceasedTask = new DeceasedUserTask();
											deceasedTask.execute(userId, nodeId, date, sessionname);
											Handler handler = new Handler();
											handler.postDelayed(new Runnable() {
												@Override
												public void run() {
													if (deceasedTask.getStatus() == AsyncTask.Status.RUNNING){
														deceasedTask.cancel(true);
													}
												}
											}, 10000);
										}
									} else {
										Toast.makeText(getActivity(), "!No Internet Connection,Try again",
												Toast.LENGTH_LONG).show();
									}
								}
								}
							}, year, month, day);
					dpd.setButton(DialogInterface.BUTTON_POSITIVE, "SET", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_POSITIVE) {
								set = true;
							}
						}
					});

					dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int which) {
							if (which == DialogInterface.BUTTON_NEGATIVE) {
								set = false;
								dpd.hide();
							}
						}
					});
					dpd.show();
				}

			}
		});
		Log.i("Profile", "btndeceased click");
		btndeleteuser.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				flag = 0;
				Log.d("profile", "DElete tgos!!");
				AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
				// Setting Dialog Title
				alertDialog.setTitle("Confirm Delete...");
				// Setting Dialog Message
				alertDialog.setMessage("Are you sure you want delete?");
				// Setting Icon to Dialog
				alertDialog.setIcon(R.drawable.delete);
				// Setting Positive "Yes" Button
				alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						Log.d("profile", "delete nodeid " + nodeId + " userid " + userId);
						// Write your code here to invoke YES event
						boolean bool = new ConDetect(getActivity()).isOnline();
						if (bool) {
							// Create object of AsycTask and execute
							if (flag == 0) {
								flag = 1;
								final DeleteUserTask deleteUserTask = new DeleteUserTask();
								deleteUserTask.execute(nodeId, userId);
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (deleteUserTask.getStatus() == AsyncTask.Status.RUNNING){
											deleteUserTask.cancel(true);
										}
									}
								}, 10000);
							}
						} else {
							Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG)
									.show();
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

			}
		});		
		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final ImmediateFamilyTask iFT = new ImmediateFamilyTask();
			iFT.execute(nodeId);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (iFT.getStatus() == AsyncTask.Status.RUNNING){
						iFT.cancel(true);
					}
				}
			}, 10000);
			
			final ProfileTask pT = new ProfileTask();
			pT.execute(nodeId,userId);
			Handler handler1 = new Handler();
			handler1.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (pT.getStatus() == AsyncTask.Status.RUNNING){
						pT.cancel(true);
					}
				}
			}, 10000);

		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
		return rootView;
	}
	
	@Override
	public void onPause() {
		super.onPause();
		
		if ((pDialog != null))
			pDialog.dismiss();
		pDialog = null;
	    
	}
	
	@Override
	public void onResume() {
		super.onResume();
	}

	public class ProfileTask extends AsyncTask<String, String, String> {

		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null))
				pDialog.dismiss();
			
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Fetching profile details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return	HttpConnectionUtils.getProfileViewResponse(params[0],params[1],activity.getResources().getString(R.string.hostname)
					+ activity.getResources().getString(R.string.url_view_profile2));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("Profile Fetch Response ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				String showdeceased = loginResponseObject.getString("showdeceased");
				String showprivacy = loginResponseObject.getString("showprivacy");
				view = loginResponseObject.getString("view");
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					
					JSONObject userProfilePrivacy = loginResponseObject.getJSONObject("access");
					JSONArray data = loginResponseObject.getJSONArray("data");
					JSONObject userProfileData = (JSONObject) data.get(0);

					// String genderprivacy =
					// userProfilePrivacy.getString("Gender");
					if(userProfilePrivacy.has("dob")){
						profilePrivacyDetails.setDobprivacy(userProfilePrivacy.getString("dob")); 
					}else{
						profilePrivacyDetails.setDobprivacy("1"); 
					}
					if(userProfilePrivacy.has("locality")){
						profilePrivacyDetails.setLocalityprivacy(userProfilePrivacy.getString("locality"));
					}else{
						profilePrivacyDetails.setLocalityprivacy("1"); 
					}
					if(userProfilePrivacy.has("pin")){
						profilePrivacyDetails.setPincodeprivacy(userProfilePrivacy.getString("pin")); 
					}else{
						profilePrivacyDetails.setPincodeprivacy("1"); 
					}
					if(userProfilePrivacy.has("hometown")){
						profilePrivacyDetails.setHometownprivacy(userProfilePrivacy.getString("hometown")); 
					}else{
						profilePrivacyDetails.setHometownprivacy("1"); 
					}
					if(userProfilePrivacy.has("mobile")){
						profilePrivacyDetails.setMobileprivacy(userProfilePrivacy.getString("mobile")); 
					}else{
						profilePrivacyDetails.setMobileprivacy("1"); 
					}
					if(userProfilePrivacy.has("marital_status")){
						profilePrivacyDetails.setMaritalStatusprivacy(userProfilePrivacy.getString("marital_status")); 
					}else{
						profilePrivacyDetails.setMaritalStatusprivacy("1"); 
					}
					if(userProfilePrivacy.has("wedding_date")){
						profilePrivacyDetails.setWeddingDateprivacy(userProfilePrivacy.getString("wedding_date"));
					}else{
						profilePrivacyDetails.setWeddingDateprivacy("1"); 
					}
					if(userProfilePrivacy.has("religion")){
						profilePrivacyDetails.setReligionprivacy(userProfilePrivacy.getString("religion")); 
					}else{
						profilePrivacyDetails.setReligionprivacy("1"); 
					}
					if(userProfilePrivacy.has("community")){
						profilePrivacyDetails.setCommunityprivacy(userProfilePrivacy.getString("community")); 
					}else{
						profilePrivacyDetails.setCommunityprivacy("1"); 
					}
					if(userProfilePrivacy.has("gothra")){
						profilePrivacyDetails.setGothraprivacy(userProfilePrivacy.getString("gothra"));
					}else{
						profilePrivacyDetails.setGothraprivacy("1"); 
					}
					if(userProfilePrivacy.has("profession")){
						profilePrivacyDetails.setProfessionprivacy(userProfilePrivacy.getString("profession"));
					}else{
						profilePrivacyDetails.setProfessionprivacy("1"); 
					}				
					String dob = userProfileData.getString("Dob");
					String email = userProfileData.getString("Email");
					String firstName = userProfileData.getString("Firstname");
					String lastName = userProfileData.getString("Lastname");
					String gender = userProfileData.getString("Gender");
					String locality = userProfileData.getString("locality");
					String pincode = userProfileData.getString("pincode");
					String hometown = userProfileData.getString("hometown");
					String mobile = userProfileData.getString("mobile");
					String maritalStatus;
					if (userProfileData.has("Maritalstatus")) {
						maritalStatus = userProfileData.getString("Maritalstatus");
					} else {
						maritalStatus = "NA";
					}

					String weddingDate = userProfileData.getString("wedding_Date");
					String religion = userProfileData.getString("religion");
					String community = userProfileData.getString("community");
					String gothra = userProfileData.getString("gothra");
					String profession = userProfileData.getString("profession");
					String deceased = userProfileData.getString("deceased");
					String deceasedDate = userProfileData.getString("deceaseddate");
					String deceasedUpdateBy = userProfileData.getString("deceased_updated_by");
					Log.d("image-------fghfg------- ", "gender " + gender);
					imageViewProfilePic.setLayoutParams(new LayoutParams(180, 180));	
					RectangularImageView holder= new RectangularImageView(context);
					holder= (RectangularImageView) rootView.findViewById(R.id.imageView1);
					if (!deceased.equals("NA")) {
						holder.setBorderColor(activity.getResources().getColor(R.color.pt_gold));
						UrlImageViewHelper.setUrlDrawable(holder,
								"https://www.parivartree.com/profileimages/thumbs/" + nodeId + "PROFILE.jpeg",
								activity.getResources().getDrawable(R.drawable.male),0);
						
					}else if (gender.equals("Male")) {
						
						holder.setBorderColor(activity.getResources().getColor(R.color.pt_blue));
						UrlImageViewHelper.setUrlDrawable(holder,
								"https://www.parivartree.com/profileimages/thumbs/" + nodeId + "PROFILE.jpeg",
								activity.getResources().getDrawable(R.drawable.male),0);
					} else {
						holder.setBorderColor(Color.MAGENTA);
						UrlImageViewHelper.setUrlDrawable(holder,
								"https://www.parivartree.com/profileimages/thumbs/" + nodeId + "PROFILE.jpeg",
								activity.getResources().getDrawable(R.drawable.female), 0);
					}
										
					textViewName1.setText(firstName + " " + lastName);
					textViewEmail1.setText(email);				
					
					if (dob.equals("NA")) {
						linearDob1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewDob1.setText(dob);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getDobprivacy().equals("1"))) {
						textViewDob1.setText(dob);
					} else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getDobprivacy().equals("3")){
						textViewDob1.setText(dob);
					}else if(view.equals("1")){
						textViewDob1.setText(dob);
					}else {
						linearDob1.setVisibility(View.GONE);
					}
					if (gender.equals("NA")) {
						linearGender1.setVisibility(View.GONE);
					} else {
						textViewGender1.setText(gender);
					}
					if (maritalStatus.equals("NA")) {
						linearRelation1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewMaritalStatus1.setText(maritalStatus);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getMaritalStatusprivacy().equals("1"))) {
						textViewMaritalStatus1.setText(maritalStatus);
					} else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getMaritalStatusprivacy().equals("3")){
						textViewMaritalStatus1.setText(maritalStatus);
					} else if(view.equals("1")){
						textViewMaritalStatus1.setText(maritalStatus);
					}else {
						linearRelation1.setVisibility(View.GONE);
					}
					if (weddingDate.equals("NA")) {
						linearWedDate1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewWeddingDate1.setText(weddingDate);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getWeddingDateprivacy().equals("1"))) {
						textViewWeddingDate1.setText(weddingDate);
					} else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getWeddingDateprivacy().equals("3")){
						textViewWeddingDate1.setText(weddingDate);
					}else if(view.equals("1")){
						textViewWeddingDate1.setText(weddingDate);
					} else {
						linearWedDate1.setVisibility(View.GONE);
					}
					if (locality.equals("NA")) {
						linearLocation1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewLocality1.setText(locality);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getLocalityprivacy().equals("1"))) {
						textViewLocality1.setText(locality);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getLocalityprivacy().equals("3")){
						textViewLocality1.setText(locality);
					}else if(view.equals("1")){
						textViewLocality1.setText(locality);
					} else {
						linearLocation1.setVisibility(View.GONE);
					}
					if (pincode.equals("NA")) {
						linearPincode1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewPincode1.setText(pincode);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getPincodeprivacy().equals("1"))) {
						textViewPincode1.setText(pincode);
					} else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getPincodeprivacy().equals("3")){
						textViewPincode1.setText(pincode);
					} else if(view.equals("1")){
						textViewPincode1.setText(pincode);
					}else {
						linearPincode1.setVisibility(View.GONE);
					}
					if (hometown.equals("NA")) {
						linearHomeTown1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewHometown1.setText(hometown);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getHometownprivacy().equals("1"))) {
						textViewHometown1.setText(hometown);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getHometownprivacy().equals("3")){
						textViewHometown1.setText(hometown);
					} else if(view.equals("1")){
						textViewHometown1.setText(hometown);
					}else {
						linearHomeTown1.setVisibility(View.GONE);
					}
					if (mobile.equals("NA")) {
						linearMobile1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewMobile1.setText(mobile);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getMobileprivacy().equals("1"))) {
						textViewMobile1.setText(mobile);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getMobileprivacy().equals("3")){
						textViewMobile1.setText(mobile);
					}else if(view.equals("1")){
						textViewMobile1.setText(mobile);
					} else {
						linearMobile1.setVisibility(View.GONE);
					}
					if (religion.equals("NA")) {
						linearReligion1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewReligion1.setText(religion);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getReligionprivacy().equals("1"))) {
						textViewReligion1.setText(religion);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getReligionprivacy().equals("3")){
						textViewReligion1.setText(religion);
					} else if(view.equals("1")){
						textViewReligion1.setText(religion);
					}else {
						linearReligion1.setVisibility(View.GONE);
					}
					if (community.equals("NA")) {
						linearCommunity1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewCommunity1.setText(community);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getCommunityprivacy().equals("1"))) {
						textViewCommunity1.setText(community);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getCommunityprivacy().equals("3")){
						textViewCommunity1.setText(community);
					} else if(view.equals("1")){
						textViewCommunity1.setText(community);
					}else {
						linearCommunity1.setVisibility(View.GONE);
					}
					if (gothra.equals("NA")) {
						linearGothra1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewGothra1.setText(gothra);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getGothraprivacy().equals("1"))) {
						textViewGothra1.setText(gothra);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getGothraprivacy().equals("3")){
						textViewGothra1.setText(gothra);
					} else if(view.equals("1")){
						textViewGothra1.setText(gothra);
					}else {
						linearGothra1.setVisibility(View.GONE);
					}
					if (profession.equals("NA")) {
						linearProfession1.setVisibility(View.GONE);
					} else if (userId.equals(nodeId)) {
						textViewProfession1.setText(profession);
					} else if (listFamilyId.contains(userId) && (!profilePrivacyDetails.getProfessionprivacy().equals("1"))) {
						textViewProfession1.setText(profession);
					}  else if(!(listFamilyId.contains(userId)) && profilePrivacyDetails.getProfessionprivacy().equals("3")){
						textViewProfession1.setText(profession);
					}else if(view.equals("1")){
						textViewProfession1.setText(profession);
					} else {
						linearProfession1.setVisibility(View.GONE);
					}
					
					if (view.equals("1")) {
						viewEditProfile.setVisibility(View.VISIBLE);
					}else if(view.equals("2")){
						viewEditProfile.setVisibility(View.INVISIBLE);
					}
					if (showprivacy.equals("0")) {
						imageViewDob.setVisibility(View.INVISIBLE);
						imageViewLocation.setVisibility(View.INVISIBLE);
						imageViewPincode.setVisibility(View.INVISIBLE);
						imageViewHomeTown.setVisibility(View.INVISIBLE);
						imageViewMob.setVisibility(View.INVISIBLE);
						imageViewMarital.setVisibility(View.INVISIBLE);
						imageViewWeddate.setVisibility(View.INVISIBLE);
						imageViewReligion.setVisibility(View.INVISIBLE);
						imageViewCommunity.setVisibility(View.INVISIBLE);
						imageViewGothra.setVisibility(View.INVISIBLE);
						imageViewProfession.setVisibility(View.INVISIBLE);
					}else if(showprivacy.equals("1")){
						imageViewDob.setVisibility(View.VISIBLE);
						imageViewLocation.setVisibility(View.VISIBLE);
						imageViewPincode.setVisibility(View.VISIBLE);
						imageViewHomeTown.setVisibility(View.VISIBLE);
						imageViewMob.setVisibility(View.VISIBLE);
						imageViewMarital.setVisibility(View.VISIBLE);
						imageViewWeddate.setVisibility(View.VISIBLE);
						imageViewReligion.setVisibility(View.VISIBLE);
						imageViewCommunity.setVisibility(View.VISIBLE);
						imageViewGothra.setVisibility(View.VISIBLE);
						imageViewProfession.setVisibility(View.VISIBLE);
					}
					
					if (deceased.equals("NA") && (showdeceased.equals("1"))) {
						btndeceased.setVisibility(View.VISIBLE);
						btndeleteuser.setVisibility(View.VISIBLE);
						textViewDeceasedDate.setVisibility(View.GONE);
						textViewUpdatedBy.setVisibility(View.GONE);
						btndeceased.setText("Deceased?");

					} else if (deceased.equals("NA") && (showdeceased.equals("0"))) {

						btndeleteuser.setVisibility(View.GONE);
						btndeceased.setVisibility(View.GONE);
						textViewDeceasedDate.setVisibility(View.GONE);
						textViewUpdatedBy.setVisibility(View.GONE);

					} else if ((!deceased.equals("NA")) && (userId.equals(nodeId))) {
						btndeceased.setVisibility(View.VISIBLE);
						btndeleteuser.setVisibility(View.GONE);
						textViewDeceasedDate.setVisibility(View.VISIBLE);
						textViewUpdatedBy.setVisibility(View.VISIBLE);
						btndeceased.setText("Make Me Alive");

					}else if ((!deceased.equals("NA")) && (!userId.equals(nodeId)) && (showdeceased.equals("1")) && (view.equals("1"))) {
						btndeceased.setVisibility(View.VISIBLE);
						btndeleteuser.setVisibility(View.VISIBLE);
						textViewDeceasedDate.setVisibility(View.VISIBLE);
						textViewUpdatedBy.setVisibility(View.VISIBLE);
						btndeceased.setText("Make Me Alive");

					} else if ((!deceased.equals("NA")) && (!userId.equals(nodeId)) && (showdeceased.equals("1"))) {
						btndeceased.setVisibility(View.VISIBLE);
						btndeleteuser.setVisibility(View.VISIBLE);
						textViewDeceasedDate.setVisibility(View.VISIBLE);
						textViewUpdatedBy.setVisibility(View.VISIBLE);
						btndeceased.setText("Update Deceased Date");

					} else if ((!deceased.equals("NA")) && (!userId.equals(nodeId)) && (showdeceased.equals("0"))) {
						btndeceased.setVisibility(View.GONE);
						btndeleteuser.setVisibility(View.GONE);
						textViewDeceasedDate.setVisibility(View.VISIBLE);
						textViewUpdatedBy.setVisibility(View.VISIBLE);

					} 
					if (deceasedDate.equals("NA")) {

					} else {
						textViewDeceasedDate.setText("Deceased on " + deceasedDate);
					}
					if (deceasedUpdateBy.equals("NA")) {

					} else {
						textViewUpdatedBy.setText("Updated by " + deceasedUpdateBy);
					}
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber() + " " + tempStack.getMethodName());
				}
				Log.d("ProfileFragment", " - " + e.getMessage());
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class DeceasedUserTask extends AsyncTask<String, String, String> {

		//private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Deceased user......");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getDeceasedUserResponse(
					params[0],
					params[1],
					params[2],
					params[3],
					getActivity().getResources().getString(R.string.hostname)
							+ getResources().getString(R.string.url_deceased_user));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("Deceased Fetch Response ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);

				int status = loginResponseObject.getInt("AuthenticationStatus");
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {

					textViewDeceasedDate.setVisibility(View.VISIBLE);
					textViewUpdatedBy.setVisibility(View.VISIBLE);
					btndeceased.setText("Update Deceased Date");
					textViewDeceasedDate.setText("Deceased on " + date);
					textViewUpdatedBy.setText("Updated by " + sessionname);
				}
				if (status == 2) {
					Toast.makeText(context, "Server Response Failure", Toast.LENGTH_LONG).show();
				}
				if (status == 3) {
					Toast.makeText(context, "Server Response Failure", Toast.LENGTH_LONG).show();
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
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class DeleteUserTask extends AsyncTask<String, String, String> {

		//private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Delete user...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getDeleteUserResponse(params[0], params[1], getActivity().getResources()
					.getString(R.string.hostname) + getResources().getString(R.string.url_delete_user));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("delete user Fetch Response ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					Log.i("delete user Fetch Response ", "user deleted");

					sharedPreferencesEditor.putString("node_id", userId);
					sharedPreferencesEditor.commit();
					Log.d("profile", "delete nodeid " + sharedPreferences.getString("node_id", "0") + " userid "
							+ userId);
					String nodeName = textViewName1.getText().toString();
				     Crouton.makeText(activity, "You have successfully delete " + nodeName + " from your family tree", Style.INFO).show();
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
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class MakeAliveTask extends AsyncTask<String, String, String> {

		//private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Alive user......");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getMakeAliveResponse(params[0], params[1], getActivity().getResources()
					.getString(R.string.hostname) + getResources().getString(R.string.url_make_alive));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("Deceased Fetch Response ", response);
			try {

				JSONObject loginResponseObject = new JSONObject(response);

				int status = loginResponseObject.getInt("AuthenticationStatus");
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {

//					btndeleteuser.setVisibility(View.GONE);
//					btndeceased.setVisibility(View.GONE);
//					textViewDeceasedDate.setVisibility(View.GONE);
//					textViewUpdatedBy.setVisibility(View.GONE);
					
					((MainActivity)getActivity()).changeFragment("ProfileFragment");
					
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
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (view.equals("1")) {
			if (v.getId() == R.id.view2) {
				((MainActivity) this.getActivity()).changeFragment("EditProfileFragment");
			} else if (v.getId() == R.id.imageView1) {
				linearLayoutOverlay.setVisibility(View.VISIBLE);
			}else if (v.getId() == R.id.camera) {
			    linearLayoutOverlay.setVisibility(View.INVISIBLE);
			    ((MainActivity)activity).cameraImagePath = Crop.captureImage(activity);
			   }else if (v.getId() == R.id.gallery) {
				linearLayoutOverlay.setVisibility(View.INVISIBLE);
				Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
				//startActivityForResult(i, IMAGE_PICKER_SELECT);
				Crop.pickImage(activity);

			} else {
				Log.d("profile", "linearLayoutOverlay!!");
				linearLayoutOverlay.setVisibility(View.INVISIBLE);
			}
		}
		if (v.getId() == R.id.imageviewdob) {
			showPrivacyDialog("dob",profilePrivacyDetails.getDobprivacy());
		} else if (v.getId() == R.id.imageviewlocation) {
			showPrivacyDialog("locality",profilePrivacyDetails.getLocalityprivacy());
		} else if (v.getId() == R.id.imageviewpincode) {
			showPrivacyDialog("pin",profilePrivacyDetails.getPincodeprivacy());
		} else if (v.getId() == R.id.imageviewhometown) {
			showPrivacyDialog("hometown",profilePrivacyDetails.getHometownprivacy());
		} else if (v.getId() == R.id.imageviewmobile) {
			showPrivacyDialog("mobile",profilePrivacyDetails.getMobileprivacy());
		} else if (v.getId() == R.id.imageviewrelation) {
			showPrivacyDialog("maritalstatus",profilePrivacyDetails.getMaritalStatusprivacy());
		} else if (v.getId() == R.id.imageviewweddate) {
			showPrivacyDialog("wedding_date",profilePrivacyDetails.getWeddingDateprivacy());
		} else if (v.getId() == R.id.imageviewreligion) {
			showPrivacyDialog("religion",profilePrivacyDetails.getReligionprivacy());
		} else if (v.getId() == R.id.imageviewcommunity) {
			showPrivacyDialog("community",profilePrivacyDetails.getCommunityprivacy());
		} else if (v.getId() == R.id.imageviewgothra) {
			showPrivacyDialog("gothra",profilePrivacyDetails.getGothraprivacy());
		} else if (v.getId() == R.id.imageviewprofession) {
			showPrivacyDialog("profession",profilePrivacyDetails.getProfessionprivacy());
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Here we need to check if the activity that was triggers was the Image
		// Gallery.
		// If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
		// If the resultCode is RESULT_OK and there is some data we know that an
		// image was picked.
		if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK && data != null) {
			// Let's read picked image data - its URI
			Uri pickedImage = data.getData();
			// Let's read picked image path using content resolver
			String[] filePath = { android.provider.MediaStore.Images.Media.DATA };
			Cursor cursor = activity.getContentResolver().query(pickedImage, filePath, null, null, null);
			cursor.moveToFirst();
			String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
			Log.d("image fetch------", "path : " + imagePath);
		
			String extension = "";

			int i = imagePath.lastIndexOf('.');
			if (i > 0) {
				extension = imagePath.substring(i + 1);
			}
			Log.d("image extension fetch------", "extension : " + extension);
			// Now we need to set the GUI ImageView data with data read from the
			// picked file.
			// imageViewProfilePic.setImageBitmap(BitmapFactory.decodeFile(imagePath));
			String base64str = null;
			;
			try {
				base64str = encodeFileToBase64Binary(imagePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String finalimageString = "data:image/" + extension.trim() + ";base64," + base64str;
			Log.d("base64 image string------", finalimageString);

			// execute asyncTask for image upload
			final ImageUploadTask imageUploadTask = new ImageUploadTask();
			imageUploadTask.execute(userId, finalimageString);			
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (imageUploadTask.getStatus() == AsyncTask.Status.RUNNING){
						imageUploadTask.cancel(true);
					}
				}
			}, 10000);
			// At the end remember to close the cursor or you will end with the
			// RuntimeException!
			cursor.close();
		}
	}

	private String encodeFileToBase64Binary(String fileName) throws IOException {

		File file = new File(fileName);
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.encodeBase64(bytes);
		String encodedString = new String(encoded);

		return encodedString;
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());

		}

		is.close();
		return bytes;
	}

	public class ImageUploadTask extends AsyncTask<String, String, String> {
		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Uploading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getImageUploadResponse(params[0], params[1], getActivity().getResources()
					.getString(R.string.hostname) + getResources().getString(R.string.url_profile_imageupload));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("image upload Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					Log.i("image upload Response ", "success");
					((MainActivity) activity).changeFragment("ProfileFragment");
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
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class ImmediateFamilyTask extends AsyncTask<String, String, String> {

		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			if ((pDialog != null))
				pDialog.dismiss();
			
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Fetching immediate family details...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			//pDialog.show();
			Log.d("Immediate Family", "Progress dialog called");
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_NOSENSOR);
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			// return HttpConnectionUtils.getProfileResponse(params[0],
			// getActivity().getResources().getString(R.string.hostname) +
			// getResources().getString(R.string.url_view_profile));
			return HttpConnectionUtils.getImmediateFamilyResponse(
					getActivity().getResources().getString(R.string.hostname)
							+ getResources().getString(R.string.url_immediate_family), params[0]);
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			
			try {

				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");

				// ArrayList<HashMap<String, String>> tempImmediateFamily = new
				// ArrayList<HashMap<String, String>>();
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					JSONArray data = loginResponseObject.getJSONArray("data");

					immediateFamily.clear();
					listFamilyId.clear();
					// immediateFamily.addAll(tempImmediateFamily);
					// data.length();
					for (int i = 0; i < data.length(); i++) {
						JSONObject tempData = (JSONObject) data.get(i);
						listFamilyId.add(tempData.getString("relnodeid"));
						HashMap<String, String> tempMember = new HashMap<String, String>();
						tempMember.put("relnodeid", tempData.getString("relnodeid"));
						tempMember.put("relationid", tempData.getString("relationid"));
						tempMember.put("email", tempData.getString("email"));
						tempMember.put("firstname", tempData.getString("firstname"));
						tempMember.put("lastname", tempData.getString("lastname"));
						tempMember.put("gender", tempData.getString("gender"));
						Log.d("ImmediateFamilyTask", "Added: relnodeid-" + tempData.getString("relnodeid"));
						immediateFamily.add(tempMember);
					}
					// ifa = new ImmediateFamilyAdapter(activity,
					// immediateFamily);

					Log.d("ImmediateFamilyTask", "Adapter item count: " + ifa.getCount());
					// horizontialListView.setAdapter(ifa);
					
					// ifa.notifyDataSetInvalidated();
					
					ifa.notifyDataSetChanged();
					if(immediateFamily.size()==0){
						viewImmediate.setVisibility(View.GONE);
						horizontialListView.setVisibility(View.GONE);
					}else{
						viewImmediate.setVisibility(View.VISIBLE);
						horizontialListView.setVisibility(View.VISIBLE);
					}
					Log.d("ImmediateFamilyTask", "immediateFamily size -" + immediateFamily.size());
					// horizontialListView.setAdapter(ifa);
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Log.d("profile", "Invalid Server content from Profile!!");
				Toast.makeText(context, "Invalid Server content - " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			//activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR);
			if ((pDialog != null)) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class FieldPrivacyTask extends AsyncTask<String, String, String> {

		

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			return HttpConnectionUtils
					.getFieldPrivacyResponse(
							params[0],
							params[1],
							params[2],
							getActivity().getResources().getString(R.string.hostname)
									+ getResources().getString(R.string.url_field_privacy));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("Field Privacy Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");

				if (responseResult.equals("Success")) {

				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Log.d("Field Privacy", "Invalid Server content from Profile!!");
				Toast.makeText(context, "Invalid Server content - " + e.getMessage(), Toast.LENGTH_SHORT).show();
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	private void showPrivacyDialog(final String fieldname,String setStr) {
		int set= Integer.parseInt(setStr);
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("Select a Privacy");
		builder.setSingleChoiceItems(PRIVACY,(set-1), new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int item) {
				// TODO Auto-generated method stub
				Log.d("Field Privacy", "" + fieldname + "--------" + (item + 1) + "---------" + userId);
				final FieldPrivacyTask fieldPrivacyTask = new FieldPrivacyTask();
				fieldPrivacyTask.execute(nodeId, fieldname, "" + (item + 1));
				Handler handler = new Handler();
				handler.postDelayed(new Runnable() {
					@Override
					public void run() {
						if (fieldPrivacyTask.getStatus() == AsyncTask.Status.RUNNING){
							fieldPrivacyTask.cancel(true);
						}
					}
				}, 10000);
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();

	}
}