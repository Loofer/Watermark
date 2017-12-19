package org.loofer.ui.home;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;

import org.loofer.ui.photo.CropActivity;
import org.loofer.utils.Constants;
import org.loofer.utils.FileUtils;
import org.loofer.utils.SimpleRxGalleryFinal;
import org.loofer.utils.ToastUtils;
import org.loofer.utils.Utils;
import org.loofer.view.FillGridView;
import org.loofer.view.HomeGridAdapter;
import org.loofer.watermark.MainActivity;
import org.loofer.watermark.R;

import java.io.File;

import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;
import cn.finalteam.rxgalleryfinal.utils.Logger;

public class HomeActivity extends AppCompatActivity {


    File photoFile;


    public static final String CROP_PIC_PATH = "crop_image";
    private FillGridView mGridHome;
    private Toolbar mToolBar;
    private TextView mTvTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    private void initView() {
        mToolBar = (Toolbar) findViewById(R.id.common_toolbar);
        mTvTitle = (TextView) findViewById(R.id.toolbar_title_tv);
        mTvTitle.setText("水印砖家");
        mGridHome = (FillGridView) findViewById(R.id.grid_home);
        mGridHome.setAdapter(new HomeGridAdapter());
        mGridHome.setOnItemClickListener(mOnItemClickListener);
    }

    private void initData() {
        if (Utils.isExternalStorageAvailable()) {
            photoFile = Utils.getFilePathByExtennel("/WaterMarker/crop", FileUtils.getFileNameByTime());
        } else {
            ToastUtils.showToast(HomeActivity.this, "内部存储不可用！");
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //默认
        SimpleRxGalleryFinal.get().onActivityResult(requestCode, resultCode, data);
        //智能裁剪
        if (resultCode != RESULT_OK) {
            return;
        }
        if (requestCode == 100 && photoFile.exists()) {
            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
            intent.putExtra(CROP_PIC_PATH, photoFile.getPath());
            startActivity(intent);
        }

    }

    AdapterView.OnItemClickListener mOnItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            switch (position) {
                case 0:
                    ImageFromCameraDefault();
                    break;
                case 1:
                    //手工裁剪方式
                    ImageFromGalleryDefault();
                    break;
                case 2:
                    //拍照获取图片智能裁剪
                    startActivityForResult(CropActivity.getJumpIntent(HomeActivity.this, false, photoFile), 100);
                    break;
                case 3:
                    //相册中获取图片智能裁剪
                    startActivityForResult(CropActivity.getJumpIntent(HomeActivity.this, true, photoFile), 100);
                    break;
                case 4:
                    break;
                case 5:

                    break;
            }
        }
    };


    /**
     * 从相机中获取图片
     * 手工裁剪方式
     */
    private void ImageFromCameraDefault() {
        if (!Utils.isExternalStorageAvailable()) {
            ToastUtils.showToast(HomeActivity.this, R.string.sd_invailed);
            return;
        }
        RxGalleryFinalApi instance = RxGalleryFinalApi.getInstance(HomeActivity.this);

        RxGalleryFinalApi.setImgSaveRxCropDir(new File(Environment.getExternalStorageDirectory() + File.separator + Constants.IMAGE_BASE));
//        RxGalleryFinalApi.setImgSaveRxSDCard("WaterMarker");
        //裁剪会自动生成路径；也可以手动设置裁剪的路径；
        RxGalleryFinalApi.setImgSaveRxCropSDCard(Constants.IMAGE_BASE + Constants.IMAGE_CROP);


        SimpleRxGalleryFinal.get().init(
                new SimpleRxGalleryFinal.RxGalleryFinalCropListener() {
                    @NonNull
                    @Override
                    public Activity getSimpleActivity() {
                        return HomeActivity.this;
                    }

                    @Override
                    public void onCropCancel() {
                        ToastUtils.showToast(HomeActivity.this, "取消裁剪");
                    }

                    @Override
                    public void onCropSuccess(@Nullable Uri uri) {
//                        uri.getPath();
//                        ToastUtils.shoToast(HomeActivity.this, uri.getPath());
                        File file = new File(uri.getPath());
                        if (file.exists()) {
                            Logger.i("裁剪成功");
                            Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                            intent.putExtra(CROP_PIC_PATH, file.getPath());
                            startActivity(intent);
                        } else {
                            ToastUtils.showToast(HomeActivity.this, "失败");
                        }
//                        Toast.makeText(getSimpleActivity(), "裁剪成功：" + uri, Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onCropError(@NonNull String errorMessage) {
                        ToastUtils.showToast(HomeActivity.this, errorMessage);
//                        Toast.makeText(getSimpleActivity(), errorMessage, Toast.LENGTH_SHORT).show();
                    }
                }
        ).openCamera();
    }

    /**
     * 从相册中获取图片
     * 手工裁剪方式
     */
    private void ImageFromGalleryDefault() {
        //单选，使用RxGalleryFinal默认设置，并且带有裁剪
        RxGalleryFinalApi.setImgSaveRxSDCard("WaterMarker");
        RxGalleryFinalApi.setImgSaveRxCropSDCard("WaterMarker/crop");//裁剪会自动生成路径；也可以手动设置裁剪的路径；
        RxGalleryFinalApi instance = RxGalleryFinalApi.getInstance(HomeActivity.this);
        instance
                .openGalleryRadioImgDefault(
                        new RxBusResultDisposable<ImageRadioResultEvent>() {
                            @Override
                            protected void onEvent(ImageRadioResultEvent imageRadioResultEvent) throws Exception {
                                Logger.i("只要选择图片就会触发");
                            }
                        })
                .onCropImageResult(
                        new IRadioImageCheckedListener() {
                            @Override
                            public void cropAfter(Object t) {
                                File file = new File(t.toString());
                                if (file.exists()) {
                                    Intent intent = new Intent(HomeActivity.this, MainActivity.class);
                                    intent.putExtra(CROP_PIC_PATH, file.getPath());
                                    startActivity(intent);
                                } else {
                                    ToastUtils.showToast(HomeActivity.this, "失败");
                                }
                                Logger.i("裁剪完成");
                            }

                            @Override
                            public boolean isActivityFinish() {
                                Logger.i("返回false不关闭，返回true则为关闭");
                                return true;
                            }
                        });
    }


}
