package com.parivartree;

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
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.SignUpDetailsActivity.MobileVerifyTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ForgotPasswordActivity extends Activity implements OnClickListener, ValidationListener {
	private String TAG = "ForgotPassordActivity";
	@Required(order = 1)
	EditText editEmail;
	ImageView btnSend;
	TextView enterCode;
	String forgotEmail;
	Activity activity;
	Context context;
	Validator validator;
	SharedPreferences sharedPreferences;
	View alertDialogView;
	Editor sharedPreferencesEditor;
	String message,mobileCode,mobileHash,sessionHash;
	int mobileAttempt,mobileUid;
	ProgressDialog 	pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgot_password);
		validator = new Validator(this);
		validator.setValidationListener(this);
		
		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferencesEditor.commit();
		
		activity = this;
		editEmail = (EditText) findViewById(R.id.editforgotemail);
		btnSend = (ImageView) findViewById(R.id.imageView4);
		btnSend.setOnClickListener(this);
		enterCode = (TextView) findViewById(R.id.enterCode);
		enterCode.setOnClickListener(this);
	}
	@Override
	 public void onPause() {
	  super.onPause();
	  
	  if ((pDialog != null) && pDialog.isShowing())
	   pDialog.dismiss();
	  pDialog = null;
	     
	 }
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.forgot_password, menu);
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
		if(v.getId() == R.id.imageView4){
			validator.validate();
		}else if(v.getId() == R.id.enterCode){

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
					EditText otpCode = (EditText) alertDialogView.findViewById(R.id.editText1);
					if ((otpCode.getText().toString().trim()).length() > 0) {
						// TODO send edit code to the server and show
						// appropriate
						// response
						boolean bool = new ConDetect(activity).isOnline();
						if (bool) {
							final MobileVerifyTask mobileVerifyTask = new MobileVerifyTask();							
							mobileVerifyTask.execute(sharedPreferences.getString("user_hash_forgot", "NA"), otpCode.getText().toString().trim());
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
			alert.getWindow().setLayout(700, 700); //Controlling width and height.
			alert.show();
		
		}
	}

	public class ForgotTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setMessage("");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground" + params[0]);
			return HttpConnectionUtils.getForgotPasswordResponse(params[0], getResources().getString(R.string.hostname)
					+ activity.getResources().getString(R.string.url_forgot_password));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
			    pDialog.dismiss();
			   }
			Log.i("forgot password Response ", response);
			String flag ="";
			try {
				JSONObject forgotResponseObject = new JSONObject(response);
				int status = forgotResponseObject.getInt("AuthenticationStatus");
				String responseResult = forgotResponseObject.getString("Status");
				if (forgotResponseObject.has("msg")) {
					message = forgotResponseObject.getString("msg");
				}
				if (forgotResponseObject.has("mobilecode")) {
					mobileCode = forgotResponseObject.getString("mobilecode");
				}
				if (forgotResponseObject.has("hash")) {
					mobileHash = forgotResponseObject.getString("hash");
					sharedPreferencesEditor = sharedPreferences.edit();
					sharedPreferencesEditor.putString("user_hash_forgot", mobileHash);
					sharedPreferencesEditor.commit();
				}
				if (forgotResponseObject.has("flag")){
					flag = forgotResponseObject.getString("flag");
				}
				Log.d(TAG, "onpostexecute" + responseResult);
				if (status == 1) {
					Log.d(TAG, "onpostexecute : Check u r mail");
					// mail send to user
					Log.d(TAG, "User is connected!");
					if (flag.equals("mobile")) {
						Crouton.makeText(activity, message, Style.INFO).show();
					}else if(flag.equals("email")){				
						String croutonmsg =  "Please check your Mail and Click the Link";
						Intent intentsuccess = new Intent(ForgotPasswordActivity.this, LoginDetailsActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
						intentsuccess.putExtra("croutonmsg", croutonmsg);
						startActivity(intentsuccess);
					}				
				} else if (status == 2) {
					Crouton.makeText(activity, message, Style.INFO).show();
				}
			} catch (Exception e) {
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

		boolean bool = new ConDetect(activity).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final ForgotTask forgottask = new ForgotTask();
			forgottask.execute(editEmail.getText().toString());
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (forgottask.getStatus() == AsyncTask.Status.RUNNING){
						forgottask.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(activity, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
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
	public class MobileVerifyTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
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
					Intent intentsuccess = new Intent(ForgotPasswordActivity.this, ChangingPasswordActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intentsuccess.putExtra("sessionHash", sessionHash);
					startActivity(intentsuccess);
				} else if (status == 2) {
					// TODO redirect to home page
					Crouton.makeText(activity, message, Style.ALERT).show();
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
}
