package com.like.tag.views;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;

import androidx.appcompat.widget.AppCompatTextView;

import com.view.text.bean.BitmapPackageBean;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 无内边距TextView控件
 * @author liming Create date ： 2019/01/30
 */
public class TingTextViewClear extends AppCompatTextView {

    //日志标记
    private final String TAG = TingTextViewClear.class.getSimpleName();
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

//    private float width, height;

    private Paint mBitPaint;

    private final float[] savedWidths = new float[1];

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public TingTextViewClear(Context context, AttributeSet attrs) {
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
    public TingTextViewClear(Context context, AttributeSet attrs, int defStyleAttr) {
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
                    if(index == 0)
                        break;

                    //更新当行的已绘制宽度
                    thisLineDrawWidth += getTextWidth(str.substring(totalOldIndex, totalOldIndex + index));

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
                    //图片绘制高度
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
            }
        }

        //设置布局区域
        int[] _area = getWidthAndHeight(widthMeasureSpec, heightMeasureSpec, layoutWidth, count + 1, _lineHeight);
        //设置布局
        setMeasuredDimension(_area[0], _area[1]);
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


//    @Override
//    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
//        super.onSizeChanged(w, h, oldw, oldh);
//        width = getWidth();
//        height = getHeight();
//    }


        @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {

        //行高
        float _line_height = -rect.top + rect.bottom;
        //行间距
        float _line_space = line_space_height * line_space_height_mult;
        //设置的最大行数
        int maxLines = getMaxLines();

        int thisLineDrawWidth = 0;//本行的宽度
        int count = 0;//用于记录measure了几次

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
                    if(index == 0)
                        break;

                    //绘制文字
                    canvas.drawText(str, totalOldIndex, totalOldIndex + index, thisLineDrawWidth, -rect.top + (_lineHeight + line_space_height) * count, textPaint);

                    //更新当行的已绘制宽度
                    thisLineDrawWidth += getTextWidth(str.substring(totalOldIndex, totalOldIndex + index));

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

                    //图片绘制高度
                    float measureImageWidth = radio * _lineHeight;

                    if(thisLineDrawWidth + measureImageWidth > layoutWidth){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                    Rect mSrcRect = new Rect(
                            0,
                            0,
                            (int) imageWidth ,
                            (int) imageHeight
                    );

                    int top = (int) (-rect.top + (count-1) * _lineHeight + line_space_height * count + 4);
                    int bottom = (int) (-rect.top + (_lineHeight + line_space_height) * count + 4);

                    Rect mDestRect = new Rect(
                            thisLineDrawWidth,
                            top,
                            (int) measureImageWidth + thisLineDrawWidth ,
                            bottom
                    );
                    canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);

                    thisLineDrawWidth += measureImageWidth;
                    if(thisLineDrawWidth >= layoutWidth){
                        thisLineDrawWidth = 0;
                        count++;
                    }
                }

            }
        }

    }


    /**
     * 获取最后一行的字符串内容
     * @param _drawContent
     * @return
     */
    private String getLastLineContent(String _drawContent){
        String tempStr = _drawContent;
        String threePointString = "...";
        do{
            int thisLineDrawWidth = getTextWidth(tempStr + threePointString);
            if(thisLineDrawWidth < layoutWidth){
                break;
            }
            //获取字符串长度
            int codePointCount = tempStr.codePointCount(0, tempStr.length());
            tempStr = subStringFun(tempStr, codePointCount - 1);
        }while (true);
        return tempStr + threePointString;
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
