package com.example.tank.adapters;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tank.R;
import com.example.tank.databinding.ActivitySeePdfBinding;
import com.example.tank.ui.SeePdfActivity;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class AdapterPDF extends RecyclerView.Adapter<AdapterPDF.ViewHolder> {

    List<File> item;
    Context context;
    public AdapterPDF(List<File> item) {
        this.item = item;
    }
    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_pdf,parent,false);
        context = parent.getContext();
        return new AdapterPDF.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        holder.name.setText(item.get(position).getName());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
        String lastModified = dateFormat.format(new Date(item.get(position).lastModified()));
        long fileSizeInKb = item.get(position).length() / 1024;

        holder.info.setText(lastModified+" - "+fileSizeInKb+"KB");

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SeePdfActivity.class);
                intent.putExtra("path", item.get(position).getAbsolutePath());
                intent.putExtra("name",item.get(position).getName()) ;
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return item.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        LinearLayout container;
        TextView name;
        TextView info;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            container = itemView.findViewById(R.id.container_pdf_item);
            name = itemView.findViewById(R.id.pdf_name);
            info = itemView.findViewById(R.id.pdf_info);
        }
    }
}
