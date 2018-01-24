package android.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.media.ExifInterface;
import android.net.Uri;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;

@SuppressLint("NewApi")
public class BitmapUtil {

    public static final String TAG = "BitmapUtil";
    public static final String BITMAP_PREFIX="data:image/png;base64,";
    /**
     * 修改图片的亮度
     */
    public static Bitmap bitmapBrightness(Bitmap bitmap, int brightness) {
        final Bitmap bmp = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_4444);
        final ColorMatrix cMatrix = new ColorMatrix();
        cMatrix.set(new float[]{1, 0, 0, 0, brightness, 0, 1, 0, 0, brightness,// 改变亮度
                0, 0, 1, 0, brightness, 0, 0, 0, 1, 0});

        final Paint paint = new Paint();
        paint.setColorFilter(new ColorMatrixColorFilter(cMatrix));

        final Canvas canvas = new Canvas(bmp);
        // 在Canvas上绘制一个已经存在的Bitmap。这样，dstBitmap就和srcBitmap一摸一样了
        canvas.drawBitmap(bitmap, 0, 0, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bmp;
    }

    /**
     * 灰度化图片
     */
    public static Bitmap toGrayscale(Bitmap bitmap) {
        int width, height;
        height = bitmap.getHeight();
        width = bitmap.getWidth();
        Bitmap bmpGrayscale = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        Canvas c = new Canvas(bmpGrayscale);
        Paint paint = new Paint();
        ColorMatrix cm = new ColorMatrix();
        cm.setSaturation(0);
        ColorMatrixColorFilter f = new ColorMatrixColorFilter(cm);
        paint.setColorFilter(f);
        c.drawBitmap(bitmap, 0, 0, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return bmpGrayscale;
    }

    /**
     * 把图片变成圆角
     *
     * @param bitmap 需要修改的图片
     * @param pixels 圆角的弧度
     * @return 圆角图片
     */
    public static Bitmap toRoundCorner(Bitmap bitmap, int pixels) {

        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        if (!bitmap.isRecycled()) {
            bitmap.recycle();
        }
        return output;
    }

    /**
     *  图片圆形处理 
     */
    public static Bitmap toRoundBitmap(Bitmap bitmap) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        float roundPx;
        float left, top, right, bottom, dst_left, dst_top, dst_right, dst_bottom;
        if (width <= height) {
            roundPx = width / 2;
            top = 0;
            bottom = width;
            left = 0;
            right = width;
            height = width;
            dst_left = 0;
            dst_top = 0;
            dst_right = width;
            dst_bottom = width;
        } else {
            roundPx = height / 2;
            float clip = (width - height) / 2;
            left = clip;
            right = width - clip;
            top = 0;
            bottom = height;
            width = height;
            dst_left = 0;
            dst_top = 0;
            dst_right = height;
            dst_bottom = height;
        }

        Bitmap output = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect src = new Rect((int) left, (int) top, (int) right, (int) bottom);
        final Rect dst = new Rect((int) dst_left, (int) dst_top, (int) dst_right, (int) dst_bottom);
        final RectF rectF = new RectF(dst);
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(android.graphics.PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, src, dst, paint);
//		if (!bitmap.isRecycled()) {
//			bitmap.recycle();
//		}
        return output;
    }

    /**
     * 对分辨率较大的图片进行缩放
     *
     * @param bitmap  原始图片
     * @param desSize 要压缩的大小
     * @return
     */
    public static Bitmap zoomBitmap(Bitmap bitmap, float desSize) {
        float srcWidth = bitmap.getWidth();
        float srcHeight = bitmap.getHeight();
        Matrix matrix = new Matrix();
        float scaleWidth = 0;
        float scaleHeight = 0;
        if (srcWidth > srcHeight) {
            if (srcWidth >= desSize) {
                scaleHeight = scaleWidth = desSize / srcWidth;

            } else if (srcWidth < desSize) {
                scaleHeight = scaleWidth = srcWidth / srcWidth;
            }

        } else if (srcWidth < srcHeight) {
            if (srcHeight >= desSize) {
                scaleWidth = scaleHeight = desSize / srcHeight;
            } else if (srcWidth < desSize) {
                scaleHeight = scaleWidth = srcWidth / srcWidth;
            }
        }
        // 等比
        else {
            if (srcWidth >= desSize) {
                scaleHeight = scaleWidth = desSize / srcWidth;
            } else {
                scaleHeight = scaleWidth = srcWidth / srcWidth;
            }
        }
        matrix.postScale(scaleWidth, scaleHeight);// 利用矩阵进行缩放不会造成内存溢出
        Bitmap newbmp = Bitmap.createBitmap(bitmap, 0, 0, (int) srcWidth, (int) srcHeight, matrix, true);
        return newbmp;
    }

