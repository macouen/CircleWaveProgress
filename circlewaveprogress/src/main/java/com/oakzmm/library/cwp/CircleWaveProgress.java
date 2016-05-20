package com.oakzmm.library.cwp;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;

import java.lang.ref.WeakReference;

/**
 * Created by oak_zmm
 * on 2016/3/14
 * Description: circle progress with wave
 */
public class CircleWaveProgress extends View {
    public static final int LARGE = 1;
    public static final int MIDDLE = 2;
    public static final int LITTLE = 3;

    private static final int DEFAULT_WAVE_FRONT_ALPHA = 200;
    private static final int DEFAULT_WAVE_BEHIND_ALPHA = 100;
    private static final int DEFAULT_WAVE_FRONT_COLOR = Color.parseColor("#325fad");
    private static final int DEFAULT_WAVE_BEHIND_COLOR = Color.parseColor("#7663a9");
    private static final int DEFAULT_BORDER_COLOR = Color.parseColor("#325fad");
    private static final int DEFAULT_BG_COLOR = Color.WHITE;
    private static final int DEFAULT_TEXT_COLOR = Color.BLACK;
    private final int WAVE_HEIGHT_LARGE = 16;
    private final int WAVE_HEIGHT_MIDDLE = 8;
    private final int WAVE_HEIGHT_LITTLE = 5;
    private final float WAVE_LENGTH_MULTIPLE_LARGE = 1.5f;
    private final float WAVE_LENGTH_MULTIPLE_MIDDLE = 1f;
    private final float WAVE_LENGTH_MULTIPLE_LITTLE = 0.5f;
    private final float WAVE_HZ_FAST = 0.18f;
    private final float WAVE_HZ_NORMAL = 0.12f;
    private final float WAVE_HZ_SLOW = 0.05f;
    private final float DEFAULT_TEXT_SIZE;
    private final float DEFAULT_BORDER_WITH;
    private final int minSize;
    private final float X_SPACE = 10;
    private final double PI2 = 2 * Math.PI;
    private int max = 100;
    private Paint textPaint;
    private RectF rectF = new RectF();
    private Paint paint = new Paint();
    private Paint circlePaint = new Paint();
    private float textSize;
    private int textColor;
    private int progress = 0;
    private int circleBgColor;
    private String circleText = "";
    private Path mFrontWavePath = new Path();
    private Path mBehindWavePath = new Path();
    private Paint mFrontWavePaint = new Paint();
    private Paint mBehindWavePaint = new Paint();
    private int mFrontWaveColor;
    private int mBehindWaveColor;
    private int mBorderColor;
    private float mBorderWith;
    private float mWaveMultiple;
    private float mWaveLength;
    private int mWaveHeight;
    private float mMaxRight;
    private float mWaveHz;
    // wave animation
    private float mFrontOffset;
    private float mBehindOffset = 0.0f;
    private RefreshProgressRunnable mRefreshProgressRunnable;
    private int left, right, bottom;
    // ω
    private double omega;
    private Path mPath;
    private float mYHeight;
    private Matrix mMatrix;


    public CircleWaveProgress(Context context) {
        this(context, null);
    }

    public CircleWaveProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleWaveProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        DEFAULT_TEXT_SIZE = DensityUtil.sp2px(getResources(), 18);
        DEFAULT_BORDER_WITH = DensityUtil.dip2px(context, 2);
        minSize = DensityUtil.dip2px(context, 100);
        final TypedArray attributes = context.getTheme().obtainStyledAttributes(attrs, R.styleable.CircleWaveProgress, defStyleAttr, 0);
        initByAttributes(attributes);
        attributes.recycle();

