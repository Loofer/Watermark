package org.loofer.watermark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;

import org.loofer.utils.ToastUtils;
import org.loofer.utils.Utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.loofer.photo.SelectPicActivity.CROP_PIC_PATH;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private SeekBar mSeekBar;
    private ImageUtil mImageUtil;
    private ImageView mImageViewH;
    private String picPath;
    private Bitmap srcBitmap;
    private Button mBtnSave;

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
        mBtnSave = (Button) findViewById(R.id.btn_save);
        mBtnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveImage();
            }
        });
        mImageViewH = (ImageView) findViewById(R.id.iv_horizontal);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mImageUtil = new ImageUtil();
//        mSeekBar.setMax(180);
        setWaterMask(45);
//        setHWaterMask(45);
    }

    public void saveImage() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //创建一个文件夹对象，赋值为外部存储器的目录
            File sdcardDir = Environment.getExternalStorageDirectory();
            //得到一个路径，内容是sdcard的文件夹路径和名字
            String path = sdcardDir.getPath() + "/WaterMarker";
            File path1 = new File(path);
            if (!path1.exists()) {
                //若不存在，创建目录，可以在应用启动的时候创建
                path1.mkdirs();
            }

            System.out.println(path);
            long time = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = new Date(time);
            String t1 = format.format(d1);
            File file = new File(path, t1 + "-marked.jpg");
            ToastUtils.showToast(MainActivity.this, "文件已保存到：" + file.getPath());
            mImageView.setDrawingCacheEnabled(true);
            Bitmap bm = mImageView.getDrawingCache();
            BufferedOutputStream bos;

            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }


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