    public static int getRotateDegree(String path) {
        ExifInterface exif = null;
        int rotate = 0;
        try {
            exif = new ExifInterface(path);
            int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);

            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_270:
                    rotate = 270;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    rotate = 180;
                    break;
                case ExifInterface.ORIENTATION_ROTATE_90:
                    rotate = 90;
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rotate;
    }

    //FIXME 该方法压缩后的图片有问题，无法正常上传服务器和使用图片,待修复
    public static void getScaledBitmap(String srcPath, String desPath, int width, int height) {

        // 第一步获得原始图片的高宽
        int[] sizes = getWrappedSize(srcPath, width, height);
        Log.i(TAG, "use inSampleSize >> " + sizes[0]);
        Log.i(TAG, "use out width >> " + sizes[1]);
        Log.i(TAG, "use out height >> " + sizes[2]);

        final BitmapFactory.Options options__ = new BitmapFactory.Options();
        options__.inSampleSize = sizes[0];
        options__.inJustDecodeBounds = false;

        FileOutputStream fos = null;
        // 这个是被等比缩放过的Bitmap
        Bitmap bitmap_compressed = BitmapFactory.decodeFile(srcPath, options__);
        if (bitmap_compressed == null) {
            Log.e(TAG, "decodeFile >> " + srcPath + " failed.");
            return;
        }
        Log.e(TAG, "压缩后 bitmap_compressed 的大小 >> " + bitmap_compressed.getByteCount());
        Bitmap scaleBitmap = null; // 这个是被裁剪成规定长宽的Bitmap
        FileChannel fileChannel = null;
        try {
            fos = new FileOutputStream(new File(desPath));
            fileChannel = fos.getChannel();
            fileChannel.lock();
            scaleBitmap = Bitmap.createScaledBitmap(bitmap_compressed, sizes[1], sizes[2], true);
            Log.e(TAG, "按大小 " + sizes[1] + " x " + sizes[2] + " 的大小 >> " + scaleBitmap.getByteCount());
            // 压入bos的内存中
            scaleBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (fos != null) {
                    fos.close();
                }
                if (fileChannel != null) {
                    fileChannel.close();
                }
                if (bitmap_compressed != null && !bitmap_compressed.isRecycled()) {
                    bitmap_compressed.recycle();
                    bitmap_compressed = null;
                }
                if (scaleBitmap != null && !scaleBitmap.isRecycled()) {
                    scaleBitmap.recycle();
                    scaleBitmap = null;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    // 图片按质量压缩方法

    /**
     * @param image
     * @param compressSize 压缩标准
     * @return
     */
    public static Bitmap compressByQuality(Bitmap image, int compressSize) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);// 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        Log.e(TAG, "[before compressByQuality]:" + baos.toByteArray().length);
        int options = 100;
        while (baos.toByteArray().length / 1024 > compressSize) { // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
                baos.reset();// 重置baos即清空baos
                image.compress(Bitmap.CompressFormat.JPEG, options, baos);// 这里压缩options%，把压缩后的数据存放到baos中
                options -= 10;// 每次都减少10
            Log.e(TAG,"[compressByQuality options]: "+options);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());// 把压缩后的数据baos存放到ByteArrayInputStream中
        Log.e(TAG, "[after compressByQuality]:" + baos.toByteArray().length);
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);// 把ByteArrayInputStream数据生成图片
        return bitmap;
    }

