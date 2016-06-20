package me.sethallen.popularmovies.activity;

import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;

public class BaseActivity
        extends AppCompatActivity {

    public DisplayMetrics getDisplayMetrics()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics;
    }

    public float getDisplayWidthPixels()
    {
        return getDisplayMetrics().widthPixels;
    }

    public float getDisplayHeightPixels()
    {
        return getDisplayMetrics().heightPixels;
    }

    public float getDisplayDensityDPI()
    {
        return getDisplayMetrics().densityDpi;
    }

    public float getDisplayXDPI()
    {
        return getDisplayMetrics().xdpi;
    }

    public float getDisplayYDPI()
    {
        return getDisplayMetrics().ydpi;
    }

    public int getOrientation()
    {
        // orientation (either ORIENTATION_LANDSCAPE, ORIENTATION_PORTRAIT)
        return getResources().getConfiguration().orientation;
    }

    public double getScreenWidthInInches()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.widthPixels / metrics.xdpi;
    }

    public double getScreenHeightInInches()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return metrics.heightPixels / metrics.ydpi;
    }

    public double getScreenSizeInInches()
    {
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        double x = Math.pow(metrics.widthPixels / metrics.xdpi, 2);
        double y = Math.pow(metrics.heightPixels / metrics.ydpi, 2);
        return Math.sqrt(x + y);
    }
}
