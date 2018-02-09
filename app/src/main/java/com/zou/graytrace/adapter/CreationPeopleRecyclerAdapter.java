package com.zou.graytrace.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zou.graytrace.R;
import com.zou.graytrace.bean.ItemCreationPeople;

import java.util.ArrayList;

/**
 * Created by zou on 2018/2/8.
 */

public class CreationPeopleRecyclerAdapter extends RecyclerView.Adapter<CreationPeopleRecyclerAdapter.ViewHolder>{
    private ArrayList<ItemCreationPeople> itemCreationPeoples;
    private Context context;
    public CreationPeopleRecyclerAdapter(Context context, ArrayList<ItemCreationPeople> itemCreationPeoples){
        this.itemCreationPeoples = itemCreationPeoples;
        this.context = context;
    }

    @Override
    public CreationPeopleRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
//        View v = View.inflate(parent.getContext(), R.layout.item_creation_people,null);
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_creation_people,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(CreationPeopleRecyclerAdapter.ViewHolder holder, int position) {
        ItemCreationPeople itemCreationPeople = itemCreationPeoples.get(position);
        String title = itemCreationPeople.getTitle();
        String content = itemCreationPeople.getContent();
        String cover_url = itemCreationPeople.getCover_url();
        if(title!=null) {
            holder.tv_item_creation_people_title.setText(title);
        }
        if(content!=null) {
            holder.tv_item_creation_people_content.setText(content);
        }
        if(cover_url!=null) {
            Glide.with(context).load(cover_url).into(holder.iv_item_creation_people_cover);
        }
    }

    @Override
    public int getItemCount() {
        return itemCreationPeoples.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_creation_people_title;
        TextView tv_item_creation_people_content;
        ImageView iv_item_creation_people_cover;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_creation_people_title = itemView.findViewById(R.id.tv_item_creation_people_title);
            tv_item_creation_people_content = itemView.findViewById(R.id.tv_item_creation_people_content);
            iv_item_creation_people_cover = itemView.findViewById(R.id.iv_item_creation_people_cover);
        }
    }
}
