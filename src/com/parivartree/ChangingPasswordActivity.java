package com.parivartree;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
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
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ChangingPasswordActivity extends Activity implements OnClickListener, ValidationListener{
	@Password(order = 1)
	EditText editPassword;
	@ConfirmPassword(order = 2)
	EditText editConfirmPassword;
	Button submitButton;
	String sessionHash;
	Activity activity;
	// Saripaar validator
	Validator validator;
	ProgressDialog pDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_changing_password);
		activity = this;
		validator = new Validator(this);
		validator.setValidationListener(this);
		Intent intent = getIntent();
		Bundle bndl = intent.getExtras();
		if(bndl != null){		
			if(bndl.containsKey("sessionHash")){
				sessionHash = bndl.getString("sessionHash");
			}		
		}				
		editPassword = (EditText)findViewById(R.id.editTextpassword);
		editConfirmPassword = (EditText)findViewById(R.id.editTextconfirmpassword);
		submitButton = (Button) findViewById(R.id.submitbtn);
		submitButton.setOnClickListener(this);
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
		getMenuInflater().inflate(R.menu.changing_password, menu);
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
		if(v.getId() == R.id.submitbtn ){
			validator.validate();
		}
	}
	class SetPasswordTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Setting Password...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}
		@Override
		protected String doInBackground(String... params) {
			// ---------change method name
			return HttpConnectionUtils.changePasswordResponse(params[0], params[1],getResources()
					.getString(R.string.hostname) + getResources().getString(R.string.url_change_password));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) { 
			    pDialog.dismiss();
			   }
			Log.i("event list Response ", response);

			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				String responseResult = eventListResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
				
					String croutonmsg =  "Setting Password successful";
					Intent intentsuccess = new Intent(activity, LoginMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					intentsuccess.putExtra("croutonmsg", croutonmsg);
					startActivity(intentsuccess);
				}else{
					Crouton.makeText(activity, "!Error occur, Try again", Style.ALERT).show();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();	if ((pDialog != null) && pDialog.isShowing()) { 
			    pDialog.dismiss();
			   }
			Crouton.makeText(activity, "Network connection is slow, Try again", Style.ALERT).show();
		}

	}
	@Override
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		/**
		 * add unsaved data (password), as well as email in user preferences
		 */
		// /asynTAsk execute
		boolean bool = new ConDetect(ChangingPasswordActivity.this).isOnline();
		if (bool) {
			final SetPasswordTask setPasswordTask = new SetPasswordTask();
			setPasswordTask.execute(sessionHash, editPassword.getText().toString().trim());
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (setPasswordTask.getStatus() == AsyncTask.Status.RUNNING) {
						setPasswordTask.cancel(true);
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
		} else {
			Log.d("Signup settings ", message);
		}
	}
}
