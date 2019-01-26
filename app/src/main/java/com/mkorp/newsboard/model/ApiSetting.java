package com.mkorp.newsboard.model;

class ApiSetting {

    private final String baseUrl;
    private final String apiKey;

    ApiSetting(String baseUrl, String apiKey) {
        this.baseUrl = baseUrl;
        this.apiKey = apiKey;
    }

    String getBaseUrl() {
        return baseUrl;
    }
    String getApiKey() {
        return apiKey;
    }
}
