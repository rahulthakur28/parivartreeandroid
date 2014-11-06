package com.parivartree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.app.ActionBar;
import android.app.Activity;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.WebView;

public class TermsActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_terms);

		WebView upperView = (WebView) findViewById(R.id.webviewterms);

		StringBuilder sb = new StringBuilder("<html><body>");
		sb.append(readTextFile(""));
		sb.append("</body></html>");
		upperView.loadData(sb.toString(), "text/html", "utf-8");

	}

	public String readTextFile(String filename) {
		AssetManager am = getApplicationContext().getAssets();
		StringBuilder strbuild = new StringBuilder();
		try {
			InputStream is = am.open("terms.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(is));
			String str;
			while ((str = in.readLine()) != null)
				strbuild.append(str);
			in.close();
		} catch (IOException e) {
		}
		return strbuild.toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.terms, menu);
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
}
