package com.mkorp.newsboard.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.mkorp.newsboard.ArticlesFragment;
import com.mkorp.newsboard.ArticlesFragment.OnArticleClickedListener;
import com.mkorp.newsboard.ArticlesFragment.OnBottomReachedListener;
import com.mkorp.newsboard.R;
import com.mkorp.newsboard.model.Article;
import com.mkorp.newsboard.model.ArticleItem;
import com.mkorp.newsboard.model.Category;
import com.mkorp.newsboard.model.FirstArticleItem;
import com.mkorp.newsboard.model.LastArticleItem;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private final List<ArticleItem> articles;
    private OnArticleClickedListener onArticleClickedListener;
    private OnBottomReachedListener onBottomReachedListener;

    public ArticlesAdapter() {
        this.articles = new ArrayList<>();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        switch (viewType) {
            case Article.ARTICLE_TYPE:
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_article, parent, false);
                return new ArticleViewHolder(view);
            case FirstArticleItem.FIRST_ARTICLE_TYPE:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_first_article, parent, false);
                return new FirstViewHolder(view);
            case LastArticleItem.LAST_ARTICLE_TYPE:
            default:
                view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.fragment_last_article, parent, false);
                return new LastViewHolder(view);
        }
    }

    @Override
    public int getItemViewType(int position) {
        return this.articles.get(position).getItemType();
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (articles.size() > 3 && position == (articles.size() - 1) / 3)
            onBottomReachedListener.onBottomReached(position);

        switch (holder.getItemViewType()) {
            case Article.ARTICLE_TYPE:
                final Article article = (Article) articles.get(position);
                final ArticleViewHolder articleHolder = (ArticleViewHolder) holder;

                articleHolder.articleTitleView.setText(article.getTitle());

                Glide.with(holder.getContext())
                        .setDefaultRequestOptions(new RequestOptions().error(R.drawable.no_image_available))
                        .load(article.getSource().getUrlToImage())
                        .into(articleHolder.sourceImageView);
                articleHolder.sourceNameView.setText(article.getSource().getName());

                Glide.with(holder.getContext())
                        .load(article.getUrlToImage())
                        .listener(new RequestListener<Drawable>() {
                            @Override
                            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                                articleHolder.articleImageView.setVisibility(View.GONE);
                                return false;
                            }

                            @Override
                            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                                return false;
                            }
                        })
                        .into(articleHolder.articleImageView);

                DateFormat dateInstance = DateFormat.getDateTimeInstance();
                articleHolder.articleDateView.setText(dateInstance.format(article.getPublishedAt()));

                holder.mView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (null != onArticleClickedListener) {
                            onArticleClickedListener.onArticleClicked(article);
                        }
                    }
                });
                break;
            case FirstArticleItem.FIRST_ARTICLE_TYPE:
                final FirstArticleItem categoryItem = (FirstArticleItem) articles.get(position);
                FirstViewHolder firstHolder = (FirstViewHolder) holder;
                firstHolder.categoryTextView.setText(categoryItem.getCategory().toString());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void loadNextArticles(List<Article> articles, Category category) {
        int size = this.articles.size();
        if (size == 0)
            this.articles.add(new FirstArticleItem(category));
        this.articles.addAll(articles);
        notifyItemRangeInserted(size, this.articles.size());
    }

    public void clearArticles() {
        notifyItemRangeRemoved(0, 100);

        this.articles.clear();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    public void setOnArticleClickedListener(OnArticleClickedListener onArticleClickedListener) {
        this.onArticleClickedListener = onArticleClickedListener;
    }

    public void setOnArticlesChangedListener(final ArticlesFragment.OnArticlesChangedListener onArticlesChangedListener) {
        registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                onArticlesChangedListener.onArticleRangeInserted(positionStart, itemCount);
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                onArticlesChangedListener.onArticleRangeRemoved(positionStart, itemCount);
            }
        });
    }

    public void notifyLastPageReached() {
        articles.add(new LastArticleItem());
    }

    class FirstViewHolder extends ViewHolder {

        final TextView categoryTextView;
        final View categoryView;

        FirstViewHolder(View view) {
            super(view);
            categoryTextView = view.findViewById(R.id.categoryName);
            categoryView = view.findViewById(R.id.categoryColor);
        }
    }

    class LastViewHolder extends ViewHolder {

        LastViewHolder(View view) {
            super(view);
        }
    }

    class ArticleViewHolder extends ViewHolder {

        final ImageView sourceImageView;
        final TextView sourceNameView;
        final TextView articleTitleView;
        final ImageView articleImageView;
        final TextView articleDateView;

        ArticleViewHolder(View view) {
            super(view);
            sourceImageView = view.findViewById(R.id.sourceImage);
            sourceNameView = view.findViewById(R.id.sourceName);
            articleTitleView = view.findViewById(R.id.articleTitle);
            articleImageView = view.findViewById(R.id.articleImage);
            articleDateView = view.findViewById(R.id.articleDate);
        }
    }

    abstract class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;

        ViewHolder(View view) {
            super(view);
            mView = view;
        }

        Context getContext() {
            return mView.getContext();
        }
    }

}
