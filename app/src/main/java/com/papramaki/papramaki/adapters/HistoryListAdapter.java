package com.papramaki.papramaki.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import com.papramaki.papramaki.R;
import com.papramaki.papramaki.models.Expenditure;
import java.util.List;


/**
 * Created by djdmfd on 3/20/16.
 */
public class HistoryListAdapter extends ArrayAdapter<Expenditure> {

    private Context context;
    private List<Expenditure> items;

    public HistoryListAdapter(Context context, List<Expenditure> items) {
        super(context, R.layout.history_item, items);
        this.context = context;
        this.items = items;
    }

    public int getCount() {
        return items.size();
    }

    /**
     * Holder for the list items.
     */
    private class ViewHolder{
        TextView titleText;
        TextView dateText;
    }

    /**
     *
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        Expenditure expenditure = items.get(position);

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.history_item, parent, false);
            holder = new ViewHolder();
            holder.titleText = (TextView)convertView.findViewById(R.id.titleTextView);
            holder.dateText = (TextView)convertView.findViewById(R.id.date_and_category_text);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        holder.titleText.setText(expenditure.getItemTitle());
        if (expenditure.getCategory()!=null) {
            holder.dateText.setText(expenditure.getDate().toString() + ", " + expenditure.getCategory());
        }
        else {
            holder.dateText.setText(expenditure.getDate().toString());
        }

        return convertView;
    }
}
