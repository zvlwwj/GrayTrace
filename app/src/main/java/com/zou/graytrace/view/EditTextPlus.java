package com.zou.graytrace.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.style.ImageSpan;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.span.VerticalCenterImageSpan;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zou on 2018/1/25.
 * TODO 需要整理无用的代码
 * TODO 考虑多张图片同时添加的需求
 */

public class EditTextPlus extends android.support.v7.widget.AppCompatEditText {
    private static String TAG = "EditTextPlus";
    /**
     * 最大输入字符
     */
    public static final int MAXLENGTH = 2000;


    private Context mContext;
    private boolean insertImage = false;

    private OnDeleteContentListener onDeleteContentListener;

    private float startY;
    private float startX;

    public EditTextPlus(Context context) {
        super(context);
        mContext = context;
        init();
    }

    public EditTextPlus(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        init();
    }

    public void init() {
        setGravity(Gravity.TOP);
        addTextChangedListener(watcher);
    }

    /**
     * 添加图片
     *
     * @param
     */
    public void addImage(String path,String url,String fileName) {
        Editable editable = getText();
        if (path != null && !TextUtils.isEmpty(path)) {
            if (!TextUtils.isEmpty(getText().toString()) && !insertImage) {
                //如果第一张就是图片不用换行
                editable.insert(getSelectionStart(), "\n");
            } else if (getSelectionStart() < getText().length()) {
                //当从中间插入时
                editable.insert(getSelectionStart(), "\n");
            }
            CharSequence sequence = getDrawableStr(path,url+fileName);
            if (sequence != null) {
                editable.insert(getSelectionStart(), sequence);
                editable.insert(getSelectionStart(), "\n");
                insertImage = true;
            }
        } else {
            Toast.makeText(mContext, "图片路径为空", Toast.LENGTH_SHORT).show();
        }

        //让光标始终在最后
        this.setSelection(getText().toString().length());
    }

    /**
     * 添加video
     *
     * @param
     */
    public void addVideo(String url,Bitmap bm,String fileName) {
        Editable editable = getText();
        if (url != null && !TextUtils.isEmpty(url)) {
            if (!TextUtils.isEmpty(getText().toString()) && !insertImage) {
                //如果第一张就是图片不用换行
                editable.insert(getSelectionStart(), "\n");
            } else if (getSelectionStart() < getText().length()) {
                //当从中间插入时
                editable.insert(getSelectionStart(), "\n");
            }
            CharSequence sequence = getVideoDrawableStr(url+fileName,bm);
            if (sequence != null) {
                editable.insert(getSelectionStart(), sequence);
                editable.insert(getSelectionStart(), "\n");
                insertImage = true;
            }
        } else {
            Toast.makeText(mContext, "图片路径为空", Toast.LENGTH_SHORT).show();
        }

        //让光标始终在最后
        this.setSelection(getText().toString().length());
    }


    /**
     * 这个TextWatcher用来监听删除和输入的内容如果是图片的话 要相应把list集合中的图片也要移除 不然最后获取到的图片集合是错误的
     */
    private String tempString;

    private TextWatcher watcher = new TextWatcher() {
        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            insertImage = false;
            //如果小于就是删除操作
            if (s.length() < tempString.length()) {
                int end = start + before;
                Spanned spanned = getEditableText();
                ImageSpan[] imageSpans = spanned.getSpans(0,spanned.length(),ImageSpan.class);
                for (int i = imageSpans.length - 1; i >= 0; i--) {
                    int startImageSpan = spanned.getSpanStart(imageSpans[i]);
                    int endImageSpan = spanned.getSpanEnd(imageSpans[i]);
                    if(end<=endImageSpan&&end>startImageSpan||(start<=endImageSpan&&start>startImageSpan)) {
                        Editable et = getText();
                        String imageSpan = et.toString().substring(startImageSpan,endImageSpan);
                        String url = imageSpan.substring(12,imageSpan.length()-3);
                        et.delete(startImageSpan, endImageSpan);
                        Log.i(TAG,"url : "+url);
                        if(onDeleteContentListener!=null){
                            onDeleteContentListener.onDrawableDeleted(url);
                        }
                    }
                }
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            tempString = s.toString();
        }

        @Override
        public void afterTextChanged(Editable s) {
            invalidate();
            requestLayout();
        }
    };

