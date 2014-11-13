package com.parivartree;

import org.json.JSONObject;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.SignUpDetailsActivity.SignUpDetailsTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
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
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class OtpcodeActivity extends Activity implements OnClickListener , ValidationListener{
	@Required(order = 1)
EditText editotpcode;
Button submitOtpBtn;
Activity activity;
SharedPreferences sharedPreferences;
Editor sharedPreferencesEditor;
String mobileUid,mobileNumber, mobileCode, successHash,croutonmsg;
// Saripaar validator
Validator validator;
ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_otpcode);
		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		sharedPreferencesEditor.commit();
		activity = this;
		validator = new Validator(this);
		validator.setValidationListener(this);
		Intent intent = getIntent();
		Bundle bndl = intent.getExtras();
		if(bndl != null){		
			if(bndl.containsKey("croutonmsg")){
				croutonmsg = bndl.getString("croutonmsg");
				Crouton.makeText(activity, croutonmsg, Style.INFO).show();
			}
			if(bndl.containsKey("mobileCode")){
				mobileCode = bndl.getString("mobileCode");
			}
			if(bndl.containsKey("successHash")){
				successHash = bndl.getString("successHash");
			}
			if(bndl.containsKey("mobileUid")){
				mobileUid = bndl.getString("mobileUid");
			}
			if(bndl.containsKey("mobileNumber")){
				mobileNumber = bndl.getString("mobileNumber");
			}
		}
		editotpcode = (EditText) findViewById(R.id.editTextotpcode);
		submitOtpBtn = (Button) findViewById(R.id.submitotpcodebtn);
		submitOtpBtn.setOnClickListener(this);
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
		getMenuInflater().inflate(R.menu.otpcode, menu);
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
		if(v.getId() == R.id.submitotpcodebtn){
			validator.validate();
		}
	}
	@Override
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		/**
		 * add unsaved data (password), as well as email in user preferences
		 */
		boolean bool = new ConDetect(activity).isOnline();
		if (bool) {
			final MobileVerifyProfileTask mobileVerifyProfileTask = new MobileVerifyProfileTask();
			mobileVerifyProfileTask.execute(successHash, (editotpcode.getText().toString().trim()), mobileNumber, mobileUid);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (mobileVerifyProfileTask.getStatus() == AsyncTask.Status.RUNNING) {
						mobileVerifyProfileTask.cancel(true);
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
	public class MobileVerifyProfileTask extends AsyncTask<String, String, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(OtpcodeActivity.this);
			pDialog.setMessage("");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return HttpConnectionUtils.getMobileVerifyProfileUpdateResponse(
					params[0],
					params[1],
					params[2],
					params[3],
					getResources().getString(R.string.hostname)
							+ activity.getResources().getString(R.string.url_mobileverifyprofileupdate));
		}
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();	if ((pDialog != null) && pDialog.isShowing()) { 
			    pDialog.dismiss();
			   }
			Log.i("Signup Response ", result);
			try {
				String message = "";
				String codemobile,idmobile;
				JSONObject SignupResponseObject = new JSONObject(result);
				int status = SignupResponseObject.getInt("AuthenticationStatus");
				if(SignupResponseObject.has("msg")){
					message = SignupResponseObject.getString("msg");
				}
				if(SignupResponseObject.has("userid")){
					idmobile = SignupResponseObject.getString("userid");
				}
				if(SignupResponseObject.has("mobilecode")){
					codemobile= SignupResponseObject.getString("mobilecode");
				}
				if (status == 1) {		
					// TODO successful authentication
					Log.d("Signup Response ", "Registration was successful");
					Intent intentsuccess = new Intent(OtpcodeActivity.this, MainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intentsuccess.putExtra("croutonmsg", message);
					intentsuccess.putExtra("changefragment", "ProfileFragment");
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
