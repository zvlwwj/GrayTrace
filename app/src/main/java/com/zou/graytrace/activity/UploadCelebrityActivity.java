package com.zou.graytrace.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.mukesh.countrypicker.CountryPicker;
import com.mukesh.countrypicker.CountryPickerListener;
import com.zou.graytrace.R;
import com.zou.graytrace.Utils.Tools;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnFocusChange;

/**
 * Created by zou on 2018/1/18.
 * 上传界面
 */

public class UploadCelebrityActivity extends AppCompatActivity{
    @BindView(R.id.btn_select_cover)
    Button btn_select_cover;
    @BindView(R.id.iv_cover)
    ImageView iv_cover;
    @BindView(R.id.et_celebrity_name)
    EditText et_celebrity_name;
    @BindView(R.id.et_birthday)
    EditText et_birthday;
    @BindView(R.id.et_celebrity_nationality)
    EditText et_celebrity_nationality;
    @BindView(R.id.et_birth_place)
    EditText et_birth_place;
    @BindView(R.id.et_long_sleep_place)
    EditText et_long_sleep_place;
    @BindView(R.id.et_residence)
    EditText et_residence;
    @BindView(R.id.et_motto)
    EditText et_motto;
    @BindView(R.id.textInputLayout_celebrity_nationality)
    TextInputLayout textInputLayout_celebrity_nationality;
    CountryPicker picker;

    private static int TO_LOCAL_PIC = 100;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_celebrity);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        et_celebrity_nationality.setInputType(InputType.TYPE_NULL);
//        textInputLayout_celebrity_nationality.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//
//                return true;
//            }
//        });
        et_celebrity_nationality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"onTouch",Toast.LENGTH_SHORT).show();
            }
        });
//        et_celebrity_nationality.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                Toast.makeText(getApplicationContext(),"onTouch",Toast.LENGTH_SHORT).show();
//                return true;
//            }
//        });
    }

    /**
     * 选择封面
     */
    @OnClick(R.id.btn_select_cover)
    public void selectCover(){
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, TO_LOCAL_PIC);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            Uri uri = data.getData();
            Glide.with(this).load(uri).into(iv_cover);
        }
    }

    @OnFocusChange(R.id.et_celebrity_nationality)
    public void selectCountry(boolean focus){
        if(focus) {
            showCountryPicker();
        }
    }

    private void showCountryPicker() {
        if (picker == null) {
            picker = CountryPicker.newInstance(getResources().getString(R.string.select_country));
            picker.setListener(new CountryPickerListener() {
                @Override
                public void onSelectCountry(String name, String code, String dialCode, int flagDrawableResID) {
                    // Implement your code here
                    Drawable image = getResources().getDrawable(flagDrawableResID);
                    int h = image.getIntrinsicHeight();
                    int w = image.getIntrinsicWidth();
                    image.setBounds(0, 0, w / 2, h / 2);
                    et_celebrity_nationality.setCompoundDrawables(image, null, null, null);
                    et_celebrity_nationality.setCompoundDrawablePadding(Tools.dip2px(UploadCelebrityActivity.this, 5));
                    et_celebrity_nationality.setText(name);
                    picker.dismiss();
                }
            });
        }
        if (!picker.isVisible()) {
            picker.show(getSupportFragmentManager(), "COUNTRY_PICKER");
        }
    }
}