package com.parivartree;

import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

public class SignUpDetailsActivity extends Activity implements OnClickListener, ValidationListener {

	ImageView next;
	int day, month, year;
	DatePicker datePicker;
	String Gender = "Male";
	@Required(order = 1)
	EditText editTextFirstName;
	@Required(order = 2)
	EditText editTextLastName;
	@Required(order = 3)
	EditText editTextEmail;

	EditText editTextPhone;

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

	private final String TAG = "SignUpDetailsActivity";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_signup_details);
		activity = this;
		validator = new Validator(this);
		validator.setValidationListener(this);
		editTextFirstName = (EditText) findViewById(R.id.editText1);
		editTextLastName = (EditText) findViewById(R.id.editText2);
		editTextEmail = (EditText) findViewById(R.id.editText6);
		editTextPhone = (EditText) findViewById(R.id.editText3);
		editTextPassword = (EditText) findViewById(R.id.editText4);
		editTextRePassword = (EditText) findViewById(R.id.editText5);

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
		validator.validate();
		/*
		 * if(v.getId()==R.id.nextButton){ SignUpDetailsTask sigupTask = new
		 * SignUpDetailsTask();
		 * sigupTask.execute(editTextEmail.getText().toString
		 * (),editTextFirstName
		 * .getText().toString(),editTextLastName.getText().toString
		 * (),switchGender.getText().toString()); }
		 */
	}

	public class SignUpDetailsTask extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUpDetailsActivity.this);
			pDialog.setMessage("Loggin in...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			return HttpConnectionUtils.getSignupResponse(params[0], params[1], params[2], params[3], getResources()
					.getString(R.string.hostname) + activity.getResources().getString(R.string.url_signup));
		}

		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
			Log.i("Signup Response ", result);
			try {

				JSONObject SignupResponseObject = new JSONObject(result);
				int status = SignupResponseObject.getInt("AuthenticationStatus");
				if (status == 1) {
					// TODO successful authentication
					// startActivity(new Intent(SignUpDetailsActivity.this,
					// SignUpActivity.class));
					Log.d("Signup Response ", "Registration was successful");
					Toast.makeText(SignUpDetailsActivity.this, "Please check your mail", Toast.LENGTH_LONG).show();
					startActivity(new Intent(SignUpDetailsActivity.this, LoginMainActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
				} else if (status == 5) {
					// TODO redirect to home page
					Toast.makeText(SignUpDetailsActivity.this, " Please enter a valid email-id.", Toast.LENGTH_LONG)
							.show();
				} else if (status == 6) {
					// TODO redirect to home page
					Toast.makeText(SignUpDetailsActivity.this, "You have already been invited to join Parivartree",
							Toast.LENGTH_LONG).show();
					startActivity(new Intent(SignUpDetailsActivity.this, LoginMainActivity.class)
							.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK));
				} else if (status == 7) {
					// TODO redirect to home page
					Toast.makeText(SignUpDetailsActivity.this, "This email is already registered with us.",
							Toast.LENGTH_LONG).show();
					// startActivity(new Intent(SignUpDetailsActivity.this,
					// LoginMainActivity.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
					// | Intent.FLAG_ACTIVITY_CLEAR_TASK));
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
	}

	@Override
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		/**
		 * add unsaved data (password), as well as email in user preferences
		 */
		// SignUpDetailsTask sUDT = new SignUpDetailsTask();
		// sUDT.execute(params)

		/*
		 * day = datePicker.getDayOfMonth(); month = datePicker.getMonth() + 1;
		 * year = datePicker.getYear();
		 */
		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		/*
		 * sharedPreferencesEditor.putString("fname", "");
		 * sharedPreferencesEditor.putString("lname", "");
		 * sharedPreferencesEditor.putString("pass", "");
		 * sharedPreferencesEditor.putString("email", "");
		 * sharedPreferencesEditor.putString("phone", "");
		 * sharedPreferencesEditor.putString("gender", "");
		 * sharedPreferencesEditor.putString("dob", day + "-" + month + "-" +
		 * year);
		 */
		sharedPreferencesEditor.commit();

		/*
		 * Intent signUpIntent = new Intent(this, SignUpActivity.class);
		 * signUpIntent.putExtra("fname",
		 * editTextFirstName.getText().toString());
		 * signUpIntent.putExtra("lname",
		 * editTextLastName.getText().toString()); signUpIntent.putExtra("pass",
		 * editTextPassword.getText().toString());
		 * signUpIntent.putExtra("email", editTextEmail.getText().toString());
		 * startActivity(signUpIntent);
		 */
		Log.d(TAG, "email:" + editTextEmail.getText().toString() + ", fname:" + editTextFirstName.getText().toString()
				+ ", lname" + editTextLastName.getText().toString() + ", gender :" + Gender);

		boolean bool = new ConDetect(activity).isOnline();
		if (bool) {
			SignUpDetailsTask sigupTask = new SignUpDetailsTask();
			sigupTask.execute(editTextEmail.getText().toString(), editTextFirstName.getText().toString(),
					editTextLastName.getText().toString(), Gender);
		} else {
			Toast.makeText(SignUpDetailsActivity.this, "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
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
			Log.d("Signup Response ", message);
		}
	}
}
