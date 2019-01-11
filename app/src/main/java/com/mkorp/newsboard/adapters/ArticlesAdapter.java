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
import com.mkorp.newsboard.ArticlesFragment.OnArticleClickedListener;
import com.mkorp.newsboard.ArticlesFragment.OnBottomReachedListener;
import com.mkorp.newsboard.R;
import com.mkorp.newsboard.model.Article;

import java.text.DateFormat;
import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private final List<Article> articles;
    private final OnArticleClickedListener onArticleClickedListener;
    private OnBottomReachedListener onBottomReachedListener;

    public ArticlesAdapter(List<Article> articles, OnArticleClickedListener onArticleClickedListener) {
        this.articles = articles;
        this.onArticleClickedListener = onArticleClickedListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_article, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        if (position == (articles.size() - 1) * 2 / 3)
            onBottomReachedListener.onBottomReached(position);

        Article article = articles.get(position);
        holder.article = article;

        holder.articleTitleView.setText(article.getTitle());

        Glide.with(holder.getContext())
                .setDefaultRequestOptions(new RequestOptions().error(R.drawable.no_image_available))
                .load(article.getSource().getUrlToImage())
                .into(holder.sourceImageView);
        holder.sourceNameView.setText(article.getSource().getName());

        Glide.with(holder.getContext())
                .load(article.getUrlToImage())
                .thumbnail(Glide.with(holder.getContext()).load(R.drawable.loading_image))
                .listener(new RequestListener<Drawable>() {
                    @Override
                    public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                        holder.articleImageView.setVisibility(View.GONE);
                        return false;
                    }

                    @Override
                    public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                        return false;
                    }
                })
                .into(holder.articleImageView);

        DateFormat dateInstance = DateFormat.getDateTimeInstance();
        holder.articleDateView.setText(dateInstance.format(article.getPublishedAt()));

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onArticleClickedListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    onArticleClickedListener.onArticleClicked(holder.article);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public void loadNextArticles(List<Article> articles) {
        notifyItemRangeInserted(0, articles.size());
        this.articles.addAll(articles);
    }

    public void clearArticles()
    {
        notifyItemRangeRemoved(0, articles.size());
        this.articles.clear();
    }

    public void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView sourceImageView;
        final TextView sourceNameView;
        final TextView articleTitleView;
        final ImageView articleImageView;
        final TextView articleDateView;

        Article article;

        ViewHolder(View view) {
            super(view);
            mView = view;
            sourceImageView = view.findViewById(R.id.sourceImage);
            sourceNameView = view.findViewById(R.id.sourceName);
            articleTitleView = view.findViewById(R.id.articleTitle);
            articleImageView = view.findViewById(R.id.articleImage);
            articleDateView = view.findViewById(R.id.articleDate);
        }

        public Context getContext() {
            return mView.getContext();
        }
    }
}
