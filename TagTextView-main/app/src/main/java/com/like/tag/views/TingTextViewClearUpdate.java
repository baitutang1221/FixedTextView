package com.like.tag.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

import com.view.text.annotation.Align;
import com.view.text.bean.BitmapPackageBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @ClassName TingTextViewClearUpdate
 * @Description 自定义TextView，可图文混排，末尾断点显示，已去掉默认行间距
 * @Author xiazhenjie
 * @Date 2023/2/9 10:42
 * @Version 1.0
 */
public class TingTextViewClearUpdate extends AppCompatTextView {

    //日志标记
    private final String TAG = TingTextViewClearUpdate.class.getSimpleName();
    //文本画笔
    private TextPaint textPaint;
    //绘制矩形
    private Rect rect;
    //默认宽度
    private int layoutWidth = -1;
    //获取行间距的额外空间
    private float line_space_height = 0.0f;
    //获取行间距乘法器
    private float line_space_height_mult = 1.0f;
    //绘制的内容(数据源)
    private final List<Object> mCurContentList = new ArrayList<>();
    //行高
    private int _lineHeight;
    //图片画笔
    private Paint mBitPaint;
    private final float[] savedWidths = new float[1];
    //断尾打点的行号,-1为不断尾
    private int ellipsizeLineNum = -1;
    private final String THREE_POINTS = "...";

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public TingTextViewClearUpdate(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     * @param defStyleAttr
     */
    public TingTextViewClearUpdate(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    /**
     * 初始化方法
     */
    private void init(Context context, AttributeSet attrs) {
        //声明画笔对象
        textPaint = getPaint();
        //声明矩形绘制对象
        rect = new Rect();
        //获得行间距额外数据
        line_space_height = getLineSpacingExtra();
        //获得行间距方法器
        line_space_height_mult = getLineSpacingMultiplier();
        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Layout _layout = getLayout();

        //设置控件总宽度
        layoutWidth = MeasureSpec.getSize(widthMeasureSpec);

        if (_layout != null) {
            //获得文本内容文本内容不可以修改,平切判断当前当前内容是否为null
            final String _tvContent = TextUtils.isEmpty(getText()) ? "" : getText().toString();
            //获取文本长度
            final int _tvLenght = _tvContent.length();
            //设置文本宽度
            textPaint.getTextBounds(_tvContent, 0, _tvLenght, rect);
            //设置文本大小
            textPaint.setTextSize(getTextSize());
            //设置文本颜色
            textPaint.setColor(getCurrentTextColor());
            //获得行高
            _lineHeight = -rect.top + rect.bottom;
            //设置布局宽高
            initLayoutParams( widthMeasureSpec, heightMeasureSpec);
        }
    }

    /**
     * 设置布局宽高
     */
    private void initLayoutParams(int widthMeasureSpec, int heightMeasureSpec){
        int thisLineDrawWidth = 0;//本行的宽度
        int count = 0;//用于记录measure了几次

        OUT_FOR:
        for (int i = 0; i < mCurContentList.size(); i++) {
            int index = 0;
            int totalOldIndex = 0;//用于记录一行的开头Index
            int oldIndex = 0;//用来记录上一行measure了几个字
            int oldNums = 0;//记录已经绘制了多少个字

            Object o = mCurContentList.get(i);
            if(o instanceof String){
                String str = (String) o;
                do{
                    oldIndex = index;
                    totalOldIndex += oldIndex;

                    //获取当行可以绘制的文字数量
                    index = textPaint.breakText(str, totalOldIndex, str.length(), true, layoutWidth - thisLineDrawWidth, savedWidths);
                    //本行末尾不足以支持一个字符，所以换行重置thisLineDrawWidth为0，再次计算可绘制的字数
                    if(index == 0){
                        count++;
                        thisLineDrawWidth = 0;
                        index = textPaint.breakText(str, totalOldIndex, str.length(), true, layoutWidth - thisLineDrawWidth, savedWidths);
                    }

                    //绘制文字
                    String substring = str.substring(totalOldIndex, totalOldIndex + index);

                     //=======================================
                     //此处省略一万行绘制文字的代码。。。。。。。。
                     //=======================================

                    //更新当行的已绘制宽度
                    thisLineDrawWidth += getTextWidth(substring);

                    //更新已绘制的总字数 (仅限当前字符串)
                    oldNums += index;

                    if((totalOldIndex + index) != str.length()){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                }while (/*oldIndex != index &&*/ layoutWidth != 0 && !TextUtils.isEmpty(str) && (oldNums != str.length() && index != 0));
            }
            else if(o instanceof BitmapPackageBean){
                BitmapPackageBean bitmapPackageBean = (BitmapPackageBean) o;
                Bitmap bitmap = bitmapPackageBean.getBitmap();

                if(bitmap != null){

                    //图片默认属性
                    float imageWidth = bitmap.getWidth();
                    float imageHeight = bitmap.getHeight();

                    //手动设置的属性
                    float marginLeft = bitmapPackageBean.getMarginLeft();
                    float marginRight = bitmapPackageBean.getMarginRight();
                    float marginTop = bitmapPackageBean.getMarginTop();
                    float marginBottom = bitmapPackageBean.getMarginBottom(); //暂时没用
                    float align = bitmapPackageBean.getAlign();
                    float setImageWidth = bitmapPackageBean.getWidth();
                    float setImageHeight = bitmapPackageBean.getHeight();

                    //水平方向缩放比例
                    float xScale = 1.0f;
                    //竖直方向缩放比例
                    float yScale = 1.0f;
                    //图片绘制宽度
                    float measureImageWidth;

                    //如果设置图片宽高,按照缩放比例进行显示
                    if(setImageWidth > 0 && setImageHeight > 0){
                        xScale = setImageWidth / imageWidth;
                        imageWidth = setImageWidth;

                        yScale = setImageHeight / imageHeight;
                        imageHeight = setImageHeight;

                        measureImageWidth = imageWidth;
                    }else{ //反之，图片与文字等高，宽度等比缩放
                        float radio = imageWidth / imageHeight;//宽高比例
                        measureImageWidth = radio * _lineHeight;//图片绘制宽度
                    }

                    //换行
                    if(thisLineDrawWidth + measureImageWidth + marginLeft + marginRight > layoutWidth){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                    //图片的裁减区域
                    Rect mSrcRect = new Rect(
                            0,
                            0,
                            floatToInt(imageWidth) ,
                            floatToInt(imageHeight)
                    );

                    //图片上边界到控件顶部的距离
                    float top = count * (_lineHeight + line_space_height) + marginTop;
                    if(align == Align.CENTER){
                        top += (_lineHeight - imageHeight) / 2.0f;
                    }else if(align == Align.BOTTOM){
                        top += (_lineHeight - imageHeight);
                    }

                    //图片下边界到控件顶部的距离
                    float bottom;
                    if(setImageHeight == 0){
                        bottom = top + _lineHeight;
                    }else{
                        bottom = top + setImageHeight;
                    }

                    //图片的外框区域
                    Rect mDestRect = new Rect(
                            floatToInt(thisLineDrawWidth + marginLeft),//图片左边界到控件左边界的距离
                            floatToInt(top),
                            floatToInt(measureImageWidth + thisLineDrawWidth + marginLeft),//图片右边界到控件左边界的距离
                            floatToInt(bottom)
                    );

                    //如果没有断尾打点的情况
                    if(ellipsizeLineNum == -1){
                        //=======================================
                        //此处省略一万行绘制图片的代码。。。。。。。。
                        //=======================================
                    }else{
                        //设置了断尾，但是实际总行数达不到断尾的行数
                        if((count + 1) < ellipsizeLineNum){
                            //=======================================
                            //此处省略一万行绘制图片的代码。。。。。。。。
                            //=======================================
                        }else{
                            //如果绘制当前图片会超出边界，则丢弃该图片，直接在末尾显示三个点。
                            if((thisLineDrawWidth + measureImageWidth + marginLeft + marginRight) >= layoutWidth){
                                //绘制三个点

                            }else{
                                //获取下一个元素的宽度
                                int nextItemWidth = getNextItemWidth(i + 1);
                                //绘制完这个图片还未超出右边界，但是加上下一个元素就达到右边界了
                                if((thisLineDrawWidth + measureImageWidth  + marginLeft + marginRight + nextItemWidth) >= layoutWidth){

                                    //绘制完这个图片，再绘制三个点，不会超出边界，则丢弃下一个元素。
                                    if((thisLineDrawWidth + measureImageWidth  + marginLeft + marginRight + getTextWidth(THREE_POINTS)) <= layoutWidth){
                                        //=======================================
                                        //此处省略一万行绘制图片的代码。。。。。。。。
                                        //=======================================

                                        //绘制完图片后，立马绘制三个点，需要更新thisLineDrawWidth
                                        thisLineDrawWidth += measureImageWidth + marginLeft + marginRight;
                                    }
                                    //绘制三个点
                                    break OUT_FOR;
                                }
                                //=======================================
                                //此处省略一万行绘制图片的代码。。。。。。。。
                                //=======================================
                            }
                        }
                    }

                    thisLineDrawWidth += measureImageWidth + marginLeft + marginRight;
                    if(thisLineDrawWidth >= layoutWidth){
                        thisLineDrawWidth = 0;
                        count++;
                    }
                }

            }else{
                Log.d(TAG,"发现其他类型.....");
            }
        }

        //获取断尾行号
        setEllipsizeLineNum(count);

        //获取布局数据
        int[] _area = getWidthAndHeight(widthMeasureSpec, heightMeasureSpec, layoutWidth, ellipsizeLineNum==-1 ? (count+1) : ellipsizeLineNum, _lineHeight);
        //设置布局宽高
        setMeasuredDimension(_area[0], _area[1]);
    }

    /**
     * 获取断尾行号
     * @param count
     */
    private void setEllipsizeLineNum(int count){
        //多行断尾
        int maxLines = getMaxLines();
        TextUtils.TruncateAt ellipsize = getEllipsize();
        if(maxLines != -1 && maxLines < (count + 1) && ellipsize == TextUtils.TruncateAt.END){
            ellipsizeLineNum = maxLines;
        }else{
            ellipsizeLineNum = -1;
        }
    }

    /**
     * 获取布局数据
     *
     * @param pWidthMeasureSpec
     * @param pHeightMeasureSpec
     * @param pWidth
     * @return 返回宽高数组
     */
    private int[] getWidthAndHeight(int pWidthMeasureSpec, int pHeightMeasureSpec, int pWidth, int pLineCount, int pLineHeight) {
        int _widthMode = MeasureSpec.getMode(pWidthMeasureSpec);   //获取宽的模式
        int _heightMode = MeasureSpec.getMode(pHeightMeasureSpec); //获取高的模式
        int _widthSize = MeasureSpec.getSize(pWidthMeasureSpec);   //获取宽的尺寸
        int _heightSize = MeasureSpec.getSize(pHeightMeasureSpec); //获取高的尺寸
        //声明控件尺寸
        int _width;
        int _height;
        //判断模式
        if (_widthMode == MeasureSpec.EXACTLY) {
            //如果match_parent或者具体的值，直接赋值
            _width = _widthSize;
        } else {
            _width = pWidth - rect.left;
        }
        //高度跟宽度处理方式一样
        if (_heightMode == MeasureSpec.EXACTLY) {
            _height = _heightSize;
        } else {
            if(pLineCount > 1){
                _height = pLineHeight * pLineCount + (int) (line_space_height * line_space_height_mult * (pLineCount -1));
            }else{
                _height = pLineHeight * pLineCount;
            }
        }
        //初始化宽高数组
        int[] _area = {
                _width,
                _height
        };
        return _area;
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        int thisLineDrawWidth = 0;//本行的宽度
        int count = 0;//用于记录measure了几次

        OUT_FOR:
        for (int i = 0; i < mCurContentList.size(); i++) {
            int index = 0;
            int totalOldIndex = 0;//用于记录一行的开头Index
            int oldIndex = 0;//用来记录上一行measure了几个字
            int oldNums = 0;//记录已经绘制了多少个字

            Object o = mCurContentList.get(i);
            if(o instanceof String){
                String str = (String) o;
                do{
                    oldIndex = index;
                    totalOldIndex += oldIndex;

                    //获取当行可以绘制的文字数量
                    index = textPaint.breakText(
                            str,
                            totalOldIndex,
                            str.length(),
                            true,
                            layoutWidth - thisLineDrawWidth,
                            savedWidths
                    );
                    //本行末尾不足以支持一个字符，所以换行重置thisLineDrawWidth为0，再次计算可绘制的字数
                    if(index == 0){
                        count++;
                        thisLineDrawWidth = 0;
                        index = textPaint.breakText(
                                str,
                                totalOldIndex,
                                str.length(),
                                true,
                                layoutWidth - thisLineDrawWidth,
                                savedWidths
                        );
                    }

                    //绘制文字
                    String substring = str.substring(totalOldIndex, totalOldIndex + index);
                    //本次绘制的文字宽度
                    int drawTextRealWidth = 0;

                    if(ellipsizeLineNum == -1){
                        canvas.drawText(
                                str,
                                totalOldIndex,
                                totalOldIndex + index, thisLineDrawWidth,
                                getTextY(count),
                                textPaint
                        );
                        drawTextRealWidth = getTextWidth(substring);
                    }else{
                        if((count + 1) < ellipsizeLineNum){
                            canvas.drawText(
                                    str,
                                    totalOldIndex,
                                    totalOldIndex + index,
                                    thisLineDrawWidth,
                                    getTextY(count),
                                    textPaint
                            );
                            drawTextRealWidth = getTextWidth(substring);
                        }else{
                            int textWidth = getTextWidth(substring + THREE_POINTS);
                            //如果当前字符串拼接上三个点后绘制，总宽度小于控件宽度
                            if((textWidth + thisLineDrawWidth) <= layoutWidth){
                                //获取下一个元素的宽度
                                int nextItemWidth = getNextItemWidth(i + 1);

                                //如果（当前已绘制的宽度 + 当前字符串的宽度 + 下个元素的宽度） >= 控件宽度，则裁减当前字符串
                                if((textWidth + thisLineDrawWidth + nextItemWidth) >= layoutWidth){
                                    String lastLineContent = getLastLineContent(substring, layoutWidth - thisLineDrawWidth);
                                    canvas.drawText(
                                            lastLineContent,
                                            thisLineDrawWidth,
                                            getTextY(count),
                                            textPaint
                                    );
                                    drawTextRealWidth = getTextWidth(lastLineContent);
                                    break OUT_FOR;
                                }
                                //否则正常绘制当前字符串
                                canvas.drawText(
                                        str,
                                        totalOldIndex,
                                        totalOldIndex + index,
                                        thisLineDrawWidth,
                                        getTextY(count),
                                        textPaint
                                );
                                drawTextRealWidth = getTextWidth(substring);
                            }else{
                                String lastLineContent = getLastLineContent(substring, layoutWidth - thisLineDrawWidth);
                                canvas.drawText(
                                        lastLineContent,
                                        thisLineDrawWidth,
                                        getTextY(count),
                                        textPaint
                                );
                                drawTextRealWidth = getTextWidth(lastLineContent);
                                break OUT_FOR;
                            }
                        }
                    }

                    //更新当行的已绘制宽度
                    thisLineDrawWidth += drawTextRealWidth;

                    //更新已绘制的总字数 (仅限当前字符串)
                    oldNums += index;

                    if((totalOldIndex + index) != str.length()){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                }while (/*oldIndex != index &&*/ layoutWidth != 0 && !TextUtils.isEmpty(str) && (oldNums != str.length() && index != 0));
            }
            else if(o instanceof BitmapPackageBean){
                BitmapPackageBean bitmapPackageBean = (BitmapPackageBean) o;
                Bitmap bitmap = bitmapPackageBean.getBitmap();

                if(bitmap != null){

                    //图片默认属性
                    float imageWidth = bitmap.getWidth();
                    float imageHeight = bitmap.getHeight();

                    //手动设置的属性
                    float marginLeft = bitmapPackageBean.getMarginLeft();
                    float marginRight = bitmapPackageBean.getMarginRight();
                    float marginTop = bitmapPackageBean.getMarginTop();
                    float marginBottom = bitmapPackageBean.getMarginBottom(); //暂时没用
                    float align = bitmapPackageBean.getAlign();
                    float setImageWidth = bitmapPackageBean.getWidth();
                    float setImageHeight = bitmapPackageBean.getHeight();

                    //水平方向缩放比例
                    float xScale = 1.0f;
                    //竖直方向缩放比例
                    float yScale = 1.0f;
                    //图片绘制宽度
                    float measureImageWidth;

                    //如果设置图片宽高,按照缩放比例进行显示
                    if(setImageWidth > 0 && setImageHeight > 0){
                        xScale = setImageWidth / imageWidth;
                        imageWidth = setImageWidth;

                        yScale = setImageHeight / imageHeight;
                        imageHeight = setImageHeight;

                        measureImageWidth = imageWidth;
                    }else{ //反之，图片与文字等高，宽度等比缩放
                        float radio = imageWidth / imageHeight;//宽高比例
                        measureImageWidth = radio * _lineHeight;//图片绘制宽度
                    }

                    //换行
                    if(thisLineDrawWidth + measureImageWidth + marginLeft + marginRight > layoutWidth){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                    //图片的裁减区域
                    Rect mSrcRect = new Rect(
                            0,
                            0,
                            floatToInt(imageWidth) ,
                            floatToInt(imageHeight)
                    );

                    //图片上边界到控件顶部的距离
                    float top = count * (_lineHeight + line_space_height * line_space_height_mult) + marginTop;
                    if(align == Align.CENTER){
                        top += (_lineHeight - imageHeight) / 2.0f;
                    }else if(align == Align.BOTTOM){
                        top += (_lineHeight - imageHeight);
                    }

                    //图片下边界到控件顶部的距离
                    float bottom;
                    if(setImageHeight == 0){
                        bottom = top + _lineHeight;
                    }else{
                        bottom = top + setImageHeight;
                    }

                    //图片的外框区域
                    Rect mDestRect = new Rect(
                            floatToInt(thisLineDrawWidth + marginLeft),//图片左边界到控件左边界的距离
                            floatToInt(top),
                            floatToInt(measureImageWidth + thisLineDrawWidth + marginLeft),//图片右边界到控件左边界的距离
                            floatToInt(bottom)
                    );

                    float thisImageRealWidth;

                    //如果没有断尾打点的情况
                    if(ellipsizeLineNum == -1){
                        if(setImageWidth > 0 && setImageHeight > 0){
                            // 定义矩阵对象
                            Matrix matrix = new Matrix();
                            // 缩放原图
                            matrix.postScale(xScale, yScale);
                            //如果设置了宽高，则按照缩放后的位图宽高显示，否则高度与文字等高，宽度按宽高比显示
                            Bitmap dstBmp = Bitmap.createBitmap(
                                    bitmap,
                                    0,
                                    0,
                                    bitmap.getWidth(),
                                    bitmap.getHeight(),
                                    matrix,
                                    true
                            );
                            canvas.drawBitmap(dstBmp, thisLineDrawWidth + marginLeft, top, null);
                            thisImageRealWidth = dstBmp.getWidth() + marginLeft + marginRight;

                        }else{
                            canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
                            thisImageRealWidth = measureImageWidth + marginLeft + marginRight;
                        }
                    }else{
                        //设置了断尾，但是实际总行数达不到断尾的行数
                        if((count + 1) < ellipsizeLineNum){
                            //设置了图片宽高
                            if(setImageWidth > 0 && setImageHeight > 0){
                                // 定义矩阵对象
                                Matrix matrix = new Matrix();
                                // 缩放原图
                                matrix.postScale(xScale, yScale);
                                //如果设置了宽高，则按照缩放后的位图宽高显示，否则高度与文字等高，宽度按宽高比显示
                                Bitmap dstBmp = Bitmap.createBitmap(
                                        bitmap,
                                        0,
                                        0,
                                        bitmap.getWidth(),
                                        bitmap.getHeight(),
                                        matrix,
                                        true
                                );
                                canvas.drawBitmap(dstBmp, thisLineDrawWidth + marginLeft, top, null);
                                thisImageRealWidth = dstBmp.getWidth() + marginLeft + marginRight;
                            }else{
                                canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
                                thisImageRealWidth = measureImageWidth + marginLeft + marginRight;
                            }
                        }else{
                            //如果绘制当前图片会超出边界，则丢弃该图片，直接在末尾显示三个点。
                            if((thisLineDrawWidth + measureImageWidth + marginLeft + marginRight) >= layoutWidth){
                                //绘制三个点
                                canvas.drawText(
                                        THREE_POINTS,
                                        thisLineDrawWidth,
                                        getTextY(count),
                                        textPaint
                                );
                                break OUT_FOR;
                            }else{
                                //获取下一个元素的宽度
                                int nextItemWidth = getNextItemWidth(i + 1);
                                //绘制完这个图片还未超出右边界，但是加上下一个元素就达到右边界了
                                if((thisLineDrawWidth + measureImageWidth  + marginLeft + marginRight + nextItemWidth) >= layoutWidth){

                                    //绘制完这个图片，再绘制三个点，不会超出边界，则丢弃下一个元素。
                                    if((thisLineDrawWidth + measureImageWidth  + marginLeft + marginRight + getTextWidth(THREE_POINTS)) <= layoutWidth){
                                        if(setImageWidth > 0 && setImageHeight > 0){
                                            // 定义矩阵对象
                                            Matrix matrix = new Matrix();
                                            // 缩放原图
                                            matrix.postScale(xScale, yScale);
                                            //如果设置了宽高，则按照缩放后的位图宽高显示，否则高度与文字等高，宽度按宽高比显示
                                            Bitmap dstBmp = Bitmap.createBitmap(
                                                    bitmap,
                                                    0,
                                                    0,
                                                    bitmap.getWidth(),
                                                    bitmap.getHeight(),
                                                    matrix,
                                                    true
                                            );
                                            canvas.drawBitmap(dstBmp, thisLineDrawWidth + marginLeft, top, null);
                                            //绘制完图片后，立马绘制三个点，需要更新thisLineDrawWidth
                                            thisImageRealWidth = dstBmp.getWidth() + marginLeft + marginRight;
                                        }else{
                                            canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
                                            //绘制完图片后，立马绘制三个点，需要更新thisLineDrawWidth
                                            thisImageRealWidth = measureImageWidth + marginLeft + marginRight;
                                        }
                                    }
                                    //绘制三个点
                                    canvas.drawText(
                                            THREE_POINTS,
                                            thisLineDrawWidth,
                                            getTextY(count),
                                            textPaint
                                    );
                                    break OUT_FOR;
                                }
                                if(setImageWidth > 0 && setImageHeight > 0){
                                    // 定义矩阵对象
                                    Matrix matrix = new Matrix();
                                    // 缩放原图
                                    matrix.postScale(xScale, yScale);
                                    //如果设置了宽高，则按照缩放后的位图宽高显示，否则高度与文字等高，宽度按宽高比显示
                                    Bitmap dstBmp = Bitmap.createBitmap(
                                            bitmap,
                                            0,
                                            0,
                                            bitmap.getWidth(),
                                            bitmap.getHeight(),
                                            matrix,
                                            true
                                    );
                                    canvas.drawBitmap(dstBmp, thisLineDrawWidth + marginLeft, top, null);
                                    thisImageRealWidth = dstBmp.getWidth() + marginLeft + marginRight;
                                }else{
                                    canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
                                    thisImageRealWidth = measureImageWidth + marginLeft + marginRight;
                                }
                            }
                        }
                    }

                    thisLineDrawWidth += thisImageRealWidth;
                    if(thisLineDrawWidth >= layoutWidth){
                        thisLineDrawWidth = 0;
                        count++;
                    }
                }

            }else{
                 Log.d(TAG,"发现其他类型.....");
            }
        }

    }

    /**
     * 数字类型转换
     * @param f
     * @return
     */
    public int floatToInt(float f){
        int i = 0;
        if(f > 0) {
            i = (int)(f*10 + 5)/10;
        }
        else if(f < 0) {
            i =  (int)(f*10 - 5)/10;
        }
        else {
            i = 0;
        }
        return i;
    }

    /**
     * 获取某一个元素的宽度
     * @param i
     * @return
     */
    private int getNextItemWidth(int i){
        Object o = mCurContentList.get(i);
        if(o instanceof String){
            String str = (String) o;
            return getTextWidth(str);
        }else if(o instanceof BitmapPackageBean){
            BitmapPackageBean bitmapPackageBean = (BitmapPackageBean) o;
            Bitmap bitmap = bitmapPackageBean.getBitmap();
            if(bitmap != null){
                int marginLeft = bitmapPackageBean.getMarginLeft();
                int marginRight = bitmapPackageBean.getMarginRight();
                int setImageWidth = bitmapPackageBean.getWidth();
                if(setImageWidth > 0){
                    return setImageWidth + marginLeft + marginRight;
                }else{
                    int imageWidth = bitmap.getWidth();
                    int imageHeight = bitmap.getHeight();
                    //宽高比例
                    int radio = imageWidth / imageHeight;
                    //图片绘制宽度
                    return radio * _lineHeight + marginLeft + marginRight;
                }
            }
        }
        return 0;
    }


    /**
     * 获取最后一行剩余宽度下，可以容纳的字符串内容
     * @param _drawContent
     * @param leaveWidth 剩余宽度
     * @return
     */
    private String getLastLineContent(String _drawContent, int leaveWidth){
        String tempStr = _drawContent;
        do{
            int thisLineDrawWidth = getTextWidth(tempStr + THREE_POINTS);
            if(thisLineDrawWidth <= leaveWidth){
                break;
            }
            //获取字符串长度
            int codePointCount = tempStr.codePointCount(0, tempStr.length());
            tempStr = subStringFun(tempStr, codePointCount - 1);
        }while (true);
        return tempStr + THREE_POINTS;
    }

    /**
     * 获取字符串所占宽度
     * @param str
     * @return
     */
    private int  getTextWidth(String str) {
        float iSum = 0;
        if(str != null && !str.equals(""))
        {
            int len = str.length();
            float[] widths = new float[len];
            getPaint().getTextWidths(str, widths);
            for(int i = 0; i < len; i++)
            {
                iSum += Math.ceil(widths[i]);
            }
        }
        return (int)iSum;
    }

    /**
     * 截取字符串
     * @param value 字符串原数据
     * @param lengthShown 要保留的数据长度
     * @return
     */
    private static String subStringFun(String value, int lengthShown) {
        String result;
        if(TextUtils.isEmpty(value))
            return "";
        if (lengthShown <= 0 || value.length() <= lengthShown)
            return value;
        try {
            result = value.substring(value.offsetByCodePoints(0, 0),
                    value.offsetByCodePoints(0, lengthShown)) ;
        } catch (Exception e) {
            result = "";
        }
        return result;
    }

    /**
     * 传参，重绘
     * @param values
     */
    public void setContent(Object... values){
        mCurContentList.addAll(Arrays.asList(values));
        invalidate();
    }

    /**
     * 绘制文字的Y坐标
     * @param i 行数
     * @return
     */
    private float getTextY(int i){
        return -rect.top + (_lineHeight + line_space_height * line_space_height_mult) * i;
    }

}
