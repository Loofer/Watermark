package org.loofer;

import android.app.Application;

/**
 * Created by LooferDeng on 2017/12/20.
 */

public class MarkApplication extends Application {


    private static MarkApplication sInstance;

    public MarkApplication() {
        sInstance = this;
    }

    public static MarkApplication getInstance() {
        return sInstance;
    }



}
