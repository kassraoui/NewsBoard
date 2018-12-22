package com.mkorp.newsboard;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.mkorp.newsboard.HomeFragment.OnArticleClickedListener;
import com.mkorp.newsboard.HomeFragment.OnBottomReachedListener;
import com.mkorp.newsboard.dummy.DummyContent.DummyItem;
import com.mkorp.newsboard.model.Article;

import java.text.DateFormat;
import java.util.List;

/**
 * {@link RecyclerView.Adapter} that can display a {@link DummyItem} and makes a call to the
 * specified {@link OnArticleClickedListener}.
 * TODO: Replace the implementation with code for your data type.
 */
public class ArticlesAdapter extends RecyclerView.Adapter<ArticlesAdapter.ViewHolder> {

    private final List<Article> articles;
    private final OnArticleClickedListener onArticleClickedListener;
    private OnBottomReachedListener onBottomReachedListener;

    ArticlesAdapter(List<Article> articles, OnArticleClickedListener onArticleClickedListener) {
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
        if(position == articles.size() - 1)
            onBottomReachedListener.onBottomReached(position);
        RequestOptions requestOptions = new RequestOptions();
        requestOptions.error(R.drawable.no_image_available);

        Article article = articles.get(position);
        holder.article = article;
        Glide.with(holder.sourceImageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(article.getSource().getUrlToImage())
                .into(holder.sourceImageView);
        holder.sourceNameView.setText(article.getSource().getName());
        holder.articleTitleView.setText(article.getTitle());

        requestOptions.placeholder(R.drawable.loading_image);
        Glide.with(holder.articleImageView.getContext())
                .setDefaultRequestOptions(requestOptions)
                .load(article.getUrlToImage())
                .into(holder.articleImageView);

        DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(holder.articleDateView.getContext());
        holder.articleDateView.setText(dateFormat.format(article.getPublishedAt()));

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

    void loadNextArticles(List<Article> articles) {
        this.articles.addAll(articles);
        notifyDataSetChanged();
    }

    void setOnBottomReachedListener(OnBottomReachedListener onBottomReachedListener) {
        this.onBottomReachedListener = onBottomReachedListener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView sourceImageView;
        final TextView sourceNameView;
        final TextView  articleTitleView;
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
    }
}
