package org.loofer.ui.lead;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.RadioGroup;

import org.loofer.ui.home.HomeActivity;
import org.loofer.ui.base.view.BaseActivity;
import org.loofer.utils.SPUtils;
import org.loofer.watermark.R;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by rcjs on 2017/10/31.
 * Description:
 */

public class GuideActivity extends BaseActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.vp_guide)
    ViewPager mViewPager;
    @BindView(R.id.btn_login)
    Button startBtn;
    @BindView(R.id.rg_dot)
    RadioGroup mRadioGroup;
    private GuideViewPagerAdapter mPagerAdapter;
    private List<View> views = new ArrayList<View>();
    // 引导页图片资源
    private final int[] pics = {R.layout.guid_view1, R.layout.guid_view2, R.layout.guid_view3, R.layout.guid_view4};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        ButterKnife.bind(this);
        initView();
        initListener();
    }


    private void initView() {
        mRadioGroup.check(mRadioGroup.getChildAt(0).getId());
        // 初始化引导页视图列表
        for (int pic : pics) {
            View view = LayoutInflater.from(this).inflate(pic, null);
            views.add(view);
        }
        // 初始化adapter
        mPagerAdapter = new GuideViewPagerAdapter(views);
        mViewPager.setAdapter(mPagerAdapter);

    }


    private void initListener() {
        startBtn.setOnClickListener(this);
        mViewPager.addOnPageChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SPUtils.put(this, "isFirst", false);
        finish();
    }


    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, HomeActivity.class);
        startActivity(intent);
        SPUtils.put(this, "isFirst", false);
        finish();
    }


    /**
     * 当前页面被滑动时调用
     *
     * @param position             当前页面，及你点击滑动的页面
     * @param positionOffset       当前页面偏移的百分比
     * @param positionOffsetPixels 当前页面偏移的像素位置
     */
    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    // 当新的页面被选中时调用
    @Override
    public void onPageSelected(int position) {
        // 设置底部小点选中状态
        mRadioGroup.check(mRadioGroup.getChildAt(position).getId());
        if (position == mPagerAdapter.getCount() - 1) {
            startBtn.setVisibility(View.VISIBLE);
        } else {
            startBtn.setVisibility(View.GONE);
        }
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {
        // state ==1的时辰默示正在滑动，state==2的时辰默示滑动完毕了，state==0的时辰默示什么都没做。

    }
}