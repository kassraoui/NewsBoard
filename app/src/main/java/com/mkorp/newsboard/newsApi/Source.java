package com.mkorp.newsboard.newsApi;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

public class Source {
    private final String id;
    private final String name;
    private final String urlToImage;
    private static final String LOGO_FINDER_URL = "http://logo.clearbit.com";

    @JsonCreator
    public Source(@JsonProperty("id") String id,@JsonProperty("name") String name, @JsonProperty("domain") String domain) {
        this.id = id;
        this.name = name;
        this.urlToImage = LOGO_FINDER_URL + "/" + domain ;
    }

    public String getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public String getUrlToImage() {
        return urlToImage;
    }
}
