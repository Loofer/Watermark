package org.loofer.ui.photo;

import android.graphics.Bitmap;

import org.loofer.mvp.common.lce.MvpLceView;

/**
 * Created by LooferDeng on 2017/12/19.
 */

public interface SelectPicMvpView extends MvpLceView<Bitmap> {

    void showError(int msg);

}
