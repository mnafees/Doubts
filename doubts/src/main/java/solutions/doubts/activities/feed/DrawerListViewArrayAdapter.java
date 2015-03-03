/*
 * This file is part of Doubts.
 * Copyright (c) 2015 Mohammed Nafees (original author) <nafees.technocool@gmail.com>.
 */

package solutions.doubts.activities.feed;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import solutions.doubts.R;

public class DrawerListViewArrayAdapter extends ArrayAdapter<DrawerListViewItem> {

    private final Context context;
    private final List<DrawerListViewItem> values;

    public DrawerListViewArrayAdapter(Context context, List<DrawerListViewItem> values) {
        super(context, R.layout.layout_feed_drawer_listview_item, values);
        this.context = context;
        this.values = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final LayoutInflater inflater = (LayoutInflater)this.context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.layout_feed_drawer_listview_item, parent, false);
        final TextView textView = (TextView)rowView.findViewById(R.id.title);
        final ImageView imageView = (ImageView)rowView.findViewById(R.id.icon);
        textView.setText(values.get(position).getTitle());
        imageView.setImageResource(values.get(position).getIconId());

        return rowView;
    }

}
