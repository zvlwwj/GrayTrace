package com.zou.graytrace.activity;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ImageSpan;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zou on 2018/1/22.
 */

/**
 * TODO 1.退出Activity动画应该和回退时的不一样
 *      2.编辑时删除图片，要点好多下删除键
 */


public class EditEventsActivity extends AppCompatActivity {
    private static final int ADD_IMAGE = 100;
    private static final int ADD_VIDEO = 101;
    @BindView(R.id.toolbar_upload_edit_events)
    Toolbar toolbar_upload_edit_events;
    @BindView(R.id.et_event_title)
    EditText et_event_title;
    @BindView(R.id.et_event_content)
    EditText et_event_content;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_events);
        MPermissions.requestPermissions(this, 4, Manifest.permission.READ_EXTERNAL_STORAGE);

    }

    private void initView() {
        setSupportActionBar(toolbar_upload_edit_events);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_commit,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                //TODO 保存到草稿 返回主界面
                finish();
                break;
            case R.id.action_menu_commit:
                //TODO 提交
                Toast.makeText(getApplicationContext(),"提交",Toast.LENGTH_SHORT).show();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * 添加视频
     */
    @OnClick(R.id.iv_add_video)
    public void addVideo(){
        if(et_event_content.isFocused()) {

            Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, ADD_VIDEO);
        }
    }

    /**
     * 权限请求成功
     */
    @PermissionGrant(4)
    public void requestPermissSuccess(){
        ButterKnife.bind(this);
        initView();
    }

    /**
     * 权限请求失败
     */
    @PermissionDenied(4)
    public void requestPermissFail(){
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermissions.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * 添加图片
     */
    @OnClick(R.id.iv_add_image)
    public void addImage(){
        if(et_event_content.isFocused()) {
            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.addCategory(Intent.CATEGORY_OPENABLE);
            intent.setType("image/*");
            startActivityForResult(intent, ADD_IMAGE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case ADD_IMAGE:
                if (resultCode == RESULT_OK ) {
                    // 获得图片的uri
                    Uri originalUri = data.getData();
                    try {
                        Bitmap originalBitmap = Tools.getFitSampleBitmap(getContentResolver().openInputStream(originalUri),Tools.dip2px(this,300),Tools.dip2px(this,150));
                        // 将原始图片的bitmap转换为文件
                        // 上传该文件并获取url
                        insertPic(originalBitmap);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_VIDEO:
                if(resultCode == RESULT_OK){
                    Uri originalUri = data.getData();
                    Bitmap bm = Tools.getVideoThumbnail(getContentResolver(),originalUri);
                    insertVideo(bm);
                }
                break;
        }

    }

    /**
     * 插入video
     */
    private void insertVideo(Bitmap bm) {

        View spanView = View.inflate(this,R.layout.span_video,null);

        ImageView iv_span_video = spanView.findViewById(R.id.iv_span_video);
        ImageView iv_span_play = spanView.findViewById(R.id.iv_span_play);
//        Glide.with(this).load(originalUri).into(iv_span_video);
        iv_span_video.setImageBitmap(bm);

        BitmapDrawable drawable = Tools.convertView2BitmapDrawable(spanView);
        drawable.setBounds(0,0,Tools.dip2px(this,300),Tools.dip2px(this,150));


        ImageSpan imageSpan = new ImageSpan(drawable);
        String tempUrl = "<img src=\"" + "img" + "\" />";
        SpannableString spannableString = new SpannableString(tempUrl);
        // 用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将选择的图片追加到EditText中光标所在位置
        int index = et_event_content.getSelectionStart(); // 获取光标所在位置
        Editable edit_text = et_event_content.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append("\n");
            edit_text.append(spannableString);
            edit_text.append("\n");
        } else {
            edit_text.insert(index, "\n");
            edit_text.insert(index, spannableString);
            edit_text.insert(index, "\n");
        }
    }

    /**
     * 插入图片
     */
    private void insertPic(Bitmap bm) {
        // 根据Bitmap对象创建ImageSpan对象
        ImageSpan imageSpan = new ImageSpan(this, bm);
        // 创建一个SpannableString对象，以便插入用ImageSpan对象封装的图像
        String tempUrl = "<img src=\"" + "img" + "\" />";
        SpannableString spannableString = new SpannableString(tempUrl);
        // 用ImageSpan对象替换你指定的字符串
        spannableString.setSpan(imageSpan, 0, tempUrl.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        // 将选择的图片追加到EditText中光标所在位置
        int index = et_event_content.getSelectionStart(); // 获取光标所在位置
        Editable edit_text = et_event_content.getEditableText();
        if (index < 0 || index >= edit_text.length()) {
            edit_text.append("\n");
            edit_text.append(spannableString);
            edit_text.append("\n");
        } else {
            edit_text.insert(index, "\n");
            edit_text.insert(index, spannableString);
            edit_text.insert(index, "\n");
        }
    }
}
