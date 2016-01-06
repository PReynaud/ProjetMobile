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
    /**
     * Liste de toutes les places de l'application
     */
    private ArrayList<Place> placeList;
    /**
     * Liste des résultats
     */
    private ArrayList<Place> resultList;
    Filter nameFilter = new Filter() {
        @Override
        public String convertResultToString(Object resultValue) {
            return ((Place) (resultValue)).getName();
        }

        /**
         * Trouve les résultats à partir de la requête
         *
         * @param constraint requête
         * @return Une liste de résultats avec le nombre de résultats
         */
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            if (constraint != null) {
                resultList.clear();
                for (Place place : placeList) {
                    // Cette ligne fait le tri à partir de la requête
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

    /**
     * Constructeur, créé aussi la vue (dropdown menu) des résultats
     * @param context
     * @param items
     */
    public PlaceAdapter(Context context, ArrayList<Place> items) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        this.placeList = (ArrayList<Place>) items.clone();
        this.resultList = new ArrayList<>();
    }

    /**
     * Retourne la vue correspondant à la liste des résultats
     *
     * @param position    position de la vue
     * @param convertView
     * @param parent
     * @return la vue correspondant à la liste des résultats
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = super.getView(position, convertView, parent);
        Place place = getItem(position);
        if (place != null) {
            TextView label = (TextView) v.findViewById(android.R.id.text1);
            TextView description = (TextView) v.findViewById(android.R.id.text2);
            if (label != null) {
                label.setText(place.getName());
            }
            if (description != null) {
                description.setText(place.getVicinity());
            }
        }
        return v;
    }

    /**
     * Retourne le nombre de résultats
     */
    @Override
    public int getCount() {
        return resultList.size();
    }

    /**
     * Retourne un résultat à partir de sa position
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