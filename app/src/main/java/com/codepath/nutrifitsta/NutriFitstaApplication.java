package com.codepath.nutrifitsta;

import android.app.Application;

import com.parse.Parse;

public class NutriFitstaApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        Parse.initialize(new Parse.Configuration.Builder(this)
                .applicationId("vr6OTu5oWNL5fDgGpx4pXqJQnaGpIH63ubaE5fHC")
                .clientKey("399wfLTUN7NnUsX34J0shBnJPfE1btwvPmELtILj")
                .server("https://parseapi.back4app.com")
                .build()
        );
    }
}
