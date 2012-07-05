package com.vitalyobukhov.plasmademo;

import android.os.SystemClock;

/**
 * Thread which able to perform provided <code>update</code> action
 * using provided fps and calculate real fps
 *
 * @author  Vitaly Obukhov
 * @version 1.1
 */
public class UpdateThread extends Thread {


    public static final int FPS_MAX = 1000;
    public static final int FPS_DEFAULT = 100;
    public static final int REAL_FPS_UPDATE_DELAY_DEFAULT = 1000;
    private static final int MILLISECONDS_IN_SECOND = 1000;


    /* thread state */
    private boolean isRunning;
    private final Object isRunningSync;

    /* desired fps */
    private int fps;
    private final Object fpsSync;
    private int delay;

    /* actual fps */
    private int realFps;
    private final Object realFpsSync;

    /* real fps update latency */
    private int realFpsUpdateDelay;
    private final Object realFpsUpdateDelaySync;
    private long lastRealFpsUpdateTime;

    /**
     * Creates instance with default fields values.
     */
    public UpdateThread() {
        isRunningSync = new Object();
        fpsSync = new Object();
        realFpsSync = new Object();
        realFpsUpdateDelaySync = new Object();

        isRunning = false;

        fps = FPS_DEFAULT;
        delay = MILLISECONDS_IN_SECOND / fps;
        realFps = 0;
        realFpsUpdateDelay = REAL_FPS_UPDATE_DELAY_DEFAULT;
        lastRealFpsUpdateTime = 0;
    }


    /**
     * Performs overridable client action with related delay.
     */
    public void update() { }

    /**
     * Performs main cyclic logic.
     */
    @Override
    public final void run() {
        synchronized (isRunningSync) {
            if (!isRunning) {
                isRunning = true;
            } else {
                return;
            }
        }

        setRealFps(0);

        while (true) {
            synchronized (isRunningSync) {
                if (!isRunning) {
                    break;
                }
            }

            /* calculate update time span */
            long startTime = SystemClock.uptimeMillis();
            update();
            long spentTime = SystemClock.uptimeMillis() - startTime;
            long diffTime = delay - spentTime;

            /* delay */
            if (diffTime > 0) {
                try {
                    Thread.sleep(diffTime);
                }
                catch(InterruptedException ignored){
                }
            }

            /* recalculate real fps if required */
            long now = SystemClock.uptimeMillis();
            if (lastRealFpsUpdateTime + realFpsUpdateDelay <= now) {
                int newRealFps = spentTime != 0 ? (int)Math.floor((double) MILLISECONDS_IN_SECOND / spentTime) : FPS_MAX;
                setRealFps(newRealFps);
                lastRealFpsUpdateTime = now;
            }
        }

        setRealFps(0);
    }

    /**
     * Finishes current instance execution.
     */
    public final void end() {
        synchronized (isRunningSync) {
            if (isRunning) {
                isRunning = false;
            }
        }
    }

    public final void setFps(int val) {
        synchronized (fpsSync) {
            if (val > 0 && val <= FPS_MAX) {
                fps = val;
                delay = 1000 / fps;
            }
        }
    }

    public final int getFps() {
        synchronized (fpsSync) {
            return fps;
        }
    }

    public final int getRealFps() {
        synchronized (realFpsSync) {
            return realFps;
        }
    }

    public final int getRealFpsUpdateDelay() {
        synchronized (realFpsUpdateDelaySync) {
            return  realFpsUpdateDelay;
        }
    }

    public final void setRealFpsUpdateDelay(int val) {
        synchronized (realFpsUpdateDelaySync) {
            if (val >= 0) {
                realFpsUpdateDelay = val;
            }
        }
    }

    private void setRealFps(int val) {
        synchronized (realFpsSync) {
            realFps = val;
        }
    }
}
