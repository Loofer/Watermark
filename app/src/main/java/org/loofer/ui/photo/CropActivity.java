package org.loofer.ui.photo;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.view.View;

import org.loofer.ui.base.view.BaseLceActivity;
import org.loofer.utils.ToastUtils;
import org.loofer.watermark.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;

import butterknife.BindView;
import butterknife.OnClick;
import me.pqpo.smartcropperlib.view.CropImageView;

public class CropActivity extends BaseLceActivity<View, Bitmap, CropMvpView, CropPresenter> implements CropMvpView {


    private static final String EXTRA_FROM_ALBUM = "extra_from_album";
    private static final String EXTRA_CROPPED_FILE = "extra_cropped_file";
    private static final int REQUEST_CODE_TAKE_PHOTO = 100;
    private static final int REQUEST_CODE_SELECT_ALBUM = 200;


    @BindView(R.id.iv_crop)
    CropImageView mIvCrop;

    public static Intent getJumpIntent(Context context, boolean fromAlbum, File croppedFile) {
        Intent intent = new Intent(context, CropActivity.class);
        intent.putExtra(EXTRA_FROM_ALBUM, fromAlbum);
        intent.putExtra(EXTRA_CROPPED_FILE, croppedFile);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_crop);
        boolean fromAlbum = getIntent().getBooleanExtra(EXTRA_FROM_ALBUM, true);
        getPresenter().setFromAlbum(fromAlbum);
        File croppedFile = (File) getIntent().getSerializableExtra(EXTRA_CROPPED_FILE);
        getPresenter().setCroppedFile(croppedFile);
    }

    @NonNull
    @Override
    public CropPresenter createPresenter() {
        return new CropPresenter();
    }

    @OnClick({R.id.btn_cancel, R.id.btn_ok})
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_ok:
                cropPic();
                break;
            case R.id.btn_cancel:
                getPresenter().cancelCrop();
                break;
        }
    }

    @Override
    public void cancelCrop() {
        setResult(RESULT_CANCELED);
        finish();
    }


    //裁剪图片
    private void cropPic() {
        if (mIvCrop.canRightCrop()) {
            Bitmap cropBitmap = mIvCrop.crop();
            if (cropBitmap != null) {
                getPresenter().saveImage(cropBitmap);
                setResult(RESULT_OK);
            } else {
                setResult(RESULT_CANCELED);
            }
            finish();
        } else {
            showError(R.string.pic_crop_error);
        }
    }


    /**
     * 从相册获取图片
     */
    @Override
    public void getPicFromAlbum() {
        Intent selectIntent = new Intent(Intent.ACTION_PICK);
        selectIntent.setType("image/*");
        if (selectIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(selectIntent, REQUEST_CODE_SELECT_ALBUM);
        }

    }

    /**
     * 从相机获取图片
     */
    @Override
    public void getPicFromCamera(Uri uri) {
        Intent startCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
        if (startCameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(startCameraIntent, REQUEST_CODE_TAKE_PHOTO);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) {
            setResult(RESULT_CANCELED);
            finish();
            return;
        }

        switch (requestCode) {
            case REQUEST_CODE_TAKE_PHOTO:
                getPresenter().deCodeCameraPic();
                break;
            case REQUEST_CODE_SELECT_ALBUM:
                try {
                    if (data != null && data.getData() != null) {
                        ContentResolver cr = getContentResolver();
                        Uri bmpUri = data.getData();
                        InputStream inputStream = cr.openInputStream(bmpUri);
                        getPresenter().decodeAlbumPic(inputStream);
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    showError(R.string.pic_crop_error);
                }
                break;
        }
    }


    @Override
    public void setData(Bitmap bitmap) {

        if (bitmap != null) {
            mIvCrop.setImageToCrop(bitmap);
        } else {
            showError(R.string.pic_get_error);
        }
    }

    @Override
    public void showError(int msg) {
        ToastUtils.showToast(CropActivity.this, msg);
    }

}
