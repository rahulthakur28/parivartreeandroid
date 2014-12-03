package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.customviews.HorizontalListView;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.Albums;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class ViewAlbumPhotosFragment extends Fragment {

	HorizontalListView horizontal_list;
	ArrayList<String> photohasharray;
	private String albumhash;
	private String loggeduid;
	ProgressDialog pDialog;
	Activity mActivity;
	PhotosListingAdapter photoadapter;
	
	ImageView im;

	String TAG = "parivar";
	
	public ViewAlbumPhotosFragment(){
		
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		View view= inflater.inflate(R.layout.fragment_view_album_photo, null);
		mActivity = getActivity();

		Bundle bundle_args = getArguments();
		if (bundle_args != null) {
			albumhash = bundle_args.getString("albumhash");
			loggeduid = bundle_args.getString("loggeduid");
		}
		
		photohasharray=new ArrayList<String>();
		horizontal_list=(HorizontalListView) view.findViewById(R.id.horizontal_photos);
		photoadapter=new PhotosListingAdapter(mActivity, R.layout.item_photo_album_list, photohasharray);
		horizontal_list.setAdapter(photoadapter);
		
		horizontal_list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				setImage(photohasharray.get(arg2));
			}
		});
		
		im=(ImageView) view.findViewById(R.id.image_photo);
		
		new GetAlbumPhotosTask().execute(albumhash,loggeduid);
		return view;
	}


	private class GetAlbumPhotosTask extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(mActivity);
			pDialog.setMessage("Loding Picture...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			
			return HttpConnectionUtils.getPhotoListResponse(
					params[0], // albumhash
					params[1], // loggeduid
					mActivity.getResources().getString(R.string.hostname)
							+ mActivity.getResources().getString(
									R.string.url_gallary_album));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}

			Log.i("Gallery list Response ", response);

			try {
				JSONObject photoResponseObject = new JSONObject(response);
				String responseResult = photoResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);

				if (responseResult.equals("Success")) {

					JSONArray dataArray = photoResponseObject
							.getJSONArray("photos");

					// if(null!=mPhoto && mPhoto.size()>0){
					// mPhoto.clear();
					// }
					
					Albums tempobj;
					photohasharray = new ArrayList<String>();
					// albumHash=photoResponseObject.getString("albumhash");
					for (int i = 0; i < dataArray.length(); i++) {						
						photohasharray.add(dataArray.getString(i));

					}

					Log.e("Photos -->", "" + photohasharray);
					showPhotosGridView();
					// https://www.parivartree.com/imagegallery/MMw5LRAHEuZLuF9D/photos/z7VNNPoaxQcgUSGf.jpeg
					
					setImage(photohasharray.get(0));

				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(mActivity,
						"Invalid Server Content - ",
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content from Notification!!");
			}

		}

		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}
			Crouton.makeText(mActivity,
					"Your Network Connection is Very Slow, Try again",
					Style.ALERT).show();
		}

		protected void showPhotosGridView() {
			photoadapter=new PhotosListingAdapter(mActivity, R.layout.item_photo_album_list, photohasharray);
			horizontal_list.setAdapter(photoadapter);
		}

	}
	
	private class PhotosListingAdapter extends ArrayAdapter<String>{
		
		LayoutInflater tinflater;
		public PhotosListingAdapter(Context context, int resource,
				List<String> objects) {
			super(context, resource, objects);
			// TODO Auto-generated constructor stub
			tinflater=LayoutInflater.from(context);
		}
		
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			PhotoListHolder photoholder;
			if(convertView==null){
				convertView=tinflater.inflate(R.layout.item_photo_album_list, null);
				photoholder=new PhotoListHolder();
				photoholder.mImage=(ImageView) convertView.findViewById(R.id.image_album);
				convertView.setTag(photoholder);
			}
			else{
				photoholder=(PhotoListHolder) convertView.getTag();
			}
			
			UrlImageViewHelper.setUrlDrawable(photoholder.mImage, mActivity.getResources().getString(R.string.galleryhostname)+
					albumhash+"/thumbs/"+getItem(position)+".jpeg", R.drawable.parivartree_logo_127);
			return convertView;
		}
		
	}
	
	private class PhotoListHolder{
		ImageView mImage;		
	}
	
	public void setImage(String photohash){
		UrlImageViewHelper.setUrlDrawable(im,  mActivity.getResources().getString(R.string.galleryhostname)+
				albumhash+"/photos/"+photohash+".jpeg", R.drawable.parivartree_logo_127);
	}
	

}
