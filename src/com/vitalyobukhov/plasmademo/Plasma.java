package com.vitalyobukhov.plasmademo;

import android.graphics.Bitmap;
import android.graphics.Rect;
import java.util.Random;


/**
 * Plasma effect logic.
 *
 * @author Vitaly Obukhov
 * @version 1.3
 */
public final class Plasma {


    /* color constants */
    private final int COLOR_VAL_MAX = 255;
    private final int COLOR_VAL_HALF = 127;
    private final int COLOR_VAL_MIN = 0;

    /* speed constants */
    private final double SPEED_MIN_DIV = 40.0;
    private final double SPEED_MAX_DIV = 20.0;
    private final double TIME_DIV = 100.0;

    /* dimensions */
    private int width;
    private int height;
    private double diag;

    /* drawing objects */
    private int[] pixels;
    private Bitmap bitmap;

    /* initial coordinates */
    private double rcx1, rcy1, gcx1, gcy1, bcx1, bcy1;
    private double rcx2, rcy2, gcx2, gcy2, bcx2, bcy2;

    /* speeds */
    private double rsx1, rsy1, gsx1, gsy1, bsx1, bsy1;
    private double rsx2, rsy2, gsx2, gsy2, bsx2, bsy2;


    /**
     * Creates instance width desired <code>size</code>
     * @param size  plasma effect dimensions
     */
    public Plasma(Rect size) {
        /* init dimensions */
        width = size.width();
        height = size.height();
        diag = Math.sqrt(width * width + height * height);

        /* init drawing data */
        pixels = new int[width * height];
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        Random rnd = new Random();

        /* attractors coordinates */
        rcx1 = rnd.nextDouble() * width;
        rcy1 = rnd.nextDouble() * height;
        gcx1 = rnd.nextDouble() * width;
        gcy1 = rnd.nextDouble() * height;
        bcx1 = rnd.nextDouble() * width;
        bcy1 = rnd.nextDouble() * height;

        /* repulsors coordinates*/
        rcx2 = rnd.nextDouble() * width;
        rcy2 = rnd.nextDouble() * height;
        gcx2 = rnd.nextDouble() * width;
        gcy2 = rnd.nextDouble() * height;
        bcx2 = rnd.nextDouble() * width;
        bcy2 = rnd.nextDouble() * height;

        /* speed constants */
        double sxb = 1.0 * width / SPEED_MIN_DIV;
        double sxa = 1.0 * width / SPEED_MAX_DIV;
        double syb = 1.0 * height / SPEED_MIN_DIV;
        double sya = 1.0 * height / SPEED_MAX_DIV;

        /* attractors speeds */
        rsx1 = syb + rnd.nextDouble() * sxa;
        rsy1 = sxb + rnd.nextDouble() * sya;
        gsx1 = sxb + rnd.nextDouble() * sxa;
        gsy1 = sxb + rnd.nextDouble() * sya;
        bsx1 = sxb + rnd.nextDouble() * sxa;
        bsy1 = sxb + rnd.nextDouble() * sya;

        /* repulsors speeds */
        rsx2 = sxb + rnd.nextDouble() * sxa;
        rsy2 = sxb + rnd.nextDouble() * sya;
        gsx2 = sxb + rnd.nextDouble() * sxa;
        gsy2 = sxb + rnd.nextDouble() * sya;
        bsx2 = sxb + rnd.nextDouble() * sxa;
        bsy2 = sxb + rnd.nextDouble() * sya;
    }

