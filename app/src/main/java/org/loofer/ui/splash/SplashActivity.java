package org.loofer.ui.splash;

import android.content.Intent;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.loofer.ui.home.HomeActivity;
import org.loofer.ui.base.view.BaseActivity;
import org.loofer.ui.lead.GuideActivity;
import org.loofer.utils.SPUtils;
import org.loofer.watermark.R;

import butterknife.BindView;

/**
 * Created by rcjs on 2017/10/31.
 * Description:闪屏页
 */
public class SplashActivity extends BaseActivity {

    @BindView(R.id.iv_splash_bg)
    ImageView mIvSplashBg;
    private boolean isFirst;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        initData();
    }


    private void initData() {
        isFirst = (boolean) SPUtils.get(this, "isFirst", true);
        listenAnimation();
    }

    private void listenAnimation() {
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.alpha);
        animation.setDuration(2000);
        //监听动画过程
        animation.setAnimationListener(new Animation.AnimationListener() {

            @Override
            public void onAnimationStart(Animation arg0) {

            }

            @Override
            public void onAnimationRepeat(Animation arg0) {

            }

            @Override
            public void onAnimationEnd(Animation arg0) {

                if (!isFirst) {
                    //是第一次进来
                    Intent intent = new Intent(SplashActivity.this, GuideActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    //不是第一次进来
                    Intent intent = new Intent(SplashActivity.this, HomeActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });

        mIvSplashBg.setAnimation(animation);
    }


}