    public static Bitmap cropBitmap(Bitmap bitmap, boolean recycle) {
        int w = bitmap.getWidth();
        int h = bitmap.getHeight();

        int wh = w > h ? h : w;// 裁切后所取的正方形区域边长

        int retX = w > h ? (w - h) / 2 : 0;// 基于原图，取正方形左上角x坐标
        int retY = w > h ? 0 : (h - w) / 2;

        Bitmap bmp = Bitmap.createBitmap(bitmap, retX, retY, wh, wh, null, false);
//        if(recycle && !bitmap.isRecycled()) {
//            bitmap.recycle();
//        }
        return bmp;
    }

    /**
     * 返回缩放的倍数
     *
     * @param srcPath
     * @param wantedWidth
     * @param wantedHeight
     * @return
     */
    public static int[] getWrappedSize(String srcPath, int wantedWidth, int wantedHeight) {

        int[] sizes = new int[3];
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        int height = options.outHeight;
        int width = options.outWidth;

        // 宽和高，谁大用谁
        int wrapSize = wantedWidth > wantedHeight ? wantedWidth : wantedHeight;

        // 第二步 大小压缩比按照 wrapSize X wrapSize 计算出我们所期望的图片大小
        int outWidth = 0;
        int outHeight = 0;

        if (width > height) {
            if (width > wrapSize) {
                // 如果宽比高要长 又超过了400
                outWidth = wrapSize; // 宽就是400
                outHeight = wrapSize * height / width; // 高就是400xratio
            } else {
                outWidth = width;
                outHeight = height;
            }
        } else if (height > width) {
            if (height > wrapSize) {
                // 如果高比宽要长 又超过了400
                outHeight = wrapSize; // 宽就是400
                outWidth = wrapSize * width / height; // 高就是400xratio
            } else {
                outWidth = width;
                outHeight = height;
            }
        } else {
            outWidth = wrapSize;
            outHeight = wrapSize;
        }

        Log.i(TAG, "srcWidth >> " + width);
        Log.i(TAG, "srcHeight >> " + height);

        Log.i(TAG, "outWidth >> " + outWidth);
        Log.i(TAG, "outHeight >>" + outHeight);

        int inSampleSize = 1;

        if (height > outHeight || width > outWidth) {
            final int heightRatio = Math.round((float) height / (float) outHeight);
            final int widthRatio = Math.round((float) width / (float) outWidth);
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        sizes[0] = inSampleSize;
        sizes[1] = outWidth;
        sizes[2] = outHeight;

        return sizes;
    }

    /**
     * 安分辨率压缩图片
     *
     * @param srcPath
     * @param minSideLength
     * @param maxNumOfPixels
     * @return
     */
    private static int computeSampleSize(String srcPath, int minSideLength, int maxNumOfPixels) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(srcPath, options);
        int initialSize = computeInitialSampleSize(options, -1, maxNumOfPixels);
        int roundedSize;
        if (initialSize <= 8) {
            roundedSize = 1;
            while (roundedSize < initialSize) {
                roundedSize <<= 1;
            }
        } else {
            roundedSize = (initialSize + 7) / 8 * 8;
        }
        return initialSize;
    }

    private static int computeInitialSampleSize(BitmapFactory.Options options, int minSideLength, int maxNumOfPixels) {
        double w = options.outWidth;
        double h = options.outHeight;
        int lowerBound = (maxNumOfPixels == -1) ? 1 : (int) Math.ceil(Math.sqrt(w * h / maxNumOfPixels));
        int upperBound = (minSideLength == -1) ? 128 : (int) Math.min(Math.floor(w / minSideLength), Math.floor(h / minSideLength));
        if (upperBound < lowerBound) {
            // return the larger one when there is no overlapping zone.
            return lowerBound;
        }
        if ((maxNumOfPixels == -1) && (minSideLength == -1)) {
            return 1;
        } else if (minSideLength == -1) {
            return lowerBound;
        } else {
            return upperBound;
        }
    }

