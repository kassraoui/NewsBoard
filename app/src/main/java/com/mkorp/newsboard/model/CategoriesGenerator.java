package com.mkorp.newsboard.model;

import com.mkorp.newsboard.R;

public class CategoriesGenerator {

    public static CategoryCard[] getCategories() {
        return new CategoryCard[]{
                new CategoryCard(Category.Business, R.drawable.business, R.string.business),
                new CategoryCard(Category.Technology, R.drawable.technology, R.string.technology),
                new CategoryCard(Category.Sports, R.drawable.sports, R.string.sports),
                new CategoryCard(Category.Science, R.drawable.science, R.string.science),
                new CategoryCard(Category.Health, R.drawable.health, R.string.health),
                new CategoryCard(Category.Entertainment, R.drawable.entertainment, R.string.entertainment)
        };
    }


    public static class CategoryCard {
        public final int imageId;
        public final int labelId;
        public final Category category;

        CategoryCard(Category category, int imageId, int labelId) {
            this.imageId = imageId;
            this.labelId = labelId;
            this.category = category;
        }
    }
}
