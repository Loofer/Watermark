package org.loofer.ui.mark;

import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.content.res.ResourcesCompat;
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

import org.loofer.ui.base.view.BaseMvpActivity;
import org.loofer.utils.DefaultDialogUtils;
import org.loofer.utils.SPUtils;
import org.loofer.utils.ScreenUtils;
import org.loofer.utils.ToastUtils;
import org.loofer.view.ColorGridAdapter;
import org.loofer.view.FillGridView;
import org.loofer.watermark.R;

import butterknife.BindView;

import static org.loofer.ui.photo.SelectPicActivity.CROP_PIC_PATH;
import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE;
import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE_INDEX;

public class MarkActivity extends BaseMvpActivity<View, Bitmap, MarkMvpView, MarkPresenter> implements MarkMvpView {

    public static final String KEY_DEFAULT_TEXT = "default_text";
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
    @BindView(R.id.seekBar_size)
    BubbleSeekBar mSeekBarSize;
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


    private String picPath;
    private ColorGridAdapter mColorGridAdapter;
    private Dialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle extras = getIntent().getExtras();
        picPath = extras.getString(CROP_PIC_PATH);
        getPresenter().initView(picPath);
        initView();
    }

    private void initView() {
        initIndicator();
        initToolbar();

        mTvTitle.setText("水印处理");
        mColorGridAdapter = new ColorGridAdapter(MarkActivity.this);
        mColorGridAdapter.setOnColorItemClickListener(mOnColorItemClickListener);
        mGridColor.setAdapter(mColorGridAdapter);
        mGridColor.setSelector(ResourcesCompat.getDrawable(getResources(), R.drawable.md_transparent, null));

        mEtWaterMarker.addTextChangedListener(mTextWatcher);
        String defaultText = (String) SPUtils.get(this, KEY_DEFAULT_TEXT, "仅提供XX银行申请XX基金扣帐他用无效");
        mEtWaterMarker.setText(defaultText);
        mSeekBarDirection.setOnProgressChangedListener(mOnProgressChangedListener);
        mSeekBarAlpha.setOnProgressChangedListener(mOnProgressAlphaChangedListener);
        mSeekBarSize.setOnProgressChangedListener(mOnProgressSizeChangedListener);
        int color = (int) SPUtils.get(MarkActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));

        getPresenter().setWaterMask(mEtWaterMarker.getText().toString(), 45, 255, color, 18);
    }

    @Override
    public void resizeImageView(boolean horizontal) {
        if (horizontal) {

            LinearLayout.LayoutParams layoutParams =
                    new LinearLayout.LayoutParams(ScreenUtils.getScreenWidth(this), (int) (ScreenUtils.getScreenWidth(this) * 5.0 / 8.0 + 0.5));
            mImageView.setLayoutParams(layoutParams);
        } /*else {
            mImageView.setMaxWidth(ScreenUtils.getScreenWidth(this));
            mImageView.setMaxHeight((int) (ScreenUtils.getScreenWidth(this) * 5.0 / 8.0 + 0.5));
        }*/
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


    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int color = (int) SPUtils.get(MarkActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            getPresenter().setWaterMask(s.toString(), mSeekBarDirection.getProgress(), mSeekBarAlpha.getProgress(), color, mSeekBarSize.getProgress());
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    //文字方向
    BubbleSeekBar.OnProgressChangedListener mOnProgressChangedListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            int color = (int) SPUtils.get(MarkActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            getPresenter().setWaterMask(mEtWaterMarker.getText().toString(), progress, mSeekBarAlpha.getProgress(), color, mSeekBarSize.getProgress());
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
            int color = (int) SPUtils.get(MarkActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            getPresenter().setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDirection.getProgress(), progress, color, mSeekBarSize.getProgress());
        }

        @Override
        public void getProgressOnActionUp(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

        }

        @Override
        public void getProgressOnFinally(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {

        }
    };

    //文字大小
    BubbleSeekBar.OnProgressChangedListener mOnProgressSizeChangedListener = new BubbleSeekBar.OnProgressChangedListener() {
        @Override
        public void onProgressChanged(BubbleSeekBar bubbleSeekBar, int progress, float progressFloat) {
            int color = (int) SPUtils.get(MarkActivity.this, COLOR_CHOOSE, Color.parseColor("#FF1744"));
            getPresenter().setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDirection.getProgress(), mSeekBarAlpha.getProgress(), color, progress);
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
                SPUtils.put(MarkActivity.this, COLOR_CHOOSE_INDEX, index);
                SPUtils.put(MarkActivity.this, COLOR_CHOOSE, color);
                mColorGridAdapter.notifyDataSetChanged();
                getPresenter().setWaterMask(mEtWaterMarker.getText().toString(), mSeekBarDirection.getProgress(), mSeekBarAlpha.getProgress(), color, mSeekBarSize.getProgress());
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
                    getPresenter().actionSave(mImageView);
                    break;
            }

            return true;
        }
    };


    @Override
    public void setData(Bitmap data) {
        mImageView.setImageBitmap(data);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {
        if (pullToRefresh) {
            mLoadingDialog = DefaultDialogUtils.createLoadingDialog(MarkActivity.this, "正在保存...");
        } else {
            DefaultDialogUtils.closeDialog(mLoadingDialog);
        }
    }

    @android.support.annotation.NonNull
    @Override
    public MarkPresenter createPresenter() {
        return new MarkPresenter();
    }

    @Override
    public void showError(int msg) {
        ToastUtils.showToast(MarkActivity.this, msg);
    }

    @Override
    public void saveDefaultText() {
        SPUtils.put(this, MarkActivity.KEY_DEFAULT_TEXT, mEtWaterMarker.getText().toString());
    }

    @Override
    public void closePage() {
        finish();
    }
}
