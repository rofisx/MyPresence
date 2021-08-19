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

public class AdapterCOOList extends ArrayAdapter<COOEmployee> {
    Context context;
    List<COOEmployee>arrCOOListEmployee;

    public AdapterCOOList(@NonNull Context context, List<COOEmployee> arrcooListEmployee) {
        super(context, R.layout.ci_co_list_item, arrcooListEmployee);
        this.context = context;
        this.arrCOOListEmployee = arrcooListEmployee;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ci_co_list_item,null,true);
        TextView tv_namaCOO = view.findViewById(R.id.tv_nama_ci_co);
        TextView tv_qtyCOO = view.findViewById(R.id.tv_qty_ci_co);
        tv_namaCOO.setText(arrCOOListEmployee.get(position).getNamecoo());
        tv_qtyCOO.setText(arrCOOListEmployee.get(position).getComecoo());
        return view;
    }
}
