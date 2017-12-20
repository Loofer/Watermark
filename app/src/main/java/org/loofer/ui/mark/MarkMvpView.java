package org.loofer.ui.mark;

import android.graphics.Bitmap;

import org.loofer.mvp.common.lce.MvpLceView;

/**
 * Created by LooferDeng on 2017/12/20.
 */

public interface MarkMvpView extends MvpLceView<Bitmap> {

    void resizeImageView(boolean horizontal);

    void showError(int msg);

    void saveDefaultText();

    void closePage();
}
