package com.mkorp.newsboard.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class RetrofitFactory {

    private final Api api;

    public RetrofitFactory(Api api) {
        this.api = api;
    }

    private final Map<Api, ApiSetting> supportedApis = new HashMap<Api, ApiSetting>() {{
        put(Api.NewsApi, new ApiSetting("https://newsapi.org/v2/", new String[]{
                "b3c7a2c367fe4143a8c9fc5ec4a34074",
                "4cfe0b106e2146d0b2a3550d388fff85",
                "1feacd5710f1432abd38e0d964bb1f3b",
                "80b0c7ee51ae40cabea8ab34bff87a42",
                "02ef30f1edb940ac9d66ff64d2b7dda2",
                "19f67934a5154305b3fbcbc872abeccc",
                "e6687a5987de49118b46cd7b77a860a8",
                "0eb3161008fd4afda86e2a6586707f94",
                "048b4fb45be0437bbbb829fd32943ae6",
                "8ae95a8671a44d81a74eca82b1845b8a",
                "6d1f0d6e0b6c40da94a7d782951b69c2",
                "c4729af1c0b742528d7effbb22eedefd"
        }));
    }};

    public Retrofit create() {
        ApiSetting apiSetting = supportedApis.get(api);
        if (apiSetting == null)
            throw new IllegalArgumentException("No api setting found for " + api);
        return new Retrofit.Builder()
                .baseUrl(apiSetting.getBaseUrl())
                .addConverterFactory(JacksonConverterFactory.create())
                .build();
    }

    public String getApiKey() {
        ApiSetting apiSetting = supportedApis.get(api);
        if (apiSetting == null)
            throw new IllegalArgumentException("No api setting found for " + api);
        String[] apiKeys = apiSetting.getApiKeys();
        int rnd = new Random().nextInt(apiKeys.length);
        return apiKeys[rnd];
    }
}
