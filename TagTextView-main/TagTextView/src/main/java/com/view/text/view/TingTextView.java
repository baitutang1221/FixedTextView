package com.view.text.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.Layout;
import android.text.Spannable;
import android.text.Spanned;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;

import androidx.appcompat.widget.AppCompatTextView;

import com.view.text.bean.BitmapPackageBean;
import com.view.text.span.CenterImageSpan;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * 无内边距TextView控件
 * @author liming Create date ： 2019/01/30
 */
public class TingTextView extends AppCompatTextView {

    //日志标记
    private final String TAG = TingTextView.class.getSimpleName();
    //文本画笔
    private TextPaint textPaint;
    //绘制矩形
    private Rect rect;
    //默认宽度
    private int layoutWidth = -1;
    //获得每行数据
//    private String[] lineContents;
    //获取行间距的额外空间
    private float line_space_height = 0.0f;
    //获取行间距乘法器
    private float line_space_height_mult = 1.0f;
    //获得每行数据集合
    private final ArrayList<String> contentList = new ArrayList<>(0);
    //绘制的内容
    private List<Object> curList = new ArrayList<>();

    /**
     * 构造方法
     *
     * @param context
     * @param attrs
     */
    public TingTextView(Context context, AttributeSet attrs) {
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
    public TingTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
//        textPaint.getTextBounds(getText(), 0, getText().length(), tempBoundRect);

        mBitPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    }

    private int _lineHeight, widthMeasureSpec, heightMeasureSpec;

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
            //获取行数据
//            getTextContentData(_layout);
            //获得行高
            _lineHeight = -rect.top + rect.bottom;
            //初始化布局
            initLayout(_layout);
            //获取行数据集合
            calculateLines(_tvContent,layoutWidth);

            this.widthMeasureSpec = widthMeasureSpec;
            this.heightMeasureSpec = heightMeasureSpec;



            //设置布局区域
//            int[] _area = getWidthAndHeight(widthMeasureSpec, heightMeasureSpec, layoutWidth, _layout.getLineCount(), _lineHeight);
//            //设置布局
//            setMeasuredDimension(_area[0], 1000);


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
            Log.d("xiazhenjie","layoutWidth=" + layoutWidth);
            Log.d("xiazhenjie","getHeight=" + _layout.getHeight());
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
     * 获取行数据
     *
     * @param _layout 文本布局对象（注：该布局其实使用的是Layout子类对象StaticLayout）
     */
//    private void getTextContentData(Layout _layout) {
//        //初始化行数据
//        lineContents = new String[_layout.getLineCount()];
//        //获得每行数据
//        for (int i = 0; i < _layout.getLineCount(); i++) {
//            int _start = _layout.getLineStart(i);
//            int _end = _layout.getLineEnd(i);
//            lineContents[i] = getText().subSequence(_start, _end).toString();
//        }
//    }


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

    private int hasDrawTextNums = 0;
    CenterImageSpan[] spans;
    private String THIS_TEXT;
    private float width, height;
    private StaticLayout staticLayout;
    final Rect tempBoundRect = new Rect();

    private Paint mBitPaint;


    private float[] savedWidths = new float[1];
    List<CenterImageSpan> strList = new ArrayList<>();

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = getWidth();
        height = getHeight();
//        staticLayout = new StaticLayout(getText(), textPaint, ((int) width), Layout.Alignment.ALIGN_NORMAL, 1, 0, false);
    }


        @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        //行高
        float _line_height = -rect.top + rect.bottom;
        //行间距
        float _line_space = line_space_height * line_space_height_mult;
        //设置的最大行数
        int maxLines = getMaxLines();

        onDrawImageText(canvas);


//======================================================================================================================================================
//==================================================       分割线           ============================================================================
//======================================================================================================================================================


//        measureP2(canvas);


//======================================================================================================================================================
//==================================================       分割线           ============================================================================
//======================================================================================================================================================


