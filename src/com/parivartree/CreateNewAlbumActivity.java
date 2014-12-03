package com.parivartree;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.json.JSONObject;

import android.app.ActionBar;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.parivartree.customviews.HorizontalListView;
import com.parivartree.helpers.HttpConnectionUtils;

public class CreateNewAlbumActivity extends Activity implements OnClickListener{
	private int REQUEST_PICK=1;
	ArrayList<String> photospatharray;
	EditText edit_name,edit_description;
	Spinner spin_access;
	HorizontalListView photolist;
	Button btn_submit;
	ImageView im_gallery;
	ProgressDialog pDialog;
	
	Activity activity;
	String uid;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(android.view.Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_createnewalbum);
		activity=this;
		
		SharedPreferences sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName() + getResources().getString(R.string.USER_PREFERENCES), Context.MODE_PRIVATE);
		
		uid = sharedPreferences.getString("user_id", "0");
		
//		ActionBar actionbar=getActionBar();
//		actionbar.setBackgroundDrawable(getResources().getDrawable(R.drawable.action_bar_background));
//		actionbar.setTitle("New Album");
		
		edit_name=(EditText) findViewById(R.id.edit_name);
		edit_description=(EditText) findViewById(R.id.edit_desc);
		spin_access=(Spinner) findViewById(R.id.spin_access);
		photolist=(HorizontalListView) findViewById(R.id.horizontal_photos);
		im_gallery=(ImageView) findViewById(R.id.gallery);
		btn_submit=(Button) findViewById(R.id.btn_submit);
		
		
		
		photospatharray=new ArrayList<String>();
		
		im_gallery.setOnClickListener(this);
		btn_submit.setOnClickListener(this);
		
		photolist.setAdapter(new PhotolistAdapter(CreateNewAlbumActivity.this , R.layout.item_photo_album_list, photospatharray));
		
//		Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
		//startActivityForResult(i, IMAGE_PICKER_SELECT);
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.gallery:
			pickImage();
			break;
			
		case R.id.btn_submit:
			Log.i("zacharia", ""+ spin_access.getSelectedItemPosition());
			new UploadNewAlbumTask().execute(uid,edit_name.getText().toString(),
					edit_description.getText().toString(),""+ (spin_access.getSelectedItemPosition()+1));
			break;

		default:
			break;
		}
		
	}
	
	private class PhotolistAdapter extends ArrayAdapter<String>{
		LayoutInflater inflater;
		public PhotolistAdapter(Context context, int resource,
				List<String> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			inflater=LayoutInflater.from(context);
		}
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			ImageHolder imholder;
			if(convertView==null){
				convertView=inflater.inflate(R.layout.item_photo_album_list, null);
				imholder=new ImageHolder();
				imholder.imphoto=(ImageView) convertView.findViewById(R.id.image_album);
				convertView.setTag(imholder);
			}else{
				imholder=(ImageHolder) convertView.getTag();
			}
			Bitmap bmp = BitmapFactory.decodeFile(getItem(position));
			imholder.imphoto.setImageBitmap(bmp);
			return convertView;
		}
		
	}
	
	private class ImageHolder{
		private ImageView imphoto;
	}
	
	protected void onActivityResult(int requestCode, int resultCode,
			Intent result) {
		super.onActivityResult(requestCode, resultCode, result);
		
		if (requestCode == REQUEST_PICK	&& resultCode == Activity.RESULT_OK) {
			Uri filepath =result.getData();
			photospatharray.add(getImagePath(filepath));
			Log.i("zacharia", getImagePath(filepath));
			photolist.setAdapter(new PhotolistAdapter(CreateNewAlbumActivity.this , R.layout.item_photo_album_list, photospatharray));
		} 
	}
	
	 public void pickImage() {
	    	Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
	    	try {
	            startActivityForResult(intent, REQUEST_PICK);
	        } catch (ActivityNotFoundException e) {
	            Toast.makeText(this, R.string.crop__pick_error, Toast.LENGTH_SHORT).show();
	        }
	    }
	 
		public String getImagePath(Uri path){			
			// Let's read picked image path using content resolver
			String[] filePath = { android.provider.MediaStore.Images.Media.DATA };
			Cursor cursor = getContentResolver().query(path, filePath, null, null, null);
			cursor.moveToFirst();			
			String imagePath = cursor.getString(cursor.getColumnIndex(filePath[0]));
			cursor.close();
			return imagePath;
		}

			// Here we need to check if the activity that was triggers was the Image
			// Gallery.
			// If it is the requestCode will match the LOAD_IMAGE_RESULTS value.
			// If the resultCode is RESULT_OK and there is some data we know that an
			// image was picked.
		/*	if (requestCode == IMAGE_PICKER_SELECT && resultCode == Activity.RESULT_OK && data != null) {
				// Let's read picked image data - its URI
				Uri pickedImage = data.getData();
				// Let's read picked image path using content resolver
				String[] filePath = { android.provider.MediaStore.Images.Media.DATA };
				Cursor cursor = getContentResolver().query(pickedImage, filePath, null, null, null);
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
//					ImageUploadTask imageUploadTask = new ImageUploadTask();
//					imageUploadTask.execute(userId, finalimageString);
				} else {
					Toast.makeText(getActivity(), "!No Internet Connection,Try again", Toast.LENGTH_LONG).show();
				}			
				// At the end remember to close the cursor or you will end with the
				// RuntimeException!
				
			}
		}*/
		
		public String[] createPhotoStringArray(){
			String[] photostringarray=new String[photospatharray.size()];
			String imagePath;
			for(int i=0; i<photospatharray.size();i++){
				String base64str = null;
				String extension = "";
				imagePath=photospatharray.get(i);
				int j = imagePath.lastIndexOf('.');
				if (j > 0) {
					extension = imagePath.substring(j + 1);
				}
				try {
					base64str = encodeFileToBase64Binary(imagePath);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				String finalimageString = "data:image/" + extension.trim() + ";base64," + base64str;
				photostringarray[i]=finalimageString;
			}
			return photostringarray;
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
		
		private class UploadNewAlbumTask extends AsyncTask<String, Void, String>{
			@Override
			protected void onPreExecute() {
				// TODO Auto-generated method stub
				super.onPreExecute();
				pDialog = new ProgressDialog(CreateNewAlbumActivity.this);
				pDialog.setMessage("Loading Album...");
				pDialog.setIndeterminate(false);
				pDialog.setCancelable(true);
				pDialog.show();
			}
			@Override
			protected String doInBackground(String... params) {
				// TODO Auto-generated method stub
				String[] photosstring=createPhotoStringArray();
				Log.i("zacharia", "params : "+ params[0] + params[1] +params[2] + params[3]);
				Log.i("zacharia", ""+photosstring.length);
				for(int j=0;j<photosstring.length;j++){
					Log.i("zacharia", ""+photosstring[j]);
				}
				return HttpConnectionUtils.createAlbumResponse(
						params[0], //uid
						params[1],	// name						
						params[2],  // desc
						params[3],	// access
						photosstring,
						activity.getResources().getString(R.string.hostname)
								+ activity.getResources().getString(
										R.string.url_gallary_create));
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
						setResult(RESULT_OK);
						
					}
				} catch (Exception e) {
					for (StackTraceElement tempStack : e.getStackTrace()) {
						Log.d("Exception thrown: ", "" + tempStack.getLineNumber());
					}
					Toast.makeText(activity, "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
					Log.d("profile", "Invalid Server content from Profile!!");
				}
			}
			
		}
		
}
