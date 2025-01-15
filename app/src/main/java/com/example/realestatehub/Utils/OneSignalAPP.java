package com.example.realestatehub.Utils;

import android.app.Application;
import com.onesignal.OneSignal;

public class OneSignalAPP extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        OneSignal.initWithContext(this);
        OneSignal.setAppId(Config.ONESIGNAL_APP_ID);
    }
}
