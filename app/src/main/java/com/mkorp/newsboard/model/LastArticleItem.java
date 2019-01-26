package com.mkorp.newsboard.model;

public class LastArticleItem implements ArticleItem {
    public static final int LAST_ARTICLE_TYPE = 2;

    @Override
    public int getItemType() {
        return LAST_ARTICLE_TYPE;
    }
}
