package org.loofer.ui.mark;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.loofer.mvp.MvpBasePresenter;
import org.loofer.utils.Utils;
import org.loofer.watermark.ImageUtil;
import org.loofer.watermark.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import cn.finalteam.rxgalleryfinal.utils.Logger;
import io.reactivex.Flowable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by LooferDeng on 2017/12/20.
 */

public class MarkPresenter extends MvpBasePresenter<MarkMvpView> {


    private Bitmap mSrcBitmap;
    private ImageUtil mImageUtil;


    @Override
    public void attachView(MarkMvpView view) {
        super.attachView(view);
        mImageUtil = new ImageUtil();
    }

    public void initView(String picPath) {

        mSrcBitmap = BitmapFactory.decodeFile(picPath);
        getView().resizeImageView(mSrcBitmap.getWidth() > mSrcBitmap.getHeight());

    }


    public void setWaterMask(String strWaterMarker, int degress, int alpha, int color, int size) {
        Bitmap markTextBitmap = mImageUtil.getMarkTextBitmap(strWaterMarker, mSrcBitmap.getWidth(), mSrcBitmap.getHeight(), size, 25, color, alpha, degress);
        Bitmap waterMaskBitmap = mImageUtil.createWaterMaskBitmap(mSrcBitmap, markTextBitmap, 0, 0);
        getView().setData(waterMaskBitmap);
    }


    // 将生成的图片保存到内存中
    public String saveImage(ImageView imageView) {
        Bitmap bitmap = getBitmapByView(imageView);
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
                if (bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)) {
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


    public void actionSave(final ImageView imageView) {
        Flowable.just(1)
                .throttleFirst(1, TimeUnit.SECONDS)
                .map(new Function<Integer, Integer>() {
                    @Override
                    public Integer apply(@NonNull Integer integer) throws Exception {
                        getView().showLoading(true);
                        return integer;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())
                .map(new Function<Integer, Boolean>() {
                    @Override
                    public Boolean apply(@NonNull Integer integer) throws Exception {
                        String savePath = saveImage(imageView);
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
                        getView().showLoading(false);
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(@NonNull Boolean isSaved) throws Exception {
                        if (isSaved) {
                            getView().showError(R.string.pic_save_success);
                            getView().saveDefaultText();
                            getView().closePage();
                        } else {
                            getView().showError(R.string.pic_save_error);
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(@NonNull Throwable throwable) throws Exception {
                        getView().showError(R.string.pic_save_error);
                        Log.d("---", throwable.getMessage());
                        Logger.d(throwable.getMessage());
                    }
                });
    }
}
