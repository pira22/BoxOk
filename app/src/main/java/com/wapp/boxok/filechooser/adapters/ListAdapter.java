package com.wapp.boxok.filechooser.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.wapp.boxok.R;
import com.wapp.boxok.model.Advertise;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmResults;

/**
 * Created by MacBook on 28/07/2015.
 */
public class ListAdapter extends ArrayAdapter<String> {
    private final Context context;
    public  ArrayList<String> values ;

    public ArrayList<String> getValues() {
        return values;
    }

    public void setValues(ArrayList<String> values) {
        this.values = values;
    }

    public ListAdapter(Context context, ArrayList<String> values) {
        super(context, R.layout.row_layout, values);
        this.context = context;
        this.values = values;
        Log.d("VALUES",values.toString());
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.row_layout, parent, false);
        TextView textView = (TextView) rowView.findViewById(R.id.label);
        ImageView imageView = (ImageView) rowView.findViewById(R.id.logo);

        textView.setText(values.get(position));

      /*  Button bDelete = (Button)rowView.findViewById(R.id.button_delete);
        bDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Realm realm = Realm.getInstance(getContext());

               // RealmResults<Advertise> result = realm.where(Advertise.class).findAll();
                //RealmResults<Advertise> result =

                //Advertise ad = result.where().equalTo("name", "John").findFirst();
                //Log.d("result", ad.toString());
                //Log.d("selected",(values.get(position))+"");
                //Log.d("postion",values.indexOf(values.get(position))+"");
                //log.d("find",result.first());
               // Advertise ad = result.get(values.indexOf(values.get(position)));
                Advertise ad= realm.where(Advertise.class)
                        .equalTo("path", values.get(position))
                        .findFirst();
                realm.beginTransaction();
                Log.d("REALM FOUND",ad.toString());
                ad.removeFromRealm();
               // result.clear();
                realm.commitTransaction();

                values.remove(position);
                notifyDataSetChanged();
            }
        });*/

        // Change icon based on name
        String s = values.get(position);
        System.out.println(s);

        /*if (s.equals("WindowsMobile")) {
            imageView.setImageResource(R.drawable.windowsmobile_logo);
        } else if (s.equals("iOS")) {
            imageView.setImageResource(R.drawable.ios_logo);
        } else if (s.equals("Blackberry")) {
            imageView.setImageResource(R.drawable.blackberry_logo);
        } else {
            imageView.setImageResource(R.drawable.android_logo);
        }*/

        return rowView;
    }
}