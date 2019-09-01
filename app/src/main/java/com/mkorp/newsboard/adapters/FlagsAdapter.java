package com.mkorp.newsboard.adapters;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.mkorp.newsboard.R;
import com.mkorp.newsboard.newsApi.Country;

import java.util.LinkedList;
import java.util.List;

public class FlagsAdapter extends ArrayAdapter {
    private Country[] countries;
    private Context mContext;
    private List<CountryResource> flagResources;

    public FlagsAdapter(@NonNull Context context, Country[] countries) {
        super(context, R.layout.flag_row);
        this.countries = countries;
        this.mContext = context;
        flagResources = getFlagResources();
    }

    private List<CountryResource> getFlagResources() {
        List<CountryResource> list = new LinkedList<>();
        for (Country country : countries) {
            switch (country) {
                case ma:
                    list.add(new CountryResource(R.drawable.flag_morocco, R.string.morocco));
                    break;
                case fr:
                    list.add(new CountryResource(R.drawable.flag_france, R.string.france));
                    break;
                case us:
                    list.add(new CountryResource(R.drawable.flag_usa, R.string.usa));
                    break;
                case gb:
                    list.add(new CountryResource(R.drawable.flag_uk, R.string.uk));
            }
        }
        return list;
    }

    public int getPosition(Country country) {
        int i = 0;
        for (Country cty : countries) {
            if (cty == country)
                return i;
            i++;
        }
        return i;
    }

    @Override
    public int getCount() {
        return countries.length;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FlagViewHolder viewHolder = new FlagViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.flag_row, parent, false);
            viewHolder.flag = convertView.findViewById(R.id.flagImage);
            viewHolder.label = convertView.findViewById(R.id.countryName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FlagViewHolder) convertView.getTag();
        }

        viewHolder.flag.setImageResource(flagResources.get(position).getFlagId());
        viewHolder.label.setVisibility(View.GONE);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        FlagViewHolder viewHolder = new FlagViewHolder();
        if (convertView == null) {
            LayoutInflater mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = mInflater.inflate(R.layout.flag_row, parent, false);
            viewHolder.flag = convertView.findViewById(R.id.flagImage);
            viewHolder.label = convertView.findViewById(R.id.countryName);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (FlagViewHolder) convertView.getTag();
        }

        viewHolder.flag.setImageResource(flagResources.get(position).getFlagId());
        viewHolder.label.setText(flagResources.get(position).getCountryNameId());
        return convertView;
    }

    private static class FlagViewHolder {
        TextView label;
        ImageView flag;
    }
}
