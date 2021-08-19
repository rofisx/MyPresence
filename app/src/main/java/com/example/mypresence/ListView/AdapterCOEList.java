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

public class AdapterCOEList extends ArrayAdapter<COEEmployee> {
    Context context;
    List<COEEmployee>arrCOEListEmployee;

    public AdapterCOEList(@NonNull Context context, List<COEEmployee> arrcoeListEmployee) {
        super(context, R.layout.ci_co_list_item, arrcoeListEmployee);
        this.context = context;
        this.arrCOEListEmployee = arrcoeListEmployee;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ci_co_list_item,null,true);
        TextView tv_namaCIO = view.findViewById(R.id.tv_nama_ci_co);
        TextView tv_qtyCIO = view.findViewById(R.id.tv_qty_ci_co);
        tv_namaCIO.setText(arrCOEListEmployee.get(position).getNamecoe());
        tv_qtyCIO.setText(arrCOEListEmployee.get(position).getComecoe());
        return view;
    }
}
