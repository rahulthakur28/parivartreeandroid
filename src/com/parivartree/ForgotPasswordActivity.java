package com.parivartree;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Email;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

public class ForgotPasswordActivity extends Activity implements OnClickListener, ValidationListener {
	private String TAG = "ForgotPassordActivity";
	@Email(order = 1)
	EditText editEmail;
	ImageView btnSend;
	String forgotEmail;
	Activity activity;
	Context context;
	Validator validator;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_forgot_password);
		validator = new Validator(this);
		validator.setValidationListener(this);
		
		activity = this;
		editEmail = (EditText) findViewById(R.id.editforgotemail);
		btnSend = (ImageView) findViewById(R.id.imageView4);
		btnSend.setOnClickListener(this);
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
		validator.validate();
	}

	public class ForgotTask extends AsyncTask<String, Void, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(ForgotPasswordActivity.this);
			pDialog.setMessage("Checking...");
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
			pDialog.dismiss();
			Log.i("forgot password Response ", response);

			try {
				JSONObject forgotResponseObject = new JSONObject(response);
				int status = forgotResponseObject.getInt("AuthenticationStatus");
				String responseResult = forgotResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {
					Log.d(TAG, "onpostexecute : Check u r mail");
					// mail send to user
					Log.d(TAG, "User is connected!");
					Toast.makeText(activity, "Please check your Mail and Click the Link", Toast.LENGTH_SHORT).show();
					startActivity(new Intent(activity, LoginDetailsActivity.class));
				} else if (status == 2) {
					Toast.makeText(activity, responseResult, Toast.LENGTH_SHORT).show();
				}

				// int status = 0;
				if (status == 1) {
					// TODO successful authentication
					Log.d(TAG, "success");

				}
			} catch (Exception e) {
				Log.d(TAG, "Invalid Server content!!");
			}

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
			ForgotTask forgottask = new ForgotTask();
			forgottask.execute(editEmail.getText().toString());
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
}
