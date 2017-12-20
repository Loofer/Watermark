package org.loofer.ui.base.view;

import android.view.View;

import org.loofer.mvp.MvpActivity;
import org.loofer.mvp.common.MvpPresenter;
import org.loofer.mvp.common.lce.MvpLceView;

import butterknife.ButterKnife;

/**
 * Created by LooferDeng on 2017/12/20.
 */

public abstract class BaseMvpActivity<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>> extends MvpActivity<V, P> implements MvpLceView<M> {


    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }

    @Override
    public void showLoading(boolean pullToRefresh) {

    }

    @Override
    public void showContent() {

    }

    @Override
    public void showError(Throwable e, boolean pullToRefresh) {

    }


    @Override
    public void loadData(boolean pullToRefresh) {

    }
}
