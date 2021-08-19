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

public class AdapterCILList extends ArrayAdapter<CILEmployee> {
    Context context;
    List<CILEmployee>arrCILListEmployee;

    public AdapterCILList(@NonNull Context context, List<CILEmployee> arrcilListEmployee) {
        super(context, R.layout.ci_co_list_item, arrcilListEmployee);
        this.context = context;
        this.arrCILListEmployee = arrcilListEmployee;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.ci_co_list_item,null,true);
        TextView tv_namaCIL = view.findViewById(R.id.tv_nama_ci_co);
        TextView tv_qtyCIL = view.findViewById(R.id.tv_qty_ci_co);
        tv_namaCIL.setText(arrCILListEmployee.get(position).getNamecil());
        tv_qtyCIL.setText(arrCILListEmployee.get(position).getComecil());
        return view;
    }
}
