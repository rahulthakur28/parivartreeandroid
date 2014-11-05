package com.parivartree.helpers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.parivartree.R;

public class RectangularImageView extends ImageView {

	private int borderWidth;
	private int canvasSize;
	private Bitmap image;
	private Paint paint;
	private Paint paintBorder;
	Context context;

	public RectangularImageView(final Context context) {
		this(context, null);
		this.context = context;
	}

	public RectangularImageView(Context context, AttributeSet attrs) {
		this(context, attrs, R.attr.circularImageViewStyle);
		this.context = context;
	}

	public RectangularImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		this.context = context;

		// init paint
		paint = new Paint();
		paint.setAntiAlias(true);

		paintBorder = new Paint();
		paintBorder.setAntiAlias(true);

		// load the styled attributes and set their properties
		TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.CircularImageView, defStyle, 0);

		if (attributes.getBoolean(R.styleable.CircularImageView_border, true)) {
			int defaultBorderSize = (int) (4 * getContext().getResources().getDisplayMetrics().density);
			setBorderWidth(attributes.getDimensionPixelOffset(R.styleable.CircularImageView_border_width,
					defaultBorderSize));
			setBorderColor(attributes.getColor(R.styleable.CircularImageView_border_color, Color.BLUE));
		}

		if (attributes.getBoolean(R.styleable.CircularImageView_shadow, false)) {/*
																				 * addShadow
																				 * (
																				 * )
																				 * ;
																				 */
		}

		attributes.recycle();
	}

	public void setBorderWidth(int borderWidth) {
		this.borderWidth = borderWidth;
		this.requestLayout();
		this.invalidate();
	}

	public void setBorderColor(int borderColor) {
		if (paintBorder != null)
			paintBorder.setColor(borderColor);
		this.invalidate();
	}

	/*
	 * public void addShadow() { setLayerType(LAYER_TYPE_SOFTWARE, paintBorder);
	 * paintBorder.setShadowLayer(4.0f, 0.0f, 2.0f, Color.BLACK); }
	 */

	@SuppressLint("DrawAllocation")
	@Override
	public void onDraw(Canvas canvas) {
		// load the bitmap
		image = drawableToBitmap(getDrawable());
		// canvas.

		// init shader
		if (image != null) {

			canvasSize = canvas.getWidth();
			if (canvas.getHeight() < canvasSize)
				canvasSize = canvas.getHeight();
			Log.d("CircularImageView", "size: " + canvasSize);
			BitmapShader shader = new BitmapShader(Bitmap.createScaledBitmap(image, canvasSize, canvasSize, false),
					Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
			paint.setShader(shader);

			// Rect rect = new Rect(0,0,3,3);
			// int imageCenter = (canvasSize - (borderWidth * 2)) / 2;
			int imageCenter = canvasSize / 2;
			int widthFromCenterWBorder = (int) (((canvasSize - (borderWidth * 2)) / 2) + borderWidth - 4.0f);
			int widthFromCenterWOBorder = (int) (((canvasSize - (borderWidth * 2)) / 2) - 4.0f);

			Rect rectWBorder = new Rect(imageCenter - widthFromCenterWBorder, imageCenter - widthFromCenterWBorder,
					imageCenter + widthFromCenterWBorder, imageCenter + widthFromCenterWBorder);
			RectF rectFWBorder = new RectF(rectWBorder);

			Rect rectWOBorder = new Rect(imageCenter - widthFromCenterWOBorder, imageCenter - widthFromCenterWOBorder,
					imageCenter + widthFromCenterWOBorder, imageCenter + widthFromCenterWOBorder);
			RectF rectFWOBorder = new RectF(rectWOBorder);

			canvas.drawRoundRect(rectFWBorder, canvasSize / 16, canvasSize / 16, paintBorder);
			canvas.drawRoundRect(rectFWOBorder, canvasSize / 16, canvasSize / 16, paint);
			// Hex gold = new Hex("ffc200");

			// Log.e("RectangularImageView", "color match : " +
			// paintBorder.getColor() + "," +
			// Integer.toString(paintBorder.getColor(), 16) + ", ffc200 - " +
			// Color.parseColor("#ffc200"));
			if (paintBorder.getColor() == Color.parseColor("#ffc200")) {
				Log.e("RectangularImageView", "color matched");
				// TODO put garland on the image
				Bitmap deceased = BitmapFactory.decodeResource(context.getResources(), R.drawable.deceased);
				// Rect source = new Rect(width/2 - (heart.getWidth()/2),
				// height/5 - (heart.getHeight()/2), width/2 +
				// (heart.getWidth()/2), height/5 + (heart.getHeight()/2));
				Rect bitmapRect = new Rect(imageCenter - widthFromCenterWBorder, imageCenter + widthFromCenterWBorder
						- deceased.getHeight(), imageCenter + widthFromCenterWBorder, imageCenter
						+ widthFromCenterWBorder);
				canvas.drawBitmap(deceased, null, bitmapRect, new Paint());
			}

			/*
			 * // circleCenter is the x or y of the view's center // radius is
			 * the radius in pixels of the cirle to be drawn // paint contains
			 * the shader that will texture the shape int circleCenter =
			 * (canvasSize - (borderWidth * 2)) / 2;
			 * canvas.drawCircle(circleCenter + borderWidth, circleCenter +
			 * borderWidth, ((canvasSize - (borderWidth * 2)) / 2) + borderWidth
			 * - 4.0f, paintBorder); canvas.drawCircle(circleCenter +
			 * borderWidth, circleCenter + borderWidth, ((canvasSize -
			 * (borderWidth * 2)) / 2) - 4.0f, paint);
			 */
		}
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int width = measureWidth(widthMeasureSpec);
		int height = measureHeight(heightMeasureSpec);
		setMeasuredDimension(width, height);
	}

	private int measureWidth(int measureSpec) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpec);
		int specSize = MeasureSpec.getSize(measureSpec);

		if (specMode == MeasureSpec.EXACTLY) {
			// The parent has determined an exact size for the child.
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// The parent has not imposed any constraint on the child.
			result = canvasSize;
		}

		return result;
	}

	private int measureHeight(int measureSpecHeight) {
		int result = 0;
		int specMode = MeasureSpec.getMode(measureSpecHeight);
		int specSize = MeasureSpec.getSize(measureSpecHeight);

		if (specMode == MeasureSpec.EXACTLY) {
			// We were told how big to be
			result = specSize;
		} else if (specMode == MeasureSpec.AT_MOST) {
			// The child can be as large as it wants up to the specified size.
			result = specSize;
		} else {
			// Measure the text (beware: ascent is a negative number)
			result = canvasSize;
		}

		return (result + 2);
	}

	public Bitmap drawableToBitmap(Drawable drawable) {
		if (drawable == null) {
			return null;
		} else if (drawable instanceof BitmapDrawable) {
			return ((BitmapDrawable) drawable).getBitmap();
		}

		Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
				Bitmap.Config.ARGB_8888);
		Canvas canvas = new Canvas(bitmap);
		drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
		drawable.draw(canvas);

		return bitmap;
	}
}
