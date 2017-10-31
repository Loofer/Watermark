package org.loofer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import org.loofer.utils.SPUtils;
import org.loofer.utils.ScreenUtils;
import org.loofer.watermark.R;

import static org.loofer.view.ColorGridAdapter.COLOR_CHOOSE_INDEX;

/**
 * Created by LooferDeng on 2017/10/30.
 */

public class HomeGridAdapter extends BaseAdapter {


    private int iconA[] = {R.drawable.ic_camera, R.drawable.ic_album, R.drawable.ic_camera,
            R.drawable.ic_album};
    private String production[] = {"拍照裁剪", "相册选择", "拍照调整", "相册调整"};

    @Override
    public int getCount() {
        return iconA.length;
    }

    @Override
    public Object getItem(int position) {
        return iconA[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.item_home, parent, false);
        }
        ImageView icon = (ImageView) convertView.findViewById(R.id.iv_icon);
        TextView tvProduction = (TextView) convertView.findViewById(R.id.tv_production);
        icon.setImageResource(iconA[position]);
        tvProduction.setText(production[position]);
        return convertView;
    }

}


