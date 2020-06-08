package com.zippsun.imageutil;

import java.io.IOException;
import java.io.InputStream;

/**
 * ================================================
 * 作    者：hezhipeng
 * 版    本：
 * 创建日期：2020/6/8
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class IoUtil {
    private IoUtil() {
    }

    private static class I {
        private static IoUtil sIoUtil = new IoUtil();
    }

    public static IoUtil getInstance() {
        return I.sIoUtil;
    }

    public void close(InputStream inputStream){
        try {
            inputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
