package com.epul.ProjetMobile.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import com.epul.ProjetMobile.business.Place;

import java.util.ArrayList;

public class PlaceAdapter extends ArrayAdapter<Place> implements Filterable {
    private ArrayList<Place> items;
    private ArrayList<Place> itemsAll;
    private ArrayList<Place> resultList;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            String str = ((Place) (resultValue)).getName();
            return str;
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                resultList.clear();
                for (Place place : itemsAll) {
                    if (place.getName().toLowerCase().startsWith(constraint.toString().toLowerCase())) {
                        resultList.add(place);
                    }
                }
                FilterResults filterResults = new FilterResults();
                filterResults.values = resultList;
                filterResults.count = resultList.size();
                return filterResults;
            } else {
                return new FilterResults();
            }
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            if (results != null && results.count > 0) {
                notifyDataSetChanged();
            }
        }
    };

    public PlaceAdapter(Context context, ArrayList<Place> items) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.items = items;
        this.itemsAll = (ArrayList<Place>) items.clone();
        this.resultList = new ArrayList<>();
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        Place place = getItem(position);
        if (place != null) {
            TextView label = (TextView) v.findViewById(android.R.id.text1);
            if (label != null) {
//              Log.i(MY_DEBUG_TAG, "getView Customer Name:"+customer.getName());
                label.setText(place.getName());
            }
        }
        return v;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        return resultList.size();
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public Place getItem(int position) {
        return resultList.get(position);
    }

    @Override
    public Filter getFilter() {
        return nameFilter;
    }

}