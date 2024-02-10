package com.example.realestatehub.HomeFragments.ProfileFragmentLayouts;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;

import java.util.Locale;

public class LanguageUtils {

    private static final String LANGUAGE_PREF_KEY = "LanguagePref";

    public static void setLocale(Context context, String language) {
        Locale locale = new Locale(language);
        Locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        context.getResources().updateConfiguration(config, context.getResources().getDisplayMetrics());

        // Save the selected language preference
        SharedPreferences.Editor editor = context.getSharedPreferences(LANGUAGE_PREF_KEY, Context.MODE_PRIVATE).edit();
        editor.putString("language", language);
        editor.apply();
    }

    public static void loadLocale(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(LANGUAGE_PREF_KEY, Context.MODE_PRIVATE);
        String language = prefs.getString("language", "");
        if (!language.isEmpty()) {
            setLocale(context, language);
        }
    }

    public static void recreateActivity(Activity activity, Class<?> cls) {
        Intent intent = new Intent(activity, cls);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
        activity.startActivity(intent);
        activity.finish();
    }
}

