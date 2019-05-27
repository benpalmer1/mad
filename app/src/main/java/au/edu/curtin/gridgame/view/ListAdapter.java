package au.edu.curtin.gridgame.view;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import au.edu.curtin.gridgame.R;
import au.edu.curtin.gridgame.model.ListItem;

public class ListAdapter extends BaseAdapter
{
    private ArrayList<ListItem> itemList;
    private Activity activity;

    public ListAdapter(Activity activity, ArrayList<ListItem> itemList) {
        super();
        this.activity = activity;
        this.itemList = itemList;
    }

    @Override
    public int getCount() {
        return itemList.size();
    }

    @Override
    public Object getItem(int position) {
        return itemList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    private class ViewHolder {
        TextView description;
        TextView healthPoison;
        TextView mass;
        TextView value;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder;
        LayoutInflater inflater = activity.getLayoutInflater();

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_row_layout, null);
            holder = new ViewHolder();
            holder.description = (TextView) convertView.findViewById(R.id.row_item1);
            holder.healthPoison = (TextView) convertView.findViewById(R.id.row_item2);
            holder.mass = (TextView) convertView.findViewById(R.id.row_item3);
            holder.value = (TextView) convertView.findViewById(R.id.row_item4);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        ListItem item = itemList.get(position);
        holder.description.setText(item.getDescription().toString());
        holder.healthPoison.setText(item.getHealthPoison().toString());
        holder.mass.setText(item.getMass());
        holder.value.setText(item.getValue());

        return convertView;
    }
}