//        if(maxLines != -1 && maxLines < contentList.size()){
//            for (int i = 0; i < maxLines; i++) {
//                //获得数据
//                String _drawContent = contentList.get(i);
//                //显示日志
//                Log.e(TAG, "LINE[" + (i + 1) + "]=" + _drawContent);
//                if(i != maxLines - 1){
//                    //绘制每行数据
//                    canvas.drawText(
//                            _drawContent,
//                            0,
//                            -rect.top + (_line_height + _line_space) * i,
//                            textPaint
//                    );
//                }else{
//                    canvas.drawText(
//                            getLastLineContent(_drawContent),
//                            0,
//                            -rect.top + (_line_height + _line_space) * i,
//                            textPaint
//                    );
//                }
//            }
//        }else{
//            for (int i = 0; i < contentList.size(); i++) {
//                //获得数据
//                String _drawContent = contentList.get(i);
//                //显示日志
//                Log.e(TAG, "LINE[" + (i + 1) + "]=" + _drawContent);
//
////                for (int j = 0; j < imagePositions.size(); j++) {
////                    Integer integer = imagePositions.get(j);
////                    if(integer <= hasDrawTextNums){
////                        String substring = _drawContent.substring(0, integer);
////                        canvas.drawText(
////                                substring,
////                                0,
////                                -rect.top + (_line_height + _line_space) * i,
////                                textPaint
////                        );
////                        int thisLineDrawWidth = getTextWidth(substring);
////                        CenterImageSpan span = spans[integer];
////                        span.draw(canvas, "", 0, 0, 30 + thisLineDrawWidth, 0, 40 , 0, new Paint());
////                    }
////                }
//
//                //绘制每行数据
//                canvas.drawText(
//                        _drawContent,
//                        0,
//                        -rect.top + (_line_height + _line_space) * i,
//                        textPaint
//                );
//            }
//        }

