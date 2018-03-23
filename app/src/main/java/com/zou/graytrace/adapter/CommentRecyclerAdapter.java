package com.zou.graytrace.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zou.graytrace.R;
import com.zou.graytrace.bean.CommentBean;
import com.zou.graytrace.bean.GsonGetCommentResultBean;

import java.util.ArrayList;

/**
 * Created by zou on 2018/3/19.
 */

//TODO 点赞功能+查看对话的功能
public class CommentRecyclerAdapter extends RecyclerView.Adapter<CommentRecyclerAdapter.ViewHolder>implements View.OnClickListener{
    private ArrayList<CommentBean> commentBeans;
    private Context context;
    private OnItemClickListener mOnItemClickListener = null;
    private OnUpvoteListener mOnUpvoteListener = null;
    private OnLookConversationClickListener mOnLookConversationClickListener = null;
    //是否是查看对话界面的list,如果是，则隐藏查看对话的按钮
    private boolean look_conversation = false;

    public CommentRecyclerAdapter(ArrayList<CommentBean> commentBeans,Context context, boolean look_conversation){
        this.commentBeans = commentBeans;
        this.context = context;
        this.look_conversation = look_conversation;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.item_comment,parent,false);
        ViewHolder viewHolder = new ViewHolder(v);
        v.setOnClickListener(this);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, final int position) {
        boolean hasconversation = false;
        CommentBean commentBean = commentBeans.get(position);
        String avatar_url = commentBean.getAvatar_url();
        int upvote = commentBean.getUpvote_count();
        String nickName = commentBean.getNick_name();
        String text = commentBean.getText();
        String time = commentBean.getTime_stamp();
        if(avatar_url!=null) {
            Glide.with(context).load(avatar_url).into(holder.iv_avatar);
        }
        holder.tv_upvote_count.setText(String.valueOf(upvote));
        holder.tv_comment_nick.setText(nickName);
        if(commentBean.getReply_id()!=-1){
            for(CommentBean comment:commentBeans){
                if(comment.getComment_id() == commentBean.getReply_id()){
                    holder.tv_comment_content.setText(context.getString(R.string.reply)+" "+comment.getNick_name()+":"+text);
                }
            }
        }else {
            holder.tv_comment_content.setText(text);
        }
        holder.tv_comment_time.setText(time);
        if(!look_conversation) {
            //该item是否有对话
            for (CommentBean comment : commentBeans) {
                if (comment.getReply_id() == commentBean.getComment_id()) {
                    hasconversation = true;
                }
            }
            if (commentBean.getReply_id() != -1) {
                hasconversation = true;
            }
            if (hasconversation) {
                holder.tv_look_conversation.setVisibility(View.VISIBLE);
            } else {
                holder.tv_look_conversation.setVisibility(View.GONE);
            }
            holder.tv_look_conversation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mOnLookConversationClickListener != null) {
                        mOnLookConversationClickListener.onLookConversationClick(position);
                    }
                }
            });
        }else{
            //隐藏查看对话的按钮
            holder.tv_look_conversation.setVisibility(View.GONE);
        }
        if(commentBean.is_upvote()){
            holder.iv_upvote.setSelected(true);
            holder.tv_upvote_count.setSelected(true);
        }else{
            holder.iv_upvote.setSelected(false);
            holder.tv_upvote_count.setSelected(false);
        }
        holder.ll_upvote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO 点赞
                holder.iv_upvote.setSelected(!holder.iv_upvote.isSelected());
                holder.tv_upvote_count.setSelected(!holder.tv_upvote_count.isSelected());
                int count = Integer.parseInt(holder.tv_upvote_count.getText().toString());
                if(holder.tv_upvote_count.isSelected()){
                    count ++ ;
                }else{
                    count --;
                }
                holder.tv_upvote_count.setText(String.valueOf(count));

                if(mOnUpvoteListener!=null){
                    mOnUpvoteListener.onUpvote(position,holder.iv_upvote.isSelected());
                }
            }
        });
        holder.itemView.setTag(position);
    }

    @Override
    public int getItemCount() {
        return commentBeans.size();
    }

    @Override
    public void onClick(View v) {
        if (mOnItemClickListener != null) {
            //注意这里使用getTag方法获取position
            mOnItemClickListener.onItemClick(v,(int)v.getTag());
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView iv_avatar;
        public ImageView iv_upvote;
        public TextView tv_comment_nick,tv_comment_content,tv_comment_time,tv_upvote_count,tv_look_conversation;
        public LinearLayout ll_upvote;
        ViewHolder(View itemView) {
            super(itemView);
            iv_avatar = itemView.findViewById(R.id.iv_avatar);
            tv_comment_nick = itemView.findViewById(R.id.tv_comment_nick);
            tv_comment_content = itemView.findViewById(R.id.tv_comment_content);
            tv_comment_time = itemView.findViewById(R.id.tv_comment_time);
            iv_upvote = itemView.findViewById(R.id.iv_upvote);
            tv_upvote_count = itemView.findViewById(R.id.tv_upvote_count);
            tv_look_conversation = itemView.findViewById(R.id.tv_look_conversation);
            ll_upvote = itemView.findViewById(R.id.ll_upvote);
        }
    }

    public interface OnItemClickListener {
        void onItemClick(View view , int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mOnItemClickListener = listener;
    }

    public interface OnUpvoteListener{
        void onUpvote(int position,boolean isChecked);
    }

    public void setOnUpvoteListener(OnUpvoteListener listener){
        mOnUpvoteListener = listener;
    }

    public interface OnLookConversationClickListener{
        void onLookConversationClick(int position);
    }

    public void setOnLookConversationClickListener(OnLookConversationClickListener onLookConversationClickListener){
        mOnLookConversationClickListener = onLookConversationClickListener;
    }
}
