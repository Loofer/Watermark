package org.loofer.ui.photo;

import android.graphics.Bitmap;
import android.net.Uri;

import org.loofer.mvp.common.lce.MvpLceView;

/**
 * Created by LooferDeng on 2017/12/19.
 */

public interface CropMvpView extends MvpLceView<Bitmap> {

    void showError(int msg);

    void getPicFromAlbum();

    void getPicFromCamera(Uri uri);

    void cancelCrop();
}
