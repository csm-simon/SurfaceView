package com.example.surfaceview.SurfaceView;

import android.graphics.*;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;

public class DrawTextUtil {

    /**
     * 可运用于用OpenGL实现弹幕效果
     */

    private Canvas mCanvas;

    private TextPaint mPaint;

    public DrawTextUtil() {
        initView();
    }

    private void initView() {
        mPaint = setUpMyTextPaint();
    }

    //设置画笔的字体和颜色
    private TextPaint setUpMyTextPaint(){
        TextPaint textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DEV_KERN_TEXT_FLAG);// 设置画笔
        textPaint.setTextSize(30);// 字体大小
        textPaint.setTypeface(Typeface.DEFAULT_BOLD);// 采用默认的宽度
        textPaint.setColor(Color.argb(255,0,0,0));// 采用的颜色
        return textPaint;
    }

    //写入文字，自动换行的方法
    public Bitmap drawText(String textString,int x,int y,int width,int height) {
        Bitmap bitmap = Bitmap.createBitmap(width,height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(bitmap);
        StaticLayout staticLayout = new StaticLayout(textString, setUpMyTextPaint(), width,
                Layout.Alignment.ALIGN_NORMAL, 1.5f, 0.0f, false);

        //绘制的位置
        mCanvas.translate(0, 0);
        mCanvas.drawColor(Color.RED);
        staticLayout.draw(mCanvas);

        return bitmap;
    }

}
