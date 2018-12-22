package com.mkorp.newsboard.model;

import java.util.List;


public interface NewsFinder {
    List<Article> getArticles(Country country, Category category, int page);
    List<Article> searchArticles(String keyWords, int page);
}
