package com.parivartree.adapters;

import java.util.ArrayList;

import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.internal.mc;
import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.R;
import com.parivartree.fragments.NotificationFragment;
import com.parivartree.helpers.ConDetect;
import com.parivartree.helpers.CroutonMessage;
import com.parivartree.helpers.HttpConnectionUtils;
import com.parivartree.models.NotificationModel;

import de.keyboardsurfer.android.widget.crouton.Crouton;
import de.keyboardsurfer.android.widget.crouton.Style;

@SuppressLint("ViewHolder")
public class CustomNotificationAdapter extends BaseAdapter {
	private String TAG = "CustomNotificationAdapter";
	ArrayList<NotificationModel> notificationobj;
	Activity context;
	String userId;
	int itemPosition;
	Fragment fragment;
	String typeinvitation="",entityNameStr = "",addedbyStr ="";
	boolean inactive2nd= false;
	
	public CustomNotificationAdapter(Activity context, Fragment fragment, ArrayList<NotificationModel> notificationobj,
			String userId) {
		this.context = context;
		this.notificationobj = notificationobj;
		this.userId = userId;
		this.fragment = fragment;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return notificationobj.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return notificationobj.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		itemPosition = position;

		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		LinearLayout ll = (LinearLayout) inflater.inflate(R.layout.custom_notification_list, parent, false);
		TextView txtNotification = (TextView) ll.findViewById(R.id.textviewnotifilist);
		TextView txtNotificationDate = (TextView) ll.findViewById(R.id.textviewnotifidate);
		ImageView image = (ImageView) ll.findViewById(R.id.imageViewnotifi);
		LinearLayout childll = (LinearLayout) ll.findViewById(R.id.linearacceptignore);

		image.setVisibility(View.VISIBLE);
		txtNotificationDate.setVisibility(View.VISIBLE);

		UrlImageViewHelper.setUrlDrawable(image,
				"https://www.parivartree.com/profileimages/thumbs/" + notificationobj.get(position).getOwnerid()
						+ "PROFILE.jpeg", context.getResources().getDrawable(R.drawable.parivar_mobile_profile_image_vsmall), 0);
		
		String notifiText = notificationobj.get(position).getExactNotificationText();
		txtNotification.setText(notifiText);
		txtNotificationDate.setText(notificationobj.get(position).getDate());

		final String entityIdstr = String.valueOf(notificationobj.get(position).getEntityid());
		final String notifiIdstr = String.valueOf(notificationobj.get(position).getNotifid());
		if(notificationobj.get(position).getAddedby() != null){
			addedbyStr = notificationobj.get(position).getAddedby();
		}
		if(notificationobj.get(position).getEntityname() != null){
			entityNameStr = notificationobj.get(position).getEntityname();
		}
		
				
		if (notifiText.trim().equals("You do not have any New notifications")) {
			image.setVisibility(View.GONE);
			txtNotificationDate.setVisibility(View.INVISIBLE);
			childll.setVisibility(View.GONE);
		} else {
			final int notifiType = notificationobj.get(position).getNotificationtype();
			String notifiStatus = notificationobj.get(position).getNotificationstatus();
			Log.d("layout", " notifi type : " + notifiType);
			if ((notifiType == 4 || notifiType == 11 ||notifiType == 19 || notifiType == 20) && (notifiStatus.trim().equals("0"))) {
				Log.d("layout", " visible : " + notifiType + " , " + notifiType + " , " + notifiStatus);
if(notifiType == 4){
	inactive2nd = false;
}
				childll.setVisibility(View.VISIBLE);
				// Button invite=(Button)
				// childll.findViewById(R.id.btnacceptinvitation);
				// if(notifiType==11){
				// invite.setText("Invite");
				// }
				// if(notifiType==11){
				// invite.setText("Accept");
				// }
				Button btnAccept = (Button) ll.findViewById(R.id.btnacceptinvitation);
				Button btnIgnore = (Button) ll.findViewById(R.id.btnignoreinvitation);

				btnAccept.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						boolean bool = new ConDetect(context).isOnline();
						if (bool) {
							if (notifiType == 11) {
								inactive2nd = false;
								addedbyStr = notificationobj.get(position).getAddedby();
								typeinvitation = "recomondation";
								Log.d(TAG, "invitationAcceptTask!!" + entityIdstr + "," + notifiIdstr + "," + userId);
								final InvitationAcceptTask invitationAcceptTask = new InvitationAcceptTask();
								invitationAcceptTask.execute(String.valueOf(position), entityIdstr, notifiIdstr,
										userId, "recomondation");
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (invitationAcceptTask.getStatus() == AsyncTask.Status.RUNNING){
											invitationAcceptTask.cancel(true);
										}
									}
								}, 10000);
							} else {
								if(notifiType == 20){
									addedbyStr = notificationobj.get(position).getAddedby();
									inactive2nd = true;
								}else{
									inactive2nd = false;
								}
								entityNameStr = notificationobj.get(position).getEntityname();
								typeinvitation = "invitation";
								Log.d(TAG, "invitationAcceptTask!!" + entityIdstr + "," + notifiIdstr + "," + userId);
								final InvitationAcceptTask invitationAcceptTask = new InvitationAcceptTask();
								invitationAcceptTask.execute(String.valueOf(position), entityIdstr, notifiIdstr,
										userId, "invitation");
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (invitationAcceptTask.getStatus() == AsyncTask.Status.RUNNING){
											invitationAcceptTask.cancel(true);
										}
									}
								}, 10000);
							}
						} else {
							Crouton.makeText(context, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
						}
					}
				});
				btnIgnore.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View arg0) {
						boolean bool = new ConDetect(context).isOnline();
						if (bool) {
							if (notifiType == 11) {
								final InvitationDeclineTask invitationDeclineTask = new InvitationDeclineTask();
								invitationDeclineTask.execute(String.valueOf(position), entityIdstr, notifiIdstr,
										"recomondation");
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (invitationDeclineTask.getStatus() == AsyncTask.Status.RUNNING){
											invitationDeclineTask.cancel(true);
										}
									}
								}, 10000);
							} else {
								final InvitationDeclineTask invitationDeclineTask = new InvitationDeclineTask();
								invitationDeclineTask.execute(String.valueOf(position), entityIdstr, notifiIdstr,
										"invitation");
								Handler handler = new Handler();
								handler.postDelayed(new Runnable() {
									@Override
									public void run() {
										if (invitationDeclineTask.getStatus() == AsyncTask.Status.RUNNING){
											invitationDeclineTask.cancel(true);
										}
									}
								}, 10000);
							}
						} else {
							Crouton.makeText(context, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
						}
					}
				});

			} else if(notifiType == 10 ) {
				Log.d("layout", " invisible");
				childll.setVisibility(View.GONE);
			}
		}
		return ll;
	}

	public class InvitationAcceptTask extends AsyncTask<String, Void, String> {
		int acceptPosition;
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Accepting...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground" + params[0]);
			// ---------change method name
			acceptPosition = Integer.parseInt(params[0]);
			if (params[4].equals("recomondation")) {
				return HttpConnectionUtils.InvitationAcceptResponse(
						params[1],
						params[2],
						params[3],
						context.getResources().getString(R.string.hostname)
								+ context.getResources().getString(R.string.url_recomondation_accept));
			} else {
				return HttpConnectionUtils.InvitationAcceptResponse(
						params[1],
						params[2],
						params[3],
						context.getResources().getString(R.string.hostname)
								+ context.getResources().getString(R.string.url_invitation_accept));
			}
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Accept invitation Response ", response);
			try {
				JSONObject deleteEventResponseObject = new JSONObject(response);
				int status = deleteEventResponseObject.getInt("AuthenticationStatus");
				String responseResult = deleteEventResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {

					Log.d(TAG, "Success Accept invitation");
					if(typeinvitation.equals("recomondation")){
						
						CroutonMessage.showCroutonInfo(context, "You have successfully invited "+addedbyStr+" to your family tree.You will have a complete access to further connections once " + addedbyStr + " accepts your invitation", 7000);
					
					}else if((inactive2nd = true) && (typeinvitation.equals("invitation"))){
						
						CroutonMessage.showCroutonInfo(context, addedbyStr+" has been added to your Family", 7000);
						
					}else if(typeinvitation.equals("invitation")){
						
						CroutonMessage.showCroutonInfo(context, entityNameStr+" has been added to your Family", 7000);
						
					}
					inactive2nd = false;
					((NotificationFragment) fragment).refreshNotification();

				} else if (status == 2) {
					CroutonMessage.showCroutonAlert(context, "You have already invited to your family", 7000);
					((NotificationFragment) fragment).refreshNotification();
				} else if (status == 3) {
					CroutonMessage.showCroutonAlert(context, "You are already connected", 7000);
					((NotificationFragment) fragment).refreshNotification();
				} else if (status == 4) {
					CroutonMessage.showCroutonAlert(context, "You are not authorised to access this page!", 7000);
					((NotificationFragment) fragment).refreshNotification();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(context, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content accept!!");
			}

		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(context, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}
	}

	public class InvitationDeclineTask extends AsyncTask<String, Void, String> {
		int declinePosition;
		private ProgressDialog pDialog;

		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(context);
			pDialog.setMessage("Decline...");
			pDialog.setIndeterminate(false);
			pDialog.setCancelable(true);
			pDialog.show();
		}

		@Override
		protected String doInBackground(String... params) {
			Log.d(TAG, "doInBackground : " + params[0]);
			declinePosition = Integer.parseInt(params[0]);
			if (params[3].equals("recomondation")) {
				return HttpConnectionUtils.InvitationDeclineResponse(
						params[1],
						params[2],
						context.getResources().getString(R.string.hostname)
								+ context.getResources().getString(R.string.url_recomondation_decline));
			} else {
				return HttpConnectionUtils.InvitationDeclineResponse(
						params[1],
						params[2],
						context.getResources().getString(R.string.hostname)
								+ context.getResources().getString(R.string.url_invitation_decline));
			}
		}

		protected void onPostExecute(String response) {
			super.onPostExecute(response);
			pDialog.dismiss();
			Log.i("Decline invitation Response ", response);
			try {
				JSONObject deleteEventResponseObject = new JSONObject(response);
				String responseResult = deleteEventResponseObject.getString("Status");
				Log.d(TAG, "onpostexecute" + responseResult);
				if (responseResult.equals("Success")) {

					Log.d(TAG, "Success Decline invitation");
					((NotificationFragment) fragment).refreshNotification();
				}
			} catch (Exception e) {
				for (StackTraceElement tempStack : e.getStackTrace()) {
					Log.d("Exception thrown: ",
							"" + tempStack.getLineNumber() + " methodName: " + tempStack.getClassName() + "-"
									+ tempStack.getMethodName());
				}
				Toast.makeText(context, "Invalid Server Content - ", Toast.LENGTH_LONG).show();
				// + e.getMessage()
				Log.d(TAG, "Invalid Server content decline!!");
			}

		}
		@Override
		protected void onCancelled(String result) {
			// TODO Auto-generated method stub
			super.onCancelled(result);
			pDialog.dismiss();
			Crouton.makeText(context, "Your Network Connection is Very Slow, Try again", Style.ALERT).show();
		}

	}
}
