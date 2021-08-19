package com.example.mypresence.ListView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.mypresence.R;

import java.util.List;

public class AdapterList extends ArrayAdapter<Employee> {
    Context context;
    List<Employee>arrayListEmployee;

    public AdapterList(@NonNull Context context, List<Employee> arrayListEmployee) {
        super(context, R.layout.list_item, arrayListEmployee);

        this.context = context;
        this.arrayListEmployee = arrayListEmployee;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item,null,true);
        TextView tvName =  view.findViewById(R.id.txt_listnama);
        TextView tvCome =  view.findViewById(R.id.txt_datang);
        TextView tvComeLT =  view.findViewById(R.id.txt_datangt);
        TextView tvgoHome =  view.findViewById(R.id.txt_pulang);
        TextView tvgoHomeLT =  view.findViewById(R.id.txt_pulangaw);

        tvName.setText(arrayListEmployee.get(position).getName());
        tvCome.setText(arrayListEmployee.get(position).getCome());
        tvComeLT.setText(arrayListEmployee.get(position).getComelate());
        tvgoHome.setText(arrayListEmployee.get(position).getGohome());
        tvgoHomeLT.setText(arrayListEmployee.get(position).getGohomefirst());
        return view;
    }
}
