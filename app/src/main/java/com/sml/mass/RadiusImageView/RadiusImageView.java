package com.sml.mass.RadiusImageView;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
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

import com.sml.mass.R;

/**
 * Created by Smeiling on 2018/1/17.
 */

public class RadiusImageView extends AppCompatImageView {

    private static final int COLOR_DRAWABLE_DIMEN = 2;

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
     * @default 0
     * if roundRadius value exists, single corner radius will be invalid
     */
    private int roundRadius;

    /**
     * Single corner radius
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

    private BitmapShader mBitmapShader;
    private Paint mBitmapPaint;
    private Bitmap mBitmap;

    public RadiusImageView(Context context) {
        this(context, null);
    }

    public RadiusImageView(Context context, AttributeSet attrs) {
        this(context, attrs, -1);
    }

    public RadiusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.RadiusImageView);
        shapeType = array.getInt(R.styleable.RadiusImageView_shape, SHAPE_RECTANGLE);
        roundRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_roundRadius, 0);
        if (roundRadius <= 0) {
            leftTopRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_leftTopRadius, 0);
            leftBottomRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_leftBottomRadius, 0);
            rightTopRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_rightTopRadius, 0);
            rightBottomRadius = array.getDimensionPixelSize(R.styleable.RadiusImageView_rightBottomRadius, 0);
        } else {
            leftTopRadius = roundRadius;
            leftBottomRadius = roundRadius;
            rightTopRadius = roundRadius;
            rightBottomRadius = roundRadius;
        }
        borderWidth = array.getDimensionPixelSize(R.styleable.RadiusImageView_borderWidth, 0);
        borderColor = array.getColor(R.styleable.RadiusImageView_borderColor, 0xFFEEEEEE);
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
    protected void onDraw(Canvas canvas) {
        if (shapeType == SHAPE_OVAL) {
            int radius = getWidth() > getHeight() ? getHeight() / 2 : getWidth() / 2;
            canvas.drawCircle(radius, radius, radius, mBitmapPaint);
        } else if (shapeType == SHAPE_ROUND_RECT) {
            if (roundRadius > 0) {
                canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()), roundRadius, roundRadius, mBitmapPaint);
            } else {
                setupPathValue();
                Path path = new Path();
                path.addRoundRect(new RectF(0, 0, getWidth(), getHeight()), rids, Path.Direction.CW);
                canvas.clipPath(path);
                super.onDraw(canvas);
            }
        } else {
            // Default shape is rectangle
            canvas.drawRect(new RectF(0, 0, getWidth(), getHeight()), mBitmapPaint);
        }
        drawBorder(shapeType, canvas);
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

        mBitmapShader = new BitmapShader(mBitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        if (mBitmapPaint == null) {
            mBitmapPaint = new Paint();
            mBitmapPaint.setAntiAlias(true);
        }

        mBitmapPaint.setShader(mBitmapShader);
        requestLayout();
        invalidate();
    }

    /**
     * Get bitmap
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
     * Setters
     */
    public void setShapeType(int shapeType) {
        this.shapeType = shapeType;
        invalidate();
    }

    public void setRoundRadius(int roundRadius) {
        if (roundRadius > 0) {
            this.roundRadius = roundRadius;
            leftTopRadius = leftBottomRadius = rightTopRadius = rightBottomRadius = 0;
            invalidate();
        }
    }

    public void setLeftTopRadius(int leftTopRadius) {
        if (leftTopRadius > 0) {
            this.roundRadius = 0;
            this.leftTopRadius = leftTopRadius;
            invalidate();
        }
    }

    public void setLeftBottomRadius(int leftBottomRadius) {
        if (leftBottomRadius > 0) {
            this.roundRadius = 0;
            this.leftBottomRadius = leftBottomRadius;
            invalidate();
        }
    }

    public void setRightTopRadius(int rightTopRadius) {
        if (rightTopRadius > 0) {
            this.roundRadius = 0;
            this.rightTopRadius = rightTopRadius;
            invalidate();
        }
    }

    public void setRightBottomRadius(int rightBottomRadius) {
        if (rightBottomRadius > 0) {
            this.roundRadius = 0;
            this.rightBottomRadius = rightBottomRadius;
            invalidate();
        }
    }

    public void setBorderWidth(int borderWidth) {
        if (borderWidth > 0) {
            this.borderWidth = borderWidth;
            invalidate();
        }
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
        invalidate();
    }
}