        initPainters();
    }

    protected void initByAttributes(TypedArray attributes) {
        circleBgColor = attributes.getColor(R.styleable.CircleWaveProgress_circle_background_color, DEFAULT_BG_COLOR);
        textColor = attributes.getColor(R.styleable.CircleWaveProgress_circle_text_color, DEFAULT_TEXT_COLOR);
        textSize = attributes.getDimension(R.styleable.CircleWaveProgress_circle_text_size, DEFAULT_TEXT_SIZE);

        int waveHeight = attributes.getInt(R.styleable.CircleWaveProgress_wave_height, MIDDLE);
        mWaveHeight = getWaveHeight(waveHeight);
        mFrontOffset = mWaveHeight * 0.4f;
        int waveMultiple = attributes.getInt(R.styleable.CircleWaveProgress_wave_length, MIDDLE);
        mWaveMultiple = getWaveMultiple(waveMultiple);
        int waveHz = attributes.getInt(R.styleable.CircleWaveProgress_wave_hz, MIDDLE);
        mWaveHz = getWaveHz(waveHz);

        mFrontWaveColor = attributes.getColor(R.styleable.CircleWaveProgress_wave_font_color, DEFAULT_WAVE_FRONT_COLOR);
        mBehindWaveColor = attributes.getColor(R.styleable.CircleWaveProgress_wave_behind_color, DEFAULT_WAVE_BEHIND_COLOR);
        mBorderColor = attributes.getColor(R.styleable.CircleWaveProgress_circle_border_color, DEFAULT_BORDER_COLOR);
        mBorderWith = attributes.getDimension(R.styleable.CircleWaveProgress_circle_border_with, DEFAULT_BORDER_WITH);
        setMax(attributes.getInt(R.styleable.CircleWaveProgress_circle_max, 100));
        setProgress(attributes.getInt(R.styleable.CircleWaveProgress_circle_progress, 0));

        if (attributes.getString(R.styleable.CircleWaveProgress_circle_text) != null) {
            setCircleText(attributes.getString(R.styleable.CircleWaveProgress_circle_text));
        }

    }

    protected void initPainters() {
        textPaint = new TextPaint();
        textPaint.setColor(textColor);
        textPaint.setTextSize(textSize);
        textPaint.setAntiAlias(true);

        circlePaint.setColor(mBorderColor);
        circlePaint.setStrokeWidth(mBorderWith);
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.STROKE);
        paint.setAntiAlias(true);

        mFrontWavePaint.setColor(mFrontWaveColor);
        mFrontWavePaint.setAlpha(DEFAULT_WAVE_FRONT_ALPHA);
        mFrontWavePaint.setStyle(Paint.Style.FILL);
        mFrontWavePaint.setAntiAlias(true);

        mBehindWavePaint.setColor(mBehindWaveColor);
        mBehindWavePaint.setAlpha(DEFAULT_WAVE_BEHIND_ALPHA);
        mBehindWavePaint.setStyle(Paint.Style.FILL);
        mBehindWavePaint.setAntiAlias(true);

        mPath = new Path();
        mMatrix = new Matrix();
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        rectF.set(0, 0, MeasureSpec.getSize(widthMeasureSpec), MeasureSpec.getSize(heightMeasureSpec));
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
        if (mWaveLength == 0) {
            startWave();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.save();
        // cal y with progress
        mYHeight = getProgress() / (float) getMax() * getHeight();
        //circle path
        mPath.reset();
        mPath.addCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f, Path.Direction.CW);
        canvas.clipPath(mPath);
        //draw bg color
        canvas.drawColor(circleBgColor);
        //draw wave by progress
        mMatrix.reset();
        if (mMatrix.postTranslate(0, getHeight() - mYHeight)) {
            mBehindWavePath.transform(mMatrix);
            mFrontWavePath.transform(mMatrix);
            canvas.drawPath(mBehindWavePath, mBehindWavePaint);
            canvas.drawPath(mFrontWavePath, mFrontWavePaint);
        }

        canvas.restore();
        //draw circle stroke
        canvas.drawCircle(getWidth() / 2f, getHeight() / 2f, (getWidth() - getBorderWith()) / 2f, circlePaint);
        //fro draw circle method 2
