package com.example.surfaceview.CameraGLSurfaceView.camera;

import android.support.annotation.IntDef;
import java.io.*;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import com.example.surfaceview.CameraGLSurfaceView.camera.SimonCamera.*;
import com.example.surfaceview.CommonUtil.MathUtil;
import static com.example.surfaceview.CommonUtil.DeviceUtil.getScreenHeight;
import static com.example.surfaceview.CommonUtil.DeviceUtil.getScreenWidth;

/**
 * 相机预览分辨率选择器
 */
public class CameraSizeSelector {
    public static final String TAG = CameraSizeSelector.class.getSimpleName();

    public static final int LOW = 0;
    public static final int HEIGHT = 1;

    @IntDef({LOW, HEIGHT})
    public @interface PreviewQuality {
    }

    public static PreviewSize getMaxCameraPreviewSize(float ratio, @PreviewQuality int quality) {
        if (quality == LOW) {
            if (Math.abs(ratio - 4 / 3f) < Math.abs(ratio - 16 / 9f)) { // 4:3或者1:1取最适合的4:3尺寸
                return getmem_TOLAL() < 1024 ? new SimonCamera.PreviewSize(800, 600)
                    : new PreviewSize(960, 720);
            } else { // 全屏取最合适的16:9尺寸
                return getmem_TOLAL() < 1024 ? new PreviewSize(864, 480)
                    : new PreviewSize(960, 540);
            }
        } else {
            return getmem_TOLAL() < 1024 ? new PreviewSize(960, 720)
                : new PreviewSize(1440, 1080);
        }

    }

    public static PreviewSize getMinCameraPreviewSize(@PreviewQuality int quality) {
        if (quality == LOW) {
            return getmem_TOLAL() < 1024 ? new PreviewSize(480, 360)
                : new PreviewSize(640, 480);
        } else {
            return getmem_TOLAL() < 1024 ? new PreviewSize(540, 420)
                : new PreviewSize(640, 480);
        }
    }

    public static PictureSize getCameraPictureSize(List<PictureSize> pictureSizes,
                                                   List<PreviewSize> previewSizes, float previewRatio, float pictureRatio, @PreviewQuality int quality) {
        if (pictureSizes == null || pictureSizes.isEmpty()) {
            return null;
        }

        PreviewSize maxSize = getMaxCameraPreviewSize(previewRatio, quality);
        PreviewSize minSize = getMinCameraPreviewSize(quality);
        // 清除预览比例中不合适的预览尺寸。
        List<Size> removeSizes = new ArrayList<>();
        for (Size previewSize : previewSizes) {
            if (previewSize.width * previewSize.height > getScreenWidth() * getScreenHeight()
                || previewSize.height * previewSize.width > maxSize.height * maxSize.width
                || previewSize.height * previewSize.width < minSize.height * minSize.width) {
                removeSizes.add(previewSize);
            }
        }
        if (!removeSizes.isEmpty()) {
            previewSizes.removeAll(removeSizes);
        }
        // 对原本的列表进行重排序。
        reorderPictureSizes(pictureSizes, previewRatio, pictureRatio);
        // 寻找合适的拍照尺寸，遵循几个原则：
        // 1、必须大于最小拍摄尺寸，根据ImageQuality来决定。
        // 2、预览尺寸和拍摄尺寸比例要一致。
        // 3、比例尽量与bestRatio一致
        PictureSize pictureSize = null;
        float poor = 0.0001f;
        do {
            for (PictureSize cameraSize : pictureSizes) {
                // 这个循环是遍历所有的拍摄尺寸，根据条件筛选
                if (Math.abs(previewRatio - (float) cameraSize.width / cameraSize.height) < poor) {
                    boolean find = false;
                    for (PreviewSize previewSize : previewSizes) {
                        // 查找与拍摄尺寸比例一致的预览尺寸。
                        if (isEqual(previewSize.height / (float) previewSize.width, cameraSize.height
                            / (float) cameraSize.width)) {
                            find = true;
                            break;
                        }
                    }
                    if (find) {
                        pictureSize = cameraSize;
                        break;
                    }
                }
            }
            // 逐渐增加与bestRatio的容忍度
            poor += 0.0005;
        } while (pictureSize == null);

        return pictureSize;
    }