//        super.onDraw(canvas);

    }

    /**
     * 自己纯手动的，不引用别人源码
     * @param canvas
     */
    private void onDrawImageText(Canvas canvas){

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
                    index = textPaint.breakText(str, totalOldIndex, str.length(), true, width - thisLineDrawWidth, savedWidths);
                    if(index == 0)
                        break;

                    //绘制文字
                    canvas.drawText(str, totalOldIndex, totalOldIndex + index, thisLineDrawWidth, 40 + textPaint.getFontSpacing() * count, textPaint);

                    //更新当行的已绘制宽度
                    thisLineDrawWidth += getTextWidth(str.substring(totalOldIndex, totalOldIndex + index));

                    //更新已绘制的总字数 (仅限当前字符串)
                    oldNums += index;

                    if((totalOldIndex + index) != str.length()){
                        count++;
                        thisLineDrawWidth = 0;
                    }

                }while (/*oldIndex != index &&*/ width != 0 && !TextUtils.isEmpty(str) && (oldNums != str.length() && index != 0));
            }
            else if(o instanceof BitmapPackageBean){
                BitmapPackageBean bitmapPackageBean = (BitmapPackageBean) o;
                Bitmap bitmap = bitmapPackageBean.getBitmap();

                int imageWidth = bitmap.getWidth();
                int imageHeight = bitmap.getHeight();

                //宽高比例
                int radio = imageWidth / imageHeight;

                //行高
                float _line_height = -rect.top + rect.bottom;
                Log.d("xiazhenjie","_line_height=" + _line_height);

                //图片绘制高度
                float measureImageWidth = radio * _line_height;
                Log.d("xiazhenjie","measureImageWidth=" + _line_height);

                if(thisLineDrawWidth + measureImageWidth > width){
                    count++;
                }

                Rect mSrcRect = new Rect(0, 0, (int) imageWidth , (int) imageHeight);
                Rect mDestRect = new Rect(thisLineDrawWidth, (int) (textPaint.getFontSpacing() * count), (int) measureImageWidth + thisLineDrawWidth , (int) _line_height + (int) (textPaint.getFontSpacing() * count));
                canvas.drawBitmap(bitmap, mSrcRect, mDestRect, mBitPaint);

                thisLineDrawWidth += measureImageWidth;
                if(thisLineDrawWidth >= width){
                    thisLineDrawWidth = 0;
                }


                //分割


//                BitmapPackageBean bitmapPackageBean = (BitmapPackageBean) o;
//                Bitmap bitmap = bitmapPackageBean.getBitmap();
//
//                CenterImageSpan centerImageSpan = new CenterImageSpan(new BitmapDrawable(bitmap));
//                int imageWidth = centerImageSpan.getDrawable().getIntrinsicWidth();
//                int imageHeight = centerImageSpan.getDrawable().getIntrinsicHeight();
//                if(thisLineDrawWidth + imageWidth > width){
//                    count++;
//                }
//                centerImageSpan.draw(canvas, "", 0, 0, thisLineDrawWidth, 0, (int) (40 + textPaint.getFontSpacing() * count), 0, new Paint());
//                thisLineDrawWidth += imageWidth;
//                if(thisLineDrawWidth >= width){
//                    thisLineDrawWidth = 0;
//                }



            }
        }

        //设置布局区域
        int[] _area = getWidthAndHeight(widthMeasureSpec, heightMeasureSpec, layoutWidth, count + 1, _lineHeight);
        //设置布局
        setMeasuredDimension(_area[0], _area[1]);
    }

    private void measureP2(Canvas canvas){
        if (getText() instanceof Spanned) {
            final Spannable text = (Spannable) getText();
            spans = text.getSpans(0, getText().length(), CenterImageSpan.class);
            for (int i = 0; i < spans.length; i++) {
                CenterImageSpan span = spans[i];
                int position = span.getPosition();
                Log.d("xiazhenjie","position==" + position);
                strList.add(span);
//                span.draw(canvas, "", 0, 0, 30, 0, 40 * (i+1), 0, new Paint());
            }
        }

        THIS_TEXT = getText().toString().replaceAll("nbspttt","");
        Log.d("xiazhenjie","THIS_TEXT.length() = " + THIS_TEXT.length());


        int index = 0;
        int oldIndex = 0;//用来记录上一行measure了几个字
        int count = 0;//用于记录measure了几次
        int totalOldIndex = 0;//用于记录一行的开头Index
//        int totalHeight = 0;//用于记录当前文字的总高度
        int oldNums = 0;//记录已经绘制了多少个字

        do {
            oldIndex = index;
//            totalOldIndex += oldIndex;
            totalOldIndex = oldNums;
            //这里画文字的时候，需要考虑，当前文字的上下边界是否与图片有重合的地方
//            totalHeight += tempBoundRect.height();
//            Log.e("test", "totalHeight:" + totalHeight);

            index = textPaint.breakText(THIS_TEXT, totalOldIndex, THIS_TEXT.length(), true, width, savedWidths);

            Iterator<CenterImageSpan> iterator = strList.iterator();
            boolean hasImage = false;
            int thisLineHasDrawWidth = 0; //当前行已经绘制的宽度

            while (iterator.hasNext()){

                CenterImageSpan span = iterator.next();

                if(span.getPosition() < (totalOldIndex + index)){
                    hasImage = true;

                    //先绘制最左侧文字
                    canvas.drawText(THIS_TEXT, oldNums, oldNums + span.getPosition(), thisLineHasDrawWidth, 40 + textPaint.getFontSpacing() * count, textPaint);
//                    oldNums = totalOldIndex + index;
                    Log.d("xiazhenjie","span.getPosition()=" + span.getPosition());
                    oldNums = span.getPosition();
                    Log.d("xiazhenjie","oldNums1 =" + oldNums);

                    //记录文字的宽度
                    thisLineHasDrawWidth += getTextWidth(THIS_TEXT.substring(totalOldIndex,totalOldIndex + span.getPosition()));

                    //绘图
                    span.draw(canvas, "", 0, 0, thisLineHasDrawWidth, 0, (int) (40 + textPaint.getFontSpacing() * count), 0, new Paint());

                    //记录图片宽度
                    thisLineHasDrawWidth += span.getDrawable().getIntrinsicWidth();
                    iterator.remove();
                }
            }

            if(hasImage && thisLineHasDrawWidth < width){
                Log.d("xiazhenjie","oldNums2 =" + oldNums);

                int index2 = textPaint.breakText(THIS_TEXT, oldNums, THIS_TEXT.length(), true, width - thisLineHasDrawWidth, savedWidths);
                canvas.drawText(THIS_TEXT, oldNums, oldNums + index2, thisLineHasDrawWidth, 40 + textPaint.getFontSpacing() * count, textPaint);
                oldNums += index2;
                Log.d("xiazhenjie","oldNums2-2 =" + oldNums);

                //记录文字的宽度
//                thisLineHasDrawWidth += getTextWidth(THIS_TEXT.substring(oldNums,oldNums + index2));
                thisLineHasDrawWidth = (int) width;

            }else{
                Log.d("xiazhenjie","oldNums3 =" + oldNums);

                canvas.drawText(THIS_TEXT, oldNums, oldNums + index, 0, 40 + textPaint.getFontSpacing() * count, textPaint);
                oldNums += index;
                Log.d("xiazhenjie","oldNums3-2 =" + oldNums);

            }

            count++;

        }
        while (/*oldIndex != index && */ width != 0 && !TextUtils.isEmpty(THIS_TEXT) && oldNums != THIS_TEXT.length());
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
        requestLayout();
        invalidate();
    }



}
