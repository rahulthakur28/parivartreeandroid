package com.parivartree;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AutoCompleteTextView;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.adapters.LocationHintAdapter;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SignUpDetailsActivity extends Activity implements OnClickListener, ValidationListener {
	public static final String REGEX_EMAIL = "[a-zA-Z0-9\\+\\.\\_\\%\\-\\+]{1,256}"
			+ "\\@[a-zA-Z0-9][a-zA-Z0-9\\-]{0,64}" + "(\\.[a-zA-Z0-9][a-zA-Z0-9\\-]{0,25})+";
	ImageView next;
	int day, month, year;
	DatePicker datePicker;
	String Gender = "1";
	@Required(order = 1)
	EditText editTextFirstName;
	@Required(order = 2)
	EditText editTextLastName;
	@Required(order = 3)
	EditText editTextEmail;
	@Required(order = 4)
	AutoCompleteTextView editTextLocation;
	EditText otpCode;

	private LocationHintAdapter locationHintAdpter;
	private ArrayList<String> locationHints;
	SearchPlacesTask searchPlacesTask;
	String emailMobileTest, successFlag, mobileCode, successHash, sessionHash, croutonmsg,successemailHash;
	int mobileUid, mobileAttempt;

	TextView enterCode;

	View alertDialogView,alertDialogViewpassword;

	// EditText editTextPhone;

	// @Required(order = 4)
	EditText editTextPassword;

	// @ConfirmPassword(order = 5)
	EditText editTextRePassword;

	Switch switchGender;

	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	// Saripaar validator
	Validator validator;
	Activity activity;
	ProgressDialog pDialog;
	private final String TAG = "SignUpDetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup_details);
		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferencesEditor.commit();	
		activity = this;
		validator = new Validator(this);
		validator.setValidationListener(this);
		editTextFirstName = (EditText) findViewById(R.id.editText1);
		editTextLastName = (EditText) findViewById(R.id.editText2);
		editTextEmail = (EditText) findViewById(R.id.editText3);

		editTextLocation = (AutoCompleteTextView) findViewById(R.id.editText4);

		enterCode = (TextView) findViewById(R.id.enterCode);
		enterCode.setOnClickListener(this);

		// editTextPhone = (EditText) findViewById(R.id.editText3);
		// editTextPassword = (EditText) findViewById(R.id.editText4);
		// editTextRePassword = (EditText) findViewById(R.id.editText5);

		switchGender = (Switch) findViewById(R.id.switch1);

		// datePicker = (DatePicker) findViewById(R.id.datePicker1);

		next = (ImageView) findViewById(R.id.nextButton);
		next.setOnClickListener(this);
		switchGender.setOnCheckedChangeListener(new OnCheckedChangeListener() {

			@Override
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

				if (isChecked) {
					// Gender="Female";
					Gender = "2";
				} else {
					// Gender="Male";
					Gender = "1";
				}
			}
		});

		// provide hinting for the location fields from Google Places API
		locationHints = new ArrayList<String>();
		locationHintAdpter = new LocationHintAdapter(activity, R.layout.item_location, locationHints);
		editTextLocation.setAdapter(locationHintAdpter);
		editTextLocation.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				Log.d("Search User ", "s=" + s + " ,start=" + start + " ,count=" + count + " ,after=" + after);
				boolean bool = new ConDetect(activity).isOnline();
				if (bool) {
					if (searchPlacesTask != null) {
						searchPlacesTask.cancel(true);
					}
					Log.d("Search user", "AsyncTask calling");
					searchPlacesTask = new SearchPlacesTask();
					searchPlacesTask.execute(s.toString().trim(), getResources().getString(R.string.places_key));
				} else {
					Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
				}
			}

			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub

			}

			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});
	}
	public void onPause() {
		super.onPause();
		
		if ((pDialog != null) && pDialog.isShowing())
			pDialog.dismiss();
		pDialog = null;
	    
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up_details, menu);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.nextButton) {
			validator.validate();

		} else if (v.getId() == R.id.enterCode) {
			// show popup for entering code
			AlertDialog alert;
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			// Setting Dialog Title
			alertDialog.setTitle("Confirm Your Mobile");
			// set custom view
			alertDialogView = getLayoutInflater().inflate(R.layout.alert_dialog_otp_code, null);
			alertDialog.setView(alertDialogView);
			// Setting Dialog Message
			// alertDialog.setMessage("Enter code");
			alertDialog.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					otpCode = (EditText) alertDialogView.findViewById(R.id.editText1);
					if ((otpCode.getText().toString().trim()).length() > 0) {
						// TODO send edit code to the server and show
						// appropriate
						// response
						boolean bool = new ConDetect(activity).isOnline();
						if (bool) {
							final MobileVerifyTask mobileVerifyTask = new MobileVerifyTask();							
							mobileVerifyTask.execute(sharedPreferences.getString("user_hash", "NA"), otpCode.getText().toString().trim());
							Handler handler = new Handler();
							handler.postDelayed(new Runnable() {
								@Override
								public void run() {
									if (mobileVerifyTask.getStatus() == AsyncTask.Status.RUNNING) {
										mobileVerifyTask.cancel(true);
									}
								}
							}, 10000);
						} else {
							Crouton.makeText(activity, "!No Internet Connection,Try again", Style.ALERT).show();
						}
					}

				}
			});

			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to invoke NO event
					dialog.cancel();
				}
			});
			alert = alertDialog.create();
			alert.show();
		}
	}

	public class SignUpDetailsTask extends AsyncTask<String, String, String> {

		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUpDetailsActivity.this);
			pDialog.setMessage("");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return HttpConnectionUtils.getSignupResponse(params[0], params[1], params[2], params[3], params[4],
					getResources().getString(R.string.hostname)
							+ activity.getResources().getString(R.string.url_signup));
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if ((pDialog != null) && pDialog.isShowing()) { 
				pDialog.dismiss();
			}
			Log.i("Signup Response ", result);
			try {
				String message = "";
				JSONObject SignupResponseObject = new JSONObject(result);
				int status = SignupResponseObject.getInt("AuthenticationStatus");
				if (SignupResponseObject.has("msg")) {
					message = SignupResponseObject.getString("msg");
				}
				if (SignupResponseObject.has("flag")) {
					successFlag = SignupResponseObject.getString("flag");
				}
				if (SignupResponseObject.has("mobilecode")) {
					mobileCode = SignupResponseObject.getString("mobilecode");
				}
				if (SignupResponseObject.has("hash")) {
					successHash = SignupResponseObject.getString("hash");
					sharedPreferencesEditor = sharedPreferences.edit();
					sharedPreferencesEditor.putString("user_hash", successHash);
					sharedPreferencesEditor.commit();
				}
				if (SignupResponseObject.has("userhash")) {
					successemailHash = SignupResponseObject.getString("userhash");
				}
				if (status == 1) {
					if (successFlag.equals("mobile")) {
						Crouton crouton;
						crouton = Crouton.makeText(activity, message, Style.INFO);
						crouton.setOnClickListener(SignUpDetailsActivity.this).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(7000).build()).show();
					} else if (successFlag.equals("email")) {
						croutonmsg =  "Please check your Mail and Click the Link";
						Intent intentsuccess = new Intent(SignUpDetailsActivity.this, LoginMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intentsuccess.putExtra("croutonmsg", croutonmsg);
						startActivity(intentsuccess);
					}
					// TODO successful authentication
					Log.d("Signup Response ", "Registration was successful");
					
				} else if (status == 2) {
					// TODO redirect to home page
					Crouton.makeText(activity, message, Style.ALERT).show();
				}else if (status == 4) {
					// TODO redirect to home page
					Crouton crouton;
					crouton = Crouton.makeText(activity, message, Style.ALERT);
					crouton.setOnClickListener(SignUpDetailsActivity.this).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(7000).build()).show();			
				} else if (status == 5) {
					// TODO redirect to home page
					croutonmsg = 
							"This "
									+ emailMobileTest
									+ " is registered with us. Please login to access the Parivartree, or click forgot password link, if you have forgotten your password.";
					Crouton crouton;
					crouton = Crouton.makeText(activity, croutonmsg, Style.ALERT);
					crouton.setOnClickListener(SignUpDetailsActivity.this).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(8000).build()).show();
					//Intent intentfail = new Intent(SignUpDetailsActivity.this, LoginMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					//intentfail.putExtra("croutonmsg", croutonmsg);
					//startActivity(intentfail);
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("Signup Response ", "Invalid Server content!!");
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

	public class MobileVerifyTask extends AsyncTask<String, String, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUpDetailsActivity.this);
			pDialog.setMessage("");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return HttpConnectionUtils.getMobileVerifyResponse(
					params[0],
					params[1],
					getResources().getString(R.string.hostname)
							+ activity.getResources().getString(R.string.url_mobileverify));
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			if ((pDialog != null) && pDialog.isShowing()) { 
			    pDialog.dismiss();
			   }
			Log.i("Signup Response ", result);
			try {
				String message = "";
				JSONObject SignupResponseObject = new JSONObject(result);
				int status = SignupResponseObject.getInt("AuthenticationStatus");
				if(SignupResponseObject.has("msg")){
					message = SignupResponseObject.getString("msg");
				}
				if(SignupResponseObject.has("attempt")){
					mobileAttempt = SignupResponseObject.getInt("attempt");
				}
				if(SignupResponseObject.has("uid")){
					mobileUid= SignupResponseObject.getInt("uid");
				}
				if(SignupResponseObject.has("sessionhash")){
					sessionHash= SignupResponseObject.getString("sessionhash");
//					sharedPreferencesEditor = sharedPreferences.edit();
//					sharedPreferencesEditor.putString("session_hash", sessionHash);
//					sharedPreferencesEditor.commit();
				}
				if (status == 1) {		
					// TODO successful authentication
					Log.d("Signup Response ", "Registration was successful");		
					//call for password setting dialog
					Intent intentsuccess = new Intent(SignUpDetailsActivity.this, ChangingPasswordActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intentsuccess.putExtra("sessionHash", sessionHash);
					startActivity(intentsuccess);
				} else if (status == 2) {
					// TODO redirect to home page
					Crouton crouton;
					crouton = Crouton.makeText(activity, message, Style.ALERT);
					crouton.setOnClickListener(SignUpDetailsActivity.this).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(7000).build()).show();			
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("Signup Response ", "Invalid Server content!!");
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
		// SignUpDetailsTask sUDT = new SignUpDetailsTask();
		// sUDT.execute(params)

		emailMobileTest = editTextEmail.getText().toString();
		Log.d(TAG, "email:" + editTextEmail.getText().toString() + ", fname:" + editTextFirstName.getText().toString()
				+ ", lname" + editTextLastName.getText().toString() + ", gender :" + Gender);

		boolean bool = new ConDetect(activity).isOnline();
		if (bool) {
			final SignUpDetailsTask sigupTask = new SignUpDetailsTask();
			sigupTask.execute(editTextEmail.getText().toString(), editTextFirstName.getText().toString(),
					editTextLastName.getText().toString(), Gender, editTextLocation.getText().toString());
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (sigupTask.getStatus() == AsyncTask.Status.RUNNING) {
						sigupTask.cancel(true);
					}
				}
			}, 10000);
		} else {
			Crouton.makeText(activity, "!No Internet Connection,Try again", Style.ALERT).show();
		}
	}

	@Override
	public void onValidationFailed(View failedView, Rule<?> failedRule) {
		// TODO Auto-generated method stub
		String message = failedRule.getFailureMessage();

		if (failedView instanceof EditText) {
			failedView.requestFocus();
			((EditText) failedView).setError(message);
		} else if (failedView instanceof AutoCompleteTextView) {
			failedView.requestFocus();
			((AutoCompleteTextView) failedView).setError(message);
		} else {
			Log.d("Signup Response ", message);
		}
	}

	/**
	 * Get hint of places from google servers
	 * 
	 * @author rahul
	 *
	 */
	public class SearchPlacesTask extends AsyncTask<String, Void, String> {
		// private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {

			// TODO Auto-generated method stub
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground uid  " + params[0]);
			return HttpConnectionUtils.getPlacesResponse(params[0], params[1]);
		}

		protected void onPostExecute(String response) {

			super.onPostExecute(response);
			// pDialog.dismiss();
			Log.i("Ceate Event Response ", response);
			try {
				JSONObject createEventObject = new JSONObject(response);
				JSONArray predictionsArray = createEventObject.getJSONArray("predictions");
				/*
				 * String responseResult =
				 * createEventObject.getString("Status"); Log.d(TAG,
				 * "onpostexecute" + responseResult); if
				 * (responseResult.equals("Success")) { }
				 */
				locationHints.clear();
				for (int i = 0; i < predictionsArray.length() && i < 20; i++) {
					JSONObject tempItem = predictionsArray.getJSONObject(i);
					locationHints.add(tempItem.getString("description"));
				}
				locationHintAdpter = new LocationHintAdapter(activity, R.layout.item_location, locationHints);
				editTextLocation.setAdapter(locationHintAdpter);
				locationHintAdpter.notifyDataSetChanged();
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content!!");
			}
		}
	}
}
