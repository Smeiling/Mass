package com.sml.mass.RadiusImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;

import com.sml.mass.R;

/**
 * @Author: Smeiling
 * @Date: 2018-01-22
 * @Description: Upgraded ImageView support corner radius customization.
 */
public class RadiusImageView extends AppCompatImageView {

    private static final int COLOR_DRAWABLE_DIMEN = 2;

    /**
     * Supported shapes
     */
    public static final int SHAPE_RECTANGLE = 1;
    public static final int SHAPE_OVAL = 2;
    public static final int SHAPE_ROUND_RECT = 3;

    /**
     * ImageView shape
     *
     * @default Rectangle
     */
    private int shapeType = SHAPE_RECTANGLE;

    /**
     * Uniform radius
     *
     * @default 0px
     * If roundRadius value exists, single corner radius will be invalid
     */
    private int roundRadius;

    /**
     * Single corner radius
     *
     * @default 0px
     */
    private int leftTopRadius;
    private int leftBottomRadius;
    private int rightTopRadius;
    private int rightBottomRadius;

    /**
     * Path value use to draw single corner
     */
    private float[] rids = new float[8];

    /**
     * Border attributes
     */
    private int borderWidth = 0;
    private int borderColor = 0xFFEEEEEE;

    /**
     * Draw image
     */
    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint;
    private Bitmap mBitmap;
    private Matrix mMatrix;
    private RectF rectF;
    private Path path;

    /**
     * ImageView size
     */
    private int mWidth;
    private int mHeight;

    private boolean mNeedResetShader = false;

    public RadiusImageView(Context context) {
        this(context, null);
    }

