package com.mkorp.newsboard.model;

import java.util.HashMap;
import java.util.Map;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitFactory {
    private final Map<Api, ApiSetting> supportedApis = new HashMap<Api, ApiSetting>(){{
        put(Api.NewsApi, new ApiSetting("https://newsapi.org/v2/", "6e0c897063c440f985a7b7d34f913693"));
    }};
    public Retrofit create(Api api){
        ApiSetting apiSetting = supportedApis.get(api);
        if(apiSetting == null)
            throw new IllegalArgumentException("No api setting found for " + api);
        return new Retrofit.Builder()
                .baseUrl(apiSetting.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }
    public String getApiKey(Api api){
        ApiSetting apiSetting = supportedApis.get(api);
        if(apiSetting == null)
            throw new IllegalArgumentException("No api setting found for " + api);
        return apiSetting.getApiKey();
    }
}
