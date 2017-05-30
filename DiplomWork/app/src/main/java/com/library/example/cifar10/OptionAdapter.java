package com.library.example.cifar10;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by develop on 30.05.2017.
 */

public class OptionAdapter extends BaseAdapter {

    private LayoutInflater lInflater;
    private OptionAdapter.ViewHolder viewHolder;

    ArrayList<ResponseOption> array =  new ArrayList<>();

    public OptionAdapter(Context context, ArrayList<ResponseOption> array) {
        this.array = array;
        lInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @SuppressWarnings("deprecation")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.item, parent, false);
            viewHolder = new OptionAdapter.ViewHolder();

            viewHolder.tvName = (TextView) convertView.findViewById(R.id.tvName);
            viewHolder.tvPercent = (TextView) convertView.findViewById(R.id.tvPercent);

            convertView.setTag(viewHolder);
        } else {
            viewHolder = (OptionAdapter.ViewHolder) convertView.getTag();
        }

        ResponseOption option = array.get(position);

        viewHolder.tvName.setText(option.getName());
        viewHolder.tvPercent.setText("% " + Float.toString(option.getOption()));

        return convertView;
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public Object getItem(int position) {
        return array.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    static class ViewHolder {
        TextView tvName;
        TextView tvPercent;
    }
}
