package org.loofer.watermark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.os.Looper;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.loofer.utils.SPUtils;
import org.loofer.utils.ScreenUtils;
import org.loofer.utils.ToastUtils;
import org.loofer.view.ColorGridAdapter;
import org.loofer.view.FillGridView;

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

import static org.loofer.ui.photo.SelectPicActivity.CROP_PIC_PATH;
import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE;
import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE_INDEX;

public class MainActivity extends AppCompatActivity {

    private ImageView mImageView;
    private BubbleSeekBar mSeekBarDrection;
    private BubbleSeekBar mSeekBarAlpha;
    private ImageUtil mImageUtil;
    private String picPath;
    private Bitmap srcBitmap;
    //    private TextView mTvSize;
    private AppCompatEditText mEtWaterMarker;
    private FillGridView mGridView;
    private ColorGridAdapter mColorGridAdapter;
    private Toolbar mToolBar;

    private PercentLinearLayout mPllDirection;
    private LinearLayout mPllStyle;
    private PercentLinearLayout mPllOthor;
    private TabLayout mTabLayout;
    private TextView mTvTitle;
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
        initToolbar();

        mPllDirection = (PercentLinearLayout) findViewById(R.id.pll_direction);
        mPllStyle = (LinearLayout) findViewById(R.id.pll_style);
        mPllOthor = (PercentLinearLayout) findViewById(R.id.pll_othor);

        mImageView = (ImageView) findViewById(R.id.iv_bg);

        mTvTitle = (TextView) findViewById(R.id.toolbar_title_tv);
        mTvTitle.setText("水印处理");
        mGridView = (FillGridView) findViewById(R.id.grid_color);
        mColorGridAdapter = new ColorGridAdapter(MainActivity.this);
        mColorGridAdapter.setOnColorItemClickListener(mOnColorItemClickListener);
        mGridView.setAdapter(mColorGridAdapter);
        mGridView.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));

        mEtWaterMarker = (AppCompatEditText) findViewById(R.id.et_watermarker);
        mEtWaterMarker.addTextChangedListener(mTextWatcher);
        if (srcBitmap.getWidth() > srcBitmap.getHeight()) {
            mImageView.setMaxWidth(ScreenUtils.getScreenWidth(this));
            mImageView.setMaxHeight((int) (ScreenUtils.getScreenWidth(this) * 5.0 / 8.0 + 0.5));
        }
//        mTvSize = (TextView) findViewById(R.id.tv_size);

//        mTvSize.setText("w：" + srcBitmap.getWidth() + "h：" + srcBitmap.getHeight());
        mSeekBarDrection = (BubbleSeekBar) findViewById(R.id.seekBar_direction);
        mSeekBarAlpha = (BubbleSeekBar) findViewById(R.id.seekBar_alpha);
        mSeekBarDrection.setOnProgressChangedListener(mOnProgressChangedListener);
        mSeekBarAlpha.setOnProgressChangedListener(mOnProgressAlphaChangedListener);
        mImageUtil = new ImageUtil();
        int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
        setWaterMask(mEtWaterMarker.getText().toString(), 45, 255, color);
    }

    private void initToolbar() {
        mToolBar = (Toolbar) findViewById(R.id.common_toolbar);
        mToolBar.setTitle("");
        mToolBar.setNavigationIcon((R.drawable.abc_ic_ab_back_material));
        mToolBar.inflateMenu(R.menu.menu_main);
        mToolBar.setOnMenuItemClickListener(mOnMenuItemClickListener);
        mToolBar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initIndicator() {
        mTabLayout = (TabLayout) findViewById(R.id.tabLayout);
        mTabLayout.addTab(mTabLayout.newTab().setText("方向大小"));
        mTabLayout.addTab(mTabLayout.newTab().setText("样式"));
        mTabLayout.addTab(mTabLayout.newTab().setText("水印文字"));
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

    private void setWaterMask(String strWaterMarker, int degress, int alpha, int color) {

        Bitmap markTextBitmap = mImageUtil.getMarkTextBitmap(this, strWaterMarker, srcBitmap.getWidth(), srcBitmap.getHeight(), 18, 25, color, alpha, degress);
        Bitmap waterMaskBitmap = mImageUtil.createWaterMaskBitmap(srcBitmap, markTextBitmap, 0, 0);
        mImageView.setImageBitmap(waterMaskBitmap);
    }


    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            setWaterMask(s.toString(), mSeekBarDrection.getProgress(), mSeekBarAlpha.getProgress(), color);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //文字方向
    BubbleSeekBar.OnProgressChangedListener mOnProgressChangedListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            setWaterMask(mEtWaterMarker.getText().toString(), progress, mSeekBarAlpha.getProgress(), color);
        }

        @Override
        public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

        }

        @Override
        public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

        }
    };

    //透明度
    BubbleSeekBar.OnProgressChangedListener mOnProgressAlphaChangedListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDrection.getProgress(), progress, color);
        }

        @Override
        public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

        }

        @Override
        public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

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
                setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDrection.getProgress(), mSeekBarAlpha.getProgress(), color);
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
