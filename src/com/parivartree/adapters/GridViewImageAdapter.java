package com.parivartree.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.koushikdutta.urlimageviewhelper.UrlImageViewHelper;
import com.parivartree.MainActivity;
import com.parivartree.R;
import com.parivartree.models.Albums;
import com.parivartree.models.PostItem;
import com.parivartree.models.UserAlbum;

public class GridViewImageAdapter extends BaseAdapter {

	private Activity mActivity;
	private UserAlbum mUserAlbum;
	
	private LayoutInflater inflater;
	
	public GridViewImageAdapter(Activity activity, UserAlbum userAlbum) {
		this.mActivity = activity;
		this.mUserAlbum = userAlbum;
		inflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return this.mUserAlbum.getmAlbums().size();
	}

	@Override
	public Object getItem(int position) {
		return this.mUserAlbum.getmAlbums().get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		
		ImageView mAlbumImage;
		TextView mAlbumName,mPhotocount;
       ViewHolder viewHolder;
       
		
		if(convertView == null) {
			convertView =  (LinearLayout)inflater.inflate(R.layout.view_album_item, parent, false);
			mAlbumImage=(ImageView)convertView.findViewById(R.id.album_image);
			mAlbumName=(TextView)convertView.findViewById(R.id.album_name);
			mPhotocount=(TextView)convertView.findViewById(R.id.photo_count);
			viewHolder=new ViewHolder(mAlbumImage, mAlbumName, mPhotocount);
			convertView.setTag(viewHolder);
		}
		else {
			viewHolder = (ViewHolder) convertView.getTag();
			mAlbumImage=viewHolder.getmAlbumImage();
			mAlbumName=viewHolder.getmAlbumName();
			mPhotocount=viewHolder.getmPhotocount();
		}
		
		Albums mAlbums=(Albums)mUserAlbum.getmAlbums().get(position);
		
		UrlImageViewHelper.setUrlDrawable(mAlbumImage, mActivity.getResources().getString(R.string.galleryhostname)+
				mAlbums.getmAlbumHash()+"/thumbs/"+mAlbums.getmPhotoHash()+".jpeg", R.drawable.parivartree_logo_127);
				
		viewHolder.mAlbumName.setText(mAlbums.getmAlbumName());
		//viewHolder.mPhotocount.setText(mUserAlbum.getmPhotoCount());
		
		Log.e(mAlbums.getmAlbumName(), mUserAlbum.getmPhotoCount());
		
		// image view click listener
		viewHolder.mAlbumImage.setOnClickListener(new OnImageClickListener(position));

		return convertView;
	}
	
	private class ViewHolder{
		private ImageView mAlbumImage;
		private TextView mAlbumName,mPhotocount;
		public ViewHolder(ImageView mAlbumImage, TextView mAlbumName,
				TextView mPhotocount) {
			super();
			this.mAlbumImage = mAlbumImage;
			this.mAlbumName = mAlbumName;
			this.mPhotocount = mPhotocount;
		}
		public ImageView getmAlbumImage() {
			return mAlbumImage;
		}
		public TextView getmAlbumName() {
			return mAlbumName;
		}
		public TextView getmPhotocount() {
			return mPhotocount;
		}
	}

	private class OnImageClickListener implements OnClickListener {

		int _postion;

		// constructor
		public OnImageClickListener(int position) {
			this._postion = position;
		}

		@Override
		public void onClick(View v) {
			// on selecting grid view image
			// launch full screen activity
			//Bundle bndl = new Bundle();
		//	bndl.putInt("position", _postion);
			//((MainActivity)mActivity).changeFragment("FullScreenFragment", bndl);
			
			Log.e("Album Clicked", ""+_postion);
			
//			Intent i = new Intent(_activity, FullScreenViewActivity.class);
//			i.putExtra("position", _postion);
//			_activity.startActivity(i);
		}

	}

	/*
	 * Resizing image size
	 */
	public static Bitmap decodeFile(InputStream stream, int WIDTH, int HIGHT) {
		try {

//			File f = new File(filePath);

			BitmapFactory.Options o = new BitmapFactory.Options();
			o.inJustDecodeBounds = true;
			BitmapFactory.decodeStream(stream, null, o);

			final int REQUIRED_WIDTH = WIDTH;
			final int REQUIRED_HIGHT = HIGHT;
			int scale = 1;
			while (o.outWidth / scale / 2 >= REQUIRED_WIDTH
					&& o.outHeight / scale / 2 >= REQUIRED_HIGHT)
				scale *= 2;

			BitmapFactory.Options o2 = new BitmapFactory.Options();
			o2.inSampleSize = scale;
			return BitmapFactory.decodeStream(stream, null, o2);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	class ImageDownloaderTask extends AsyncTask<String, Void, Bitmap> {
		private final WeakReference imageViewReference;

		public ImageDownloaderTask(ImageView imageView) {
			imageViewReference = new WeakReference(imageView);
		}

		@Override
		// Actual download method, run in the task thread
		protected Bitmap doInBackground(String... params) {
			// params comes from the execute() call: params[0] is the url.
			return downloadBitmap(params[0]);
		}

		@Override
		// Once the image is downloaded, associates it to the imageView
		protected void onPostExecute(Bitmap bitmap) {
			if (isCancelled()) {
				bitmap = null;
			}

			if (imageViewReference != null) {
				ImageView imageView = (ImageView)imageViewReference.get();
				if (imageView != null) {

					if (bitmap != null) {
						imageView.setImageBitmap(bitmap);
					} else {
						imageView.setImageResource(R.drawable.parivar_mobile_profile_image_normal);
					}
				}

			}
		}

	}
	
	static Bitmap downloadBitmap(String url) {
		final AndroidHttpClient client = AndroidHttpClient.newInstance("Android");
		final HttpGet getRequest = new HttpGet(url);
		try {
			HttpResponse response = client.execute(getRequest);
			final int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode != HttpStatus.SC_OK) {
				Log.w("ImageDownloader", "Error " + statusCode
						+ " while retrieving bitmap from " + url);
				return null;
			}

			final HttpEntity entity = response.getEntity();
			if (entity != null) {
				InputStream inputStream = null;
				try {
					inputStream = entity.getContent();
					final Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
					return bitmap;
				} finally {
					if (inputStream != null) {
						inputStream.close();
					}
					entity.consumeContent();
				}
			}
		} catch (Exception e) {
			// Could provide a more explicit error message for IOException or
			// IllegalStateException
			getRequest.abort();
			Log.w("ImageDownloader", "Error while retrieving bitmap from " + url);
		} finally {
			if (client != null) {
				client.close();
			}
		}
		return null;
	}
	

}
