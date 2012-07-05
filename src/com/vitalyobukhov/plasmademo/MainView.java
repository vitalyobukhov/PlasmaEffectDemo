package com.vitalyobukhov.plasmademo;

import android.content.Context;
import android.graphics.*;
import android.view.*;

/**
 * Main {@link View} of app.
 * Contains drawing logic.
 *
 * @author Vitaly Obukhov
 * @version 1.3
 */
public final class MainView extends SurfaceView {


    /* fps text related */
    private static final boolean FPS_VISIBLE_DEFAULT = false;

    private static final float FPS_TEXT_DEFAULT_SIZE = 100f;
    private static final int FPS_TEXT_SCREEN_CHAR_COUNT = 20;
    private static final int FPS_TEXT_STROKE_WIDTH = 4;
    private static final int FPS_TEXT_STROKE_COLOR = Color.BLACK;
    private static final int FPS_TEXT_FILL_COLOR = Color.WHITE;
    private static final Typeface FPS_TEXT_TYPEFACE = Typeface.create(Typeface.MONOSPACE, Typeface.BOLD);

    /* plasma effect size divider */
    private static final int PLASMA_SIZE_DIV = 40;


    private final PointF FPS_TEXT_OFFSET;

    /* state */
    private boolean isRunning;
    private final Object isRunningSync;

    /* fps text related */
    private boolean fpsVisible;
    private final Object fpsVisibleSync;

    private Paint fpsTextStrokePaint;
    private Paint fpsTextFillPaint;

    /* drawing invocation thread*/
    private UpdateThread updateThread;

    /* plasma effect data */
    private Plasma plasma;
    private Matrix plasmaMatrix;
    private Paint plasmaPaint;


    public MainView(Context context) {
        super(context);
        this.setWillNotDraw(false);

        /* init default values*/
        isRunning = false;
        isRunningSync = new Object();

        fpsVisibleSync = new Object();
        fpsVisible = FPS_VISIBLE_DEFAULT;

        Rect screenSize = Utility.ViewUtility.getScreenSize(this);

        String fpsText = Integer.toString(UpdateThread.FPS_MAX);

        fpsTextFillPaint = new Paint();
        fpsTextFillPaint.setColor(FPS_TEXT_FILL_COLOR);
        fpsTextFillPaint.setStyle(Paint.Style.FILL);
        fpsTextFillPaint.setTypeface(FPS_TEXT_TYPEFACE);
        fpsTextFillPaint.setTextSize(FPS_TEXT_DEFAULT_SIZE);
        Utility.PaintUtility.adjustFontSize(fpsTextFillPaint, fpsText, FPS_TEXT_SCREEN_CHAR_COUNT, screenSize, false);

        fpsTextStrokePaint = new Paint();
        fpsTextStrokePaint.setColor(FPS_TEXT_STROKE_COLOR);
        fpsTextStrokePaint.setStyle(Paint.Style.STROKE);
        fpsTextStrokePaint.setStrokeWidth(FPS_TEXT_STROKE_WIDTH);
        fpsTextStrokePaint.setTypeface(FPS_TEXT_TYPEFACE);
        fpsTextStrokePaint.setTextSize(fpsTextFillPaint.getTextSize());

        float fpsTextCharWidth = Utility.PaintUtility.getFontCharWidth(fpsTextFillPaint, fpsText);
        float fpsTextCharHeight = Utility.PaintUtility.getFontCharHeight(fpsTextFillPaint, fpsText);
        FPS_TEXT_OFFSET = new PointF(fpsTextCharWidth, fpsTextCharHeight * 2);

        /* configure drawing invocation thread */
        final MainView that = this;
        updateThread = new UpdateThread() {
            @Override
            public void update() {
                that.postInvalidate();
                try {
                    synchronized (that) {
                        that.wait();
                    }
                } catch (InterruptedException ignored)
                { }
            }
        };

        /* configure plasma effect */
        plasma = new Plasma(new Rect(0, 0, screenSize.width() / PLASMA_SIZE_DIV,
                screenSize.height() / PLASMA_SIZE_DIV));
        plasmaMatrix = new Matrix();
        plasmaMatrix.setScale(PLASMA_SIZE_DIV, PLASMA_SIZE_DIV);
        plasmaPaint = new Paint();
        plasmaPaint.setFilterBitmap(true);
    }


    @Override
    public void onDraw(Canvas canvas) {
        /* draw plasma effect */
        Bitmap plasmaBitmap = plasma.getBitmap(System.currentTimeMillis());
        canvas.drawBitmap(plasmaBitmap, plasmaMatrix, plasmaPaint);

        /* draw fps if required */
        if (getFpsVisible()) {
            String fpsText = Integer.toString(updateThread.getRealFps());
            canvas.drawText(fpsText, FPS_TEXT_OFFSET.x, FPS_TEXT_OFFSET.y, fpsTextStrokePaint);
            canvas.drawText(fpsText, FPS_TEXT_OFFSET.x, FPS_TEXT_OFFSET.y, fpsTextFillPaint);
        }

        synchronized (this) {
            this.notify();
        }
    }

    /**
     * Starts drawing cycle.
     */
    public void start() {
        synchronized (isRunningSync) {
            if (!isRunning) {
                isRunning = true;
                updateThread.start();
            }
        }
    }

    /**
     * Finishes drawing cycle.
     */
    public void end() {
        synchronized (isRunningSync) {
            if (isRunning) {
                updateThread.end();
                isRunning = false;
            }
        }
    }

    public boolean getFpsVisible() {
        synchronized (fpsVisibleSync) {
            return fpsVisible;
        }
    }

    public void setFpsVisible(boolean val) {
        synchronized (fpsVisibleSync) {
            fpsVisible = val;
        }
    }

    public void toggleFpsVisible() {
        synchronized (fpsVisibleSync) {
            fpsVisible = !fpsVisible;
        }
    }
}
