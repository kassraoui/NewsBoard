package com.mkorp.newsboard.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.mkorp.newsboard.R;
import com.mkorp.newsboard.model.Country;

import java.util.LinkedList;
import java.util.List;

public class FlagsAdapter extends ArrayAdapter {
    private Country[] countries;
    private Context mContext;
    private List<Integer> flagResources;

    public FlagsAdapter(@NonNull Context context, Country[] countries) {
        super(context, R.layout.flag_row);
        this.countries = countries;
        this.mContext = context;
        flagResources = getFlagResources();
    }

    private List<Integer> getFlagResources() {
        List<Integer> list = new LinkedList<>();
        for (Country country : countries) {
            switch (country) {
                case ma:
                    list.add(R.drawable.flag_morocco); break;
                case fr:
                    list.add(R.drawable.flag_france); break;
                case us :
                    list.add(R.drawable.flag_usa); break;
                case gb :
                    list.add(R.drawable.flag_uk);
            }
        }
        return list;
    }

    @Override
    public int getCount() {
        return countries.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ViewHolder mViewHolder = new ViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.
                    getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.flag_row, parent, false);
            mViewHolder.mFlag = convertView.findViewById(R.id.flagImage);
            convertView.setTag(mViewHolder);
        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        mViewHolder.mFlag.setImageResource(flagResources.get(position));
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return getView(position, convertView, parent);
    }

    private static class ViewHolder {
        ImageView mFlag;
    }
}
