package com.mkorp.newsboard.ui;

import android.content.Context;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.mkorp.newsboard.CategoriesGenerator;
import com.mkorp.newsboard.R;
import com.mkorp.newsboard.adapters.CategoryAdapter;
import com.mkorp.newsboard.newsApi.Category;

public class CategoryFragment extends Fragment {

    public static final int TAG = 1;
    private static final String ARG_COLUMN_COUNT = "column-count";
    private int mColumnCount = 2;
    private OnCategoryClickedListener onCategoryClickedListener;

    public CategoryFragment() {
    }

    public static CategoryFragment newInstance(int columnCount) {
        CategoryFragment fragment = new CategoryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_category_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }
            recyclerView.setAdapter(new CategoryAdapter(CategoriesGenerator.getCategories(), onCategoryClickedListener));
        }
        return view;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnCategoryClickedListener) {
            onCategoryClickedListener = (OnCategoryClickedListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnCategoryClickedListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        onCategoryClickedListener = null;
    }

    public interface OnCategoryClickedListener {
        void onCategoryClicked(Category category);
    }
}
