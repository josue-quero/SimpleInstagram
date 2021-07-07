package com.codepath.simpleinstagram;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;

public class ParseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Post.class);
        // Add your initialization code here
        // set applicationId, and server server based on the values in the back4app settings.
        // any network interceptors must be added with the Configuration Builder given this syntax
        //ParseObject.registerSubclass(Post.class);
        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("WZ1MdYJkSHWVXDoBtLvVhQEqD4NVhxzcDg7rGTZc") // should correspond to Application Id env variable
                .clientKey("PlomdxvFPa0sjazMXNOytvhDmScTvpa5G9EidayX")  // should correspond to Client key env variable
                .server("https://parseapi.back4app.com").build());

        // New test creation of object below
        /*
        ParseObject testObject = new ParseObject("TestObject");
        testObject.put("foo", "bar");
        testObject.saveInBackground();

         */
    }
}