    /**
     * Calculates plasma effect image.
     *
     * @param time  desired time
     * @return      plasma effect {@link Bitmap}
     */
    public final Bitmap getBitmap(long time) {
        /* scale time */
        double t = time / TIME_DIV;

        /* new coordinates and color parts*/
        double rcx1n, rcy1n, gcx1n, gcy1n, bcx1n, bcy1n;
        double rcx2n, rcy2n, gcx2n, gcy2n, bcx2n, bcy2n;
        double rn, gn, bn;

        /* cycle each pixel */
        for(int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                /* calculate new coordinates */
                rcx1n = getNewCord(rcx1, rsx1, width, t);
                rcy1n = getNewCord(rcy1, rsy1, height, t);
                gcx1n = getNewCord(gcx1, gsx1, width, t);
                gcy1n = getNewCord(gcy1, gsy1, height, t);
                bcx1n = getNewCord(bcx1, bsx1, width, t);
                bcy1n = getNewCord(bcy1, bsy1, height, t);

                rcx2n = getNewCord(rcx2, rsx2, width, t);
                rcy2n = getNewCord(rcy2, rsy2, height, t);
                gcx2n = getNewCord(gcx2, gsx2, width, t);
                gcy2n = getNewCord(gcy2, gsy2, height, t);
                bcx2n = getNewCord(bcx2, bsx2, width, t);
                bcy2n = getNewCord(bcy2, bsy2, height, t);

                /* calculate new color parts using current coordinates and related attractors / repulsors weight */
                rn = COLOR_VAL_HALF + COLOR_VAL_MAX * (getColorWeight(rcx1n, rcy1n, x, y) - getColorWeight(rcx2n, rcy2n, x, y));
                gn = COLOR_VAL_HALF + COLOR_VAL_MAX * (getColorWeight(gcx1n, gcy1n, x, y) - getColorWeight(gcx2n, gcy2n, x, y));
                bn = COLOR_VAL_HALF + COLOR_VAL_MAX * (getColorWeight(bcx1n, bcy1n, x, y) - getColorWeight(bcx2n, bcy2n, x, y));

                /* fix color parts */
                rn = rn > COLOR_VAL_MAX ? COLOR_VAL_MAX : (rn < COLOR_VAL_MIN ? COLOR_VAL_MIN : rn);
                gn = gn > COLOR_VAL_MAX ? COLOR_VAL_MAX : (gn < COLOR_VAL_MIN ? COLOR_VAL_MIN : gn);
                bn = bn > COLOR_VAL_MAX ? COLOR_VAL_MAX : (bn < COLOR_VAL_MIN ? COLOR_VAL_MIN : bn);

                /* set color */
                pixels[y * width + x] = (255 << 24) | ((int)rn << 16) | ((int)gn << 8) | (int)bn;
            }
        }

        /* compose bitmap */
        bitmap.setPixels(pixels, 0, width, 0, 0, width, height);
        return bitmap;
    }

    /**
     * Calculates actual object coordinate.
     *
     * @param cord      initial object coordinate
     * @param speed     object speed
     * @param size      area size
     * @param time      desired time
     * @return          new object coordinate
     */
    private double getNewCord(double cord, double speed, double size, double time) {
        /* new coordinate remainder */
        double rem = (speed * time) % (2.0 * size);
        double val;

        /* object in area scope */
        if (cord + rem >= 0.0 && cord + rem < size){
            val = cord + rem;
        /* object has left area scope */
        } else if (cord + rem > size) {
            if (cord + rem > 2.0 * size) {
                val = cord + rem - 2.0 * size;
            } else {
                val = size - (cord + rem - size);
            }
        } else {
            if ( cord + rem < -1.0 * size) {
                val = size - (-1.0 * (cord + rem) - size);
            } else {
                val = -1.0 * (cord + rem);
            }
        }

        return val;
    }

    /**
     * Calculates color weight metric.
     *
     * @param cx    object x coordinate
     * @param cy    object y coordinate
     * @param tx    target x coordinate
     * @param ty    target y coordinate
     * @return      color weight metric from 0 to 1
     */
    private double getColorWeight(double cx, double cy, double tx, double ty) {
        double dx = cx - tx;
        double dy = cy - ty;

        return 1.0 - Math.sqrt(dx * dx + dy * dy) / diag;
    }
}
