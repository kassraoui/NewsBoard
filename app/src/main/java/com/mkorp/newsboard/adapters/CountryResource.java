package com.mkorp.newsboard.adapters;

class CountryResource {
    private int flagId;
    private int countryNameId;

    CountryResource(int flagId, int countryName) {
        this.flagId = flagId;
        this.countryNameId = countryName;
    }

    int getFlagId() {
        return flagId;
    }

    int getCountryNameId() {
        return countryNameId;
    }
}
