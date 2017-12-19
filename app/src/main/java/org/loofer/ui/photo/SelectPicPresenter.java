package org.loofer.ui.photo;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;

import org.loofer.mvp.MvpBasePresenter;
import org.loofer.watermark.R;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by LooferDeng on 2017/12/19.
 */

public class SelectPicPresenter extends MvpBasePresenter<SelectPicMvpView> {

    private File mPhotoFile;
    private boolean mIsGetPic;


    @Override
    public void attachView(SelectPicMvpView view) {
        super.attachView(view);
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
            mPhotoFile = new File(path, t1 + ".jpg");
        } else {
            getView().showError(R.string.sd_invailed);
        }

    }

    public File getPicFile() {
        return mPhotoFile;
    }

    public void selectPic() {
        if (mPhotoFile.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(mPhotoFile.getPath());
            getView().setData(bitmap);
            mIsGetPic = true;
        }
    }

    public void setGetPic(boolean getPic) {
        mIsGetPic = getPic;
    }

    public boolean getIsGetPic() {
        return mIsGetPic;
    }
}
