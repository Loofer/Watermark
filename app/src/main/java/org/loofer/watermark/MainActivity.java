package org.loofer.watermark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import org.loofer.utils.ScreenUtils;
import org.loofer.utils.ToastUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;


import cn.finalteam.rxgalleryfinal.utils.Logger;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.loofer.photo.SelectPicActivity.CROP_PIC_PATH;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private SeekBar mSeekBar;
    private SeekBar mSeekBarAlpha;
    private ImageUtil mImageUtil;
    private String picPath;
    private Bitmap srcBitmap;
    private TextView mTvSize;
    private EditText mEtWaterMarker;
    private FillGridView mGridView;
    private ColorGridAdapter mColorGridAdapter;
    private Toolbar mToolBar;

    private PercentLinearLayout mPllDirection;
    private LinearLayout mPllStyle;
    private PercentLinearLayout mPllOthor;
    private TabLayout mTabLayout;
    //    private MagicIndicator mIndicator;

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
        initIndicator();

        mPllDirection = (PercentLinearLayout) findViewById(R.id.pll_direction);
        mPllStyle = (LinearLayout) findViewById(R.id.pll_style);
        mPllOthor = (PercentLinearLayout) findViewById(R.id.pll_othor);

        mImageView = (ImageView) findViewById(R.id.iv_bg);
        mToolBar = (Toolbar) findViewById(R.id.common_toolbar);
        mToolBar.inflateMenu(R.menu.menu_main);
        mToolBar.setOnMenuItemClickListener(mOnMenuItemClickListener);

        mGridView = (FillGridView) findViewById(R.id.grid_color);
        mColorGridAdapter = new ColorGridAdapter(MainActivity.this);
        mColorGridAdapter.setOnColorItemClickListener(mOnColorItemClickListener);
        mGridView.setAdapter(mColorGridAdapter);
        mGridView.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));

        mEtWaterMarker = (EditText) findViewById(R.id.et_watermarker);
        mEtWaterMarker.addTextChangedListener(mTextWatcher);
        if (srcBitmap.getWidth() > srcBitmap.getHeight()) {
            mImageView.setMaxWidth(ScreenUtils.getScreenWidth(this));
            mImageView.setMaxHeight((int) (ScreenUtils.getScreenWidth(this) * 5.0 / 8.0 + 0.5));
        }
        mTvSize = (TextView) findViewById(R.id.tv_size);

        mTvSize.setText("w：" + srcBitmap.getWidth() + "h：" + srcBitmap.getHeight());
        mSeekBar = (SeekBar) findViewById(R.id.seekBar_degree);
        mSeekBarAlpha = (SeekBar) findViewById(R.id.seekBar_alpha);
        mSeekBar.setOnSeekBarChangeListener(mOnSeekBarChangeListener);
        mSeekBarAlpha.setOnSeekBarChangeListener(mOnSeekBarAlphaChangeListener);
        mImageUtil = new ImageUtil();
        setWaterMask(45, 255, Color.parseColor("#FF1744"));
    }

    private void initIndicator() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("方向大小"));
        mTabLayout.addTab(mTabLayout.newTab().setText("样式"));
        mTabLayout.addTab(mTabLayout.newTab().setText("其他设置"));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    public boolean saveImage() {
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
            mImageView.setDrawingCacheEnabled(true);
            Bitmap bm = mImageView.getDrawingCache();
            BufferedOutputStream bos;

            try {
                bos = new BufferedOutputStream(new FileOutputStream(file));
                bm.compress(Bitmap.CompressFormat.PNG, 100, bos);
                bos.flush();
                bos.close();
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;

    }

    private void setWaterMask(String strWaterMarker, int degress, int alpha) {
    private void setWaterMask(int degress, int alpha, int color) {

        Bitmap markTextBitmap = mImageUtil.getMarkTextBitmap(this, strWaterMarker, srcBitmap.getWidth(), srcBitmap.getHeight(), 18, 25, Color.WHITE, alpha, degress);
        Bitmap waterMaskBitmap = mImageUtil.createWaterMaskBitmap(srcBitmap, markTextBitmap, 0, 0);
        mImageView.setImageBitmap(waterMaskBitmap);
    }


    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            setWaterMask(s.toString(), mSeekBar.getProgress(), mSeekBarAlpha.getProgress());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //文字方向
    SeekBar.OnSeekBarChangeListener mOnSeekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ToastUtils.showToast(MainActivity.this, progress + "");
            setWaterMask(mEtWaterMarker.getText().toString(), progress, mSeekBarAlpha.getProgress());
            int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            setWaterMask(progress, mSeekBarAlpha.getProgress(), color);
//            setHWaterMask(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };
    //透明度
    SeekBar.OnSeekBarChangeListener mOnSeekBarAlphaChangeListener = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            ToastUtils.showToast(MainActivity.this, progress + "");
            setWaterMask(mEtWaterMarker.getText().toString(), mSeekBar.getProgress(), progress);
            int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            setWaterMask(mSeekBar.getProgress(), progress, color);
//            setHWaterMask(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    //颜色选择的 adapter
    ColorGridAdapter.OnColorItemClickListener mOnColorItemClickListener = new ColorGridAdapter.OnColorItemClickListener() {
        @Override
        public void onColorItemClick(View v) {
            if (v.getTag() != null) {
                final String[] tag = ((String) v.getTag()).split(":");
                final int index = Integer.parseInt(tag[0]);
                final int color = Integer.parseInt(tag[1]);
                SPUtils.put(MainActivity.this, COLOR_CHOOSE_INDEX, index);
                SPUtils.put(MainActivity.this, COLOR_CHOOSE, color);
                mColorGridAdapter.notifyDataSetChanged();
                setWaterMask(mSeekBar.getProgress(), mSeekBarAlpha.getProgress(), color);
            }
        }
    };

    //指示器
    TabLayout.OnTabSelectedListener mOnTabSelectedListener = new TabLayout.OnTabSelectedListener() {
        @Override
        public void onTabSelected(TabLayout.Tab tab) {
            switch (tab.getPosition()) {
                case 0:
                    mPllDirection.setVisibility(View.VISIBLE);
                    mPllStyle.setVisibility(View.GONE);
                    mPllOthor.setVisibility(View.GONE);
                    break;
                case 1:
                    mPllDirection.setVisibility(View.GONE);
                    mPllStyle.setVisibility(View.VISIBLE);
                    mPllOthor.setVisibility(View.GONE);
                    break;
                case 2:
                    mPllDirection.setVisibility(View.GONE);
                    mPllStyle.setVisibility(View.GONE);
                    mPllOthor.setVisibility(View.VISIBLE);
                    break;
            }
        }

        @Override
        public void onTabUnselected(TabLayout.Tab tab) {

        }

        @Override
        public void onTabReselected(TabLayout.Tab tab) {

        }
    };


    Toolbar.OnMenuItemClickListener mOnMenuItemClickListener = new Toolbar.OnMenuItemClickListener() {
        @Override
        public boolean onMenuItemClick(MenuItem item) {

            switch (item.getItemId()) {
                case R.id.action_save:
                    ToastUtils.showToast(MainActivity.this, "保存");
                    Flowable.just(1)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .map(new Function<Integer, Boolean>() {
                                @Override
                                public Boolean apply(@NonNull Integer integer) throws Exception {
                                    Looper.prepare();
                                    // TODO: 2017/10/27 loading
                                    ToastUtils.showToast(MainActivity.this, "正在保存文件请稍等");
                                    Looper.loop();
                                    return saveImage();
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(@NonNull Boolean b) throws Exception {
                                    // TODO: 2017/10/27 hide loading
                                    if (b) {
                                        ToastUtils.showToast(MainActivity.this, "文件已保存");
                                        Logger.d("文件保存成功");
                                    } else {
                                        ToastUtils.showToast(MainActivity.this, "保存文件失败");
                                        Logger.d("文件保存失败");
                                    }
                                }
                            }, new Consumer<Throwable>() {
                                @Override
                                public void accept(@NonNull Throwable throwable) throws Exception {
                                    // TODO: 2017/10/27 hide loading
                                    ToastUtils.showToast(MainActivity.this, "保存文件失败");
                                    Logger.d(throwable.getMessage());
                                }
                            });

                    break;
            }

            return true;
        }
    };


}
