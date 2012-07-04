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


public final class Utility {


    public static class ActivityUtility {
        public static void goFullScreen(Activity activity) {
            activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                    WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }

        public static void removeTitle(Activity activity) {
            activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        }

        public static void preventScreenDisabling(Activity activity) {
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        }
    }


    public static class PaintUtility {


        public static void adjustFontSize(Paint paint, String text, int charCount, Rect area, boolean isVerticalDirection) {
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

        public static float getFontCharWidth(Paint paint, String text) {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            return 1.0f * bounds.width() / text.length();
        }

        public static float getFontCharHeight(Paint paint, String text) {
            Rect bounds = new Rect();
            paint.getTextBounds(text, 0, text.length(), bounds);

            return 1.0f * bounds.height();
        }
    }


    public static class ViewUtility {


        public static Rect getScreenSize(View view){
            WindowManager windowManager = (WindowManager)view.getContext().getSystemService(Context.WINDOW_SERVICE);
            Display display = windowManager.getDefaultDisplay();

            Point sd = new Point();
            display.getSize(sd);

            return new Rect(0, 0, sd.x, sd.y);
        }
    }
}
