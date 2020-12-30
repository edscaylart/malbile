package br.scaylart.malbile.views.adapters;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

import br.scaylart.malbile.R;
import br.scaylart.malbile.models.BaseRecord;

public class SimilarAdapter<T> extends ArrayAdapter<T> {
    private Context context;
    private ArrayList<BaseRecord> list;

    private int selectedIndex;

    public SimilarAdapter(Context context, ArrayList<BaseRecord> list) {
        super(context, R.layout.item_similar);
        this.context = context;
        this.list = list;
        selectedIndex = -1;
    }

    public void setSelectedIndex(int ind) {
        selectedIndex = ind;
        notifyDataSetChanged();
    }

    public View getView(int position, View view, ViewGroup parent) {
        final BaseRecord record = (list.get(position));
        ViewHolder viewHolder;

        if (view == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            view = inflater.inflate(R.layout.item_similar, parent, false);

            viewHolder = new ViewHolder();
            viewHolder.nameText = (TextView) view.findViewById(R.id.nameTextView);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }

        if (selectedIndex != -1 && position == selectedIndex) {
            viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.accentPinkA200));
        } else {
            viewHolder.nameText.setTextColor(context.getResources().getColor(R.color.primaryBlue500));
        }

        viewHolder.nameText.setText(record.getTitle());

        return view;
    }

    public void supportAddAll(Collection<? extends T> collection) {
        this.clear();
        list = (ArrayList<BaseRecord>) collection;
        for (T record : collection) {
            this.add(record);
        }
    }

    static class ViewHolder {
        TextView nameText;
    }
}
