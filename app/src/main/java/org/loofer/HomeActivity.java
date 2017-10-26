package org.loofer;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

import org.loofer.photo.CropActivity;
import org.loofer.photo.SelectPicActivity;
import org.loofer.utils.SimpleRxGalleryFinal;
import org.loofer.utils.ToastUtils;
import org.loofer.view.FullyGridLayoutManager;
import org.loofer.watermark.MainActivity;
import org.loofer.watermark.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.finalteam.rxgalleryfinal.RxGalleryFinalApi;
import cn.finalteam.rxgalleryfinal.rxbus.RxBusResultDisposable;
import cn.finalteam.rxgalleryfinal.rxbus.event.ImageRadioResultEvent;
import cn.finalteam.rxgalleryfinal.ui.base.IRadioImageCheckedListener;
import cn.finalteam.rxgalleryfinal.utils.Logger;

import static okhttp3.internal.Internal.instance;
import static org.loofer.photo.SelectPicActivity.CROP_PIC_PATH;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView mRecyclerView;

    File photoFile;


    public static final String CROP_PIC_PATH = "crop_image";
    private boolean isGetPic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        initView();
        initData();
    }

    private void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setNestedScrollingEnabled(false);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.HORIZONTAL));
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
        FullyGridLayoutManager layoutManager = new FullyGridLayoutManager(this, 3, GridLayoutManager.VERTICAL, false);
        mRecyclerView.setAdapter(new HoneAdapter());
        mRecyclerView.setLayoutManager(layoutManager);
    }

    private void initData() {
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            String path = sdcardDir.getPath() + "/WaterMarker/crop";
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            long time = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = new Date(time);
            String t1 = format.format(d1);
            photoFile = new File(path, t1 + ".jpg");
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

    class HoneAdapter extends RecyclerView.Adapter<HoneAdapter.ListHolder> {
        int iconA[] = {R.drawable.ic_camera, R.drawable.ic_album, R.drawable.ic_camera,
                R.drawable.ic_album, R.drawable.ic_camera, R.drawable.ic_album,
                R.drawable.ic_camera, R.drawable.ic_album, R.drawable.ic_camera};

        @Override
        public ListHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home, parent, false);
            return new ListHolder(itemView);
        }

        @Override
        public void onBindViewHolder(final ListHolder holder, final int position) {
            holder.icon.setImageResource(iconA[position]);
            holder.mItemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (position) {
                        case 0:
                            ImageFromCameraDefault();
//                            startActivity(new Intent(HomeActivity.this, SelectPicActivity.class));
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
                    ToastUtils.showToast(holder.mItemView.getContext(), "点击事件：" + position);
                }
            });
        }

        @Override
        public int getItemCount() {
            return iconA.length;
        }


        class ListHolder extends RecyclerView.ViewHolder {
            ImageView icon;
            View mItemView;

            ListHolder(View itemView) {
                super(itemView);
                mItemView = itemView;
                icon = (ImageView) itemView.findViewById(R.id.iv_icon);
            }
        }


    }

    /**
     * 从相机中获取图片
     * 手工裁剪方式
     */
    private void ImageFromCameraDefault() {
        RxGalleryFinalApi instance = RxGalleryFinalApi.getInstance(HomeActivity.this);

        RxGalleryFinalApi.setImgSaveRxSDCard("WaterMarker");
        RxGalleryFinalApi.setImgSaveRxCropSDCard("WaterMarker/crop");//裁剪会自动生成路径；也可以手动设置裁剪的路径；
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
                        ToastUtils.showToast(HomeActivity.this, uri.getPath());
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
        instance.openGalleryRadioImgDefault(
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
