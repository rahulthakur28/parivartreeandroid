package com.parivartree.fragments;

import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.adapters.AutocompleteCustomArrayAdapter;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.MyObject;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class InviteFragment extends Fragment {
	ArrayAdapter<MyObject> myAdapter;
	// MyObject[] ObjectItemData;
	ArrayList<MyObject> ObjectItemData = new ArrayList<MyObject>();
	SearchUserTask searchUserTask = null;
	private String TAG = "InviteFragment";
	private String userId;
	Activity activity;
	//CustomAutoCompleteView searchUserAutoComplete;
ListView searchList;
EditText searchNameEdit;
	SharedPreferences sharedPreferences;
	Editor sharedPreferencesEditor;
	
	int searchTaskProcessCalledCount = 0;

	public InviteFragment() {
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_invite, container, false);
		sharedPreferences = this
				.getActivity()
				.getApplicationContext()
				.getSharedPreferences(
						getActivity().getPackageName() + getResources().getString(R.string.USER_PREFERENCES),
						Context.MODE_PRIVATE);
		userId = sharedPreferences.getString("user_id", null);
		activity = getActivity();

		sharedPreferencesEditor = sharedPreferences.edit();

		// Instantiating an adapter to store each items
		// R.layout.listview_layout defines the layout of each item
		//searchUserAutoComplete = (CustomAutoCompleteView) rootView.findViewById(R.id.autoCompleteUser);
		searchList=(ListView) rootView.findViewById(R.id.searchlist);
		searchNameEdit= (EditText) rootView.findViewById(R.id.Searchname);
		myAdapter = new AutocompleteCustomArrayAdapter(getActivity(), R.layout.list_view_row, ObjectItemData);
		searchList.setAdapter(myAdapter);

		searchNameEdit.clearFocus();

		searchList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int spos, long dpos) {
				// TODO Auto-generated method stub
				
				MyObject objectItem = ObjectItemData.get(spos);

				searchNameEdit.setText("");
				if (!(objectItem.objectName.trim()).equals("No results found")) {
					sharedPreferencesEditor.putString("node_id", objectItem.objectId);
					sharedPreferencesEditor.commit();
					Bundle bundle = new Bundle();
					if (objectItem.objectStatus.trim().equalsIgnoreCase("Already Connected")) {
						bundle.putInt("relation", 0);
					} else {
						bundle.putInt("relation", 1);
					}
					((MainActivity) getActivity()).changeFragment("ProfileFragment", bundle);
				}
			}
		});

		searchNameEdit.addTextChangedListener(new TextWatcher() {

			@Override
			public void onTextChanged(CharSequence s, int start, int count, int after) {
				// TODO Auto-generated method stub
				if((searchNameEdit.getText().toString().trim().length()) > 0){
					boolean bool = new ConDetect(getActivity()).isOnline();
					if (bool) {
						if (searchUserTask != null) {
							searchUserTask.cancel(true);
						}
						Log.d("Search user", "AsyncTask calling");
						searchUserTask = new SearchUserTask();
						searchUserTask.execute(s.toString(), userId);
						/*
						Handler handler = new Handler();
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								if (searchUserTask.getStatus() == AsyncTask.Status.RUNNING){
									searchUserTask.cancel(true);
								}
							}
						}, 10000);
						*/
					} else {
						//Toast.makeText(getActivity(), "No Internet Connection,Try again", Toast.LENGTH_LONG).show();
						Crouton.makeText(activity, "No internet connection found", Style.ALERT).show();
					}
				}
			}
			
			@Override
			public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void afterTextChanged(Editable arg0) {
				// TODO Auto-generated method stub

			}
		});

		return rootView;
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	public class SearchUserTask extends AsyncTask<String, Void, String> {

		
		
		@Override
		protected void onPreExecute() {
			/*
			if (searchNameEdit.getText().toString().trim().equals("")) {
				ObjectItemData.clear();
			}
			*/
			searchTaskProcessCalledCount++;
			
			Log.e(TAG, "SearchUserTask processCalledCount increased- " + searchTaskProcessCalledCount);
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground  : " + params[0]);
			// ---------change method name
			if (isCancelled()) {
				// Log.d(TAG, "async task is cancelled");
				return (null); // don't forget to terminate this method
			}
			return HttpConnectionUtils.getSearchUserResponse(
					params[0],
					params[1],
					getResources().getString(R.string.hostname)
							+ getActivity().getResources().getString(R.string.url_search_users));

		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			searchTaskProcessCalledCount--;
			Log.e(TAG, "SearchUserTask processCalledCount decreased- " + searchTaskProcessCalledCount);
			
			if(response.equals("timeout")) {
				if(searchTaskProcessCalledCount == 0) {
					
					Crouton.makeText(activity, "Your network connection is very slow", Style.ALERT).show();
				}
			} else {
				Log.i("event list Response ", response);
				try {
					JSONArray eventListResponseArray = new JSONArray(response);
					// ObjectItemData = new
					// MyObject[eventListResponseArray.length()];
					ObjectItemData.clear();
					for (int i = 0; i < eventListResponseArray.length(); i++) {
						JSONObject c = eventListResponseArray.getJSONObject(i);
						if (c.has("result")) {
							String result = c.getString("result");
							if (result.equals("Success")) {
								int gender=1,deceased=0;
								if (c.has("gender")) {
									gender = c.getInt("gender");
								}
								if (c.has("deceased")) {
									deceased = c.getInt("deceased");
								}
								String inviteUserId = c.getString("id");
								String name = c.getString("firstname") + " " + c.getString("lastname");
								String status = c.getString("parameter");
								String fullname = name;
								if(status.equalsIgnoreCase("Unhide")){
									ObjectItemData.add(new MyObject(fullname, inviteUserId, status, gender, deceased));
								}else{
									ObjectItemData.add(new MyObject(fullname, inviteUserId, "NA", gender, deceased));
								}
							}
						} else {
							ObjectItemData.add(new MyObject("No results found", null, "NA",0,0));
						}

					}

					Log.i(TAG, "ObjectItemData size: - " + ObjectItemData.size());
					if (ObjectItemData.size() > 20) {
						ObjectItemData.subList(20, ObjectItemData.size() - 1).clear();
						;
					}

					// myAdapter.notifyDataSetInvalidated();
					// myAdapter.notifyDataSetChanged();
					// searchUserAutoComplete.showDropDown();
					Log.d(TAG, "dataSetChanged");
					Log.d(TAG, "dataSetChanged :"+ObjectItemData);
					
					//myAdapter = new AutocompleteCustomArrayAdapter(getActivity(), R.layout.list_view_row, ObjectItemData);
					myAdapter.notifyDataSetChanged();
					//Log.d(TAG, "event handle : "+searchUserAutoComplete.dispatchWindowFocusChanged(true));
					//searchUserAutoComplete.dispatchWindowFocusChanged(true);
//					KeyEvent eventSpace = new KeyEvent(KeyEvent.ACTION_UP, 62);
//				    KeyEvent delete = new KeyEvent(KeyEvent.ACTION_UP, 67);
//				    searchUserAutoComplete.dispatchKeyEvent(eventSpace);
//				    searchUserAutoComplete.dispatchKeyEvent(delete);

				} catch (Exception e) {
					for (StackTraceElement tempStack : e.getStackTrace()) {
						// Log.d("Exception thrown: Treeview Fetch", "" +
						// tempStack.getLineNumber());
						Log.d("Exception thrown: ",
								"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
										+ tempStack.getMethodName());
					}
					Toast.makeText(getActivity(), "Invalid Server Content - " + e.getMessage(), Toast.LENGTH_LONG).show();
					Log.d(TAG, "Invalid Server content!!");
				}
			}
		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			searchTaskProcessCalledCount--;
			Log.e(TAG, "SearchUserTask processCalledCount decreased - " + searchTaskProcessCalledCount);
			
			if(searchTaskProcessCalledCount == 0) {
				
				Crouton.makeText(activity, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
			}
			
			
			
			//Crouton.
			
		}
	}

	@Override
	public void onDestroy() {
		// you may call the cancel() method but if it is not handled in
		// doInBackground() method
		if (searchUserTask != null && searchUserTask.getStatus() != AsyncTask.Status.FINISHED) {
			searchUserTask.cancel(true);
		}
		super.onDestroy();
	}
}
