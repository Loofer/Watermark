package org.loofer.ui.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Environment;

import org.loofer.mvp.common.MvpPresenter;
import org.loofer.ui.base.presenter.MvpLceRxPresenter;
import org.loofer.watermark.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LooferDeng on 2017/12/19.
 */

public class CropPresenter extends MvpLceRxPresenter<CropMvpView, Bitmap> implements MvpPresenter<CropMvpView> {


    private File tempFile;
    private boolean mIsFromAlbum;
    private File mCroppedFile;


    @Override
    public void attachView(CropMvpView view) {
        super.attachView(view);

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            File sdcardDir = Environment.getExternalStorageDirectory();
            String path = sdcardDir.getPath() + "/WaterMarker/temp";
            File path1 = new File(path);
            if (!path1.exists()) {
                path1.mkdirs();
            }
            long time = System.currentTimeMillis();
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date d1 = new Date(time);
            String t1 = format.format(d1);
            tempFile = new File(path, t1 + ".jpg");
        } else {
            getView().showError(R.string.sd_invailed);
            return;
        }

        selectPhoto();
    }

    public void setFromAlbum(boolean isFromAlbum) {
        mIsFromAlbum = isFromAlbum;
    }


    private void selectPhoto() {
        if (mIsFromAlbum) {
            getView().getPicFromAlbum();
        } else {
            getView().getPicFromCamera(Uri.fromFile(tempFile));
        }
    }

    public void deCodeCameraPic() {
        if (tempFile.exists()) {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(tempFile.getPath(), options);
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateSampleSize(options);
            Bitmap selectedBitmap = BitmapFactory.decodeFile(tempFile.getPath(), options);
            getView().setData(selectedBitmap);
        }
    }

    public void decodeAlbumPic(InputStream inputStream) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(inputStream, new Rect(), options);
        options.inJustDecodeBounds = false;
        options.inSampleSize = calculateSampleSize(options);
        Bitmap selectedBitmap = BitmapFactory.decodeStream(inputStream, new Rect(), options);
        getView().setData(selectedBitmap);
    }


    private int calculateSampleSize(BitmapFactory.Options options) {
        int outHeight = options.outHeight;
        int outWidth = options.outWidth;
        int sampleSize = 1;
        int destHeight = 1000;
        int destWidth = 1000;
        if (outHeight > destHeight || outWidth > destHeight) {
            if (outHeight > outWidth) {
                sampleSize = outHeight / destHeight;
            } else {
                sampleSize = outWidth / destWidth;
            }
        }
        if (sampleSize < 1) {
            sampleSize = 1;
        }
        return sampleSize;
    }

    public void cancelCrop() {
        getView().cancelCrop();
    }

    public void cropPic() {

    }

    public void setCroppedFile(File croppedFile) {
        if (croppedFile == null) {
            getView().cancelCrop();
            return;
        }
        mCroppedFile = croppedFile;
    }

    public void saveImage(Bitmap cropBitmap) {
        try {
            FileOutputStream fos = new FileOutputStream(mCroppedFile);
            cropBitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}
