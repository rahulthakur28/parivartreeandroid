package com.parivartree;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.ImageView;

public class SignUpActivity extends Activity implements OnClickListener {

	ImageView next;
	EditText editTextFirstName, editTextLastName, editTextEmail, editTextPhone, editTextPassword, editTextRePassword;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_signup_details);
		setContentView(R.layout.activity_signup);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.sign_up, menu);
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
			// this.startActivity(intent);

			startActivity(new Intent(this, SignUpDetailsActivity.class));
		}
	}

	public class LoginTask extends AsyncTask<String, String, String> {

		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(SignUpActivity.this);
			pDialog.setMessage("Loggin in...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();

		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			// return HttpConnectionUtils.getLoginResponse(params[0], params[1],
			// activity.getResources().getString(R.string.login_url));
			return null;
		}
	}
}
