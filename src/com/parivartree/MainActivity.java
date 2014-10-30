package com.parivartree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.parivartree.adapters.NavDrawerListAdapter;
import com.parivartree.crop.Crop;
import com.parivartree.fragments.AllEventsFragment;
import com.parivartree.fragments.CommunityFragment;
import com.parivartree.fragments.CreateEventFragment;
import com.parivartree.fragments.CreateRelationFragment;
import com.parivartree.fragments.DeleteEventFragment;
import com.parivartree.fragments.DiscussionFragment;
import com.parivartree.fragments.EditEventFragment;
import com.parivartree.fragments.EditProfileFragment;
import com.parivartree.fragments.FindPeopleFragment;
import com.parivartree.fragments.HomeFragment;
import com.parivartree.fragments.InviteFragment;
import com.parivartree.fragments.MessageFragment;
import com.parivartree.fragments.NotificationFragment;
import com.parivartree.fragments.PagesFragment;
import com.parivartree.fragments.PhotosFragment;
import com.parivartree.fragments.ProfileFragment;
import com.parivartree.fragments.RelationFragment;
import com.parivartree.fragments.SelectRelationFragment;
import com.parivartree.fragments.SettingsFragment;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.NavDrawerItem;

public class MainActivity extends FragmentActivity implements OnClickListener {

	private final String TAG = "MainActivity";
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private ActionBarDrawerToggle mDrawerToggle;

	// Action Bar items
	ImageView imageView1, imageView2, imageView3, imageView4;

	private Fragment fragment,currentFragment;

	// nav drawer title
	private CharSequence mDrawerTitle;

	// used to store app title
	private CharSequence mTitle;
	// slide menu items
	private String[] navMenuTitles;
	private TypedArray navMenuIcons;

	private ArrayList<NavDrawerItem> navDrawerItems;
	private NavDrawerListAdapter adapter;

	// Shared preferences
	private SharedPreferences sharedPreferences;
	private Editor sharedPreferencesEditor;
	final int IMAGE_PICKER_SELECT = 100;
	public File cameraImagePath = null;
	FragmentManager fragmentManager;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		ActionBar actionBar = this.getActionBar();

