package com.parivartree.fragments;

import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
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
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.ConfirmPassword;
import com.mobsandgeeks.saripaar.annotation.Password;
import com.parivartree.R;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class SettingsFragment extends Fragment implements OnClickListener, ValidationListener {
	private String TAG = "AllEventsFragment";
	TextView textName, textEmail;
	ImageView imageEdit;

	@Password(order = 1)
	EditText editNewPassword;
	@ConfirmPassword(order = 2)
	EditText editCnfPassword;

	Button btnChangePassword, btnCancel;
	SharedPreferences sharedPreferences;
	String userId, sessionname, sessionemail;
	LinearLayout linearLayout323;
	Activity activity;
	Context context;
	// Saripaar validator
	Validator validator;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_settings, container, false);

		activity = getActivity();
		context = getActivity().getApplicationContext();

		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		validator = new Validator(this);
		validator.setValidationListener(this);

		userId = sharedPreferences.getString("user_id", "0");
		sessionname = sharedPreferences.getString("sessionname", "NA");
		sessionemail = sharedPreferences.getString("sessionemail", "NA");
		linearLayout323 = (LinearLayout) rootView.findViewById(R.id.linear323);
		linearLayout323.setVisibility(View.GONE);
		textName = (TextView) rootView.findViewById(R.id.textsettingname);
		textEmail = (TextView) rootView.findViewById(R.id.textsettingemail);
		imageEdit = (ImageView) rootView.findViewById(R.id.imageview1);
		editNewPassword = (EditText) rootView.findViewById(R.id.txtnewpassword);
		editCnfPassword = (EditText) rootView.findViewById(R.id.txtcnfpassword);
		btnChangePassword = (Button) rootView.findViewById(R.id.btnchange);
		btnCancel = (Button) rootView.findViewById(R.id.btnCancel);

		imageEdit.setOnClickListener(this);
		btnChangePassword.setOnClickListener(this);
		btnCancel.setOnClickListener(this);
		textName.setText(sessionname);
		textEmail.setText(sessionemail);
		return rootView;
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.imageview1) {
			// showChangePassword();
			linearLayout323.setVisibility(View.VISIBLE);
			imageEdit.setVisibility(View.GONE);
		}
		if (v.getId() == R.id.btnchange) {
			// showChangePassword();
			validator.validate();
		}
		if (v.getId() == R.id.btnCancel) {
			// showChangePassword();
			linearLayout323.setVisibility(View.GONE);
			imageEdit.setVisibility(View.VISIBLE);
		}
	}

	// private void showChangePassword() {
	//
	// // Create Object of Dialog class
	// final Dialog login = new Dialog(getActivity());
	// // Set GUI of login screen
	// login.setContentView(R.layout.change_password);
	// login.setTitle("Change Password");
	//
	// // Init button of login GUI
	// btnChangePassword = (Button) login.findViewById(R.id.btnchange);
	// btnCancel = (Button) login.findViewById(R.id.btnCancel);
	// editNewPassword = (EditText) login.findViewById(R.id.txtnewpassword);
	// editCnfPassword = (EditText) login.findViewById(R.id.txtcnfpassword);
	//
	// // Attached listener for login GUI button
	// btnChangePassword.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// String password = editNewPassword.getText().toString().trim();
	// String confPassword = editCnfPassword.getText().toString().trim();
	// if (password.length() > 0 && confPassword.length() > 0 &&
	// password.equals(confPassword)) {
	//
	// boolean bool = new ConDetect(getActivity()).isOnline();
	// if(bool){
	// //Create object of AsycTask and execute
	// SetPasswordTask setPasswordTask = new SetPasswordTask();
	// setPasswordTask.execute(userId,password);
	// }else{
	// Toast.makeText(getActivity(), "!No Internet Connection,Try again",
	// Toast.LENGTH_LONG).show();
	// }
	//
	// // Redirect to dashboard / home screen.
	// login.dismiss();
	// } else {
	// Log.d("settings", "Password Not Match");
	// Toast.makeText(getActivity(), "Password Not Match",
	// Toast.LENGTH_LONG).show();
	//
	// }
	// }
	// });
	// btnCancel.setOnClickListener(new OnClickListener() {
	// @Override
	// public void onClick(View v) {
	// login.dismiss();
	// }
	// });
	//
	// // Make dialog box visible.
	// login.show();
	// }

	class SetPasswordTask extends AsyncTask<String, Void, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Setting Password...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground" + params[0]);
			// ---------change method name
			return HttpConnectionUtils.setPasswordResponse(params[0], params[1], getActivity().getResources()
					.getString(R.string.hostname) + getActivity().getResources().getString(R.string.url_set_password));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("event list Response ", response);

			try {
				JSONObject eventListResponseObject = new JSONObject(response);
				String responseResult = eventListResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {
					Log.d(TAG, "onpostexecute : changing password");
					linearLayout323.setVisibility(View.GONE);
					imageEdit.setVisibility(View.VISIBLE);
				}
				Log.d("settings", "changing password ");

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("settings", "Invalid Server content from settings!!");
			}

		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
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

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final SetPasswordTask setPasswordTask = new SetPasswordTask();
			setPasswordTask.execute(userId, editNewPassword.getText().toString().trim());
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (setPasswordTask.getStatus() == AsyncTask.Status.RUNNING){
						setPasswordTask.cancel(true);
					}
				}
			}, 10000);
		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
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
	@Override
	public void onDestroyView() {
		super.onDestroyView();
	}
}
