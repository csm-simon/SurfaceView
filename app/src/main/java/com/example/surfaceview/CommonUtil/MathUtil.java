package com.example.surfaceview.CommonUtil;

import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.text.TextUtils;

/**
 * 数学计算类
 * Created by simon
 */

public class MathUtil {

    /**
     * 判断字符串是否是正数字
     * @param str
     * @return
     */
    public static boolean isNumber(@Nullable String str) {
        if (TextUtils.isEmpty(str)) {
            return false;
        }
        for (int i = 0; i < str.length(); i++) {
            if (!Character.isDigit(str.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获取两个向量之间的角度
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float getAngle(float x1, float y1, float x2, float y2) {
        float len = (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
        float angle = (float) Math.acos((y2 - y1) / len);
        if (x2 - x1 < 0) {
            angle = 2 * (float) Math.PI - angle;
        }
        return angle;
    }

    /**
     * 获取两点之间距离
     * @param x1
     * @param y1
     * @param x2
     * @param y2
     * @return
     */
    public static float getDistance(float x1, float y1, float x2, float y2) {
        return (float) Math.sqrt(Math.pow(x2 - x1, 2) + Math.pow(y2 - y1, 2));
    }

    /**
     * 判断两个浮点数是否相等
     * @param a
     * @param b
     * @return
     */
    public static boolean isEqual(float a, float b) {
        return Math.abs(a - b) < 0.001f;
    }

    /**
     * 判断两个矩形是否相等
     * @param r1
     * @param r2
     * @return
     */
    public static boolean isEqual(RectF r1, RectF r2) {
        if (r1 == null || r2 == null) {
            return false;
        }
        return isEqual(r1.left, r2.left) && isEqual(r1.right, r2.right) && isEqual(r1.top, r2.top)
            && isEqual(r1.bottom, r2.bottom);
    }

    /**
     * 缩放矩阵
     * @param rectF
     * @param scaleX
     * @param scaleY
     */
    public static void scaleRectF(RectF rectF, float scaleX, float scaleY) {
        if (rectF != null) {
            rectF.left *= scaleX;
            rectF.right *= scaleX;
            rectF.top *= scaleY;
            rectF.bottom *= scaleY;
        }
    }

    /**
     * 将一个长数字的字符串转换为int型。
     * @param intString
     * @return
     */
    public static int toInt(String intString) {
        if (!isNumber(intString)) {
            return -1;
        }
        // 32位int型大概是一个10位数，区字符串后9位，不超过int的取值范围。
        return Integer.parseInt(intString.substring(Math.max(0, intString.length() - 9), intString.length()));
    }
}
