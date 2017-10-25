package org.loofer.watermark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.loofer.utils.ToastUtils;
import org.loofer.utils.Utils;

import static org.loofer.photo.SelectPicActivity.CROP_PIC_PATH;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private SeekBar mSeekBar;
    private ImageUtil mImageUtil;
    private ImageView mImageViewH;
    private String picPath;
    private Bitmap srcBitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        picPath = extras.getString(CROP_PIC_PATH);
        srcBitmap = BitmapFactory.decodeFile(picPath);
        initView();
    }

    private void initView() {
        mImageView = (ImageView) findViewById(R.id.iv_bg);
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mImageViewH = (ImageView) findViewById(R.id.iv_horizontal);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mImageUtil = new ImageUtil();
//        mSeekBar.setMax(180);
        setWaterMask(45);
//        setHWaterMask(45);
    }

    private void setWaterMask(int degress) {

        Bitmap markTextBitmap = mImageUtil.getMarkTextBitmap(this, "我是水印", srcBitmap.getWidth(), srcBitmap.getHeight(), true, degress);
        Bitmap waterMaskBitmap = mImageUtil.createWaterMaskBitmap(srcBitmap, markTextBitmap, 0, 0);
        mImageView.setImageBitmap(waterMaskBitmap);
    }

    private void setHWaterMask(int degress) {
        Bitmap srcBitmap = Utils.getImageFromAssetsFile(this, "srcHImage.png");
        Bitmap markTextBitmap = mImageUtil.getMarkTextBitmap(this, "我是水印", srcBitmap.getWidth(), srcBitmap.getHeight(), true, degress);
        Bitmap waterMaskBitmap = mImageUtil.createWaterMaskBitmap(srcBitmap, markTextBitmap, 0, 0);
        mImageViewH.setImageBitmap(waterMaskBitmap);
    }


    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ToastUtils.showToast(MainActivity.this, progress + "");
            setWaterMask(progress);
//            setHWaterMask(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

}
