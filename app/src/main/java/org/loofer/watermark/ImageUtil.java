package org.loofer.watermark;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.TypedValue;

import org.loofer.MarkApplication;

/**
 * 图片工具类
 *
 * @author PeggyTong
 */
public class ImageUtil {

    public Bitmap getMarkTextBitmap(String gText, int width, int height, float textSize, float inter, int color, int alpha, int degress) {
        textSize = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, textSize, MarkApplication.getInstance().getResources().getDisplayMetrics());
        inter = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, inter, MarkApplication.getInstance().getResources().getDisplayMetrics());

        int sideLength;
        if (width > height) {
            sideLength = (int) Math.sqrt(2 * (width * width));
        } else {
            sideLength = (int) Math.sqrt(2 * (height * height));
        }


        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        Rect rect = new Rect();
        paint.setTextSize(textSize);
        //获取文字长度和宽度
        paint.getTextBounds(gText, 0, gText.length(), rect);

        int strwid = rect.width();
        int strhei = rect.height();

        Bitmap markBitmap = null;
        try {
            markBitmap = Bitmap.createBitmap(sideLength, sideLength, Bitmap.Config.ARGB_4444);
            Canvas canvas = new Canvas(markBitmap);
            //创建透明画布
            canvas.drawColor(Color.TRANSPARENT);

            paint.setColor(color);
            paint.setAlpha(alpha);
//            paint.setAlpha((int) (0.1 * 255f));
            // 获取跟清晰的图像采样
            paint.setDither(true);
            paint.setFilterBitmap(true);

            //先平移，再旋转才不会有空白，使整个图片充满
            if (width > height) {
                if (degress < 90) {
                    canvas.translate(width - sideLength - inter, degress * (sideLength - width + inter) / 90);
                } else {
                    canvas.translate((degress - 180) * (width - sideLength - inter) / 180, (degress - 180) * (sideLength - width + inter) / 180);
                }
            } else {
                if (degress < 90) {
                    canvas.translate(height - sideLength - inter, (sideLength - height + inter) * degress / 90);
                } else {
                    canvas.translate(degress * (height - sideLength - inter) / 180, (degress - 180) * (sideLength - height + inter) / 180);
                }
            }

            //将该文字图片逆时针方向倾斜45度
            if (degress < 90) {
                canvas.rotate(-degress, width / 2, height / 2);
            } else {
                canvas.rotate(180 - degress, width / 2, height / 2);
            }

            for (int i = 0; i <= sideLength; ) {
                int count = 0;
                for (int j = 0; j <= sideLength; count++) {
                    if (count % 2 == 0) {
                        canvas.drawText(gText, i, j, paint);
                    } else {
                        //偶数行进行错开
                        canvas.drawText(gText, i + strwid / 2, j, paint);
                    }
                    j = (int) (j + inter + strhei);
                }
                i = (int) (i + strwid + inter);
            }
            canvas.save(Canvas.ALL_SAVE_FLAG);
//      ACache.get(gContext).put(gText, markBitmap);
        } catch (OutOfMemoryError e) {
            if (markBitmap != null && !markBitmap.isRecycled()) {
                markBitmap.recycle();
                markBitmap = null;
            }
        }
        return markBitmap;
    }

    public Bitmap createWaterMaskBitmap(Bitmap src, Bitmap watermark,
                                        int paddingLeft, int paddingTop) {
        if (src == null) {
            return null;
        }
        int width = src.getWidth();
        int height = src.getHeight();
        //创建一个bitmap
        Bitmap newb = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);// 创建一个新的和SRC长度宽度一样的位图
        //将该图片作为画布
        Canvas canvas = new Canvas(newb);
        //在画布 0，0坐标上开始绘制原始图片
        canvas.drawBitmap(src, 0, 0, null);
        //在画布上绘制水印图片
        canvas.drawBitmap(watermark, paddingLeft, paddingTop, null);
        // 保存
        canvas.save(Canvas.ALL_SAVE_FLAG);
        // 存储
        canvas.restore();
        return newb;
    }


}