    /**
     * 获得相机预览分辨率
     * @param list
     * @return
     */
    public static PreviewSize getCameraPreviewSizes(List<PreviewSize> list, float ratio,
                                                             @PreviewQuality int quality) {
        // 选出符合屏幕分辨率比例的size
        PreviewSize previewSize = null;
        Size maxSize = getMaxCameraPreviewSize(ratio, quality);
        Size minSize = getMinCameraPreviewSize(quality);

        // 精确查找
        boolean result = false;
        for (int i = list.size() - 1; i >= 0; i--) {
            previewSize = list.get(i);
            if (previewSize.height * previewSize.width > maxSize.height * maxSize.width
                || previewSize.height * previewSize.width < minSize.height * minSize.width
                || previewSize.height * previewSize.width > getScreenWidth()
                    * getScreenHeight()) {
                continue;
            }
            if (isEqual(previewSize.width / (float) previewSize.height, ratio) && isSafeValue(previewSize)) {
                result = true;
                break;
            }
        }

        // 精确查找失败, 则近似查找
        if (!result) {
            for (int i = list.size() - 1; i >= 0; i--) {
                previewSize = list.get(i);
                if (previewSize.height * previewSize.width > maxSize.height * maxSize.width) {
                    continue;
                }
                if (isEqual(previewSize.width / (float) previewSize.height, ratio)) {
                    result = true;
                    break;
                }
            }
        }
        if (previewSize == null || !result) {
            // previewSize = new CameraSize(864, 480);
            previewSize = list.get(0);
        }
        return previewSize;
    }

    /**
     * 对pictureSize进行重新排序
     * @param pictureSizes 从小到大的拍摄尺寸列表，排序结束后，此列表为：前半部分是大于最小拍摄尺寸从小到大排序，后半部分是小于最小拍摄尺寸的从大到小排序。
     * @param previewRatio
     * @param pictureRatio
     */
    private static void reorderPictureSizes(List<PictureSize> pictureSizes, float previewRatio,
                                            float pictureRatio) {
        // 获取拍摄图片的最大边长作为拍摄的最小尺寸
        Collections.sort(pictureSizes, new Comparator<PictureSize>() {
            @Override
            public int compare(PictureSize o1, PictureSize o2) {
                if (o1 == null || o2 == null) {
                    return 1;
                }
                if (o1.width * o1.height > o2.width * o2.height) {
                    return -1;
                }
                return 1;
            }
        });

    }

    /**
     * 获取当前拍摄尺寸下拍摄出来的图片。
     * @param pictureSize
     * @param previewRatio
     * @param pictureRatio
     * @return
     */
    private static int getRealPictureSizeByRatio(Size pictureSize, float previewRatio, float pictureRatio) {
        int width = pictureSize.width;
        int height = pictureSize.height;
        if (!MathUtil.isEqual(width / (float) height, previewRatio)) {
            if (width > height * previewRatio) {
                width = Math.round(height * previewRatio);
            } else {
                height = Math.round(width / previewRatio);
            }
        }
        if (!MathUtil.isEqual(width / (float) height, pictureRatio)) {
            if (width > height * pictureRatio) {
                width = Math.round(height * pictureRatio);
            } else {
                height = Math.round(width / pictureRatio);
            }
        }

        return Math.max(width, height);
    }

    private static boolean isEqual(float num1, float num2) {
        return Math.abs(num1 - num2) < 0.001;
    }

    // 判断宽高是否是16倍数
    private static boolean isSafeValue(Size size) {
        if (size == null) {
            return false;
        }
        return size.width % 16 == 0 && size.height % 16 == 0;
    }

    // 获得总内存
    public static long getmem_TOLAL() {
        long mTotal;
        // /proc/meminfo读出的内核信息进行解释
        String path = "/proc/meminfo";
        String content = null;
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(path), Charset.defaultCharset()), 8);
            String line;
            if ((line = br.readLine()) != null) {
                content = line;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        // beginIndex
        int begin = content.indexOf(':');
        // endIndex
        int end = content.indexOf('k');
        // 截取字符串信息

        content = content.substring(begin + 1, end).trim();
        mTotal = Integer.parseInt(content);
        return mTotal / 1024;
    }
}
