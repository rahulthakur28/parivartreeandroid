package com.parivartree.fragments;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import com.mobsandgeeks.saripaar.Rule;
import com.mobsandgeeks.saripaar.Validator;
import com.mobsandgeeks.saripaar.Validator.ValidationListener;
import com.mobsandgeeks.saripaar.annotation.Required;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.fragments.ProfileFragment.ImageUploadTask;
import com.parivartree.fragments.SettingsFragment.SetPasswordTask;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class CreatePhotosFragment extends Fragment implements OnClickListener,  ValidationListener {
	Activity activity;
	Context context;
	TextView textViewPhotos, textViewVideos;
	Button buttonCreatePhotos,browseAlbum;
	FragmentManager fragmentManager;
	Fragment fragment;
	@Required(order = 1)
	EditText editAlbumName;
	@Required(order = 2)
	EditText editAlbumDescription;
	private Spinner spinnerReach;
	private ArrayList<String> spinnerReachList;
	static final int IMAGE_PICKER_SELECT = 200;
	String userId,sessionname;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	// Saripaar validator
		Validator validator;
	public CreatePhotosFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_create_photos, container, false);
		context = getActivity().getApplicationContext();
		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		sharedPreferencesEditor = sharedPreferences.edit();
		userId = sharedPreferences.getString("user_id", "0");
		sessionname = sharedPreferences.getString("sessionname", "Unknown");
		validator = new Validator(this);
		validator.setValidationListener(this);
		
		textViewPhotos = (TextView) rootView.findViewById(R.id.textviewphotos);
		textViewVideos = (TextView) rootView.findViewById(R.id.textviewvideos);
		buttonCreatePhotos = (Button) rootView.findViewById(R.id.btncreatestudio);	
		spinnerReach = (Spinner) rootView.findViewById(R.id.createalbumspinner);
		editAlbumName = (EditText) rootView.findViewById(R.id.edit_album_name);
		editAlbumDescription = (EditText) rootView.findViewById(R.id.edit_album_description);
		browseAlbum = (Button) rootView.findViewById(R.id.btnbrowsephoto);
		
		textViewPhotos.setBackgroundColor(getResources().getColor(R.color.pt_dark_grey));
		textViewPhotos.setOnClickListener(this);
		textViewVideos.setOnClickListener(this);
		buttonCreatePhotos.setOnClickListener(this);
		
		spinnerReachList = new ArrayList<String>();
		spinnerReachList.add("Private");
		spinnerReachList.add("Family");
		spinnerReachList.add("Public");
		ArrayAdapter<String> spinnerReachAdapter = new ArrayAdapter<String>(getActivity(),
				android.R.layout.simple_spinner_item, spinnerReachList);
		spinnerReach.setAdapter(spinnerReachAdapter);
		return rootView;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		activity= getActivity();
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub	
		int flag = 0;//for fragment
		
		if (v.getId() == R.id.btnbrowsephoto) {
			validator.validate();
			Log.d("image fetch------", "validator : ");
		}else if (v.getId() == R.id.textviewphotos) {
			flag = 1;
			fragment = new CreatePhotosFragment();
		} else if (v.getId() == R.id.textviewvideos) {
			flag = 1;
			fragment = new CreateVideosFragment();
		} else if (v.getId() == R.id.btncreatestudio) {
			flag = 1;
			fragment = new ViewPhotosFragment();
		}
		if(flag == 1){
		if (fragment != null) {
			fragmentManager = getActivity().getSupportFragmentManager();
			fragmentManager.beginTransaction()
			// Add this transaction to the back stack
					.addToBackStack(null).replace(R.id.frame_container, fragment).commit();
		}
		}
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);

		// Here we need to check if the activity that was triggers was the Image
		// Gallery.
		// If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
		// If the resultCode is RESULT_OK and there is some data we know that an
		// image was picked.
		if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK && data != null) {
			// Let's read picked image data - its URI
			Uri pickedImage = data.getData();
			// Let's read picked image path using content resolver
			String[] filePath = { android.provider.MediaStore.Images.Media.DATA };
			Cursor cursor = activity.getContentResolver().query(pickedImage, filePath, null, null, null);
			cursor.moveToFirst();
			String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
			Log.d("image fetch------", "path : " + imagePath);
		
			String extension = "";

			int i = imagePath.lastIndexOf('.');
			if (i > 0) {
				extension = imagePath.substring(i + 1);
			}
			Log.d("image extension fetch------", "extension : " + extension);
			// Now we need to set the GUI ImageView data with data read from the
			// picked file.
			// imageViewProfilePic.setImageBitmap(BitmapFactory.decodeFile(imagePath));
			String base64str = null;
			;
			try {
				base64str = encodeFileToBase64Binary(imagePath);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			String finalimageString = "data:image/" + extension.trim() + ";base64," + base64str;
			Log.d("base64 image string------", finalimageString);

			// execute asyncTask for image upload
			boolean bool = new ConDetect(getActivity()).isOnline();
			if (bool) {
				// Create object of AsycTask and execute
//				ImageUploadTask imageUploadTask = new ImageUploadTask();
//				imageUploadTask.execute(userId, finalimageString);
			} else {
				Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
			}			
			// At the end remember to close the cursor or you will end with the
			// RuntimeException!
			cursor.close();
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
	@Override
	public void onValidationSucceeded() {
		// TODO Auto-generated method stub
		/**
		 * add unsaved data (password), as well as email in user preferences
		 */
		// /asynTAsk execute
		Log.d("image fetch------", "validator 2: ");
		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		startActivityForResult(i, IMAGE_PICKER_SELECT);
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
	public void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
	}
	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}
	
	public class ImageUploadTask extends AsyncTask<String, String, String> {
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(activity);
			pDialog.setMessage("Uploading...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.getImageUploadResponse(params[0], params[1], getActivity().getResources()
					.getString(R.string.hostname) + getResources().getString(R.string.url_profile_imageupload));
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
					((MainActivity) activity).changeFragment("ViewPhotosFragment");
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
				}
				Toast.makeText(context, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
				Log.d("profile", "Invalid Server content from Profile!!");
			}
		}
	}
}
