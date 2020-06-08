package com.zippsun.imageutil;

import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * ================================================
 * 作    者：hezhipeng
 * 版    本：
 * 创建日期：2020/6/8
 * 描    述：对大图片内存地址复用
 * 修订历史：
 * ================================================
 */
public class ImageUtil {
    private static final String TAG = "ImageUtil";
    private Bitmap btp;

    private ImageUtil() {
    }

    private static class I {
        private static ImageUtil sImageUtil = new ImageUtil();
    }

    public static ImageUtil getInstance() {
        return I.sImageUtil;
    }


    private boolean canUserForInBitmap(Bitmap candidate, BitmapFactory.Options targetOptions) {
        int width = (int) (targetOptions.outWidth / Math.max(targetOptions.inSampleSize, 1) + 0.5f);
        int height = (int) (targetOptions.outHeight / Math.max(targetOptions.inSampleSize, 1) + 0.5f);
        int byteCount = width * height * (getBytesPerpixel(candidate.getConfig()));
        return byteCount <= candidate.getAllocationByteCount();
    }

    private int getBytesPerpixel(Bitmap.Config config) {
        int bytesPerPixel;
        switch (config) {
            case ALPHA_8:
                bytesPerPixel = 1;
                break;
            case RGB_565:
            case ARGB_4444:
                bytesPerPixel = 2;
                break;
            default:
                bytesPerPixel = 4;
                break;
        }
        return bytesPerPixel;
    }

    /**
     * 复用Assets 目录下图片路径
     *
     * @param context
     * @param imagePath
     * @return
     */
    public Bitmap getBitMapByPath(Context context, String imagePath) {
        Log.i(TAG, "setImageResurce imagePath = " + imagePath);
        FileInputStream fileInputStream = null;
        try {
            AssetFileDescriptor fileDescriptor = context.getResources().
                    getAssets().openFd(imagePath);
            fileInputStream = fileDescriptor.createInputStream();
            if (fileInputStream == null) {
                return null;
            }
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;

            BitmapFactory.decodeFileDescriptor(fileInputStream.getFD(),
                    null, options);
            if (btp != null && canUserForInBitmap(btp, options)) {
                options.inMutable = true;
                options.inBitmap = btp;
                Log.i(TAG, "canUserForInBitmap: true ");
            }

            options.inJustDecodeBounds = false;
            btp = BitmapFactory.decodeFileDescriptor(fileInputStream.getFD(),
                    null, options);

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            IoUtil.getInstance().close(fileInputStream);
        }
        return btp;
    }

    /**
     * 复用res 目录下图片
     *
     * @param context
     * @param imageResuceId
     * @return
     */
    public Bitmap getBitMapById(Context context, int imageResuceId) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeResource(context.getResources(), imageResuceId, options);
        if (btp != null && canUserForInBitmap(btp, options)) {
            options.inMutable = true;
            options.inBitmap = btp;
            Log.i(TAG, "canUserForInBitmap: true ");
        }

        options.inJustDecodeBounds = false;
        btp = BitmapFactory.decodeResource(context.getResources(), imageResuceId, options);
        return btp;
    }


    /**
     *
     * @param originalW 原图宽
     * @param originalH 原图高
     * @param pixelW 指定图片宽度
     * @param pixelH 指定图片高度
     * @return 返回原图缩放大小
     */
    private int getSampleSize(int originalW, int originalH, int pixelW, int pixelH) {
        int simpleSize = 1;
        if (originalW > originalH && originalW > pixelW) {
            simpleSize = originalW / pixelW;
        }else if (originalH > originalW && originalH > pixelH){
            simpleSize = originalH / pixelH;
        }
        if (simpleSize <= 0) {
            simpleSize = 1;
        }
        Log.e("Bitmap", "simpleSize" + simpleSize);
        return simpleSize;
    }

}
