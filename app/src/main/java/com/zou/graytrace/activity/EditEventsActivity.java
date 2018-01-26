package com.zou.graytrace.activity;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.zhy.m.permission.MPermissions;
import com.zhy.m.permission.PermissionDenied;
import com.zhy.m.permission.PermissionGrant;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;
import com.zou.graytrace.view.EditTextPlus;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by zou on 2018/1/22.
 */

/**
 * TODO 1.退出Activity动画应该和回退时的不一样
 *      2.android8.0无法找到video路径，找不到解决方案！
 */


public class EditEventsActivity extends AppCompatActivity {
    private static final int ADD_IMAGE = 100;
    private static final int ADD_VIDEO = 101;
    @BindView(R.id.toolbar_upload_edit_events)
    Toolbar toolbar_upload_edit_events;
    @BindView(R.id.et_event_title)
    EditText et_event_title;
    @BindView(R.id.et_event_content)
    EditTextPlus et_event_content;
    private static final String TAG="EditEventsActivity";

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
                String text = et_event_content.getText().toString();
                Log.i(TAG,text);
                Log.i(TAG,"SelectionStart : "+et_event_content.getSelectionStart());
                Log.i(TAG,"SelectionEnd : "+et_event_content.getSelectionEnd());
                Log.i(TAG,"SelectedText : "+text.substring(et_event_content.getSelectionStart(),et_event_content.getSelectionEnd()));
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
//                        Bitmap originalBitmap = Tools.getFitSampleBitmap(getContentResolver().openInputStream(originalUri),Tools.dip2px(this,300),Tools.dip2px(this,150));
                        // 将原始图片的bitmap转换为文件
                        // 上传该文件并获取url
//                        List<String> listPath = new ArrayList<>();
//                        listPath.add();
                        et_event_content.addImage(Tools.getPathFromUri(EditEventsActivity.this,originalUri));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                break;
            case ADD_VIDEO:
                if(resultCode == RESULT_OK){
                    Uri originalUri = data.getData();
                    Bitmap bm = Tools.getVideoThumbnail(this,originalUri,et_event_content.getWidth(),Tools.dip2px(getApplicationContext(),200));
                    et_event_content.addVideo(originalUri.getPath(),bm);
                }
                break;
        }

    }
}
