/*
 * BORQS Software Solutions Pvt Ltd. CONFIDENTIAL
 * Copyright (c) 2017 All rights reserved.
 *
 * The source code contained or described herein and all documents
 * related to the source code ("Material") are owned by BORQS Software
 * Solutions Pvt Ltd. No part of the Material may be used,copied,
 * reproduced, modified, published, uploaded,posted, transmitted,
 * distributed, or disclosed in any way without BORQS Software
 * Solutions Pvt Ltd. prior written permission.
 *
 * No license under any patent, copyright, trade secret or other
 * intellectual property right is granted to or conferred upon you
 * by disclosure or delivery of the Materials, either expressly, by
 * implication, inducement, estoppel or otherwise. Any license
 * under such intellectual property rights must be express and
 * approved by BORQS Software Solutions Pvt Ltd. in writing.
 *
 */
/*
 * Modifications by Yoni Samlan; based on RealViewSwitcher, whose license is:
 *
 * Copyright (C) 2010 Marc Reichelt
 *
 * Work derived from Workspace.java of the Launcher application
 *  see http://android.git.kernel.org/?p=platform/packages/apps/Launcher.git
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.borqs.ime.adapters;

import android.content.Context;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Scroller;

public final class HorizontalPager extends ViewGroup {

    private static final int ANIMATION_SCREEN_SET_DURATION_MILLIS = 500;
    // What part of the screen the user must swipe to indicate a page change
    private static final int FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE = 6;
    private static final int IF_INVALID_SCREEN = -1;
    /*
     * Velocity of a swipe (in density-independent pixels per second) to force a swipe to the
     * next/previous screen. Adjusted into mDensityAdjustedSnapVelocity on init.
     */
    private static final int VELOCITY_PER_SECOND = 600;
    // Argument to getVelocity for units to give pixels
    // per second (1 = pixels per millisecond).
    private static final int UNIT_PIXELS_PER_SECOND = 1000;
    private static final int STATE_REST = 0;
    private static final int STATE_HORIZONTAL_SCROLLING = 1;
    private static final int STATE_VERTICAL_SCROLLING = -1;
    private int mCurrentPage;
    private int mDensityAdjustedVelocity;
    private boolean mInitialLayout = true;
    private float mLastMotionX;
    private float mLastMotionY;
    private OnScreenSwitchListener mOnScreenSwitchListener;
    private int mMaximumVelocity;
    private int mNextScreen = IF_INVALID_SCREEN;
    private Scroller mScroller;
    private int mTouchSlop;
    private int mTouchState = STATE_REST;
    private VelocityTracker mVelocityTracker;
    private int mLastCurrentLayoutWidth = -1;
    public HorizontalPager(final Context context) {
        super(context);
        init();
    }
    public HorizontalPager(final Context context, final AttributeSet attrs) {
        super(context, attrs);
        init();
    }
     //Sets up the scroller and touch/fling sensitivity parameters for the pager.
    private void init() {
        mScroller = new Scroller(getContext());
        // Calculate the density-dependent snap velocity in pixels
        DisplayMetrics displayMetrics = new DisplayMetrics();
        ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).
                getDefaultDisplay().getMetrics(displayMetrics);
        mDensityAdjustedVelocity =
                (int) (displayMetrics.density * VELOCITY_PER_SECOND);
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mTouchSlop = configuration.getScaledTouchSlop();
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
    }
    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        final int width = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        // The children are given the same width and height as the workspace
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            getChildAt(i).measure(widthMeasureSpec, heightMeasureSpec);
        }
        if (mInitialLayout) {
            scrollTo(mCurrentPage * width, 0);
            mInitialLayout = false;
        }
        else if (width != mLastCurrentLayoutWidth) {
            Display display =
                    ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE))
                    .getDefaultDisplay();
            int displayWidth = display.getWidth();
            mNextScreen = Math.max(0, Math.min(getCurrentScreen(), getChildCount() - 1));
            final int newX = mNextScreen * displayWidth;
            final int delta = newX - getScrollX();
            mScroller.startScroll(getScrollX(), 0, delta, 0, 0);
        }
        mLastCurrentLayoutWidth   = width;
    }
    @Override
    protected void onLayout(final boolean changed, final int l, final int t, final int r,
            final int b) {
        int childLeft = 0;
        final int count = getChildCount();
        for (int i = 0; i < count; i++) {
            final View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                final int childWidth = child.getMeasuredWidth();
                child.layout(childLeft, 0, childLeft + childWidth, child.getMeasuredHeight());
                childLeft += childWidth;
            }
        }
    }
    @Override
    public boolean onInterceptTouchEvent(final MotionEvent ev) {
        final int action = ev.getAction();
        boolean intercept = false;
        switch (action) {
        case MotionEvent.ACTION_MOVE:
            if (mTouchState == STATE_HORIZONTAL_SCROLLING) {
                intercept = true;
            } else if (mTouchState == STATE_VERTICAL_SCROLLING) {
                // Let children handle the events for the duration of the scroll event.
                intercept = false;
            } else { // We haven't picked up a scroll event yet; check for one
                final float x = ev.getX();
                final int xDiff = (int) Math.abs(x - mLastMotionX);
                boolean xMoved = xDiff > mTouchSlop;

                if (xMoved) {
                    // Scroll if the user moved far enough along the X axis
                    mTouchState = STATE_HORIZONTAL_SCROLLING;
                    mLastMotionX = x;
                }
                final float y = ev.getY();
                final int yDiff = (int) Math.abs(y - mLastMotionY);
                boolean yMoved = yDiff > mTouchSlop;
                if (yMoved) {
                    mTouchState = STATE_VERTICAL_SCROLLING;
                }
            }
            break;
        case MotionEvent.ACTION_CANCEL:
        case MotionEvent.ACTION_UP:
            // Release the drag.
            mTouchState = STATE_REST;
            break;
        case MotionEvent.ACTION_DOWN:
            /*
             * No motion yet, but register the coordinates so we can check for intercept at the
             * next MOVE event.
             */
            mLastMotionY = ev.getY();
            mLastMotionX = ev.getX();
            break;
        default:
            break;
        }
        return intercept;
    }
    @Override
    public boolean onTouchEvent(final MotionEvent ev) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);
        final int action = ev.getAction();
        final float x = ev.getX();
        switch (action) {
        case MotionEvent.ACTION_DOWN:
            if (!mScroller.isFinished()) {
                mScroller.abortAnimation();
            }
           // Remember where the motion event started
            mLastMotionX = x;
            if (mScroller.isFinished()) {
                mTouchState = STATE_REST;
            } else {
                mTouchState = STATE_HORIZONTAL_SCROLLING;
            }
            break;
        case MotionEvent.ACTION_MOVE:
            final int xDiff = (int) Math.abs(x - mLastMotionX);
            boolean xMoved = xDiff > mTouchSlop;
            if (xMoved) {
                // Scroll if the user moved far enough along the X axis
                mTouchState = STATE_HORIZONTAL_SCROLLING;
            }
            if (mTouchState == STATE_HORIZONTAL_SCROLLING) {
                // Scroll to follow the motion event
                final int deltaX = (int) (mLastMotionX - x);
                mLastMotionX = x;
                final int scrollX = getScrollX();
                if (deltaX < 0) {
                    if (scrollX > 0) {
                        scrollBy(Math.max(-scrollX, deltaX), 0);
                    }
                } else if (deltaX > 0) {
                    final int availableToScroll =
                            getChildAt(getChildCount() - 1).getRight() - scrollX - getWidth();
                    if (availableToScroll > 0) {
                        scrollBy(Math.min(availableToScroll, deltaX), 0);
                    }
                }
            }
            break;
        case MotionEvent.ACTION_UP:
            if (mTouchState == STATE_HORIZONTAL_SCROLLING) {
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(UNIT_PIXELS_PER_SECOND,
                        mMaximumVelocity);
                int velocityX = (int) velocityTracker.getXVelocity();
                if (velocityX > mDensityAdjustedVelocity && mCurrentPage > 0) {
                    // Fling hard enough to move left
                    snapToScreen(mCurrentPage - 1);
                } else if (velocityX < -mDensityAdjustedVelocity
                        && mCurrentPage < getChildCount() - 1) {
                    // Fling hard enough to move right
                    snapToScreen(mCurrentPage + 1);
                } else {
                    snapToDestination();
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
            }
            mTouchState = STATE_REST;
            break;
        case MotionEvent.ACTION_CANCEL:
            mTouchState = STATE_REST;
            break;
        default:
            break;
        }
        return true;
    }
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        } else if (mNextScreen != IF_INVALID_SCREEN) {
            mCurrentPage = Math.max(0, Math.min(mNextScreen, getChildCount() - 1));
            if (mOnScreenSwitchListener != null) {
                mOnScreenSwitchListener.onScreenSwitched(mCurrentPage);
                }
          mNextScreen = IF_INVALID_SCREEN;
        }
    }
    public int getCurrentScreen() {
        return mCurrentPage;
    }
     // Sets the current screen.
  public void setCurrentScreen(final int currentScreen, final boolean animate) {
        mCurrentPage = Math.max(0, Math.min(currentScreen, getChildCount() - 1));
        if (animate) {
            snapToScreen(currentScreen, ANIMATION_SCREEN_SET_DURATION_MILLIS);
        } else {
            scrollTo(mCurrentPage * getWidth(), 0);
        }
        invalidate();
    }
    public void setOnScreenSwitchListener(final OnScreenSwitchListener onScreenSwitchListener) {
        mOnScreenSwitchListener = onScreenSwitchListener;
    }
    private void snapToDestination() {
        final int screenWidth = getWidth();
        int scrollX = getScrollX();
        int whichScreen = mCurrentPage;
        int deltaX = scrollX - (screenWidth * mCurrentPage);
        if ((deltaX < 0) && mCurrentPage != 0
                && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < -deltaX)) {
            whichScreen--;
        } else if ((deltaX > 0) && (mCurrentPage + 1 != getChildCount())
                && ((screenWidth / FRACTION_OF_SCREEN_WIDTH_FOR_SWIPE) < deltaX)) {
            whichScreen++;
        }
        snapToScreen(whichScreen);
    }
    private void snapToScreen(final int whichScreen) {
        snapToScreen(whichScreen, -1);
    }
    private void snapToScreen(final int whichScreen, final int duration) {
        mNextScreen = Math.max(0, Math.min(whichScreen, getChildCount() - 1));
        final int newX = mNextScreen * getWidth();
        final int delta = newX - getScrollX();

        if (duration < 0) {
            mScroller.startScroll(getScrollX(), 0, delta, 0, (int) (Math.abs(delta)
                    / (float) getWidth() * ANIMATION_SCREEN_SET_DURATION_MILLIS));
        } else {
            mScroller.startScroll(getScrollX(), 0, delta, 0, duration);
        }
        invalidate();
    }
    public static interface OnScreenSwitchListener {

        void onScreenSwitched(int screen);
    }
}