//        final Path circlePath = new Path();
//        circlePath.addCircle(getWidth() / 2f, getHeight() / 2f, getWidth() / 2f - 2f, Path.Direction.CCW);
//        canvas.drawPath(circlePath,circlePaint);

        //draw text
        String text = getCircleText();
        float textHeight = textPaint.descent() + textPaint.ascent();
        if (!TextUtils.isEmpty(text)) {
            canvas.drawText(text, (getWidth() - textPaint.measureText(text)) / 2.0f, (getWidth() - textHeight) / 2.0f, textPaint);
        }

    }

    /**
     * calculate wave track
     */
    private void calculatePath() {
        mFrontWavePath.reset();
        mBehindWavePath.reset();

        getWaveOffset();
        float y;
        mFrontWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mFrontOffset) + mWaveHeight);
            mFrontWavePath.lineTo(x, y);
        }
        mFrontWavePath.lineTo(right, bottom);

        mBehindWavePath.moveTo(left, bottom);
        for (float x = 0; x <= mMaxRight; x += X_SPACE) {
            y = (float) (mWaveHeight * Math.sin(omega * x + mBehindOffset) + mWaveHeight);
            mBehindWavePath.lineTo(x, y);
        }
        mBehindWavePath.lineTo(right, bottom);

    }

    /*
        @Override
        protected void onWindowVisibilityChanged(int visibility) {
            super.onWindowVisibilityChanged(visibility);
    //        if (View.GONE == visibility || View.INVISIBLE == visibility || getProgress() == 0) {
    //            removeCallbacks(mRefreshProgressRunnable);
    //        } else {
    //            removeCallbacks(mRefreshProgressRunnable);
    //            mRefreshProgressRunnable = new RefreshProgressRunnable(this);
    //            post(mRefreshProgressRunnable);
    //        }
        }
        @Override
        public void onWindowFocusChanged(boolean hasWindowFocus) {
            super.onWindowFocusChanged(hasWindowFocus);
    //        if (hasWindowFocus) {
    //            if (mWaveLength == 0) {
    //                startWave();
    //            }
    //        }
        }
    */
    public void setWaveRun(boolean isRun) {
        if (isRun && getProgress() > 0) {
            removeCallbacks(mRefreshProgressRunnable);
            mRefreshProgressRunnable = new RefreshProgressRunnable(this);
            post(mRefreshProgressRunnable);
        } else {
            removeCallbacks(mRefreshProgressRunnable);
        }
    }

    private void startWave() {
        if (getWidth() != 0) {
            int width = getWidth();
            mWaveLength = width * mWaveMultiple;
            left = (int) rectF.left;
            right = (int) rectF.right;
            bottom = (int) rectF.bottom;
            mMaxRight = right + X_SPACE;
            omega = PI2 / mWaveLength;
        }
    }

    private void getWaveOffset() {
        if (mBehindOffset > Float.MAX_VALUE - 100) {
            mBehindOffset = 0;
        } else {
            mBehindOffset += mWaveHz;
        }

        if (mFrontOffset > Float.MAX_VALUE - 100) {
            mFrontOffset = 0;
        } else {
            mFrontOffset += mWaveHz;
        }
    }

    private float getWaveMultiple(int size) {
        switch (size) {
            case LARGE:
                return WAVE_LENGTH_MULTIPLE_LARGE;
            case MIDDLE:
                return WAVE_LENGTH_MULTIPLE_MIDDLE;
            case LITTLE:
                return WAVE_LENGTH_MULTIPLE_LITTLE;
        }
        return 0;
    }

    private int getWaveHeight(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HEIGHT_LARGE;
            case MIDDLE:
                return WAVE_HEIGHT_MIDDLE;
            case LITTLE:
                return WAVE_HEIGHT_LITTLE;
        }
        return 0;
    }

    private float getWaveHz(int size) {
        switch (size) {
            case LARGE:
                return WAVE_HZ_FAST;
            case MIDDLE:
                return WAVE_HZ_NORMAL;
            case LITTLE:
                return WAVE_HZ_SLOW;
        }
        return 0;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
        if (this.progress > getMax()) {
            this.progress %= getMax();
        }
        setCircleText(progress + "%");
        invalidate();
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        if (max > 0) {
            this.max = max;
            invalidate();
        }
    }

    public float getTextSize() {
        return textSize;
    }

    public void setTextSize(float textSize) {
        textSize = DensityUtil.sp2px(getResources(), textSize);
        this.textSize = textSize;
        textPaint.setTextSize(textSize);
        this.invalidate();
    }

    public int getTextColor() {
        return textColor;
    }

    public void setTextColor(int textColor) {
        this.textColor = textColor;
        textPaint.setColor(textColor);
        this.invalidate();
    }

    public int getCircleBgColor() {
        return circleBgColor;
    }

    public void setCircleBgColor(int circleBgColor) {
        this.circleBgColor = circleBgColor;
        this.invalidate();
    }

    public String getCircleText() {
        return circleText;
    }

    public void setCircleText(String text) {
        this.circleText = text;
        this.invalidate();
    }

    public int getFrontWaveColor() {
        return mFrontWaveColor;
    }

    public void setFrontWaveColor(int frontWaveColor) {
        this.mFrontWaveColor = frontWaveColor;
        mFrontWavePaint.setColor(frontWaveColor);
        mFrontWavePaint.setAlpha(DEFAULT_WAVE_FRONT_ALPHA);
        this.invalidate();
    }

    public int getBehindWaveColor() {
        return mBehindWaveColor;
    }

    public void setBehindWaveColor(int behindWaveColor) {
        this.mBehindWaveColor = behindWaveColor;
        mBehindWavePaint.setColor(behindWaveColor);
        mBehindWavePaint.setAlpha(DEFAULT_WAVE_BEHIND_ALPHA);
        this.invalidate();
    }

    public int getBorderColor() {
        return mBorderColor;
    }

    public void setBorderColor(int borderColor) {
        this.mBorderColor = borderColor;
        circlePaint.setColor(borderColor);
        this.invalidate();
    }

    public float getBorderWith() {
        return mBorderWith;
    }

    public void setBorderWith(float borderWith) {
        borderWith = DensityUtil.dip2px(getContext(), borderWith);
        this.mBorderWith = borderWith;
        circlePaint.setStrokeWidth(borderWith);
        this.invalidate();
    }

    @Override
    protected int getSuggestedMinimumHeight() {
        return minSize;
    }

    @Override
    protected int getSuggestedMinimumWidth() {
        return minSize;
    }

    public float getProgressPercentage() {
        return getProgress() / (float) getMax();
    }

    private static class RefreshProgressRunnable implements Runnable {
        private final CircleWaveProgress mWaveProgress;
        private WeakReference<CircleWaveProgress> mWeakReference;

        public RefreshProgressRunnable(CircleWaveProgress button) {
            mWeakReference = new WeakReference<CircleWaveProgress>(button);
            mWaveProgress = mWeakReference.get();
        }

        @Override
        public void run() {
            synchronized (mWaveProgress) {
                long start = System.currentTimeMillis();
                mWaveProgress.calculatePath();
                mWaveProgress.invalidate();
                long gap = 16 - System.currentTimeMillis() - start;
                mWaveProgress.postDelayed(this, gap < 0 ? 0 : gap);
//                mWaveProgress.postDelayed(this, 20);
            }
        }
    }
}
