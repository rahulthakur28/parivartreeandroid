package com.parivartree;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;

public class LoginMainActivity extends Activity implements OnClickListener {

	public static Activity activity;
	private SharedPreferences sharedPreferences;
	private String TAG = "LoginMainActivity";
	// private Context context;
	ImageView loginButton, signUpButton, termsview;
	private String userId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		// setContentView(R.layout.activity_login_main);
		setContentView(R.layout.activity_loginmain);
		activity = this;	
		Intent intent = getIntent();
		Bundle bndl = intent.getExtras();
		if(bndl != null){		
			if(bndl.containsKey("croutonmsg")){
				String msg = bndl.getString("croutonmsg");
				Crouton.makeText(activity, msg, Style.INFO).show();
			}	
		}
		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", "0");
		if (sharedPreferences.contains("user_id") && !userId.equals("0")) {
			// initiate a request to server to authenticate facebook id
			Log.d(TAG, "user_id: " + sharedPreferences.getString("user_id", null));
			this.startActivity(new Intent(this, MainActivity.class));
		} else if (sharedPreferences.contains("facebook_id")) {
			// initiate a request to server to authenticate facebook id
			Log.d(TAG, "facebook_id: " + sharedPreferences.getString("facebook_id", null));
			this.startActivity(new Intent(this, MainActivity.class));
		} else if (sharedPreferences.contains("google_id")) {
			// initiate a request to server to authenticate google id
			Log.d(TAG, "google_id: " + sharedPreferences.getString("google_id", null));
			this.startActivity(new Intent(this, MainActivity.class));
		} else if (sharedPreferences.contains("email_id") && sharedPreferences.contains("password")) {
			// initiate a request to server to authenticate email and password
			Log.d(TAG, "email_id: " + sharedPreferences.getString("email_id", null));
			Log.d(TAG, "password: " + sharedPreferences.getString("password", null));
			this.startActivity(new Intent(this, MainActivity.class));
		} else {
			// StartActivity (typeof(MainActivity));
		}

		loginButton = (ImageView) findViewById(R.id.login);
		signUpButton = (ImageView) findViewById(R.id.sign_up);
		termsview = (ImageView) findViewById(R.id.termimage);

		termsview.setOnClickListener(this);
		loginButton.setOnClickListener(this);
		signUpButton.setOnClickListener(this);

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.login_main, menu);
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
		if (v.getId() == R.id.login) {
			startActivity(new Intent(this, LoginDetailsActivity.class));
		}
		if (v.getId() == R.id.sign_up) {
			// startActivity(new Intent(this, SignUpActivity.class));
			startActivity(new Intent(this, SignUpDetailsActivity.class));
		}
		if (v.getId() == R.id.termimage) {
			// startActivity(new Intent(this, SignUpActivity.class));
			termsview.setBackgroundResource(R.drawable.help_and_support1);
			startActivity(new Intent(this, TermsActivity.class));
		}
	}
}
