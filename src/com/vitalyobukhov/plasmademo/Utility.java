package com.vitalyobukhov.plasmademo;

import android.app.Activity;
import android.content.Context;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.view.Display;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;


/**
 * Wrapper for package dependent utility classes.
 *
 * @author      Vitaly Obukhov
 * @version     1.0
 */
public final class Utility {

    /**
     * {@link Activity} utility methods wrapper.
     *
     * @author      Vitaly Obukhov
     * @version     1.0
     */
    public static class ActivityUtility {

        /**
         * Switches <code>activity</code> to full screen mode.
         *
         * @param activity source Activity
         */
        public static void goFullScreen(Activity activity) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        /**
         * Removes title from <code>activity</code>.
         *
         * @param activity source Activity
         */
        public static void removeTitle(Activity activity) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        /**
         * Forces <code>activity</code> to keep screen on.
         *
         * @param activity source Activity
         */
        public static void preventScreenDisabling(Activity activity) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    /**
     * {@link Paint} utility methods wrapper.
     *
     * @author      Vitaly Obukhov
     * @version     1.0
     */
    public static class PaintUtility {

        /**
         * Adjusts <code>paint</code> <code>textSize</code> to fit <code>area</code>
         * using specified character count and direction
         *
         * @param paint                 Paint for adjustment
         * @param text                  text for testing
         * @param charCount             count of text character to fit by <code>area</code> size
         * @param area                  area to fit
         * @param isVerticalDirection   <code>false</code> to use standard horizontal text direction
         */
        public static void adjustFontSize(Paint paint, String text,
                int charCount, Rect area, boolean isVerticalDirection) {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            float currentCharCount;
            if (!isVerticalDirection){
                currentCharCount = area.width() / (1.0f * bounds.width() / text.length());
            } else {
                currentCharCount = 1.0f * area.height() / bounds.height();
            }
            float newTextSize = paint.getTextSize() * currentCharCount / charCount;

            paint.setTextSize(newTextSize);
        }

        /**
         * Calculates single character width using <code>paint</code>
         *
         * @param paint     Paint for character
         * @param text      text for testing
         * @return          character width
         */
        public static float getFontCharWidth(Paint paint, String text) {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            return 1.0f * bounds.width() / text.length();
        }

        /**
         * Calculates single character height using <code>paint</code>
         *
         * @param paint     Paint for character
         * @param text      text for testing
         * @return          character height
         */
        public static float getFontCharHeight(Paint paint, String text) {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            return 1.0f * bounds.height();
        }
    }


    /**
     * {@link View} utility methods wrapper.
     *
     * @author Vitaly Obukhov
     * @version 1.0
     */
    public static class ViewUtility {

        /**
         * Gets device screen size.
         *
         * @param view  source View
         * @return      <code>Rect</code> which has width and height of screen
         */
        public static Rect getScreenSize(View view){
            WindowManager windowManager = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();

            return new Rect(0, 0, display.getWidth(), display.getHeight());
        }
    }
}
