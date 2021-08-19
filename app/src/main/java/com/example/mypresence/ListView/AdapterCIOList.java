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

public class AdapterCIOList extends ArrayAdapter<CIOEmployee> {
    Context context;
    List<CIOEmployee>arrCIOListEmployee;

    public AdapterCIOList(@NonNull Context context,  List<CIOEmployee> arrcioListEmployee) {
        super(context, R.layout.ci_co_list_item, arrcioListEmployee);
        this.context = context;
        this.arrCIOListEmployee = arrcioListEmployee;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ci_co_list_item,null,true);
        TextView tv_namaCIO = view.findViewById(R.id.tv_nama_ci_co);
        TextView tv_qtyCIO = view.findViewById(R.id.tv_qty_ci_co);
        tv_namaCIO.setText(arrCIOListEmployee.get(position).getNamecio());
        tv_qtyCIO.setText(arrCIOListEmployee.get(position).getComecio());
        return view;
    }
}
