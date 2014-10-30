package com.parivartree.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.parivartree.R;

public class SelectRelationFragment extends Fragment {

	Context context;
	ImageView userImageView, connectorFatherMother, connectorBrother, connectorSister, connectorSonDaughterSpouse;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

		View rootView = inflater.inflate(R.layout.fragment_select_relation, container, false);

		context = this.getActivity().getApplicationContext();

		userImageView = (ImageView) rootView.findViewById(R.id.imageView1);
		connectorFatherMother = (ImageView) rootView.findViewById(R.id.imageView2);
		connectorSonDaughterSpouse = (ImageView) rootView.findViewById(R.id.imageView3);
		connectorBrother = (ImageView) rootView.findViewById(R.id.imageView4);
		connectorSister = (ImageView) rootView.findViewById(R.id.imageView5);

		userImageView.measure(0, 0);

		connectorFatherMother.setImageDrawable(generateFatherMotherConnector(userImageView.getMeasuredHeight(),
				userImageView.getMeasuredWidth()));
		connectorBrother.setImageDrawable(generateBrotherSisterConnector(userImageView.getMeasuredHeight(),
				userImageView.getMeasuredWidth()));
		connectorSister.setImageDrawable(generateBrotherSisterConnector(userImageView.getMeasuredHeight(),
				userImageView.getMeasuredWidth()));

		return rootView;
	}

	public BitmapDrawable generateBrotherSisterConnector(int height, int width) {

		Bitmap connector = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		paint.setColor(context.getResources().getColor(R.color.pt_blue));
		paint.setStrokeWidth(5);

		connectorCanvas.drawLine(0, height / 2, width / 2, height / 2, paint);

		return new BitmapDrawable(context.getResources(), connector);
	}

	public BitmapDrawable generateFatherMotherConnector(int height, int width) {

		Bitmap connector = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		// paint.setColor(Color.BLACK);
		paint.setColor(context.getResources().getColor(R.color.pt_blue));
		paint.setStrokeWidth(5);

		connectorCanvas.drawLine(0, height / 2, width, height / 2, paint);
		connectorCanvas.drawLine(width / 2, height / 2, width / 2, height, paint);

		return new BitmapDrawable(context.getResources(), connector);
	}

	public BitmapDrawable generateSonDaughterSpouseConnector(int height, int width) {

		Bitmap connector = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
		Canvas connectorCanvas = new Canvas(connector);

		Paint paint = new Paint();
		paint.setColor(context.getResources().getColor(R.color.pt_blue));
		paint.setStrokeWidth(5);

		connectorCanvas.drawLine(0, height / 5, connector.getWidth(), height / 5, paint);

		return new BitmapDrawable(context.getResources(), connector);
	}
}