    /**
     * 编辑插入的内容
     *
     * @param picPath
     * @return
     */
    private CharSequence getDrawableStr(String picPath,String url) {
        String str = "<img src=\"" + url + "\"/>";
        Bitmap bm = createImageThumbnail(picPath);
        final SpannableString ss = new SpannableString(str);
        // 定义插入图片
        Drawable drawable = new BitmapDrawable(bm);

        float scenewidth = Tools.getScreenWidth(mContext) / 3;//TODO
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        if (width > scenewidth) {
            width = width - 20;
            height = height - 20;
        } else {
            float scale = (scenewidth) / width;
            width *= scale;
            height *= scale;
        }

        //设置图片的宽高
        drawable.setBounds(2, 0, (int) width, (int) height);
        //ALIGN_BOTTOM 调整图片距离字有一定的间隙
        VerticalCenterImageSpan span = new VerticalCenterImageSpan(drawable, 1);
        //SPAN_INCLUSIVE_EXCLUSIVE 会导致删除后面的文字消失
        ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
      /*
      Spannable.SPAN_EXCLUSIVE_EXCLUSIVE：前后都不包括，即在指定范围的前面和后面插入新字符都不会应用新样式
      Spannable.SPAN_EXCLUSIVE_INCLUSIVE：前面不包括，后面包括。即仅在范围字符的后面插入新字符时会应用新样式
      Spannable.SPAN_INCLUSIVE_EXCLUSIVE：前面包括，后面不包括。
      Spannable.SPAN_INCLUSIVE_INCLUSIVE：前后都包括。
       */
        return ss;
    }


    private CharSequence getVideoDrawableStr(String url,Bitmap bm) {
        String str = "<video src=\"" + url + "\"/>";
        final SpannableString ss = new SpannableString(str);
        // 定义插入图片
        View spanView = View.inflate(getContext(), R.layout.span_video,null);

        ImageView iv_span_video = spanView.findViewById(R.id.iv_span_video);
        ImageView iv_span_play = spanView.findViewById(R.id.iv_span_play);
        View bg_view = spanView.findViewById(R.id.bg_span_video);
        bg_view.setLayoutParams(new FrameLayout.LayoutParams(getWidth(),Tools.dip2px(getContext(),200)));
        iv_span_video.setImageBitmap(bm);
        Bitmap bitmap = Tools.convertView2Bitmap(spanView);
        Drawable drawable = new BitmapDrawable(bitmap);

        float scenewidth = Tools.getScreenWidth(mContext) ;//TODO
        float width = drawable.getIntrinsicWidth();
        float height = drawable.getIntrinsicHeight();
        if (width > scenewidth) {
            width = width - 20;
            height = height - 20;
        } else {
            float scale = (scenewidth) / width;
            width *= scale;
            height *= scale;
        }

        //设置图片的宽高
        drawable.setBounds(2, 0, (int) width, (int) height);
        //ALIGN_BOTTOM 调整图片距离字有一定的间隙
        VerticalCenterImageSpan span = new VerticalCenterImageSpan(drawable, 1);
        //SPAN_INCLUSIVE_EXCLUSIVE 会导致删除后面的文字消失
        ss.setSpan(span, 0, ss.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return ss;
    }



    /**
     * 创建图片
     * @param filePath
     * @return
     */
    public static Bitmap createImageThumbnail(String filePath) {
        Bitmap bitmap = null;
        BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inTempStorage = new byte[100 * 1024];
        // 默认是Bitmap.Config.ARGB_8888
        opts.inPreferredConfig = Bitmap.Config.RGB_565;
        opts.inSampleSize = 2;
        try {
            bitmap = BitmapFactory.decodeFile(filePath, opts);
        } catch (Exception e) {
        }
        return bitmap;
    }


    /**
     * 重写dispatchTouchEvent是为了解决上下滑动时光标跳跃的问题
     *
     * @param event
     * @return
     */
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                startY = event.getRawY();
                startX = event.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                float endY = event.getRawY();
                float endX = event.getRawX();

                if (Math.abs(endY - startY) > 10 || Math.abs(endX - startX) > 10) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    public interface OnDeleteContentListener{
        void onDrawableDeleted(String url);
    }

    public void setOnDeleteContentListener(OnDeleteContentListener onDeleteContentListener){
        this.onDeleteContentListener = onDeleteContentListener;
    }
}
