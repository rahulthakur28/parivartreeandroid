package com.parivartree.fragments;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;

public class DatePickerFragment extends DialogFragment {
	OnDateSetListener ondateSet;

	public DatePickerFragment() {
	}

	public void setCallBack(OnDateSetListener ondate) {
		ondateSet = ondate;
	}

	private int year, month, day;

	@Override
	public void setArguments(Bundle args) {
		super.setArguments(args);
		year = args.getInt("year");
		month = args.getInt("month");
		day = args.getInt("day");
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		final DatePickerDialog dpd = new DatePickerDialog(getActivity(), ondateSet, year, month, day);

		dpd.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
			}
		});

		return dpd;
	}
}