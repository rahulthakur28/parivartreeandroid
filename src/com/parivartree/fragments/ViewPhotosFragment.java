package com.parivartree.fragments;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.TextView;
import android.widget.Toast;

import com.parivartree.CreateNewAlbumActivity;
import com.parivartree.R;
import com.parivartree.adapters.GridViewImageAdapter;
import com.parivartree.helpers.AppConstant;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.helpers.Utils;
import com.parivartree.models.Albums;
import com.parivartree.models.UserAlbum;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class ViewPhotosFragment extends Fragment implements OnClickListener {
	private Utils utils;
	private UserAlbum mUserAlbum;
	private GridViewImageAdapter adapter;
	private GridView gridView;
	private int columnWidth;
	Activity activity;
	Context context;
	TextView textViewPhotos, textViewVideos;
	Button buttonCreatePhotos;
	FragmentManager fragmentManager;
	Fragment fragment;
	Point size;

	private String TAG = "ViewPhotosFragment";
	private String userId;
	private String nodeId;
	private SharedPreferences sharedPreferences;
	private ProgressDialog pDialog;

	LayoutInflater ainflater;

	public ViewPhotosFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_view_photos,
				container, false);

		ainflater = inflater;

		activity = getActivity();
		context = getActivity().getApplicationContext();
		sharedPreferences = activity.getSharedPreferences(
				activity.getPackageName()
						+ getResources().getString(R.string.USER_PREFERENCES),
				Context.MODE_PRIVATE);

		userId = sharedPreferences.getString("user_id", "0");

		nodeId = sharedPreferences.getString("node_id", userId);
		// sharedPreferences.edit().putString("nodeid", userId).commit();

		gridView = (GridView) rootView.findViewById(R.id.grid_view);
		textViewPhotos = (TextView) rootView.findViewById(R.id.textviewphotos);
		textViewVideos = (TextView) rootView.findViewById(R.id.textviewvideos);
		buttonCreatePhotos = (Button) rootView
				.findViewById(R.id.btncreatestudio);

		if (!userId.equals(nodeId)) {
			buttonCreatePhotos.setVisibility(View.GONE);
		}

		utils = new Utils(activity);
		mUserAlbum = new UserAlbum();

		getGalleryDetials();

		gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				Log.i("zacharia", "on grid item click ");
				Albums albobj = mUserAlbum.getmAlbums().get(arg2);
				Log.i("zacharia",
						"on grid item click " + albobj.getmAlbumHash());
				showAlbum(albobj.getmAlbumHash());
			}
		});

		gridView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int arg2, long arg3) {
				// TODO Auto-generated method stub
				if (userId.equals(nodeId)) {
					final Albums albobj = mUserAlbum.getmAlbums().get(arg2);
					final AlertDialog alertalbum = new AlertDialog.Builder(
							activity).create();
					View alertviewcontent = ainflater.inflate(
							R.layout.alert_album_delete, null);
					Button btn_edit = (Button) alertviewcontent
							.findViewById(R.id.btn_edit);
					btn_edit.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							alertalbum.dismiss();
							final AlertDialog editalertview = new AlertDialog.Builder(
									activity).create();
							View editalert = ainflater.inflate(
									R.layout.alert_edit_album, null);
							Button btn_save = (Button) editalert
									.findViewById(R.id.btn_save);
							Button btn_cancel = (Button) editalert
									.findViewById(R.id.btn_cancel);
							final EditText edit_name = (EditText) editalert
									.findViewById(R.id.edit_name);
							final EditText edit_desc = (EditText) editalert
									.findViewById(R.id.edit_desc);

							btn_save.setOnClickListener(new OnClickListener() {

								@Override
								public void onClick(View v) {
									// TODO Auto-generated method stub
									if (edit_name.getText().length() > 0
											& edit_desc.getText().length() > 0) {
										boolean bool = new ConDetect(
												getActivity()).isOnline();
										if (bool) {
											// Create object of AsycTask and
											// execute
											final EditAlbumTask editalbumTask = new EditAlbumTask();

											Log.e("Editing album of userId-->",
													userId + ", nodeId --> "
															+ nodeId);
											editalbumTask.execute(edit_name
													.getText().toString(),
													edit_desc.getText()
															.toString(), albobj
															.getmAlbumHash());
											Handler handler = new Handler();
											handler.postDelayed(new Runnable() {
												@Override
												public void run() {
													if (editalbumTask
															.getStatus() == AsyncTask.Status.RUNNING) {
														editalbumTask
																.cancel(true);
													}
												}
											}, 10000);
											editalertview.dismiss();
										} else {
											Toast.makeText(
													getActivity(),
													"!No Internet Connection,Try again",
													Toast.LENGTH_LONG).show();
										}
									} else {
										Toast.makeText(
												getActivity(),
												getResources()
														.getString(
																R.string.enterthevalues),
												Toast.LENGTH_LONG).show();
									}
								}
							});

							btn_cancel
									.setOnClickListener(new OnClickListener() {

										@Override
										public void onClick(View v) {
											// TODO Auto-generated method stub
											editalertview.dismiss();
										}
									});

							editalertview.setView(editalert);
							editalertview.show();

						}
					});
					Button btn_delete = (Button) alertviewcontent
							.findViewById(R.id.btn_delete);
					btn_delete.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							// TODO Auto-generated method stub
							alertalbum.dismiss();
							new AlertDialog.Builder(activity)
									.setTitle("Alert")
									.setMessage(
											"Are you sure to delete Album \""
													+ albobj.getmAlbumName()
													+ "\" ?")
									.setPositiveButton(
											"Yes",
											new DialogInterface.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface arg0,
														int arg1) {
													// TODO Auto-generated
													// method stub
													boolean bool = new ConDetect(
															getActivity())
															.isOnline();
													if (bool) {
														// Create object of
														// AsycTask and execute
														final DeleteAlbumTask delalbumTask = new DeleteAlbumTask();

														Log.e("Delete album of userId-->",
																userId
																		+ ", nodeId --> "
																		+ nodeId);
														delalbumTask.execute(
																userId,
																albobj.getmAlbumHash());
														Handler handler = new Handler();
														handler.postDelayed(
																new Runnable() {
																	@Override
																	public void run() {
																		if (delalbumTask
																				.getStatus() == AsyncTask.Status.RUNNING) {
																			delalbumTask
																					.cancel(true);
																		}
																	}
																}, 10000);

													} else {
														Toast.makeText(
																getActivity(),
																"!No Internet Connection,Try again",
																Toast.LENGTH_LONG)
																.show();
													}
												}
											}).show();

						}
					});
					alertalbum.setView(alertviewcontent);
					alertalbum.show();
				}
				return false;
			}
		});

		// Initilizing Grid View
		// InitilizeGridLayout();

		// loading all image paths from SD card
		// imagePaths = utils.getFilePaths();

		// Gridview adapter
		// List<Albums> mAlbums=new ArrayList<Albums>();
		// for(int i=0;i<10;i++){
		// Albums mAlbum=new Albums();
		// mAlbum.setmAlbumName("Album "+i);
		//
		// mAlbums.add(mAlbum);
		// }
		//
		// mUserAlbum.setmAlbums(mAlbums);
		// mUserAlbum.setmPhotoCount(5+" photos");

		textViewPhotos.setBackgroundColor(getResources().getColor(
				R.color.pt_dark_grey));
		textViewPhotos.setOnClickListener(this);
		textViewVideos.setOnClickListener(this);
		buttonCreatePhotos.setOnClickListener(this);
		return rootView;
	}

	private void showAlbum(String albumhash) {
		Bundle bundle_photos = new Bundle();
		bundle_photos.putString("albumhash", albumhash);
		bundle_photos.putString("loggeduid", userId);

		fragment = new ViewAlbumPhotosFragment();
		fragment.setArguments(bundle_photos);

		fragmentManager = getActivity().getSupportFragmentManager();
		fragmentManager.beginTransaction()
				// Add this transaction to the back stack
				.addToBackStack(null).replace(R.id.frame_container, fragment)
				.commit();
	}

	private void getGalleryDetials() {

		boolean bool = new ConDetect(getActivity()).isOnline();
		if (bool) {
			// Create object of AsycTask and execute
			final GetAlbumDetailsTask getGalleryTask = new GetAlbumDetailsTask();

			Log.e("Loding Gallery of userId-->", userId + ", nodeId --> "
					+ nodeId);
			getGalleryTask.execute(userId, nodeId);
			Handler handler = new Handler();
			handler.postDelayed(new Runnable() {
				@Override
				public void run() {
					if (getGalleryTask.getStatus() == AsyncTask.Status.RUNNING) {
						getGalleryTask.cancel(true);
					}
				}
			}, 10000);

		} else {
			Toast.makeText(getActivity(), "!No Internet Connection,Try again",
					Toast.LENGTH_LONG).show();
		}
	}

	private void InitilizeGridLayout() {
		Resources r = getResources();
		float padding = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
				AppConstant.GRID_PADDING, r.getDisplayMetrics());
		columnWidth = (int) ((utils.getScreenWidth() - ((AppConstant.NUM_OF_COLUMNS + 1) * padding)) / AppConstant.NUM_OF_COLUMNS);

		Log.d("fzdfzf----------", "dd  " + (int) utils.getScreenWidth());
		Log.d("fzdfzf----------", "dd  " + columnWidth);
		Log.d("fzdfzf----------", "dd  "
				+ (int) (utils.getScreenWidth() / columnWidth));
		gridView.setNumColumns((int) (utils.getScreenWidth() / columnWidth));
		gridView.setColumnWidth(columnWidth);
		gridView.setStretchMode(GridView.NO_STRETCH);
		gridView.setPadding((int) padding, (int) padding, (int) padding,
				(int) padding);
		gridView.setHorizontalSpacing((int) padding);
		gridView.setVerticalSpacing((int) padding);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

		switch (v.getId()) {
		case R.id.textviewphotos:
			fragment = new ViewPhotosFragment();
			if (fragment != null) {
				fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.beginTransaction()
						// Add this transaction to the back stack
						.addToBackStack(null)
						.replace(R.id.frame_container, fragment).commit();
			}
			break;
		case R.id.textviewvideos:
			fragment = new ViewVideosFragment();
			if (fragment != null) {
				fragmentManager = getActivity().getSupportFragmentManager();
				fragmentManager.beginTransaction()
						// Add this transaction to the back stack
						.addToBackStack(null)
						.replace(R.id.frame_container, fragment).commit();
			}
			break;
		case R.id.btncreatestudio:
//			fragment = new CreatePhotosFragment();
//			if (fragment != null) {
//				fragmentManager = getActivity().getSupportFragmentManager();
//				fragmentManager.beginTransaction()
//						// Add this transaction to the back stack
//						.addToBackStack(null)
//						.replace(R.id.frame_container, fragment).commit();
//			}
			startActivity(new Intent(activity, CreateNewAlbumActivity.class));
			break;
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
	}

	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
	}

	public class GetAlbumDetailsTask extends AsyncTask<String, Void, String> {

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Loading Album...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.e(TAG, "Album of : userId-->" + params[0] + "nodeId -->"
					+ params[1]);
			// ---------change method name

			return HttpConnectionUtils.getAlbumListResponse(
					params[0],
					params[1],
					getActivity().getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(
									R.string.url_gallary));
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);

			if ((pDialog != null) && pDialog.isShowing()) {
				pDialog.dismiss();
			}

			Log.i("Gallery list Response ", response);

			try {
				JSONObject galleryResponseObject = new JSONObject(response);
				String responseResult = galleryResponseObject
						.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);

				if (responseResult.equals("Success")) {

					mUserAlbum.setmUserId(galleryResponseObject
							.getString("userid"));
					mUserAlbum.setmPhotoCount(galleryResponseObject
							.getString("photocount"));
					mUserAlbum.setmUserName(galleryResponseObject
							.getString("username"));
					mUserAlbum.setmImageExists(galleryResponseObject
							.getString("imageexists"));
					mUserAlbum.setmThumFlag(String
							.valueOf(galleryResponseObject
									.getBoolean("thumbFlag")));
					mUserAlbum
							.setmView(galleryResponseObject.getString("view"));

					JSONArray dataArray = galleryResponseObject
							.getJSONArray("albums");

					if (null != mUserAlbum.getmAlbums()
							&& mUserAlbum.getmAlbums().size() > 0) {
						mUserAlbum.getmAlbums().clear();
					}
					// Toast.makeText(getActivity(),
					// "Size : "+eventArrayList.size(),
					// Toast.LENGTH_LONG).show();

					List<Albums> mAlList = new ArrayList<Albums>();
					for (int i = 0; i < dataArray.length(); i++) {
						JSONObject c = (JSONObject) dataArray.getJSONObject(i);
						Albums mAlbum = new Albums();

						mAlbum.setmAlbumName(c.getString("albumname"));
						mAlbum.setmAlbumHash(c.getString("albumhash"));
						mAlbum.setmPhotoHash(c.getString("photohash"));
						mAlbum.setmFolderFlag(String.valueOf(c
								.getBoolean("folderFlag")));
						mAlList.add(mAlbum);

					}
					mUserAlbum.setmAlbums(mAlList);

					adapter = new GridViewImageAdapter(activity, mUserAlbum);
					gridView.setAdapter(adapter);

					// adapter.notifyDataSetChanged();
					// Toast.makeText(getActivity(), "Listing Notification ",
					// Toast.LENGTH_SHORT).show();
				}

			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(),
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
			Crouton.makeText(activity,
					"Your Network Connection is Very Slow, Try again",
					Style.ALERT).show();
		}
	}

	public class DeleteAlbumTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Deleting Album...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub
			return HttpConnectionUtils.deleteAlbum(
					params[0],
					params[1],
					getActivity().getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(
									R.string.url_gallary_album_delete));
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
				JSONObject galleryResponseObject = new JSONObject(response);
				String responseResult = galleryResponseObject
						.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);

				if (responseResult.equals("Success")) {
					getGalleryDetials();
					Crouton.makeText(activity, "Album Deleted.", Style.ALERT)
							.show();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(),
						"Invalid Server Content - ",
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content from Notification!!");
			}
		}
	}

	public class EditAlbumTask extends AsyncTask<String, Void, String> {
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			pDialog = new ProgressDialog(getActivity());
			pDialog.setMessage("Editing Album...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			// TODO Auto-generated method stub

			Log.i("zacharia", "" + params[0] + " " + params[1] + " "
					+ params[2]);
			return HttpConnectionUtils.editAlbumDescription(
					params[0], // name
					params[1], // description
					params[2], // albumhash
					getActivity().getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(
									R.string.url_gallary_edit_name));
		}

		@Override
		protected void onPostExecute(String response) {
			// TODO Auto-generated method stub
			super.onPostExecute(response);
			if ((pDialog != null) && pDialog.isShowing()) {

				pDialog.dismiss();
			}

			Log.i("Edit Album Response ", response);

			try {
				JSONObject galleryResponseObject = new JSONObject(response);
				String responseResult = galleryResponseObject
						.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);

				if (responseResult.equals("Success")) {
					Crouton.makeText(activity, "Album Details Edited.",
							Style.ALERT).show();
					getGalleryDetials();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ", "" + tempStack.getLineNumber()
							+ " methodName: " + tempStack.getClassName() + "-"
							+ tempStack.getMethodName());
				}
				Toast.makeText(getActivity(),
						"Invalid Server Content - ",
						Toast.LENGTH_LONG).show();
				Log.d(TAG, "Invalid Server content from Notification!!");
			}
		}
	}
}
