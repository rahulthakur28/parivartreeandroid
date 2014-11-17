package com.parivartree.zoom;

import java.util.Observable;
import java.util.Observer;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class LayoutZoomView extends FrameLayout implements Observer {
	
	/** Paint object used when drawing bitmap. */
	//private final Paint mPaint = new Paint(Paint..FILTER_BITMAP_FLAG);
    private final Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    /** Rectangle used (and re-used) for cropping source image. */
    private final Rect mRectSrc = new Rect();

    /** Rectangle used (and re-used) for specifying drawing area on canvas. */
    private final Rect mRectDst = new Rect();

    /** Object holding aspect quotient */
    private final AspectQuotient mAspectQuotient = new AspectQuotient();

    /** The bitmap that we're zooming in, and drawing on the screen. */
    //private Bitmap mBitmap;
    
    /** The view that that we're zooming in, and drawing on the screen. */
    private View mView;

    /** State of the zoom. */
    private ZoomState mState;
    
    private float prevZoomX = 1, prevZoomY = 1;
	
	public LayoutZoomView(Context context) {
		super(context);
		
		// TODO Auto-generated constructor stub
		setWillNotDraw (false);
	}
	
	public LayoutZoomView(Context context, AttributeSet attrs) {
		super(context, attrs);
		
		// TODO Auto-generated constructor stub
		setWillNotDraw (false);
	}
	
	/**
     * Set view
     * 
     * @param bitmap The bitmap to view and zoom into
     */
    public void setView(View view) {
    	mView = view;
    	
        mAspectQuotient.updateAspectQuotient(getWidth(), getHeight(), mView.getWidth(), mView
                .getHeight());
        mAspectQuotient.notifyObservers();
        
        invalidate();
    }
	
	/**
     * Set object holding the zoom state that should be used
     * 
     * @param state The zoom state
     */
    public void setZoomState(ZoomState state) {
        if (mState != null) {
            mState.deleteObserver(this);
        }

        mState = state;
        mState.addObserver(this);

        invalidate();
    }
    
    /**
     * Gets reference to object holding aspect quotient
     * 
     * @return Object holding aspect quotient
     */
    public AspectQuotient getAspectQuotient() {
        return mAspectQuotient;
    }
    
    @SuppressLint("DrawAllocation")
	@Override
    protected void onDraw(Canvas canvas) {
    	Log.d(VIEW_LOG_TAG, "OnDraw() called - mView:" + this.toString() + ", mState:" + mState.toString());
        if (this != null && mState != null) {
            final float aspectQuotient = mAspectQuotient.get();
            
            final int viewWidth = getWidth();
            final int viewHeight = getHeight();
            //final int viewWidth = 2000;
            //final int viewHeight = 2000;
            //View childView = this.getChildAt(0);
            //final int bitmapWidth = childView.getWidth();
            //final int bitmapHeight = childView.getHeight();
            final int bitmapWidth = this.getWidth();
            final int bitmapHeight = this.getHeight();
            
            final float panX = mState.getPanX();
            final float panY = mState.getPanY();
            final float zoomX = mState.getZoomX(aspectQuotient) * viewWidth / bitmapWidth;
            final float zoomY = mState.getZoomY(aspectQuotient) * viewHeight / bitmapHeight;
            
            // Setup source and destination rectangles
            /*
            mRectSrc.left = (int)(panX * bitmapWidth - viewWidth / (zoomX * 2));
            mRectSrc.top = (int)(panY * bitmapHeight - viewHeight / (zoomY * 2));
            mRectSrc.right = (int)(mRectSrc.left + viewWidth / zoomX);
            mRectSrc.bottom = (int)(mRectSrc.top + viewHeight / zoomY);
            mRectDst.left = getLeft();
            mRectDst.top = getTop();
            mRectDst.right = getRight();
            mRectDst.bottom = getBottom();
            */
            
            mRectSrc.left = (int)(panX * bitmapWidth - viewWidth / (zoomX * 2));
            mRectSrc.top = (int)(panY * bitmapHeight - viewHeight / (zoomY * 2));
            mRectSrc.right = (int)(mRectSrc.left + viewWidth / zoomX);
            mRectSrc.bottom = (int)(mRectSrc.top + viewHeight / zoomY);
            mRectDst.left = getLeft();
            mRectDst.top = getTop();
            mRectDst.right = (int) (getRight()*zoomX);
            mRectDst.bottom = (int) (getBottom()*zoomY);
            
            // Adjust source rectangle so that it fits within the source image.
            if (mRectSrc.left < 0) {
                mRectDst.left += -mRectSrc.left * zoomX;
                mRectSrc.left = 0;
            }
            if (mRectSrc.right > bitmapWidth) {
                mRectDst.right -= (mRectSrc.right - bitmapWidth) * zoomX;
                mRectSrc.right = bitmapWidth;
            }
            if (mRectSrc.top < 0) {
                mRectDst.top += -mRectSrc.top * zoomY;
                mRectSrc.top = 0;
            }
            if (mRectSrc.bottom > bitmapHeight) {
                mRectDst.bottom -= (mRectSrc.bottom - bitmapHeight) * zoomY;
                mRectSrc.bottom = bitmapHeight;
            }
            
            /*
            canvas.scale(zoomX, zoomY);
            
            Log.d(VIEW_LOG_TAG, "width:" + this.getWidth() + ", height:" + this.getHeight());
            Log.d(VIEW_LOG_TAG, "destination width:" + mRectDst.width() + ", height:" + mRectDst.height());
            
            Bitmap.Config conf = Bitmap.Config.ARGB_8888; // see other conf types
            Bitmap bmp = Bitmap.createBitmap((int)(mRectDst.width() * zoomX), (int)(mRectDst.height()), conf);
            canvas = new Canvas(bmp);
            canvas.drawBitmap(bmp, mRectSrc, mRectDst, mPaint);
            this.setLayoutParams(new LayoutParams((int)(mRectDst.width() * zoomX), (int)(mRectDst.height())));
            
            
            Log.d(VIEW_LOG_TAG, "canvas after width:" + canvas.getWidth() + ", height:" + canvas.getHeight());
            Log.d(VIEW_LOG_TAG, "view after width:" + this.getWidth() + ", height:" + this.getHeight());
            
            if(prevZoomX != zoomX || prevZoomY != zoomY) {
            	prevZoomX = zoomX;
            	prevZoomY = zoomY;
            	//((View)getParent()).invalidate();
            }
            */
            //awakenScrollBars(1);
        }
    }
	
	@Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mAspectQuotient.updateAspectQuotient(right - left, bottom - top, this.getWidth(),
                this.getHeight());
        mAspectQuotient.notifyObservers();
    }

    // implements Observer
    public void update(Observable observable, Object data) {
        invalidate();
    }

}
