package com.parivartree.zoom;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;

public class VerticalHorizontalScrollView extends ScrollView {

    private float mX, mY;

    private HScrollView mHScrollView;

    public VerticalHorizontalScrollView(Context pContext, AttributeSet pAttrs, int pDefStyle) {
        super(pContext, pAttrs, pDefStyle);
        init(pContext);
    }

    public VerticalHorizontalScrollView(Context pContext, AttributeSet pAttrs) {
        super(pContext, pAttrs);
        init(pContext);
    }

    public VerticalHorizontalScrollView(Context pContext) {
        super(pContext);
        init(pContext);
    }

    private void init(Context pContext) {
        setLayoutParams(new ViewGroup.LayoutParams(480, 800));
        mHScrollView = new HScrollView(pContext);
        addView(mHScrollView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float curX, curY;

        switch (event.getAction()) {

            case MotionEvent.ACTION_DOWN:
                mX = event.getX();
                mY = event.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                curX = event.getX();
                curY = event.getY();
                scrollBy((int)(mX - curX), (int)(mY - curY));
                mHScrollView.scrollBy((int)(mX - curX), (int)(mY - curY));
                mX = curX;
                mY = curY;
                break;
            case MotionEvent.ACTION_UP:
                curX = event.getX();
                curY = event.getY();
                scrollBy((int)(mX - curX), (int)(mY - curY));
                mHScrollView.scrollBy((int)(mX - curX), (int)(mY - curY));
                break;
        }
        return true;
    }

    @Override
    public void addView(View pChild) {
        mHScrollView.addView(pChild);
    }

    private class HScrollView extends HorizontalScrollView {
        public HScrollView(Context pContext) {
            super(pContext);
        }

        @Override
        public boolean onTouchEvent(MotionEvent ev) {
            return false;
        }
    }
}
