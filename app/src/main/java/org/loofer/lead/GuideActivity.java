package org.loofer.lead;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.loofer.HomeActivity;
import org.loofer.utils.SPUtils;
import org.loofer.watermark.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by rcjs on 2017/10/31.
 * Description:
 */

public class GuideActivity extends AppCompatActivity implements View.OnClickListener, ViewPager.OnPageChangeListener {

    private ViewPager vp;
    private GuideViewPagerAdapter adapter;
    private List<View> views = new ArrayList<View>();
    private Button startBtn;

    // 引导页图片资源
    private static final int[] pics = {R.layout.guid_view1,
            R.layout.guid_view2, R.layout.guid_view3, R.layout.guid_view4};

    // 底部小点图片
    private ImageView[] dots;

    // 记录当前选中位置
    private int currentIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        initView();
        initData();
    }

    private void initView() {
        // 初始化引导页视图列表
        for (int i = 0; i < pics.length; i++) {
            View view = LayoutInflater.from(this).inflate(pics[i], null);

            if (i == pics.length - 1) {
                startBtn = (Button) view.findViewById(R.id.btn_login);
                startBtn.setTag("enter");
                startBtn.setOnClickListener(this);
            }

            views.add(view);

        }

        vp = (ViewPager) findViewById(R.id.vp_guide);
        // 初始化adapter
        adapter = new GuideViewPagerAdapter(views);
        vp.setAdapter(adapter);
        vp.addOnPageChangeListener(this);

        initDots();
    }

    private void initData() {

    }


    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 如果切换到后台，就设置下次不进入功能引导页
        SPUtils.put(this, "isFirst", false);
        finish();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void initDots() {
        LinearLayout ll = (LinearLayout) findViewById(R.id.ll);
        dots = new ImageView[pics.length];

        // 循环取得小点图片
        for (int i = 0; i < pics.length; i++) {
            // 得到一个LinearLayout下面的每一个子元素
            dots[i] = (ImageView) ll.getChildAt(i);
            dots[i].setEnabled(false);// 都设为灰色
          //  dots[i].setOnClickListener(this);
          //  dots[i].setTag(i);// 设置位置tag，方便取出与当前位置对应
        }

        currentIndex = 0;
        dots[currentIndex].setEnabled(true); // 设置为白色，即选中状态

    }

    /**
     * 设置当前view
     *
     * @param position
     */
    private void setCurView(int position) {
        if (position < 0 || position >= pics.length) {
            return;
        }
        vp.setCurrentItem(position);
    }

    /**
     * 设置当前指示点
     *
     * @param position
     */
    private void setCurDot(int position) {
        if (position < 0 || position > pics.length || currentIndex == position) {
            return;
        }
        dots[position].setEnabled(true);
        dots[currentIndex].setEnabled(false);
        currentIndex = position;
    }

    @Override
    public void onClick(View v) {
        if (v.getTag().equals("enter")) {
            enterMainActivity();
            return;
        }

        int position = (Integer) v.getTag();
        setCurView(position);
        setCurDot(position);
    }


    private void enterMainActivity() {
        Intent intent = new Intent(this,
                HomeActivity.class);
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
        setCurDot(position);
    }

    // 当滑动状态改变时调用
    @Override
    public void onPageScrollStateChanged(int state) {
        // state ==1的时辰默示正在滑动，state==2的时辰默示滑动完毕了，state==0的时辰默示什么都没做。

    }
}