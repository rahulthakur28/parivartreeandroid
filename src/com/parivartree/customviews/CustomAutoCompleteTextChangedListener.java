package com.parivartree.customviews;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;

public class CustomAutoCompleteTextChangedListener implements TextWatcher {

	public static final String TAG = "CustomAutoCompleteTextChangedListener.java";
	Context context;

	public CustomAutoCompleteTextChangedListener(Context context) {
		this.context = context;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onTextChanged(CharSequence userInput, int start, int before, int count) {

		try {

			// if you want to see in the logcat what the user types
			Log.e(TAG, "User input: " + userInput);

			// update the adapatermyAdapter.notifyDataSetChanged();

			// get suggestions from the database
			// MyObject[] myObjs = context.ObjectItemData;

			// update the adapter
			// context.myAdapter = new AutocompleteCustomArrayAdapter(context,
			// R.layout.list_view_row, myObjs);

			// context.myAutoComplete.setAdapter(context.myAdapter);

		} catch (NullPointerException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

}
