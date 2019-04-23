package com.mkorp.newsboard.newsApi;

class ApiSetting {

    private final String baseUrl;
    private final String[] apiKeys;

    ApiSetting(String baseUrl, String[] apiKeys) {
        this.baseUrl = baseUrl;
        this.apiKeys = apiKeys;
    }

    String getBaseUrl() {
        return baseUrl;
    }
    String[] getApiKeys() {
        return apiKeys;
    }
}
