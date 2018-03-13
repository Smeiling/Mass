package com.sml.mass.components.basic;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;

import com.sml.mass.R;

public class ZoomableImageView extends AppCompatImageView {

    private int pos = 0;
    private int count = 0;

    private float maxScale = 3f;
    private float minScale = 1f;

    private enum State {
        INIT, DRAG, ZOOM
    }

    private State state;

    private Matrix matrix;
    private float[] finalTransformation = new float[9];
    private PointF last = new PointF();
    public float currentScale = 1f;

    private int viewWidth;
    private int viewHeight;
    private float afterScaleDrawableWidth;
    private float afterScaleDrawableHeight;

    private boolean zoomable = true;

    private ScaleGestureDetector scaleDetector;

    private GestureDetector doubleTapDetector;

    public ZoomableImageView(Context context) {
        this(context, null);
    }

    public ZoomableImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public ZoomableImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.ZoomableImageView);
        zoomable = array.getBoolean(R.styleable.ZoomableImageView_ziv_zoomable, true);
        array.recycle();
        initView(context);
    }

    public void setPos(int pos, int count) {
        this.pos = pos;
        this.count = count;
    }

    public boolean isZoomable() {
        return zoomable;
    }

    public void setZoomable(boolean zoomable) {
        this.zoomable = zoomable;
        initView(getContext());
        invalidate();
        currentScale = 1f;
        resetImage();
    }

    public float getCurrentScale() {
        return currentScale;
    }

    public void setCurrentScale(float currentScale) {
        this.currentScale = currentScale;
    }

    /**
     * Set up the class. Method called by constructors.
     *
     * @param context
     */
    private void initView(Context context) {
        super.setClickable(false);
        matrix = new Matrix();
        state = State.INIT;
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        doubleTapDetector = new GestureDetector(context, new GestureListener());
        if (isZoomable()) {
            setScaleType(ScaleType.FIT_CENTER);
        } else {
            setScaleType(getScaleType());
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        viewWidth = MeasureSpec.getSize(widthMeasureSpec);
        viewHeight = MeasureSpec.getSize(heightMeasureSpec);

        // Set up drawable at first load
        if (hasDrawable()) {
            currentScale = 1f;
            resetImage();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (isZoomable()) {
            scaleDetector.onTouchEvent(event);
        }
        doubleTapDetector.onTouchEvent(event);

        PointF current = new PointF(event.getX(), event.getY());

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                last.set(current);
                state = State.DRAG;
                break;

            case MotionEvent.ACTION_MOVE:
                if (state == State.DRAG) {
                    drag(current);
                    last.set(current);
                }
                break;

            case MotionEvent.ACTION_UP:
                state = State.INIT;
                break;

            case MotionEvent.ACTION_POINTER_UP:
                state = State.INIT;
                break;
            default:
                break;
        }

        setImageMatrix(matrix);
        invalidate();
        return true;
    }

    public void resetImage() {

        // Scale Image
        float scale = getScaleForDrawable();
        matrix.setScale(scale, scale);

        // Center Image
        float marginY = 0;
        float marginX = 0;
        if (getDrawable() != null) {
            marginY = ((float) viewHeight - (scale * getDrawable()
                    .getIntrinsicHeight())) / 2;
            marginX = ((float) viewWidth - (scale * getDrawable()
                    .getIntrinsicWidth())) / 2;
        }
        matrix.postTranslate(marginX, marginY);

        afterScaleDrawableWidth = (float) viewWidth - 2 * marginX;
        afterScaleDrawableHeight = (float) viewHeight - 2 * marginY;
        if (isZoomable()) {
            setScaleType(ScaleType.MATRIX);
        } else {
            setScaleType(getScaleType());
        }
        setImageMatrix(matrix);
    }

    /**
     * Getter and setter for max/min scale. Default are min=1 and max=3
     */

    public float getMaxScale() {
        return maxScale;
    }

    public void setMaxScale(float maxScale) {
        this.maxScale = maxScale;
    }

    public float getMinScale() {
        return minScale;
    }

    public void setMinScale(float minScale) {
        this.minScale = minScale;
    }

    /**
     * Drag method
     *
     * @param current Current point to drag to.
     */
    private void drag(PointF current) {
        float deltaX = getMoveDraggingDelta(current.x - last.x, viewWidth,
                afterScaleDrawableWidth * currentScale);
        float deltaY = getMoveDraggingDelta(current.y - last.y, viewHeight,
                afterScaleDrawableHeight * currentScale);
        Log.i("zoom_img", "drag deltaX=" + deltaX + ";deltaY=" + deltaY);
        matrix.postTranslate(deltaX, deltaY);
        limitDrag();
    }

    /**
     * Scale method for zooming
     *
     * @param focusX      X of center of scale
     * @param focusY      Y of center of scale
     * @param scaleFactor scale factor to zoom in/out
     */
    private void scale(float focusX, float focusY, float scaleFactor) {
        float lastScale = currentScale;
        float newScale = lastScale * scaleFactor;

        // Calculate next scale with resetting to max or min if required
        if (newScale > maxScale) {
            currentScale = maxScale;
            scaleFactor = maxScale / lastScale;
        } else if (newScale < minScale) {
            currentScale = minScale;
            scaleFactor = minScale / lastScale;
        } else {
            currentScale = newScale;
        }

        // Do scale
        if (requireCentering()) {
            matrix.postScale(scaleFactor, scaleFactor, (float) viewWidth / 2,
                    (float) viewHeight / 2);
        } else
            matrix.postScale(scaleFactor, scaleFactor, focusX, focusY);

        limitDrag();
    }

    /**
     * This method permits to keep drag and zoom inside the drawable. It makes
     * sure the drag is staying in bound.
     */
    private void limitDrag() {
        matrix.getValues(finalTransformation);
        float finalXTransformation = finalTransformation[Matrix.MTRANS_X];
        float finalYTransformation = finalTransformation[Matrix.MTRANS_Y];

        float deltaX = getScaleDraggingDelta(finalXTransformation, viewWidth,
                afterScaleDrawableWidth * currentScale);
        float deltaY = getScaleDraggingDelta(finalYTransformation, viewHeight,
                afterScaleDrawableHeight * currentScale);
        Log.i("zoom_img", "limitDrag deltaX=" + deltaX + ";deltaY=" + deltaY);
        if (isZoom()) {
            if (deltaX != 0) {
                if (deltaX < 0) {// deltaX<0 表示已经到了左边界，再滑动deltaX值为负数
                    if (pos > 0) {
                        //HackyViewPager.setLocked(false);
                    } else {
                        //HackyViewPager.setLocked(true);
                    }
                } else if (deltaX > 0) {// deltaX>0 表示已经到了右边界，再滑动deltaX值为正数

                    if (pos < count - 1) {
                        //HackyViewPager.setLocked(false);
                    } else {
                        //HackyViewPager.setLocked(true);
                    }
                }
            } else if (deltaY != 0) {
                // deltaY<0 表示已经到了上边界，再滑动deltaY值为负数
                // deltaY>0 表示已经到了下边界，再滑动deltaY值为正数
            } else {
                //HackyViewPager.setLocked(true);
            }
        } else {
            //HackyViewPager.setLocked(false);
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    private float getScaleDraggingDelta(float delta, float viewSize,
                                        float contentSize) {
        float minTrans = 0;
        float maxTrans = 0;

        if (contentSize <= viewSize) {
            maxTrans = viewSize - contentSize;
        } else {
            minTrans = viewSize - contentSize;
        }

        if (delta < minTrans)
            return minTrans - delta;
        else if (delta > maxTrans)
            return maxTrans - delta;
        else
            return 0;
    }

    // Check if dragging is still possible if so return delta otherwise return 0
    // (do nothing)
    private float getMoveDraggingDelta(float delta, float viewSize,
                                       float contentSize) {
        if (contentSize <= viewSize) {
            return 0;
        }
        return delta;
    }

    private float getScaleForDrawable() {
        if (getDrawable() == null) return 0;

        float scaleX = (float) viewWidth
                / (float) getDrawable().getIntrinsicWidth();
        float scaleY = (float) viewHeight
                / (float) getDrawable().getIntrinsicHeight();
        return Math.min(scaleX, scaleY);
    }

    private boolean hasDrawable() {
        return getDrawable() != null && getDrawable().getIntrinsicWidth() != 0
                && getDrawable().getIntrinsicHeight() != 0;
    }

    private boolean requireCentering() {
        return afterScaleDrawableWidth * currentScale <= (float) viewWidth
                || afterScaleDrawableHeight * currentScale <= (float) viewHeight;
    }

    public boolean isZoom() {
        return currentScale != 1f;
    }

    /**
     * Listener for detecting scale.
     *
     * @author jmartinez
     */
    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        @Override
        public boolean onScaleBegin(ScaleGestureDetector detector) {
            state = State.ZOOM;
            return true;
        }

        @Override
        public boolean onScale(ScaleGestureDetector detector) {
            scale(detector.getFocusX(), detector.getFocusY(),
                    detector.getScaleFactor());
            return true;
        }
    }

    /**
     * Listener for double tap detection
     *
     * @author jmartinez
     */
    private class GestureListener extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            // 若注册单击点击处理事件则处理并返回
            if (mSingleClickListener != null) {
                return mSingleClickListener.onSingleClick(e);
            }
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onDoubleTap(MotionEvent e) {
            if (isZoomable()) {
                if (isZoom()) {
                    resetImage();
                    currentScale = 1f;
                    state = State.INIT;
                } else {
                    scale(e.getX(), e.getY(), maxScale);
                }
                return true;
            } else {
                return false;
            }
        }

        @Override
        public void onLongPress(MotionEvent e) {
            if (mImageLongClickListener != null) {
                mImageLongClickListener.onLongClick(e);
            }
            super.onLongPress(e);
        }

    }

    public interface OnSingleClickListener {
        boolean onSingleClick(MotionEvent e);
    }

    public interface OnImageLongClickListener {
        boolean onLongClick(MotionEvent e);
    }

    private OnSingleClickListener mSingleClickListener;
    private OnImageLongClickListener mImageLongClickListener;

    /**
     * 注册单击监听器处理事件
     *
     * @param mSingleClickListener 单击监听器
     */
    public void setOnSingleClickListener(OnSingleClickListener mSingleClickListener) {
        this.mSingleClickListener = mSingleClickListener;
    }

    public void setOnImageLongClickListener(OnImageLongClickListener mImageLongClickListener) {
        this.mImageLongClickListener = mImageLongClickListener;
    }

    public void suoxiao() {
        resetImage();
        currentScale = 1f;
        state = State.INIT;
    }
}