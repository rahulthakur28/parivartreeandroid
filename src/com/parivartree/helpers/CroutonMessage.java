package com.parivartree.helpers;

import android.app.Activity;
import android.view.View;
import android.view.View.OnClickListener;
import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

public class CroutonMessage implements OnClickListener {
private static final CroutonMessage object = new CroutonMessage();
private static Crouton crouton;
	public static void showCroutonAlert(Activity activity, String message, int duration){
		crouton = Crouton.makeText(activity, message, Style.ALERT);	
		crouton.setOnClickListener(object).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(duration).build()).show();			
	}
	public static void showCroutonInfo(Activity activity, String message, int duration){
		crouton = Crouton.makeText(activity, message, Style.INFO);	
		crouton.setOnClickListener(object).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(duration).build()).show();			
	}
	public static void showCroutonConfirm(Activity activity, String message, int duration){
		crouton = Crouton.makeText(activity, message, Style.CONFIRM);	
		crouton.setOnClickListener(object).setConfiguration(new de.keyboardsurfer.android.widget.crouton.Configuration.Builder().setDuration(duration).build()).show();			
	}
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub		
	}
}