    public RadiusImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RadiusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        // ImageView set default ScaleType as FIT_CENTER, but FIT_CENTER is not valid in RadiusImageView
        if (getScaleType() == ImageView.ScaleType.FIT_CENTER) {
            // Set default ScaleType as CENTER_CROP
            setScaleType(ImageView.ScaleType.CENTER_CROP);
        }
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RadiusImageView);

        shapeType = array.getInt(R.styleable.RadiusImageView_shape, SHAPE_RECTANGLE);
        roundRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_roundRadius, 0);
        if (roundRadius <= 0) {
            leftTopRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_leftTopRadius, 0);
            leftBottomRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_leftBottomRadius, 0);
            rightTopRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_rightTopRadius, 0);
            rightBottomRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_rightBottomRadius, 0);
        } else {
            leftTopRadius = leftBottomRadius = rightTopRadius = rightBottomRadius = roundRadius;
        }
        borderWidth = array.getDimensionPixelSize(R.styleable.RadiusImageView_borderWidth, 0);
        borderColor = array.getColor(R.styleable.RadiusImageView_borderColor, 0xFFEEEEEE);

        array.recycle();

        mMatrix = new Matrix();
        path = new Path();
        rectF = new RectF();
    }

    @Override
    public void setImageBitmap(Bitmap bm) {
        super.setImageBitmap(bm);
        setupBitmap();
    }

    @Override
    public void setImageDrawable(@Nullable Drawable drawable) {
        super.setImageDrawable(drawable);
        setupBitmap();
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        setupBitmap();
    }

    @Override
    public void setImageURI(@Nullable Uri uri) {
        super.setImageURI(uri);
        setupBitmap();
    }

    @Override
    public void setScaleType(ImageView.ScaleType scaleType) {
        if (scaleType == ImageView.ScaleType.CENTER_INSIDE || scaleType == ImageView.ScaleType.FIT_START
                || scaleType == ImageView.ScaleType.FIT_CENTER || scaleType == ImageView.ScaleType.FIT_END) {
            throw new IllegalArgumentException(String.format("ScaleType %s is not supported in RadiusImageView", scaleType));
        }
        super.setScaleType(scaleType);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (shapeType == SHAPE_OVAL) {
            int size = Math.min(width, height);
            setMeasuredDimension(size, size);
        } else {
            int widthMode = View.MeasureSpec.getMode(widthMeasureSpec);
            int heightMode = View.MeasureSpec.getMode(heightMeasureSpec);
            if (mBitmap == null) {
                return;
            }
            if (widthMode == View.MeasureSpec.AT_MOST || widthMode == View.MeasureSpec.UNSPECIFIED
                    || heightMode == View.MeasureSpec.AT_MOST || heightMode == View.MeasureSpec.UNSPECIFIED) {
                float bmWidth = mBitmap.getWidth();
                float bmHeight = mBitmap.getHeight();
                if (getScaleX() == getScaleY()) {
                    setMeasuredDimension(width, (int) (bmHeight * getScaleX()));
                } else {
                    setMeasuredDimension((int) (bmWidth * getScaleY()), height);
                }
            }
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint();
            mBitmapPaint.setColor(Color.TRANSPARENT);
            mBitmapPaint.setAntiAlias(true);
        }
        if (shapeType == SHAPE_OVAL) {
            updateBitmapShader();
            int radius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
            canvas.drawCircle(radius, radius, radius, mBitmapPaint);
        } else if (shapeType == SHAPE_ROUND_RECT) {
            if (roundRadius > 0) {
                updateBitmapShader();
                rectF.set(0, 0, getWidth(), getHeight());
                canvas.drawRoundRect(rectF, roundRadius, roundRadius, mBitmapPaint);
            } else {
                // Single corner customization
                setupPathValue();
                rectF.set(0, 0, getWidth(), getHeight());
                path.addRoundRect(rectF, rids, Path.Direction.CW);
                canvas.clipPath(path);
                super.onDraw(canvas);
            }
        } else {
            // Default shape is rectangle
            updateBitmapShader();
            rectF.set(0, 0, getWidth(), getHeight());
            canvas.drawRect(rectF, mBitmapPaint);
        }
        drawBorder(shapeType, canvas);
    }


    /**
     * Resize bitmap and update bitmap shader
     */
    private void updateBitmapShader() {
        if (mWidth != getWidth() || mHeight != getHeight() || mNeedResetShader) {
            mWidth = getWidth();
            mHeight = getHeight();

            mMatrix.reset();
            mNeedResetShader = false;
            if (mBitmapShader == null || mBitmap == null) {
                return;
            }

            // Resize bitmap with scaleType option
            final float bmWidth = mBitmap.getWidth();
            final float bmHeight = mBitmap.getHeight();
            final float scaleX = mWidth / bmWidth;
            final float scaleY = mHeight / bmHeight;
            if (getScaleType() == ImageView.ScaleType.MATRIX) {
            } else if (getScaleType() == ImageView.ScaleType.CENTER) {
                mMatrix.postTranslate(-(mBitmap.getWidth() - mWidth) / 2, -(mBitmap.getHeight() - mHeight) / 2);
            } else if (getScaleType() == ImageView.ScaleType.CENTER_INSIDE) {
                final float scale = Math.min(scaleX, scaleY);
                mMatrix.setScale(scale, scale);
                mMatrix.postTranslate(-(scale * bmWidth - mWidth) / 2, -(scale * bmHeight - mHeight) / 2);
            } else if (getScaleType() == ImageView.ScaleType.CENTER_CROP) {
                final float scale = Math.max(scaleX, scaleY);
                mMatrix.setScale(scale, scale);
                mMatrix.postTranslate(-(scale * bmWidth - mWidth) / 2, -(scale * bmHeight - mHeight) / 2);
            } else if (getScaleType() == ImageView.ScaleType.FIT_XY) {
                mMatrix.setScale(scaleX, scaleY);
            } else {
                final float scale = Math.min(scaleX, scaleY);
                mMatrix.setScale(scale, scale);
                if (getScaleType() == ImageView.ScaleType.FIT_START) {
                    mMatrix.postTranslate(0, 0);
                } else if (getScaleType() == ImageView.ScaleType.FIT_CENTER) {
                    if (mWidth > mHeight) {
                        mMatrix.postTranslate(-(scale * bmWidth - mWidth) / 2, 0);
                    } else {
                        mMatrix.postTranslate(0, -(scale * bmHeight - mHeight) / 2);
                    }
                } else if (getScaleType() == ImageView.ScaleType.FIT_END) {
                    if (mWidth > mHeight) {
                        mMatrix.postTranslate(-(scale * bmWidth - mWidth), 0);
                    } else {
                        mMatrix.postTranslate(0, -(scale * bmHeight - mHeight));
                    }
                }
            }
            mBitmapShader.setLocalMatrix(mMatrix);
            mBitmapPaint.setShader(mBitmapShader);
        }
    }

    /**
     * Add border on ImageView
     *
     * @param shapeType
     * @param canvas
     */
    private void drawBorder(int shapeType, Canvas canvas) {
        float halfBorderWidth = borderWidth / 2;
        if (borderWidth <= 0) {
            return;
        }
        Paint borderPaint = new Paint();
        borderPaint.setAntiAlias(true);
        borderPaint.setStrokeWidth(borderWidth);
        borderPaint.setColor(borderColor);
        borderPaint.setStyle(Paint.Style.STROKE);
        switch (shapeType) {
            case SHAPE_RECTANGLE:
                canvas.drawRect(new RectF(halfBorderWidth, halfBorderWidth, getWidth() - halfBorderWidth, getHeight() - halfBorderWidth), borderPaint);
                break;
            case SHAPE_OVAL:
                int radius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
                canvas.drawCircle(radius, radius, radius - halfBorderWidth, borderPaint);
                break;
            case SHAPE_ROUND_RECT:
                if (roundRadius > 0) {
                    canvas.drawRoundRect(new RectF(halfBorderWidth, halfBorderWidth, getWidth() - halfBorderWidth, getHeight() - halfBorderWidth), roundRadius - halfBorderWidth, roundRadius - halfBorderWidth, borderPaint);
                }
                break;
            default:
                break;
        }
    }

    /**
     * Create bitmap shader to draw bitmap
     */
    private void setupBitmap() {
        mBitmap = getBitmap();

        if (mBitmap == null) {
            mBitmapShader = null;
            invalidate();
            return;
        }

        mNeedResetShader = true;

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        mBitmapPaint = new Paint();
        mBitmapPaint.setAntiAlias(true);

        mBitmapPaint.setShader(mBitmapShader);
        requestLayout();
        invalidate();
    }

    /**
     * Get bitmap from parent ImageView
     *
     * @return target bitmap
     */
    private Bitmap getBitmap() {
        Drawable drawable = getDrawable();
        if (drawable == null) {
            return null;
        }

        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        try {
            Bitmap bitmap;
            if (drawable instanceof ColorDrawable) {
                bitmap = Bitmap.createBitmap(COLOR_DRAWABLE_DIMEN, COLOR_DRAWABLE_DIMEN, Bitmap.Config.ARGB_8888);
            } else {
                bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            }

            Canvas canvas = new Canvas(bitmap);
            drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
            drawable.draw(canvas);
            return bitmap;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Setup single corner round rectangle path value
     */
    private void setupPathValue() {
        rids[0] = rids[1] = leftTopRadius;
        rids[2] = rids[3] = rightTopRadius;
        rids[4] = rids[5] = rightBottomRadius;
        rids[6] = rids[7] = leftBottomRadius;
    }

    /**
     * Set shape of ImageView
     *
     * @Default rectangle
     */
    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
        invalidate();
    }

    /**
     * Set round rectangle radius
     *
     * @param roundRadius
     * @Default 0
     */
    public void setRoundRadius(int roundRadius) {
        if (roundRadius > 0) {
            this.roundRadius = roundRadius;
            leftTopRadius = leftBottomRadius = rightTopRadius = rightBottomRadius = roundRadius;
            invalidate();
        }
    }

    /**
     * Set round rectangle left-top radius
     *
     * @param leftTopRadius
     */
    public void setLeftTopRadius(int leftTopRadius) {
        if (leftTopRadius > 0) {
            this.roundRadius = 0; // Invalidate roundRadius
            this.leftTopRadius = leftTopRadius;
            invalidate();
        }
    }

    /**
     * Set round rectangle left-bottom radius
     *
     * @param leftBottomRadius
     */
    public void setLeftBottomRadius(int leftBottomRadius) {
        if (leftBottomRadius > 0) {
            this.roundRadius = 0; // Invalidate roundRadius
            this.leftBottomRadius = leftBottomRadius;
            invalidate();
        }
    }

    /**
     * Set round rectangle right-top radius
     *
     * @param rightTopRadius
     */
    public void setRightTopRadius(int rightTopRadius) {
        if (rightTopRadius > 0) {
            this.roundRadius = 0; // Invalidate roundRadius
            this.rightTopRadius = rightTopRadius;
            invalidate();
        }
    }

    /**
     * Set round rectangle right-bottom radius
     *
     * @param rightBottomRadius
     */
    public void setRightBottomRadius(int rightBottomRadius) {
        if (rightBottomRadius > 0) {
            this.roundRadius = 0; // Invalidate roundRadius
            this.rightBottomRadius = rightBottomRadius;
            invalidate();
        }
    }

    /**
     * Set border width
     * Border is not supported when single corner radius is set
     *
     * @param borderWidth
     */
    public void setBorderWidth(int borderWidth) {
        if (borderWidth > 0) {
            this.borderWidth = borderWidth;
            invalidate();
        }
    }

    /**
     * Set border color
     *
     * @param borderColor
     */
    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }

    /**
     * 清空边框
     */
    public void clearBorder() {
        setBorderColor(Color.TRANSPARENT);
    }
}
