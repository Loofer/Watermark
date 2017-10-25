package org.loofer.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.loofer.utils.ToastUtils;
import org.loofer.watermark.MainActivity;
import org.loofer.watermark.R;

import java.io.File;

public class SelectPicActivity extends AppCompatActivity implements View.OnClickListener {


    Button btnTake;
    Button btnSelect;
    private Button btnWaterMarker;
    ImageView ivShow;

    File photoFile;
    public static final String CROP_PIC_PATH = "crop_image";
    private boolean isGetPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);
        btnTake = (Button) findViewById(R.id.btn_take);
        btnSelect = (Button) findViewById(R.id.btn_select);
        btnWaterMarker = (Button) findViewById(R.id.btn_watermarker);
        ivShow = (ImageView) findViewById(R.id.iv_show);

        photoFile = new File(getExternalFilesDir("img"), "scan.jpg");

        btnTake.setOnClickListener(this);
        btnSelect.setOnClickListener(this);
        btnWaterMarker.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take:
                startActivityForResult(CropActivity.getJumpIntent(SelectPicActivity.this, false, photoFile), 100);
                break;
            case R.id.btn_select:
                startActivityForResult(CropActivity.getJumpIntent(SelectPicActivity.this, true, photoFile), 100);
                break;
            case R.id.btn_watermarker:
                if (isGetPic) {
                    Intent intent = new Intent(SelectPicActivity.this, MainActivity.class);
                    intent.putExtra(CROP_PIC_PATH, photoFile.getPath());
                    startActivity(intent);
                } else {
                    ToastUtils.showToast(this, "请先拍照或选择图片");
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            isGetPic = false;
            return;
        }
        if (requestCode == 100 && photoFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getPath());
            ivShow.setImageBitmap(bitmap);
            isGetPic = true;
        }
    }

}
