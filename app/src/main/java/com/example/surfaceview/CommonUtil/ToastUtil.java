package com.example.surfaceview.CommonUtil;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

public class ToastUtil {

    // add by zengbiyu
    private static Toast toast;

    // add end

    /**
     * 提示消息
     * 
     * @param context
     * @param s
     *            消息
     */
    public static void show(Context context, String s) {
        Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示消息
     * 
     * @param context
     * @param res
     *            消息
     */
    public static void show(Context context, int res) {
        Toast.makeText(context, res, Toast.LENGTH_SHORT).show();
    }

    /**
     * 提示消息
     * add by zengbiyu 
     * 多次点击不会重复显示Toast
     * @param context
     * @param res
     *            消息
     */
    public static void showOnceToast(Context context, int res) {
        if (toast == null) {
            toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        } else {
            toast.setText(res);
        }
        toast.show();
    }

    // add end

    /**
     * 提示消息控制显示位置，显示位置与B+ V3.0高级美颜图片位置底端对齐。
     * 
     * @param context
     * @param res
     *            消息
     */
    public static void showAlignImage(Context context, int res) {
        if (toast == null) {
            toast = Toast.makeText(context, res, Toast.LENGTH_SHORT);
        } else {
            toast.setText(res);
        }
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, DeviceUtil.getScreenHeight(context) / 2);
        toast.show();
    }

    /**
     * 提示消息控制显示位置，显示位置与B+ V3.0高级美颜图片位置底端对齐。
     * 
     * @param context
     * @param s
     *            消息
     */
    public static void showAlignImage(Context context, String s) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, DeviceUtil.getScreenHeight(context) / 2);
        toast.show();
    }

    /**
     * toast默认底部 - 中间居中，根据传进来的height参数调整toast高度
	 *
     * @param context 上下文
     * @param s 文案
     * @param height 高度
     */
    public static void showAlignHeight(Context context, String s, int height) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.setGravity(Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, height);
        toast.show();
    }

    /**
     * toast默认底部 - 中间居中，根据传进来的height参数调整toast高度
     *
     * @param context 上下文
     * @param s 文案
     * @param height 高度
     */
    public static void showScreenTop(Context context, String s, int height) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, height);
        toast.show();
    }

    public static void showScreen(Context context, String s, int x, int y) {
        if (toast == null) {
            toast = Toast.makeText(context, s, Toast.LENGTH_SHORT);
        } else {
            toast.setText(s);
        }
        toast.setGravity(Gravity.TOP, x, y);
        toast.show();
    }

    public static void release() {
        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }
}
