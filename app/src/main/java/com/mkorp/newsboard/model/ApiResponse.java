package com.mkorp.newsboard.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class ApiResponse {
    private int totalResults;
    private final Status status;
    private List<Article> articles;

    private String code;
    private String message;

    @JsonCreator
    public ApiResponse(@JsonProperty("totalResults") int totalResults, @JsonProperty("status") Status status, @JsonProperty("articles") List<Article> articles, @JsonProperty("code") String code, @JsonProperty("message") String message) {
        this.totalResults = totalResults;
        this.status = status;
        this.articles = articles;
        this.code = code;
        this.message = message;
    }

    public ApiResponse(Status status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }

    public Status getStatus() {
        return status;
    }

    public List<Article> getArticles() {
        return articles;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public int getTotalResults() {
        return totalResults;
    }
}
