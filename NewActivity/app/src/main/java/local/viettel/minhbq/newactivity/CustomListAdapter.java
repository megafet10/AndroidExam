package local.viettel.minhbq.newactivity;

import android.app.Activity;
import android.content.Context;
import android.media.Image;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by minhbq on 11/24/2017.
 */

public class CustomListAdapter extends ArrayAdapter<String> {
    private final Activity context;
    private final String[] itemname;
    private final int [] itemicon;

    public CustomListAdapter(Activity context, String[] itemname, int [] itemicon) {
        super(context, R.layout.name_list, itemname);
        this.context = context;
        this.itemname = itemname;
        this.itemicon = itemicon;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.name_list, null, true);

        TextView txtTile = (TextView) rowView.findViewById(R.id.item);
        TextView txtSub = (TextView) rowView.findViewById(R.id.subitem);
        ImageView img = (ImageView) rowView.findViewById(R.id.icon);

        txtTile.setText(itemname[position]);
        txtSub.setText("Description: " + itemname[position]);
        img.setImageResource(itemicon[position]);

        return rowView;
    }

    @Override
    public String getItem (int position)
    {
        return itemname[position];
    }

}