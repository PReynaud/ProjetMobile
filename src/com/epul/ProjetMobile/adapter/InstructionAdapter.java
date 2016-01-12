package com.epul.ProjetMobile.adapter;

import android.content.Context;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;
import com.epul.ProjetMobile.R;

import java.util.List;

/**
 * @author Dimitri RODARIE on 13/01/2016.
 * @version 1.0
 */
public class InstructionAdapter extends BaseAdapter {
    private final LayoutInflater mInflater;
    private List<String> instructions;

    public InstructionAdapter(List<String> instructions, Context context) {
        this.instructions = instructions;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return instructions.size();
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
        convertView = mInflater.inflate(R.layout.direction, parent, false);
        ((TextView) convertView.findViewById(R.id.instruction)).setText(Html.fromHtml(instructions.get(position)));
        return convertView;
    }
}
