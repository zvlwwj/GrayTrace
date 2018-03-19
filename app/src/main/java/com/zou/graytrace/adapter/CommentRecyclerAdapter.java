package com.zou.graytrace.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.zou.graytrace.R;
import com.zou.graytrace.bean.GsonGetCommentResultBean;

import java.util.ArrayList;

/**
 * Created by zou on 2018/3/19.
 */

public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>{
    private ArrayList<GsonGetCommentResultBean> gsonGetCommentResultBeans;
    private Context context;

    public CommentRecyclerAdapter(ArrayList<GsonGetCommentResultBean> gsonGetCommentResultBeans,Context context){
        this.gsonGetCommentResultBeans = gsonGetCommentResultBeans;
        this.context = context;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        return null;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

    }

    @Override
    public int getItemCount() {
        return 0;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ViewHolder(View itemView) {
            super(itemView);

        }
    }
}