		// add the custom view to the action bar
		actionBar.setCustomView(R.layout.actionbar_view);
		actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM | ActionBar.DISPLAY_SHOW_HOME);
		imageView1 = (ImageView) actionBar.getCustomView().findViewById(R.id.imageView1);
		imageView2 = (ImageView) actionBar.getCustomView().findViewById(R.id.imageView2);
		imageView3 = (ImageView) actionBar.getCustomView().findViewById(R.id.imageView3);
		imageView4 = (ImageView) actionBar.getCustomView().findViewById(R.id.imageView4);

		imageView1.setOnClickListener(this);
		imageView2.setOnClickListener(this);
		imageView3.setOnClickListener(this);
		imageView4.setOnClickListener(this);

		sharedPreferences = this.getApplicationContext().getSharedPreferences(
				this.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		mTitle = mDrawerTitle = getTitle();

		// load slide menu items
		navMenuTitles = getResources().getStringArray(R.array.nav_drawer_items);

		// nav drawer icons from resources
		navMenuIcons = getResources().obtainTypedArray(R.array.nav_drawer_icons);

		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mDrawerList = (ListView) findViewById(R.id.list_slidermenu);
		mDrawerList.setBackgroundResource(R.color.pt_menu_background);
		mDrawerList.setDivider(this.getResources().getDrawable(R.drawable.divider));
		mDrawerList.setDividerHeight(1);

		navDrawerItems = new ArrayList<NavDrawerItem>();

		// adding nav drawer items to array
		// Home
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[0], navMenuIcons.getResourceId(0, -1)));
		// Find on Map
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[1], navMenuIcons.getResourceId(1, -1)));
		// Find People
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[2], navMenuIcons.getResourceId(2, -1)));
		// Photos
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[3], navMenuIcons.getResourceId(3, -1)));
		// Communities, Will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[4], navMenuIcons.getResourceId(4, -1)));
		// Pages
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[5], navMenuIcons.getResourceId(5, -1)));
		// What's hot, We will add a counter here
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[6], navMenuIcons.getResourceId(6, -1)));
		// Request Received
		navDrawerItems.add(new NavDrawerItem(navMenuTitles[7], navMenuIcons.getResourceId(7, -1)));
		// Sign Out
		// navDrawerItems.add(new NavDrawerItem(navMenuTitles[8],
		// navMenuIcons.getResourceId(8, -1)));

		// Recycle the typed array
		navMenuIcons.recycle();

		mDrawerList.setOnItemClickListener(new SlideMenuClickListener());

		// setting the nav drawer list adapter
		adapter = new NavDrawerListAdapter(getApplicationContext(), navDrawerItems);
		mDrawerList.setAdapter(adapter);

		// enabling action bar app icon and behaving it as toggle button
		getActionBar().setDisplayHomeAsUpEnabled(true);
		getActionBar().setHomeButtonEnabled(true);
		getActionBar().setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));

		mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerLayout, R.drawable.ic_drawer, // nav
																								// menu
																								// toggle
																								// icon
				R.string.app_name, // nav drawer open - description for
									// accessibility
				R.string.app_name // nav drawer close - description for
									// accessibility
		) {
			public void onDrawerClosed(View view) {
				getActionBar().setTitle(mTitle);
				// calling onPrepareOptionsMenu() to show action bar icons
				invalidateOptionsMenu(); // Log.d("Drawer", "drawer closed");
			}

			public void onDrawerOpened(View drawerView) {
				getActionBar().setTitle(mDrawerTitle);
				// calling onPrepareOptionsMenu() to hide action bar icons
				invalidateOptionsMenu(); // Log.d("Drawer", "drawer opened");
			}
		};

		mDrawerLayout.setDrawerListener(mDrawerToggle);

		if (savedInstanceState == null) {
			// on first time display view for first nav item
			displayView(0);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		/*
		 * // Inflate the menu; this adds items to the action bar if it is
		 * present. getMenuInflater().inflate(R.menu.main, menu); return true;
		 */
		return false;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// toggle nav drawer on selecting action bar app icon/title
		if (mDrawerToggle.onOptionsItemSelected(item)) {
			return true;
		}
		// Handle action bar actions click
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
			/*
			 * case R.id.imageView1: Log.d("ActionBar", "Image View 1 clicked");
			 * return true; case R.id.imageView2: Log.d("ActionBar",
			 * "Image View 2 clicked"); return true; case R.id.imageView3:
			 * Log.d("ActionBar", "Image View 3 clicked"); return true; case
			 * R.id.imageView4: Log.d("ActionBar", "Image View 4 clicked");
			 * return true;
			 */
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Slide menu item click listener
	 * */
	private class SlideMenuClickListener implements ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			// display view for selected nav drawer item
			displayView(position);
		}
	}

	/***
	 * Called when invalidateOptionsMenu() is triggered
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// if nav drawer is opened, hide the action items
		boolean drawerOpen = mDrawerLayout.isDrawerOpen(mDrawerList);
		menu.findItem(R.id.action_settings).setVisible(!drawerOpen);
		return super.onPrepareOptionsMenu(menu);
	}

	/**
	 * Diplaying fragment view for selected nav drawer list item
	 * */
	private void displayView(int position) {

		// update the main content by replacing fragments
		Fragment fragment = null;
		//fragment = null;
		switch (position) {
		case 0:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new HomeFragment();
			break;
		case 1:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new ProfileFragment();
			break;
		case 2:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new FindPeopleFragment();
			break;
		case 3:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new PhotosFragment();
			break;
		case 4:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new CommunityFragment();
			break;
		case 5:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new PagesFragment();
			break;
		case 6:
			sharedPreferencesEditor = sharedPreferences.edit();
			sharedPreferencesEditor.putString("node_id",sharedPreferences.getString("user_id", "0"));
			sharedPreferencesEditor.commit();
			fragment = new SettingsFragment();
			break;
		case 7:
			// fragment = new SettingsFragment();
			// break;
		case 8:

			// TODO Auto-generated method stub

			Log.d("MainActivity", "signout tgos!!");
			AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
			// Setting Dialog Title
			alertDialog.setTitle("Confirm Signout...");
			// Setting Dialog Message
			alertDialog.setMessage("Do you want to Signout?");
			// Setting Icon to Dialog
			alertDialog.setIcon(R.drawable.signoutconfirm);
			// Setting Positive "Yes" Button
			alertDialog.setPositiveButton("YES", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to invoke YES event
					Intent signout = new Intent(getApplicationContext(), LoginMainActivity.class);
					signout.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
					sharedPreferencesEditor = sharedPreferences.edit();
					sharedPreferencesEditor.clear();
					sharedPreferencesEditor.commit();
					startActivity(signout);
				}
			});

			// Setting Negative "NO" Button
			alertDialog.setNegativeButton("NO", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int which) {
					// Write your code here to invoke NO event
					dialog.cancel();
				}
			});

			// Showing Alert Message
			alertDialog.show();
			break;

		default:
			break;

		}

		// any section accessed through the sliding bar will show main user's
		// data
		sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
		sharedPreferencesEditor.commit();

		if (fragment != null) {
			fragmentManager = getSupportFragmentManager();
			   fragmentManager
			   .beginTransaction()
			   // Add this transaction to the back stack
			            .addToBackStack(null)
			   .replace(R.id.frame_container, fragment)
			   .commit();

			// update selected item and title, then close the drawer
			mDrawerList.setItemChecked(position, true);
			mDrawerList.setSelection(position);
			setTitle(navMenuTitles[position]);
			mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainActivity", "Error in creating fragment");
		}
	}

	@Override
	public void setTitle(CharSequence title) {
		mTitle = title;
		// getActionBar().setTitle(mTitle);
	}

	/**
	 * When using the ActionBarDrawerToggle, you must call it during
	 * onPostCreate() and onConfigurationChanged()...
	 */

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		// Sync the toggle state after onRestoreInstanceState has occurred.
		mDrawerToggle.syncState();
	}

	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		// Pass any configuration change to the drawer toggls
		mDrawerToggle.onConfigurationChanged(newConfig);
	}

	public void changeFragment(String fragmentName) {
		  // TODO Auto-generated method stub

		  // select fragment
		  if (fragmentName.equals("EditProfileFragment")) {
		   fragment = new EditProfileFragment();
		  } else if (fragmentName.equals("ProfileFragment")) {
		   fragment = new ProfileFragment();
		  } else if (fragmentName.equals("NotificationsFragment")) {
		   fragment = new NotificationFragment();
		  } else if (fragmentName.equals("MessagesFragment")) {
		   fragment = new MessageFragment();
		  } else if (fragmentName.equals("InvitesFragment")) {
		   fragment = new InviteFragment();
		  } else if (fragmentName.equals("DiscussionFragment")) {
		   fragment = new DiscussionFragment();
		  } else if (fragmentName.equals("CreateEventFragment")) {
		   fragment = new CreateEventFragment();
		  } else if (fragmentName.equals("DeleteEventFragment")) {
		   fragment = new DeleteEventFragment();
		  } else if (fragmentName.equals("AllEventsFragment")) {
		   fragment = new AllEventsFragment();
		  } else if (fragmentName.equals("SelectRelationFragment")) {
		   fragment = new SelectRelationFragment();
		  } else if (fragmentName.equals("RelationFragment")) {
		   fragment = new RelationFragment();
		  } else if (fragmentName.equals("HomeFragment")) {
		   fragment = new HomeFragment();
		  }

		  // change to the new fragment
		  if (fragment != null) {
		   fragmentManager = getSupportFragmentManager();
		   
		   //fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
		    if((fragment instanceof NotificationFragment) || (fragment instanceof DiscussionFragment) || (fragment instanceof InviteFragment) || (fragment instanceof MessageFragment)) {
		     fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).addToBackStack(null).commit();
		    }
		    else {
		     fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		    }
		    currentFragment = fragment;
		   mDrawerLayout.closeDrawer(mDrawerList);
		  } else {
		   // error in creating fragment
		   Log.e("MainContainerActivity", "Error in creating fragment");
		  }
		 }

		 public void changeFragment(String fragmentName, Bundle bundle) {
		  // select fragment
		  if (fragmentName.equals("DeleteEventFragment")) {
		   fragment = new DeleteEventFragment();
		   fragment.setArguments(bundle);
		  } else if (fragmentName.equals("EditEventFragment")) {
		   fragment = new EditEventFragment();
		   fragment.setArguments(bundle);
		  } else if (fragmentName.equals("AllEventsFragment")) {
		   fragment = new AllEventsFragment();
		   fragment.setArguments(bundle);
		  } else if (fragmentName.equals("ProfileFragment")) {
		   fragment = new ProfileFragment();
		   fragment.setArguments(bundle);
		  }
		  
		  // change to the new fragment
		  if (fragment != null) {
		   fragmentManager = getSupportFragmentManager();
		   fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
		   //fragmentManager.beginTransaction().addToBackStack(null).replace(R.id.frame_container, fragment).commit();
		   currentFragment = fragment;
		   mDrawerLayout.closeDrawer(mDrawerList);
		  } else {
		   // error in creating fragment
		   Log.e("MainContainerActivity", "Error in creating fragment");
		  }
		 }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if (v.getId() == R.id.imageView1) {
			Log.d("ActionBar", "Image View 1 clicked");
			(MainActivity.this).changeFragment("NotificationsFragment");
			// TODO notifications
		} else if (v.getId() == R.id.imageView2) {
			Log.d("ActionBar", "Image View 2 clicked");
			(MainActivity.this).changeFragment("DiscussionFragment");
			// TODO messages
		} else if (v.getId() == R.id.imageView3) {
			Log.d("ActionBar", "Image View 3 clicked");
			(MainActivity.this).changeFragment("InvitesFragment");
			// TODO invitations
		} else if (v.getId() == R.id.imageView4) {
			Log.d("ActionBar", "Image View 4 clicked");
			(MainActivity.this).changeFragment("MessagesFragment");
			// TODO discussions
		}
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		Log.d(TAG, "onActivityResult");
		if (requestCode == Crop.REQUEST_PICK && resultCode == Activity.RESULT_OK) {
			beginCrop(result.getData());
		} else if (requestCode == Crop.REQUEST_TAKE_PHOTO && resultCode == Activity.RESULT_OK) {
			Log.d(TAG, "cameraImagePath - " + cameraImagePath);
			if (cameraImagePath != null) {
				beginCrop(cameraImagePath);
			}
			cameraImagePath = null;
		} else if (requestCode == Crop.REQUEST_CROP) {
			handleCrop(resultCode, result);
		}
	}

	private void beginCrop(Uri source) {
		Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
		new Crop(source).output(outputUri).asSquare().start(this);
		Log.d("ProfileFragment", "source" + source.toString());
	}

	private void beginCrop(File file) {
		Uri source = Uri.fromFile(file);
		Uri outputUri = Uri.fromFile(new File(getCacheDir(), "cropped"));
		new Crop(source).output(outputUri).asSquare().start(this);
		Log.d("ProfileFragment", "source" + source.toString());
	}

	private void handleCrop(int resultCode, Intent result) {
		if (resultCode == Activity.RESULT_OK) {
			// resultView.setImageURI(Crop.getOutput(result));
			Log.d("ProfileFragment", Crop.getOutput(result).toString());
			String imagePath = Crop.getOutput(result).getPath();
			// this.getFilesDir()
			Log.d(TAG, "internal files directory - " + getFilesDir() + ", imgepath -" + imagePath);
			// String imagePath =
			// "file:///data/data/com.parivartree/cache/cropped";
			String base64str = null;
			String extension = "";

			int i = imagePath.lastIndexOf('.');
			if (i > 0) {
				extension = imagePath.substring(i + 1);
			}

			try {
				base64str = encodeFileToBase64Binary(imagePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String finalimageString = "data:image/" + extension.trim() + ";base64," + base64str;
			Log.d("base64 image string - ", finalimageString);

			String userId = sharedPreferences.getString("user_id", "0");
			String nodeId = sharedPreferences.getString("node_id", userId);
			// execute asyncTask for image upload
			ImageUploadTask imageUploadTask = new ImageUploadTask();
			imageUploadTask.execute(nodeId, finalimageString);
		} else if (resultCode == Crop.RESULT_ERROR) {
			Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
		}
	}

	public class ImageUploadTask extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MainActivity.this);
			pDialog.setMessage("Uploading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getImageUploadResponse(
					params[0],
					params[1],
					getResources().getString(R.string.hostname)
							+ getResources().getString(R.string.url_profile_imageupload));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("image upload Fetch Response ", response);
			try {
				JSONObject loginResponseObject = new JSONObject(response);
				String responseResult = loginResponseObject.getString("Status");
				if (responseResult.equals("Success")) {
					Log.i("image upload Response ", "success");
					MainActivity.this.changeFragment("ProfileFragment");
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(MainActivity.this, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG)
						.show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}
	}

	private String encodeFileToBase64Binary(String fileName) throws IOException {

		File file = new File(fileName);
		byte[] bytes = loadFile(file);
		byte[] encoded = Base64.encodeBase64(bytes);
		String encodedString = new String(encoded);

		return encodedString;
	}

	private static byte[] loadFile(File file) throws IOException {
		InputStream is = new FileInputStream(file);

		long length = file.length();
		if (length > Integer.MAX_VALUE) {
			// File is too large
		}
		byte[] bytes = new byte[(int) length];

		int offset = 0;
		int numRead = 0;
		while (offset < bytes.length && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
			offset += numRead;
		}

		if (offset < bytes.length) {
			is.close();
			throw new IOException("Could not completely read file " + file.getName());

		}

		is.close();
		return bytes;
	}

	public void createRelation(String realtionId, String nodeId) {
		fragment = new CreateRelationFragment(realtionId, nodeId);
		// change to the new fragment
		if (fragment != null) {
			FragmentManager fragmentManager = getSupportFragmentManager();
			fragmentManager.beginTransaction().replace(R.id.frame_container, fragment).commit();
			// mDrawerLayout.closeDrawer(mDrawerList);
		} else {
			// error in creating fragment
			Log.e("MainContainerActivity", "Error in creating fragment");
		}
	}

	@Override
	 public void onBackPressed() {
	  
	    boolean change = false;
	    int exitApplication = 1;
	    //Log.e(TAG, "Fragment class - " + fragment.getClass().getPackage().getName());
	    if (currentFragment instanceof HomeFragment) {
	     Log.d(TAG, "instance of Home Fragment");
	     if (((HomeFragment) currentFragment).optionsLayout.getVisibility() == View.VISIBLE) {
	      ((HomeFragment) currentFragment).optionsLayout.setVisibility(View.INVISIBLE);
	      exitApplication = 0;
	     }
	    } else if (currentFragment instanceof SelectRelationFragment) {
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof CreateEventFragment) {
	     fragment = new CommunityFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof DeleteEventFragment) {
	     fragment = new CommunityFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof EditEventFragment) {
	     fragment = new CommunityFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof AllEventsFragment) {
	     fragment = new CommunityFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof CreateRelationFragment) {
	     Log.d(TAG, "instance of CreateRelation Fragment");
	     fragment = new RelationFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof EditProfileFragment) {
	     fragment = new ProfileFragment();
	     exitApplication = 0;
	    } /*else if (currentFragment instanceof DiscussionFragment) {
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof InviteFragment) {
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof MessageFragment) {
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof NotificationFragment) {
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    } */else if (currentFragment instanceof RelationFragment) {
	     Log.d(TAG, "instance of Relation Fragment");
	     sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
	     sharedPreferencesEditor.commit();
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    } else if (currentFragment instanceof ProfileFragment
	      && !sharedPreferences.getString("node_id", "0").equals(sharedPreferences.getString("user_id", "0"))) {
	     sharedPreferencesEditor.putString("node_id", sharedPreferences.getString("user_id", "0"));
	     sharedPreferencesEditor.commit();
	     fragment = new HomeFragment();
	     exitApplication = 0;
	    }
	    if(exitApplication == 1 && this.fragmentManager.getBackStackEntryCount() > 0) {
	     fragmentManager.popBackStack();
	     //BackStackEntry tempBackStack = (BackStackEntry) fragmentManager.getBackStackEntryAt((fragmentManager.getBackStackEntryCount()-1));
	     // currentFragment
	     //tempBackStack.getName() + ((currentFragment instanceof HomeFragment)? true:false)
	     Log.d(TAG, "backstack popped");
	     //Log.d(TAG, "instance of HomeFragment - ");
	    } else if (exitApplication == 1) {
	     Log.e(TAG, "Application exited");
	     LoginMainActivity.activity.finish();
	     finish();
	    } else if (exitApplication == 0 && fragment != null) {
	     Log.e(TAG, "Transaction by replacing in the same view");
	     //FragmentManager fragmentManager = getSupportFragmentManager();
	     fragmentManager.beginTransaction() .addToBackStack(null).replace(R.id.frame_container, fragment).commit();
	     currentFragment = fragment;
	    } else {
	     // error in creating fragment
	     Log.e("MainActivity", "Error in creating fragment");
	    }
	 }

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();

	}
}
