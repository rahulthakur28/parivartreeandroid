package com.parivartree;

import java.util.Arrays;
import java.util.List;

import org.apache.http.HttpResponse;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionLoginBehavior;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.android.Facebook;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;
import com.facebook.widget.LoginButton.UserInfoChangedCallback;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.Plus.PlusOptions;
import com.google.android.gms.plus.model.people.Person;
import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class LoginDetailsActivity extends Activity implements OnClickListener, ConnectionCallbacks,
		OnConnectionFailedListener, ValidationListener {

	private String TAG = "LoginDetailsActivity";
	private SharedPreferences sharedPreferences;
	private Editor sharedPreferencesEditor;
	private Button loginButton;
	String email, emailtext;
	LoginTask lT;
	/**
	 * Facebook
	 */
	private LoginButton facebookButton;

	// Your Facebook APP ID
	private static String FACEBOOK_APP_ID = "344736635684076"; // Replace your
																// App ID here
	private UiLifecycleHelper uiHelper;

	private static final List<String> PERMISSIONS = Arrays.asList("publish_stream");
	// private static final List<String> PERMISSIONS =
	// Arrays.asList("publish_actions","email");

	// List<String> PERMISSIONS;

	// Instance of Facebook Class
	private Facebook facebook;

	/**
	 * Google Plus
	 */
	private SignInButton googleButton;
	private ConnectionResult mConnectionResult;

	private static final int RC_SIGN_IN = 0;

	PlusOptions plusOptions;

	// Google client to interact with Google API
	private GoogleApiClient mGoogleApiClient;

	/**
	 * A flag indicating that a PendingIntent is in progress and prevents us
	 * from starting further intents.
	 */
	private boolean mIntentInProgress;
	private boolean mSignInClicked;

	Activity activity;
	Context context;
	String packageName;
	LinearLayout forgotpassord;
	@Required(order = 1)
	private EditText editTextUsername;
	@Required(order = 2)
	private EditText editTextPassword;
	ImageView login;
	Validator validator;
	
	private ProgressDialog pDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_login_details);
		setContentView(R.layout.activity_logindetails);

		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		validator = new Validator(this);
		validator.setValidationListener(this);

		// PERMISSIONS = new ArrayList<String>();
		// PERMISSIONS.add("publish_stream");
		// PERMISSIONS.add("user_likes");
		// PERMISSIONS.add("email");
		// PERMISSIONS.add("user_birthday");

		activity = this;
		context = getApplicationContext();
		packageName = getPackageName();
		Intent intent = getIntent();
		Bundle bndl = intent.getExtras();
		if (bndl != null) {
			if (bndl.containsKey("croutonmsg")) {
				String msg = bndl.getString("croutonmsg");
				Crouton.makeText(activity, msg, Style.INFO).show();
			}
		}
		uiHelper = new UiLifecycleHelper(this, statusCallback);
		uiHelper.onCreate(savedInstanceState);

		login = (ImageView) findViewById(R.id.imageView4);
		// loginButton = (Button) findViewById(R.id.login);
		facebookButton = (LoginButton) findViewById(R.id.facebookLogin);
		facebookButton.setLoginBehavior(SessionLoginBehavior.SUPPRESS_SSO);
		facebookButton.setReadPermissions(Arrays.asList("email"));
		// facebookButton.setBackgroundResource(R.drawable.parivar_mobile_fb_btn_0_74);
		// facebookButton = (Button) findViewById(R.id.facebookLogin);
		googleButton = (SignInButton) findViewById(R.id.googleLogin);
		// googleButton.setBackgroundResource(R.drawable.parivar_mobile_google_btn_0_71);
		editTextUsername = (EditText) findViewById(R.id.editText1);
		editTextPassword = (EditText) findViewById(R.id.editText2);

		forgotpassord = (LinearLayout) findViewById(R.id.linearLayout5);
		// loginButton.setOnClickListener(this);
		login.setOnClickListener(this);
		forgotpassord.setOnClickListener(this);// set listener to forgot
												// password link
		facebookButton.setOnClickListener(this);
		googleButton.setOnClickListener(this);

		// Initializing google plus api client
		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addApi(Plus.API, new Plus.PlusOptions.Builder().build())
				.addScope(Plus.SCOPE_PLUS_LOGIN).build();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_details, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings) {
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onStart() {
		super.onStart();
		// mGoogleApiClient.connect();
	}

	@Override
	public void onResume() {
		super.onResume();
		editTextUsername.clearFocus();
		editTextPassword.clearFocus();
		login.setFocusable(true);
		login.requestFocus();
		uiHelper.onResume();
		// buttonsEnabled(Session.getActiveSession().isOpened());
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		
		if ((pDialog != null) && pDialog.isShowing())
			pDialog.dismiss();
		pDialog = null;
	    
	}

	@Override
	public void onSaveInstanceState(Bundle savedState) {
		super.onSaveInstanceState(savedState);
		uiHelper.onSaveInstanceState(savedState);
	}

	@Override
	protected void onStop() {
		super.onStop();
		if (mGoogleApiClient.isConnected()) {
			mGoogleApiClient.disconnect();
			Log.d(TAG, "Google API Client was connected");
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
		if (requestCode == RC_SIGN_IN) {
			if (responseCode != RESULT_OK) {
				mSignInClicked = false;
			}

			mIntentInProgress = false;

			if (!mGoogleApiClient.isConnecting()) {
				mGoogleApiClient.connect();
			}
		}
		uiHelper.onActivityResult(requestCode, responseCode, intent);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		// if(v.getId() == R.id.googleLogin && !mGoogleApiClient.isConnecting())
		// {
		if (v.getId() == R.id.googleLogin) {
			// signInWithGooglePlus();
			mGoogleApiClient.connect();
			mSignInClicked = true;
			// resolveSignInError();
			Log.d(TAG, "Google Client - resolveSignInError called...");
		} else if (v.getId() == R.id.facebookLogin) {
			// facebookButton.setSessionStatusCallback(fbStatusCallback);
			Log.d(TAG, "Facebook button clciked");
			facebookButton.setUserInfoChangedCallback(new UserInfoChangedCallback() {
				@Override
				public void onUserInfoFetched(GraphUser user) {
					if (user != null) {
						// userName.setText("Hello, " + user.getName());
						Log.d(TAG, "username - " + user.getName());
						Log.d(TAG, "user email - " + user.asMap().get("email").toString());

						String personId = user.getId();
						String personName = user.getName();
						Log.d(TAG, "NAM - " + user.getName() + " user id - " + user.getId());
						getFacebookUser();
						sharedPreferencesEditor.putString("facebook_id", personId);
						// sharedPreferencesEditor.putString("google_email",
						// email);
						sharedPreferencesEditor.commit();
						// startActivity(new Intent(activity,
						// MainActivity.class));
						String gender;
						String mgender = (String) user.getProperty("gender");
						if (mgender.equalsIgnoreCase("male") && mgender != null) {
							gender = "1";
						} else if (mgender.equalsIgnoreCase("male") && mgender != null) {
							gender = "2";
						} else {
							gender = "1";
						}
						boolean bool = new ConDetect(activity).isOnline();
						if (bool) {
							email = user.asMap().get("email").toString();
							Log.d(TAG, "gender--------- - " + (String) user.getProperty("gender"));
							final FacebookLoginTask facebookTask = new FacebookLoginTask();
							facebookTask.execute(user.asMap().get("email").toString(), user.getLastName(), gender,
									user.getFirstName());
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if (facebookTask.getStatus() == AsyncTask.Status.RUNNING) {
										facebookTask.cancel(true);
									}
								}
							}, 10000);
							// startActivity(new
							// Intent(activity,MainActivity.class));
						} else {
							Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
						}
					} else {
						// userName.setText("You are not logged");
						Log.d(TAG, "You are not logged in");
					}
				}
			});
		} else if (v.getId() == R.id.imageView4) {
			validator.validate();
		} else if (v.getId() == R.id.linearLayout5) {
			// click forgot password link
			startActivity(new Intent(activity, ForgotPasswordActivity.class));

		}
	}

	/**
	 * Sign-in into google
	 * */
	private void signInWithGooglePlus() {
		Log.d(TAG, "called - signInWithGooglePlus");
		if (!mGoogleApiClient.isConnecting()) {
			mSignInClicked = true;
			resolveSignInError();
			Log.d(TAG, "mGoogleApiClient was not connecting..");
		} else {
			Log.d(TAG, "mGoogleApiClient is connecting..");
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
		if (!mIntentInProgress) {
			// Store the ConnectionResult so that we can use it later when the
			// user clicks
			// 'sign-in'.
			mConnectionResult = result;

			if (mSignInClicked) {
				// The user has already clicked 'sign-in' so we attempt to
				// resolve all
				// errors until the user is signed in, or they cancel.
				resolveSignInError();
			}
		}
	}

	/* A helper method to resolve the current ConnectionResult error. */
	private void resolveSignInError() {
		if (mConnectionResult.hasResolution()) {
			try {
				mIntentInProgress = true;
				startIntentSenderForResult(mConnectionResult.getResolution().getIntentSender(), RC_SIGN_IN, null, 0, 0,
						0);
			} catch (SendIntentException e) {
				// The intent was canceled before it was sent. Return to the
				// default
				// state and attempt to connect to get an updated
				// ConnectionResult.
				mIntentInProgress = false;
				mGoogleApiClient.connect();
			}
		}
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		// TODO Auto-generated method stub
		mSignInClicked = false;
		Log.d(TAG, "User is connected!");

		// Get user's information
		getProfileInformation();

		// Update the UI after signin
		updateUI(true);
	}

	/**
	 * Updating the UI, showing/hiding buttons and profile layout
	 * */
	private void updateUI(boolean isSignedIn) {
		if (isSignedIn) {
			// googleButton.setVisibility(View.GONE);
			// btnSignOut.setVisibility(View.VISIBLE);
			// btnRevokeAccess.setVisibility(View.VISIBLE);
			// llProfileLayout.setVisibility(View.VISIBLE);
		} else {
			googleButton.setVisibility(View.VISIBLE);
			// btnSignOut.setVisibility(View.GONE);
			// btnRevokeAccess.setVisibility(View.GONE);
			// llProfileLayout.setVisibility(View.GONE);
		}
	}

	@Override
	public void onConnectionSuspended(int cause) {
		// TODO Auto-generated method stub
		mGoogleApiClient.connect();
	}

	/**
	 * Fetching Google user's information name, email, profile pic
	 * */
	private void getProfileInformation() {
		try {
			if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
				Person currentPerson = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
				String personId = currentPerson.getId();
				String personName = currentPerson.getDisplayName();
				String personPhotoUrl = currentPerson.getImage().getUrl();
				String personGooglePlusProfile = currentPerson.getUrl();
				email = Plus.AccountApi.getAccountName(mGoogleApiClient);

				sharedPreferencesEditor.putString("google_id", personId);
				sharedPreferencesEditor.putString("google_email", email);
				sharedPreferencesEditor.commit();

				String[] fullName = personName.split(" ");
				String gender = (currentPerson.getGender() == 0) ? "male" : "female";
				Log.i(TAG, "ID" + personId + "Name: " + personName + ", plusProfile: " + personGooglePlusProfile
						+ ", email: " + email + ", Image: " + personPhotoUrl + " gender" + currentPerson.getGender());

				boolean bool = new ConDetect(activity).isOnline();
				if (bool) {

					final FacebookLoginTask facebookTask = new FacebookLoginTask();
					facebookTask.execute(email, fullName[1], gender, fullName[0]);
					Handler handler = new Handler();
					handler.postDelayed(new Runnable() {
						@Override
						public void run() {
							if (facebookTask.getStatus() == AsyncTask.Status.RUNNING) {
								facebookTask.cancel(true);
							}
						}
					}, 10000);
					// startActivity(new Intent(activity,MainActivity.class));
				} else {
					Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
				}

			} else {
				Log.d(TAG, "Person information is null");
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * Sign in with facebook
	 */
	private Session.StatusCallback statusCallback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state, Exception exception) {
			if (state.isOpened()) {
				// buttonsEnabled(true);
				// getFacebookUser();

				Log.d("FacebookSampleActivity", "Facebook session opened");
			} else if (state.isClosed()) {
				// buttonsEnabled(false);
				Log.d("FacebookSampleActivity", "Facebook session closed");
			}
		}
	};

	public boolean checkPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			return s.getPermissions().contains("publish_actions");
		} else
			return false;
	}

	public void requestPermissions() {
		Session s = Session.getActiveSession();
		if (s != null) {
			s.requestNewPublishPermissions(new Session.NewPermissionsRequest(this, PERMISSIONS));
		}
	}

	Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
		@SuppressWarnings("deprecation")
		public void call(Session session, SessionState state, Exception exception) {

			Request.newMeRequest(session, new Request.GraphUserCallback() {

				public void onCompleted(GraphUser user, Response response) {
					String fb_id, fb_name, fb_gender, fb_email, fb_birthday, fb_locale, fb_location;

					if (response != null) {
						// do something with <response> now
						try {
							fb_id = user.getId();
							fb_name = user.getName();
							fb_gender = (String) user.getProperty("gender");
							fb_email = (String) user.getProperty("email");
							fb_birthday = user.getBirthday();
							fb_locale = (String) user.getProperty("locale");
							fb_location = user.getLocation().toString();

							Log.d("Facebook User Data",
									user.getId() + "; " + user.getName() + "; " + (String) user.getProperty("gender")
											+ "; " + (String) user.getProperty("email") + "; " + user.getBirthday()
											+ "; " + (String) user.getProperty("locale") + "; " + user.getLocation());
						} catch (Exception e) {
							e.printStackTrace();
							Log.d("Facebook User Data", "Exception e");
						}

					}
				}
				/*
				 * @Override public void onCompleted(GraphUser user, Response
				 * response) { // TODO Auto-generated method stub
				 * 
				 * }
				 */
			});
		}
	};

	private void getFacebookUser() {
		Session.StatusCallback fbStatusCallback = new Session.StatusCallback() {
			@SuppressWarnings("deprecation")
			public void call(Session session, SessionState state, Exception exception) {

				Request.newMeRequest(session, new Request.GraphUserCallback() {

					public void onCompleted(GraphUser user, Response response) {
						String fb_id, fb_name, fb_gender, fb_email, fb_birthday, fb_locale, fb_location;

						if (response != null) {
							// do something with <response> now
							try {
								fb_id = user.getId();
								fb_name = user.getName();
								fb_gender = (String) user.getProperty("gender");
								fb_email = (String) user.getProperty("email");
								fb_birthday = user.getBirthday();
								fb_locale = (String) user.getProperty("locale");
								fb_location = user.getLocation().toString();

								Log.d("Facebook User Data",
										user.getId() + "; " + user.getName() + "; "
												+ (String) user.getProperty("gender") + "; "
												+ (String) user.getProperty("email") + "; " + user.getBirthday() + "; "
												+ (String) user.getProperty("locale") + "; " + user.getLocation());
							} catch (Exception e) {
								e.printStackTrace();
								Log.d("Facebook User Data", "Exception e");
							}

						}
					}
					/*
					 * @Override public void onCompleted(GraphUser user,
					 * Response response) { // TODO Auto-generated method stub
					 * 
					 * }
					 */
				});
			}
		};
		if (checkPermissions()) {
		}
	}

	public class LoginTask extends AsyncTask<String, String, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginDetailsActivity.this);
			pDialog.setMessage("Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			String response;
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			response = HttpConnectionUtils.getLoginResponse(params[0], params[1],
					getResources().getString(R.string.hostname)
							+ activity.getResources().getString(R.string.url_login1));

			// for (int i = 0; i < 10000; i++) {
			// Log.d("forloop ", "running");
			// if(isCancelled()){
			// Log.d("isCancelled ", "hooopliiings");
			// break;
			// }
			// }
			return response;
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			//pDialog.dismiss();
			Log.i("Login Response ", response);
			try {
				String responseResult,message="Please check your inputs";
				JSONObject loginResponseObject = new JSONObject(response);
				if(loginResponseObject.has("result")){
					responseResult = loginResponseObject.getString("result");
				}
				if(loginResponseObject.has("msg")){
					message = loginResponseObject.getString("msg");
				}
				int status = loginResponseObject.getInt("AuthenticationStatus");
				if (status == 1) {
					// TODO store the login response and
					int userId = loginResponseObject.getInt("user_id");
					String username = loginResponseObject.getString("username");
					sharedPreferences = getSharedPreferences(
							getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
							Context.MODE_PRIVATE);
					sharedPreferencesEditor = sharedPreferences.edit();
					Log.d(TAG, "SharedPreference: userid - " + userId);
					sharedPreferencesEditor.putString("user_id", "" + userId);
					sharedPreferencesEditor.putString("node_id", "" + userId);
					sharedPreferencesEditor.putString("sessionname", username);
					sharedPreferencesEditor.putString("sessionemail", emailtext);
					sharedPreferencesEditor.commit();

					startActivity(new Intent(activity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK));
				} else if (status == 2) {
					// TODO Account blocked
					/**
					 * Your account has been blocked for crossing maximum
					 * authentication failure attempts! Please click on forgot
					 * password to re-generate your password
					 */
					Crouton.makeText(
							activity,
							message,
							Style.ALERT).show();
				} else if (status == 3) {
					// TODO account disable
					/**
					 * Your account has been deactivated. Please contact the
					 * administrator!
					 */
					Crouton.makeText(activity, "Your account has been deactivated. Please contact the administrator!",
							Style.ALERT).show();
				} else if (status == 4) {
					// TODO second last failed attempt. Just one more
					// attempt left
					/**
					 * Authentication Failed! Please check your Email Id or
					 * Password! This is your fourth consecutive authenticattion
					 * failure. One more attempt, and you shall be blocked from
					 * accessing
					 */
					Crouton.makeText(activity, "Authentication Failed!, Just one more attempt left", Style.ALERT)
							.show();
				} else if (status == 5 || status == 7) {
					// TODO authentication failed please check your
					// email/password
					/**
					 * Authentication Failed! Please check your Email Id or
					 * Password!
					 */
					Crouton.makeText(activity, "Login Not Successful", Style.ALERT).show();
				} else if (status == 6) {
					// TODO authentication failed and account has been
					// blocked
					/**
					 * Authentication Failed! Please check your Email Id or
					 * Password! Your account has been blocked for 5 continous
					 * authentication failure attempts
					 */
					Crouton.makeText(activity, "Your last attempt failed and account has been blocked", Style.ALERT)
							.show();
				} else {
					// TODO redirect to home page "status=8"
					/**
					 * this means a failure from the api
					 */
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");

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

	class FacebookLoginTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(LoginDetailsActivity.this);
			pDialog.setMessage("Login...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			Log.d(TAG, "Data- " + params[0] + "," + params[1] + "," + params[2] + "," + params[3]);
			return HttpConnectionUtils.getFacebookResponse(params[0], params[1], params[2], params[3], getResources()
					.getString(R.string.hostname) + activity.getResources().getString(R.string.url_facebook_login));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			Log.i("Facebook Login Response ", response);
			try {

				JSONObject faceloginResponseObject = new JSONObject(response);
				int status = faceloginResponseObject.getInt("AuthenticationStatus");
				Log.d("Facebook Login AuthenticationStatus : ", "" + status);
				String responseResult = faceloginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					// TODO store the login response and
					int userId = faceloginResponseObject.getInt("userid");
					String username = faceloginResponseObject.getString("username");
					sharedPreferences = getSharedPreferences(
							getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
							Context.MODE_PRIVATE);
					sharedPreferencesEditor = sharedPreferences.edit();
					Log.d(TAG, "SharedPreference: userid - " + userId);
					sharedPreferencesEditor.putString("user_id", "" + userId);
					sharedPreferencesEditor.putString("node_id", "" + userId);
					sharedPreferencesEditor.putString("sessionname", username);
					sharedPreferencesEditor.putString("sessionemail", email);
					sharedPreferencesEditor.commit();

					startActivity(new Intent(activity, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
							| Intent.FLAG_ACTIVITY_CLEAR_TASK));
				} else {
					Log.d("Facebook Login AuthenticationStatus : ", "" + status);
					Toast.makeText(context, "Facebook Login Not Successful", Toast.LENGTH_LONG).show();
					if (status == 1) {
						// TODO successful authentication

					}
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					// Log.d("Exception thrown: Treeview Fetch", "" +
					// tempStack.getLineNumber());
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
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
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		/**
		 * add unsaved data (password), as well as email in user preferences
		 */
		// /asynTAsk execute
		boolean bool = new ConDetect(this).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			emailtext = editTextUsername.getText().toString();
			lT = new LoginTask();
			lT.execute(editTextUsername.getText().toString(), editTextPassword.getText().toString());
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (lT.getStatus() == AsyncTask.Status.RUNNING) {
						lT.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(this, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
		}
	}

	@Override
	public void onValidationFailed(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
		String message = failedRule.getFailureMessage();

		if (failedView instanceof EditText) {
			failedView.requestFocus();
			((EditText) failedView).setError(message);
		} else {
			Log.d("Signup settings ", message);
		}
	}

}
