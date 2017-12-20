package org.loofer.ui.photo;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import org.loofer.ui.base.view.BaseLceActivity;
import org.loofer.utils.ToastUtils;
import org.loofer.ui.mark.MarkActivity;
import org.loofer.watermark.R;

import butterknife.BindView;
import butterknife.OnClick;

public class SelectPicActivity extends BaseLceActivity<View, Bitmap, SelectPicMvpView, SelectPicPresenter> implements SelectPicMvpView {


    @BindView(R.id.btn_take)
    Button mBtnTake;
    @BindView(R.id.btn_select)
    Button mBtnSelect;
    @BindView(R.id.btn_watermarker)
    Button mBtnWatermarker;

    @BindView(R.id.iv_show)
    ImageView mIvShow;

    public static final String CROP_PIC_PATH = "crop_image";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_pic);

    }


    @OnClick({R.id.btn_take, R.id.btn_select, R.id.btn_watermarker})
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_take:
                startActivityForResult(CropActivity.getJumpIntent(SelectPicActivity.this, false, getPresenter().getPicFile()), 100);
                break;
            case R.id.btn_select:
                startActivityForResult(CropActivity.getJumpIntent(SelectPicActivity.this, true, getPresenter().getPicFile()), 100);
                break;
            case R.id.btn_watermarker:
                if (getPresenter().getIsGetPic()) {
                    Intent intent = new Intent(SelectPicActivity.this, MarkActivity.class);
                    intent.putExtra(CROP_PIC_PATH, getPresenter().getPicFile().getPath());
                    startActivity(intent);
                } else {
                    showError(R.string.pic_take_or_album);
                }
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            getPresenter().setGetPic(false);
            return;
        }
        if (requestCode == REQUEST_CODE_TAKE_PHOTO) {
            getPresenter().selectPic();
        }
    }

    @Override
    public void setData(Bitmap data) {
        mIvShow.setImageBitmap(data);
    }

    @Override
    public void showError(int msg) {
        ToastUtils.showToast(SelectPicActivity.this, msg);
    }

    @NonNull
    @Override
    public SelectPicPresenter createPresenter() {
        return new SelectPicPresenter();
    }

}
