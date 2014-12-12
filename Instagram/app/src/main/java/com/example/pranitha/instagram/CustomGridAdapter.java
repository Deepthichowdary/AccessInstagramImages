package com.example.pranitha.instagram;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Pranitha on 11-12-14.
 */
public class CustomGridAdapter extends SimpleAdapter {
    private Context mContext;
    public LayoutInflater inflater = null;
    public CustomGridAdapter(Context context,
                           List<? extends Map<String, ?>> data, int resource, String[] from,
                           int[] to) {
        super(context, data, resource, from, to);
        mContext = context;
        inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.list_row, null);

        HashMap<String, Object> data = (HashMap<String, Object>) getItem(position);

        /*TextView Vid=(TextView) vi.findViewById(R.id.id);
        Vid.setText(" Age:"+data.get("id"));
        TextView Vname=(TextView) vi.findViewById(R.id.name);
        Vname.setText(" Name:"+data.get("name"));*/
        new DownloadTask((ImageView) vi.findViewById(R.id.image))
                .execute((String) data.get("url"));

        return vi;
    }

}