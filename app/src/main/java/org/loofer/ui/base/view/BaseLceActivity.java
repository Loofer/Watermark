package org.loofer.ui.base.view;

import android.view.View;

import org.loofer.mvp.common.MvpPresenter;
import org.loofer.mvp.common.lce.MvpLceView;
import org.loofer.mvp.core.lce.MvpLceActivity;

import butterknife.ButterKnife;

/**
 * Created by LooferDeng on 2017/12/19.
 */

public abstract class BaseLceActivity<C extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>> extends BaseMvpActivity<C, M, V, P> implements View.OnClickListener {

    @Override
    public void onContentChanged() {
        super.onContentChanged();
        ButterKnife.bind(this);
    }



    @Override
    public void onClick(View v) {

    }

    /**
     * mvp
     */
   /* @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return null;
    }*/

    @Override
    public void loadData(boolean pullToRefresh) {

    }


}
