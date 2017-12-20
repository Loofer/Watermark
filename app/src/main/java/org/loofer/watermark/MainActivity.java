package org.loofer.watermark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xw.repo.BubbleSeekBar;
import com.zhy.android.percent.support.PercentLinearLayout;

import org.loofer.ui.base.view.BaseActivity;
import org.loofer.utils.SPUtils;
import org.loofer.utils.ScreenUtils;
import org.loofer.utils.ToastUtils;
import org.loofer.utils.Utils;
import org.loofer.view.ColorGridAdapter;
import org.loofer.view.FillGridView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import cn.finalteam.rxgalleryfinal.utils.Logger;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

import static org.loofer.ui.photo.SelectPicActivity.CROP_PIC_PATH;
import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE;
import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE_INDEX;

public class MainActivity extends BaseActivity {

    @BindView(R.id.toolbar_title_tv)
    TextView mTvTitle;
    @BindView(R.id.common_toolbar)
    Toolbar mToolbar;
    @BindView(R.id.iv_bg)
    ImageView mImageView;
    @BindView(R.id.tabLayout)
    TabLayout mTabLayout;
    @BindView(R.id.seekBar_direction)
    BubbleSeekBar mSeekBarDirection;
    @BindView(R.id.seekBar_alpha)
    BubbleSeekBar mSeekBarAlpha;
    @BindView(R.id.pll_direction)
    PercentLinearLayout mPllDirection;
    @BindView(R.id.grid_color)
    FillGridView mGridColor;
    @BindView(R.id.pll_style)
    LinearLayout mPllStyle;
    @BindView(R.id.et_watermarker)
    AppCompatEditText mEtWaterMarker;
    @BindView(R.id.pll_othor)
    PercentLinearLayout mPllOthor;


    private ImageUtil mImageUtil;
    private String picPath;
    private Bitmap srcBitmap;
    private ColorGridAdapter mColorGridAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        Bundle extras = getIntent().getExtras();
        picPath = extras.getString(CROP_PIC_PATH);
        srcBitmap = BitmapFactory.decodeFile(picPath);
        initView();
    }

    private void initView() {
        initIndicator();
        initToolbar();

        mTvTitle.setText("水印处理");
        mColorGridAdapter = new ColorGridAdapter(MainActivity.this);
        mColorGridAdapter.setOnColorItemClickListener(mOnColorItemClickListener);
        mGridColor.setAdapter(mColorGridAdapter);
        mGridColor.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));

        mEtWaterMarker.addTextChangedListener(mTextWatcher);
        if (srcBitmap.getWidth() > srcBitmap.getHeight()) {
            mImageView.setMaxWidth(ScreenUtils.getScreenWidth(this));
            mImageView.setMaxHeight((int) (ScreenUtils.getScreenWidth(this) * 5.0 / 8.0 + 0.5));
        }
        mSeekBarDirection.setOnProgressChangedListener(mOnProgressChangedListener);
        mSeekBarAlpha.setOnProgressChangedListener(mOnProgressAlphaChangedListener);
        mImageUtil = new ImageUtil();
        int color = (int) SPUtils.get(MainActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
        setWaterMask(mEtWaterMarker.getText().toString(), 45, 255, color);
    }

    private void initToolbar() {
        mToolbar.setTitle("");
        mToolbar.setNavigationIcon((R.drawable.abc_ic_ab_back_material));
        mToolbar.inflateMenu(R.menu.menu_main);
        mToolbar.setOnMenuItemClickListener(mOnMenuItemClickListener);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void initIndicator() {
        mTabLayout.addTab(mTabLayout.newTab().setText("方向大小"));
        mTabLayout.addTab(mTabLayout.newTab().setText("样式"));
        mTabLayout.addTab(mTabLayout.newTab().setText("水印文字"));
        mTabLayout.addOnTabSelectedListener(mOnTabSelectedListener);
    }

    // 将生成的图片保存到内存中
    public String saveImage(Bitmap bitmap) {
        if (Utils.isExternalStorageAvailable()) {
            String path = Environment.getExternalStorageDirectory().getPath() + "/WaterMarker";
            File dir = new File(path);
            if (!dir.exists())
                dir.mkdir();
            long time = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = new Date(time);
            String t1 = format.format(d1);
            File file = new File(path, t1 + "-marked.jpg");

            FileOutputStream out;

            try {
                out = new FileOutputStream(file);
                if (bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)) {
                    out.flush();
                    out.close();
                }
                return file.getAbsolutePath();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

    // 将模板View的图片转化为Bitmap
    public Bitmap getBitmapByView(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
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
            setWaterMask(s.toString(), mSeekBarDirection.getProgress(), mSeekBarAlpha.getProgress(), color);
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
            setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDirection.getProgress(), progress, color);
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
                setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDirection.getProgress(), mSeekBarAlpha.getProgress(), color);
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
                    Flowable.just(1)
                            .throttleFirst(1, TimeUnit.SECONDS)
                            .map(new Function<Integer, Boolean>() {
                                @Override
                                public Boolean apply(@NonNull Integer integer) throws Exception {
//                                    Looper.prepare();
                                    // TODO: 2017/10/27 loading
//                                    ToastUtils.showToast(MainActivity.this, "正在保存文件请稍等");
//                                    Looper.loop();
                                    Bitmap bitmap = getBitmapByView(mImageView);
                                    String savePath = saveImage(bitmap);
                                    if (!savePath.isEmpty()) {
                                        return true;
                                    } else {
                                        return false;
                                    }
                                }
                            })
                            .doFinally(new Action() {
                                @Override
                                public void run() throws Exception {
                                    Log.d("---", "最后调用-----");
                                    // TODO: 2017/10/27 hide loading
                                }
                            })
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe(new Consumer<Boolean>() {
                                @Override
                                public void accept(@NonNull Boolean isSaved) throws Exception {
                                    if (isSaved) {
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
