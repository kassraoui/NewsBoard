package com.mkorp.newsboard.adapters;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.mkorp.newsboard.ui.CategoryFragment;
import com.mkorp.newsboard.ui.CategoryFragment.OnCategoryClickedListener;
import com.mkorp.newsboard.R;
import com.mkorp.newsboard.CategoriesGenerator.CategoryCard;

public class CategoryAdapter extends RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder> {

    private final CategoryCard[] mValues;
    private final CategoryFragment.OnCategoryClickedListener onCategoryClickedListener;

    public CategoryAdapter(CategoryCard[] items, OnCategoryClickedListener listener) {
        mValues = items;
        onCategoryClickedListener = listener;
    }

    @NonNull
    @Override
    public CategoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fragment_category, parent, false);
        return new CategoryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final CategoryViewHolder holder, int position) {
        holder.item = mValues[position];
        Glide.with(holder.mView.getContext())
                .load(mValues[position].imageId)
                .into(holder.imageView);
        holder.textView.setText(mValues[position].labelId);

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != onCategoryClickedListener) {
                    onCategoryClickedListener.onCategoryClicked(holder.item.category);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mValues.length;
    }

    public class CategoryViewHolder extends RecyclerView.ViewHolder {
        final View mView;
        final ImageView imageView;
        final TextView textView;
        CategoryCard item;

        CategoryViewHolder(View view) {
            super(view);
            mView = view;
            imageView = view.findViewById(R.id.categoryImage);
            textView = view.findViewById(R.id.categoryName);
        }

        @NonNull
        @Override
        public String toString() {
            return super.toString() + " '" + textView.getText() + "'";
        }
    }
}
