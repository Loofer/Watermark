package org.loofer.ui.base.presenter;

import org.loofer.mvp.common.MvpBasePresenter;
import org.loofer.mvp.common.MvpPresenter;
import org.loofer.mvp.common.lce.MvpLceView;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.observers.DisposableObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Created by LooferDeng on 2017/12/15.
 */

public abstract class MvpLceRxPresenter<V extends MvpLceView<M>, M>
        extends MvpBasePresenter<V> implements MvpPresenter<V> {


    protected DisposableObserver<M> subscriber;

    /**
     * Unsubscribes the subscriber and set it to null
     */
    protected void unsubscribe() {
        if (null != subscriber && !subscriber.isDisposed()) {
            subscriber.dispose();
        }

        subscriber = null;
    }

    /**
     * Subscribes the presenter himself as subscriber on the observable
     *
     * @param observable    The observable to subscribe
     * @param pullToRefresh Pull to refresh?
     */
    public void subscribe(final Observable<M> observable, final boolean pullToRefresh) {
        if (isViewAttached()) {
            getView().showLoading(pullToRefresh);
        }

        unsubscribe();

        subscriber = new RxSubscriber(pullToRefresh);

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(subscriber);
    }

    //    @RxLogSubscriber
    public class RxSubscriber extends DisposableObserver<M> {
        final private boolean ptr;

        public RxSubscriber(final boolean pullToRefresh) {
            this.ptr = pullToRefresh;
        }

        @Override
        public void onComplete() {

            MvpLceRxPresenter.this.onCompleted();
        }

        @Override
        public void onError(Throwable e) {
            MvpLceRxPresenter.this.onError(e, ptr);
        }

        @Override
        public void onNext(M m) {
            MvpLceRxPresenter.this.onNext(m);
        }

    }

    protected void onCompleted() {
        if (isViewAttached()) {
            getView().showContent();
        }
        unsubscribe();
    }

    protected void onError(Throwable e, boolean pullToRefresh) {
        if (isViewAttached()) {
            getView().showError(e, pullToRefresh);
        }
        unsubscribe();
    }

    protected void onNext(M data) {
        if (isViewAttached()) {
            getView().setData(data);
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            unsubscribe();
        }
    }


}
