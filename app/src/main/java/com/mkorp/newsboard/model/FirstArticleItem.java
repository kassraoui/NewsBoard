package com.mkorp.newsboard.model;

public class FirstArticleItem implements ArticleItem {
    public static final int FIRST_ARTICLE_TYPE = 1;
    private final Category category;

    public FirstArticleItem(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    @Override
    public int getItemType() {
        return FIRST_ARTICLE_TYPE;
    }
}
