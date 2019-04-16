package com.mkorp.newsboard.model;

import android.content.Context;
import android.telephony.TelephonyManager;

public class CountryLocator {

    private final Context context;

    public CountryLocator(Context context) {
        this.context = context;
    }

    private String getCountryFromSimCardOrNetwork() {
        try {
            final TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (tm == null) return null;
            String simCountry = tm.getSimCountryIso();
            if (simCountry != null && simCountry.length() == 2) {
                return simCountry;
            } else if (tm.getPhoneType() != TelephonyManager.PHONE_TYPE_CDMA) {
                String networkCountry = tm.getNetworkCountryIso();
                if (networkCountry != null && networkCountry.length() == 2)
                    return networkCountry;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getCountryFromLocalConf() {
        return context.getResources().getConfiguration().locale.getCountry();
    }

    public Country getCountry() {
        String country = getCountryFromSimCardOrNetwork();
        if (country == null || country.isEmpty())
            country = getCountryFromLocalConf();

        switch (country.toLowerCase()) {
            case "ma":
            default:
                return Country.ma;
            case "us":
                return Country.us;
            case "fr":
                return Country.fr;
            case "gb":
                return Country.gb;
        }
    }
}
