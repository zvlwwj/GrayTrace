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
import com.zou.graytrace.bean.ItemPeopleSample;
import com.zou.graytrace.bean.ItemSampleBean;

import java.util.ArrayList;

/**
 * Created by zou on 2018/2/8.
 */

public class MainRecyclerAdapter extends RecyclerView.Adapter<MainRecyclerAdapter.ViewHolder> implements View.OnClickListener{
    private ArrayList<ItemSampleBean> itemSampleBeans;
    private Context context;
    private OnItemClickListener mOnItemClickListener = null;
    public MainRecyclerAdapter(Context context, ArrayList<ItemSampleBean> itemSampleBeans){
        this.itemSampleBeans = itemSampleBeans;
        this.context = context;
    }

    @Override
    public MainRecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate( R.layout.item_sample_bean,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(this);
        return viewHolder;
    }



    @Override
    public void onBindViewHolder(MainRecyclerAdapter.ViewHolder holder, int position) {
        ItemSampleBean itemSampleBean = itemSampleBeans.get(position);
        String title = itemSampleBean.getTitle();
        String content = itemSampleBean.getContent();
        String cover_url = itemSampleBean.getCover_url();
        if(title!=null) {
            holder.tv_item_sample_bean_title.setText(title);
        }
        if(content!=null) {
            holder.tv_item_sample_bean_content.setText(content);
        }
        if(cover_url!=null) {
            Glide.with(context).load(cover_url).into(holder.iv_item_sample_bean_cover);
        }
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return itemSampleBeans.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_sample_bean_title;
        TextView tv_item_sample_bean_content;
        ImageView iv_item_sample_bean_cover;
        public ViewHolder(View itemView) {
            super(itemView);
            tv_item_sample_bean_title = itemView.findViewById(R.id.tv_item_sample_bean_title);
            tv_item_sample_bean_content = itemView.findViewById(R.id.tv_item_sample_bean_content);
            iv_item_sample_bean_cover = itemView.findViewById(R.id.iv_item_sample_bean_cover);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.mOnItemClickListener = listener;
    }
}
