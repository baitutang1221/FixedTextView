package com.like.tag.views;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;

import com.view.text.R;
import com.view.text.utils.HelperUtils;


/**
 * 图文混排
 * 1.画图片
 * 2.画多行的文字
 */
public class ImageTextView extends View {


    private float width, height;
    private Bitmap bitmap;
    private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final String MULTI_TEXT = "其中R.drawable.danger_build10是一个vector图片，此代码在4.4上运行正常，但在5.0以上的系统会出现空指针，原因在于此本来方法不能将vector转化为bitmap，而apk编译时为了向下兼容，会根据vector生产相应的png，而4.4的系统运行此代码时其实用的是png资源。这就是为什么5.0以上会报错，而4.4不会的原因。";
    private TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
    private StaticLayout staticLayout;
    private float[] savedWidths = new float[1];
    private int bitmapBottom;
    private int bitmapTop;
    final Rect tempBoundRect = new Rect();

    public ImageTextView(Context context) {
        this(context, null);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ImageTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        bitmap = getBitmap(((int) HelperUtils.dpToPx(100)));
        bitmapTop = 100;
        textPaint.setTextSize(HelperUtils.dpToPx(18));
        textPaint.setColor(Color.BLACK);
        //1.2拿到图片的高度范围
        final int height = bitmap.getHeight();
        bitmapBottom = bitmapTop + height;
        Log.e("test", bitmapTop+":bitmapTop=========bitmapBottom:" + bitmapBottom);
        textPaint.getTextBounds(MULTI_TEXT, 0, MULTI_TEXT.length(), tempBoundRect);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
        staticLayout = new StaticLayout(MULTI_TEXT, textPaint, ((int) width), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //1.画图片
        canvas.drawBitmap(bitmap, width - HelperUtils.dpToPx(100), bitmapTop, paint);
//        canvas.drawBitmap(bitmap, 10, bitmapTop, paint);

        //2.画多行文字
        //2.1 StaticLayout 适用于静态文字
//        staticLayout.draw(canvas);
        //2.2 适用于动态文字，实时计算文字所需宽度并及时换行
        //先切出来第一行
        int index = 0;
        int oldIndex = 0;//用来记录上一行measure了几个字
        int count = 0;//用于记录measure了几次
        int totalOldIndex = 0;//用于记录一行的开头Index
        int totalHeight = 0;//用于记录当前文字的总高度
        do {
            oldIndex = index;
            totalOldIndex += oldIndex;
            //这里画文字的时候，需要考虑，当前文字的上下边界是否与图片有重合的地方
            totalHeight += tempBoundRect.height();
            Log.e("test", "totalHeight:" + totalHeight);
            if (totalHeight >= bitmapTop && totalHeight <= bitmapBottom) {
                //文字绘制区域需要减去图片的宽度
                index = textPaint.breakText(MULTI_TEXT, totalOldIndex, MULTI_TEXT.length(), true, width - bitmap.getWidth(), savedWidths);
                canvas.drawText(MULTI_TEXT, totalOldIndex, totalOldIndex + index, 0, 60 + textPaint.getFontSpacing() * count, textPaint);
            } else {
                index = textPaint.breakText(MULTI_TEXT, totalOldIndex, MULTI_TEXT.length(), true, width, savedWidths);
                canvas.drawText(MULTI_TEXT, totalOldIndex, totalOldIndex + index, 0, 60 + textPaint.getFontSpacing() * count, textPaint);
            }
            count++;
            //
        }
        while (oldIndex != index && width != 0 && !TextUtils.isEmpty(MULTI_TEXT));
    }

    private Bitmap getBitmap(int width) {
        Bitmap bitmap = null;
        try {
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeResource(getResources(), R.drawable.abc, options);
            options.inJustDecodeBounds = false;
            options.inDensity = options.outWidth;
            options.inTargetDensity = width;
            bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.abc, options);
        } catch (Exception e) {
            Log.e("test", "exception-->" + e.getMessage());
        }
        return bitmap;
    }
}