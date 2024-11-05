package com.example.tank.adapters;

import android.content.Context;
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
import com.example.tank.domain.Member;
import com.example.tank.domain.MemberGroup;

import java.util.ArrayList;
import java.util.List;

public class MembersAdpater  extends RecyclerView.Adapter<MembersAdpater.ViewHolder> {
    Context context;
    List<MemberGroup> memberGroups;
    public MembersAdpater(List<MemberGroup> groups) {
        this.memberGroups = (groups != null) ? groups : new ArrayList<>();
    }
    @NonNull
    @Override
    public MembersAdpater.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_grupos,parent,false);
        context = parent.getContext();
        return new MembersAdpater.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MembersAdpater.ViewHolder holder, int position) {



        Glide.with(context)
                .load(memberGroups.get(position).getImg())
                .circleCrop()
                .into(holder.img);

        holder.name.setText(memberGroups.get(position).getName());

        holder.integrantes.setText(memberGroups.get(position).getRol());
    }

    @Override
    public int getItemCount() {
        return memberGroups.size();
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
