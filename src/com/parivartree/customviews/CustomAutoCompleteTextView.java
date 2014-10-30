package com.parivartree.customviews;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.AutoCompleteTextView;

/**
 * Customizing AutoCompleteTextView to return Country Name corresponding to the
 * selected item
 */
public class CustomAutoCompleteTextView extends AutoCompleteTextView {

	public CustomAutoCompleteTextView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	public CustomAutoCompleteTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	// this is how to disable AutoCompleteTextView filter
	@Override
	protected void performFiltering(final CharSequence text, final int keyCode) {
		String filterText = "";
		super.performFiltering(filterText, keyCode);
	}
}
