package com.example.parks.Util;

import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class Util {
    public static final String PARKS_URL = "https://developer.nps.gov/api/v1/parks?stateCode=AZ&api_key=h1yThQNwPfgiCYJEVVQtd1LamlQcP28xNWBYbau3";

    public static String getParksUrl(String stateCode) {
        return "https://developer.nps.gov/api/v1/parks?stateCode="+stateCode+"&api_key=h1yThQNwPfgiCYJEVVQtd1LamlQcP28xNWBYbau3";
    }

}
