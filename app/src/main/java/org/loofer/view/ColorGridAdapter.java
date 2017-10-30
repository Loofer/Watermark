package org.loofer.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.annotation.ColorInt;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;

import org.loofer.utils.SPUtils;
import org.loofer.utils.ScreenUtils;

/**
 * Created by LooferDeng on 2017/10/30.
 */

public class ColorGridAdapter extends BaseAdapter implements View.OnClickListener {


    private final Context mContext;
    private int[] colorsTop = ColorPalette.ACCENT_COLORS;
    private int[][] colorsSub;
    public static final String COLOR_CHOOSE_INDEX = "color_choose_index";
    public static final String COLOR_CHOOSE = "color_choose";


    public ColorGridAdapter(Context context) {
        this.mContext = context;

    }

    @Override
    public int getCount() {
        return colorsTop.length;
    }

    @Override
    public Object getItem(int position) {
        return colorsTop[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @SuppressLint("DefaultLocale")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = new CircleView(mContext);
            convertView.setLayoutParams(new GridView.LayoutParams(ScreenUtils.dp2px(mContext, 57), ScreenUtils.dp2px(mContext, 57)));
        }
        CircleView child = (CircleView) convertView;
        @ColorInt final int color = colorsTop[position];
        child.setBackgroundColor(color);

        child.setSelected((int) SPUtils.get(mContext, COLOR_CHOOSE_INDEX, 0) == position);

        child.setTag(String.format("%d:%d", position, color));
        child.setOnClickListener(this);
        return convertView;
    }

    @Override
    public void onClick(View v) {
        if (mOnColorItemClickListener != null) {
            mOnColorItemClickListener.onColorItemClick(v);
        }
//        if (v.getTag() != null) {
//            final String[] tag = ((String) v.getTag()).split(":");
//            final int index = Integer.parseInt(tag[0]);
//            SPUtils.put(mContext, COLOR_CHOOSE_INDEX, index);
//            notifyDataSetChanged();
//        }
    }


    private OnColorItemClickListener mOnColorItemClickListener;

    public void setOnColorItemClickListener(OnColorItemClickListener onColorItemClickListener) {
        this.mOnColorItemClickListener = onColorItemClickListener;
    }

    public interface OnColorItemClickListener {

        void onColorItemClick(View v);

    }

}