    /**
     * 写图片文件 在Android系统中，文件保存在 /data/data/PACKAGE_NAME/files 目录下
     *
     * @throws IOException
     */
    public static void saveImage(Context context, String fileName, Bitmap bitmap) throws IOException {
        saveImage(context, fileName, bitmap, 100);
    }

    public static void saveImage(Context context, String fileName, Bitmap bitmap, int quality) throws IOException {
        if (bitmap == null || fileName == null || context == null)
            return;

        FileOutputStream fos = context.openFileOutput(fileName, Context.MODE_PRIVATE);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(CompressFormat.JPEG, quality, stream);
        byte[] bytes = stream.toByteArray();
        fos.write(bytes);
        fos.close();
    }

    /**
     * 写图片文件到SD卡
     *
     * @throws IOException
     */
    public static void saveImageToSD(Context ctx, String filePath, Bitmap bitmap, int quality) throws IOException {
        if (bitmap != null) {
            File file = new File(filePath.substring(0, filePath.lastIndexOf(File.separator)));
            if (!file.exists()) {
                file.mkdirs();
            }
            BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filePath));
            bitmap.compress(CompressFormat.JPEG, quality, bos);
            bos.flush();
            bos.close();
            if (ctx != null) {
                scanPhoto(ctx, filePath);
            }
        }
    }

    /**
     * 、将图片按指定长宽缩放
     *
     * @param bitmap
     * @param newWidth
     * @param newHeight
     * @return
     */
    public static Bitmap getScaleBitmap(Bitmap bitmap, int newWidth, int newHeight) {
        // 获得图片的宽高
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();
        // 计算缩放比例
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // 取得想要缩放的matrix参数
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth, scaleHeight);
        // 得到新的图片
        Bitmap newbm = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
        return newbm;
    }


    public static byte[] getByteByBitmap(Bitmap bmp) {
        byte[] compressData = null;
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
        // bmp.
        compressData = outStream.toByteArray();
        try {
            outStream.close();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return compressData;
    }

    public static void destoryBitmap(Bitmap bitmap) {
        if (bitmap != null && !bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }
    }

    /**
     * 让Gallery上能马上看到该图片
     */
    private static void scanPhoto(Context ctx, String imgFileName) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File file = new File(imgFileName);
        Uri contentUri = Uri.fromFile(file);
        mediaScanIntent.setData(contentUri);
        ctx.sendBroadcast(mediaScanIntent);
    }

    /**
     * 图片压缩成base64
     * @param path 本地图片路径
     * @return
     */
    public static String encode2Base64ByPath(String path) {
        //decode to bitmap
        Bitmap bitmap = BitmapFactory.decodeFile(path);
        Log.d(TAG, "bitmap width: " + bitmap.getWidth() + " height: " + bitmap.getHeight());
        //convert to byte array
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] bytes = baos.toByteArray();

        //base64 encode
        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
        String encodeString = new String(encode);
        return encodeString;
    }

    /**
     * 图片压缩成base64
     * @param bitmap
     * @return
     */
    public static String encode2Base64ByBitmap(Bitmap bitmap) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();// outputstream
        bitmap.compress(CompressFormat.PNG, 100, baos);
        return Base64.encodeToString(baos.toByteArray(), Base64.NO_WRAP);

        //convert to byte array
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        bitmap.compress(CompressFormat.JPEG, 50, baos);
//        byte[] bytes = baos.toByteArray();
//
//        //base64 encode
//        byte[] encode = Base64.encode(bytes,Base64.DEFAULT);
//        String encodeString = "";
//        try {
//            encodeString = new String(encode, "utf-8");
//        }catch (Exception exp){
//
//        }
//        String encodeString = new String(encode, "utf-8");
//        return Base64.encodeToString(bytes,Base64.DEFAULT);

//        return encodeString;
    }

    public static Bitmap decodeBase64ToBitmap(String base64Content){
        if(base64Content.startsWith(BITMAP_PREFIX)){
            base64Content= base64Content.replace(BITMAP_PREFIX,"");
        }
        byte[] decode = Base64.decode(base64Content,Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        //save to image on sdcard
        return bitmap;
    }
}
