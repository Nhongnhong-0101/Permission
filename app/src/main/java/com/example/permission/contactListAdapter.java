package com.example.permission;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;

public class contactListAdapter extends ArrayAdapter<ContactItem> {

    private  int resource;
    private List<ContactItem> contactItems;

    public contactListAdapter(@NonNull Context context, int resource, @NonNull List<ContactItem> objects) {
        super(context, resource, objects);
        this.resource = resource;
        this.contactItems = objects;
    }
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View v = convertView;
        if (v ==  null)
        {
            LayoutInflater vi;
            vi = LayoutInflater.from(this.getContext());
            v = vi.inflate(this.resource, null);

        }
        ContactItem t = getItem(position);
        TextView tvName = v.findViewById(R.id.contact_name);
        TextView tvPhone= v.findViewById(R.id.contact_number);

        tvName.setText(t.getName().toString());
        tvPhone.setText(t.getPhone().toString());

        return v;
    }


}
