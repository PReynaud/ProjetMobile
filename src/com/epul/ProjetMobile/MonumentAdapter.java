package com.epul.ProjetMobile;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * @Author Dimitri on 29/12/2015.
 * @Version 1.0
 */
public class MonumentAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private ArrayList<Place> places;
    private ArrayList<ItemStatus> status;

    public MonumentAdapter(Context context, ArrayList<Place> places, ArrayList<ItemStatus> status) {
        this.status = status;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.places = places;
    }

    @Override
    public int getCount() {
        return places.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.monument, parent, false);
            ((TextView) convertView.findViewById(R.id.monumentName)).setText(places.get(position).getName());
            ((TextView) convertView.findViewById(R.id.monumentAddress)).setText(places.get(position).getVicinity());
        }
        //On reset les couleurs à chaque fois pour éviter les bugs de recyclage des items (listview)
        resetItemList(status.get(position), convertView);
        return convertView;
    }

    public void resetItemList(ItemStatus status, View view) {
        switch (status) {
            case Normal:
                view.findViewById(R.id.delete).setVisibility(View.GONE);
                view.findViewById(R.id.star).setVisibility(View.GONE);
                view.findViewById(R.id.item_list).setBackgroundResource(R.color.windowBackground);
                break;
            case Delete:
                view.findViewById(R.id.delete).setVisibility(View.VISIBLE);
                view.findViewById(R.id.star).setVisibility(View.GONE);
                view.findViewById(R.id.item_list).setBackgroundResource(R.color.warning);
                break;
            case Favorite:
                view.findViewById(R.id.delete).setVisibility(View.GONE);
                view.findViewById(R.id.star).setVisibility(View.VISIBLE);
                view.findViewById(R.id.item_list).setBackgroundResource(R.color.favorite);
                break;
            default:
                break;
        }
    }
}
