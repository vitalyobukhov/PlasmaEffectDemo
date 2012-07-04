package com.vitalyobukhov.plasmademo;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;


public final class MainActivity extends Activity implements View.OnTouchListener {


    private MainView view;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Utility.ActivityUtility.removeTitle(this);
        Utility.ActivityUtility.goFullScreen(this);
        Utility.ActivityUtility.preventScreenDisabling(this);

        view = new MainView(this);
        setContentView(view);
        view.setOnTouchListener(this);

        view.start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
            view.end();
            finish();
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            view.toggleFpsVisible();
        }

        return true;
    }
}
