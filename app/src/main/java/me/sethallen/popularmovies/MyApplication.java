package me.sethallen.popularmovies;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;

public class MyApplication extends Application {
    @Override
    public void onCreate()
    {
        super.onCreate();

        Fresco.initialize(this);

        // Initialize the singletons so their instances
        // are bound to the application process.
        initSingletons();
    }

    protected void initSingletons()
    {
        // Initialize the instance of MySingleton
        //MySingleton.initInstance();
    }

    public void customAppMethod()
    {
        // Custom application method
    }
}
