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
    //获得每行数据集合
    private final ArrayList<String> contentList = new ArrayList<>(0);
    //绘制的内容
    private final List<Object> curList = new ArrayList<>();

    //行高
    private int _lineHeight;

    private Paint mBitPaint;

    private final float[] savedWidths = new float[1];

    //断尾的行号,-1为不断尾
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
            //初始化布局
            initLayout(_layout);
            //获取行数据集合
            calculateLines(_tvContent,layoutWidth);
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
        for (int i = 0; i < curList.size(); i++) {
            int index = 0;
            int totalOldIndex = 0;//用于记录一行的开头Index
            int oldIndex = 0;//用来记录上一行measure了几个字
            int oldNums = 0;//记录已经绘制了多少个字

            Object o = curList.get(i);
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
                    int imageWidth = bitmap.getWidth();
                    int imageHeight = bitmap.getHeight();

                    //宽高比例
                    int radio = imageWidth / imageHeight;

                    //图片绘制宽度
                    float measureImageWidth = radio * _lineHeight;

                    if(thisLineDrawWidth + measureImageWidth > layoutWidth){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                    thisLineDrawWidth += measureImageWidth;
                    if(thisLineDrawWidth >= layoutWidth){
                        thisLineDrawWidth = 0;
                        count++;
                    }
                }

            }else{
                Log.d(TAG,"发现其他类型.....---------------------------------------");
            }
        }

        setEllipsizeLineNum(count);

        //设置布局区域
        int[] _area = getWidthAndHeight(widthMeasureSpec, heightMeasureSpec, layoutWidth, ellipsizeLineNum==-1 ? (count+1) : ellipsizeLineNum, _lineHeight);
        //设置布局
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
     * 初始化化布局高度
     *
     * @param _layout
     */
    private void initLayout(Layout _layout) {
        //获得布局大小
        if (layoutWidth < 0) {
            //获取第一次测量数据
            layoutWidth = _layout.getWidth();
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


    /**
     * 获取行数据集合
     * @param content
     * @param width
     * @return
     */
    private ArrayList<String> calculateLines(String content, int width) {
        contentList.clear();
        int length = content.length();
        float thisLineDrawWidth = textPaint.measureText(content);
        if (thisLineDrawWidth <= width) {
            contentList.add(content);
            return contentList;
        }
        int start = 0, end = 1;
        while (start < length) {
            if (textPaint.measureText(content, start, end) > width) {
                String lineText = content.substring(start, end - 1);
                contentList.add(lineText);
                start = end - 1;
            } else if (end < length) {
                end++;
            }
            if (end == length) {
                String lastLineText = content.subSequence(start, end).toString();
                contentList.add(lastLineText);
                break;
            }
        }
        return contentList;
    }


    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        int thisLineDrawWidth = 0;//本行的宽度
        int count = 0;//用于记录measure了几次

        OUT_FOR:
        for (int i = 0; i < curList.size(); i++) {
            int index = 0;
            int totalOldIndex = 0;//用于记录一行的开头Index
            int oldIndex = 0;//用来记录上一行measure了几个字
            int oldNums = 0;//记录已经绘制了多少个字

            Object o = curList.get(i);
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

                    if(ellipsizeLineNum == -1){
                        canvas.drawText(str, totalOldIndex, totalOldIndex + index, thisLineDrawWidth, -rect.top + (_lineHeight + line_space_height) * count, textPaint);
                    }else{
                        if((count + 1) < ellipsizeLineNum){
                            canvas.drawText(str, totalOldIndex, totalOldIndex + index, thisLineDrawWidth, -rect.top + (_lineHeight + line_space_height) * count, textPaint);
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
                                            -rect.top + (_lineHeight + line_space_height) * count,
                                            textPaint
                                    );
                                    break OUT_FOR;
                                }
                                //否则正常绘制当前字符串
                                canvas.drawText(str, totalOldIndex, totalOldIndex + index, thisLineDrawWidth, -rect.top + (_lineHeight + line_space_height) * count, textPaint);
                            }else{
                                String lastLineContent = getLastLineContent(substring, layoutWidth - thisLineDrawWidth);
                                canvas.drawText(
                                        lastLineContent,
                                        thisLineDrawWidth,
                                        -rect.top + (_lineHeight + line_space_height) * count,
                                        textPaint
                                );
                                break OUT_FOR;
                            }
                        }
                    }

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
                    float marginBottom = bitmapPackageBean.getMarginBottom();
                    float align = bitmapPackageBean.getAlign();
                    float setImageWidth = bitmapPackageBean.getWidth();
                    float setImageHeight = bitmapPackageBean.getHeight();

                    //水平方向缩放比例
                    float xScale = 1.0f;
                    //竖直方向缩放比例
                    float yScale = 1.0f;
                    //图片绘制宽度
                    float measureImageWidth;

                    //如果有设置图片宽高,按照缩放比例进行显示
                    if(setImageWidth > 0 && setImageHeight > 0){
                        xScale = setImageWidth / imageWidth;
                        imageWidth = setImageWidth;

                        yScale = setImageHeight / imageHeight;
                        imageHeight = setImageHeight;

                        measureImageWidth = imageWidth;
                    }else{ //如果没设置图片宽高，图片与文字等高，宽度等比缩放
                        //宽高比例
                        float radio = imageWidth / imageHeight;
                        //图片绘制宽度
                        measureImageWidth = radio * _lineHeight;
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
                    float top = count * (_lineHeight + line_space_height);
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
//                        canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);

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
                        }else{
                            canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
                        }



                    }else{
//                        if((count + 1) < ellipsizeLineNum){
//                            canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
//                        }else{
//                            if((thisLineDrawWidth + measureImageWidth) >= layoutWidth){
//                                canvas.drawText(
//                                        THREE_POINTS,
//                                        thisLineDrawWidth,
//                                        -rect.top + (_lineHeight + line_space_height) * count,
//                                        textPaint
//                                );
//                                break OUT_FOR;
//                            }else{
//                                int nextItemWidth = getNextItemWidth(i + 1);
//                                if((thisLineDrawWidth + measureImageWidth + nextItemWidth) >= layoutWidth){
//                                    canvas.drawText(
//                                            THREE_POINTS,
//                                            thisLineDrawWidth,
//                                            -rect.top + (_lineHeight + line_space_height) * count,
//                                            textPaint
//                                    );
//                                    break OUT_FOR;
//                                }
//                                canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);
//                            }
//                        }
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

    }

    public int floatToInt(float f){
        int i = 0;
        if(f>0) //正数
        {
            i = (int)(f*10 + 5)/10;
        }
        else if(f<0) //负数
        {
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
        Object o = curList.get(i);
        if(o instanceof String){
            String str = (String) o;
            return getTextWidth(str);
        }else if(o instanceof BitmapPackageBean){
            BitmapPackageBean bitmapPackageBean = (BitmapPackageBean) o;
            Bitmap bitmap = bitmapPackageBean.getBitmap();
            if(bitmap != null){
                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();
                //宽高比例
                int radio = imageWidth / imageHeight;
                //图片绘制宽度
                return radio * _lineHeight;
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

//        Rect rect = new Rect();
//        new Paint().getTextBounds(str, 0, str.length(), rect);
//        int w = rect.width();
//        int h = rect.height();
//        return w;
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
        curList.addAll(Arrays.asList(values));
//        requestLayout();
        invalidate();
    }



}
