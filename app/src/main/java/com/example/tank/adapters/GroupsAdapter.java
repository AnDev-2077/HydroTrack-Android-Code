package com.example.tank.adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.bumptech.glide.request.RequestOptions;
import com.example.tank.R;
import com.example.tank.domain.Group;
import com.example.tank.ui.GroupInformationActivity;

import java.util.ArrayList;
import java.util.List;

public class GroupsAdapter extends RecyclerView.Adapter<GroupsAdapter.ViewHolder> {
    Context context;
    List<Group> groups;

    public GroupsAdapter(List<Group> groups) {
        this.groups = groups;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupos,parent,false);
        context = parent.getContext();
        return new GroupsAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Glide.with(context)
                .load(R.drawable.groups_img)
                .apply(RequestOptions.bitmapTransform(new CircleCrop()))
                .into(holder.img);
        holder.name.setText(groups.get(position).getName());
        holder.integrantes.setText("Tu y "+ (groups.get(position).getEmails().size()-1)+" integrantes más");

        holder.container.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, GroupInformationActivity.class);

                intent.putExtra("titulo", groups.get(position).getName());
                intent.putExtra("admin", groups.get(position).getAdmin());
                intent.putExtra("idGrupo", groups.get(position).getId());

                ArrayList<String> arrayListEmails = new ArrayList<>(groups.get(position).getEmails());

                intent.putStringArrayListExtra("emails", arrayListEmails);

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return groups.size()    ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img ;
        TextView name;
        TextView integrantes;
        LinearLayout container;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.img);
            name = itemView.findViewById(R.id.name);
            integrantes = itemView.findViewById(R.id.inte);
            container = itemView.findViewById(R.id.item_groups_container);
        }
    }
}
