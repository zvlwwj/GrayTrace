package com.zou.graytrace.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import wseemann.media.FFmpegMediaMetadataRetriever;

/**
 * Created by zou on 2018/3/28.
 */

public class RichTextViewContainer extends LinearLayout{
    private Pattern pattern;
    private Context mContext;
    private FFmpegMediaMetadataRetriever fmmr;
    private OnSelectMediaListener onSelectMediaListener;
    public RichTextViewContainer(Context context) {
        super(context);
        mContext = context;
        init();
    }
    public RichTextViewContainer(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public RichTextViewContainer(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        init();
    }

    private void init() {
        setOrientation(VERTICAL);
        String regex = "(<img src=\".*\"/>)|(<video src=\".*\"/>)";
        pattern = Pattern.compile(regex);
        fmmr = new FFmpegMediaMetadataRetriever();
    }

    public void setSpanString(CharSequence text){
        final Matcher matcher = pattern.matcher(text);
        String[] strs = pattern.split(text);
        for(String str:strs){
            TextView tv = new TextView(mContext);
            tv.setText(str);
            addView(tv);
            if(matcher.find()) {
                View v = null;
                String media_str = matcher.group();
                int type = 0;
                String url = null;
                if(Pattern.matches("(<img src=\".*\"/>)",media_str)){
                    ImageView iv = new ImageView(mContext);
                    addView(iv);
                    url = media_str.substring(10,media_str.length()-3);
                    Glide.with(mContext).load(url).into(iv);
                    type = OnSelectMediaListener.IMG;
                    v = iv;
                }else if(Pattern.matches("(<video src=\".*\"/>)",media_str)){
                    View spanView = View.inflate(getContext(), R.layout.span_video,null);
                    ImageView iv_span_video = spanView.findViewById(R.id.iv_span_video);
                    addView(spanView);
                    url = media_str.substring(12,media_str.length()-3);
                    fmmr.setDataSource(url);
                    Bitmap bitmap =fmmr.getFrameAtTime(1000);
                    Glide.with(mContext).load(Tools.bitmapToByte(bitmap)).asBitmap().into(iv_span_video);
                    type = OnSelectMediaListener.VIDEO;
                    v = spanView;
                }
                final String finalUrl = url;
                final int finalType = type;
                if(v!=null) {
                    v.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (onSelectMediaListener != null) {
                                onSelectMediaListener.OnSelected(finalType, finalUrl);
                            }
                        }
                    });
                }
            }
        }
    }

    public interface OnSelectMediaListener{
        public static final int IMG = 1;
        public static final int VIDEO = 2;
        void OnSelected(int type,String url);
    }

    public void setOnSelectMediaListener(OnSelectMediaListener onSelectMediaListener){
        this.onSelectMediaListener = onSelectMediaListener;
    }
}
