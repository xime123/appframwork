package com.app.base.baseactivity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.util.BitmapUtil;
import android.util.Log;
import android.util.ToastUtil;
import android.widget.ImageView;

import com.app.util.FileUtil;

import java.io.File;
import java.io.FileOutputStream;



/**
 * Created by 徐敏 on 2017/8/18.
 * 管理拍照的activity
 */

public abstract class CameraActivity extends StatusBarColorActivity {
    protected static final int CODE_CAMERA_REQUEST = 301;
    protected static final int CODE_GALLERY_REQUEST = 302;
    protected static final int CODE_RESULT_REQUEST = 303;
    protected final static String authorities="jzix.jzexchange.fileprovider";
    protected final static String TAG=CameraActivity.class.getSimpleName();
    protected String mImagePath = Environment.getExternalStorageDirectory()+"/Android/data/jzix.jzexchange/files/";


    protected File mCurrentFile;
    protected File mCropedImageFile;
    protected String base64Bitmap;
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case CODE_CAMERA_REQUEST:
                    String fileName = System.currentTimeMillis() + "";
                    mCropedImageFile = FileUtil.createFile(mImagePath, fileName + "_upload.jpg");
                    cropRawPhoto(getUriByFile(mCurrentFile));
                    break;
                case CODE_GALLERY_REQUEST:
                    cropRawPhoto(data.getData());
                    break;
            }
        }
    }

    protected void showSelectPicDialog(){
//
//        final SelectPicDialog dialog=new SelectPicDialog(this);
//        dialog.setOnSelectClickListener(new SelectPicDialog.OnSelectClickListener() {
//            @Override
//            public void onPhotoAlbumClicked() {
//                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(CameraActivity.this, new String[]{PermisionsConstant.CAMERA, PermisionsConstant.WRITE_EXTERNAL_STORAGE, PermisionsConstant.READ_EXTERNAL_STORAGE}, new PermissionsResultAction() {
//                    @Override
//                    public void onGranted() {
//                        choosePicture();
//                    }
//
//                    @Override
//                    public void onDenied(String permission) {
//
//                    }
//                });
//
//            }
//
//            @Override
//            public void onCameraClicked() {
//                PermissionsManager.getInstance().requestPermissionsIfNecessaryForResult(CameraActivity.this, new String[]{PermisionsConstant.CAMERA, PermisionsConstant.WRITE_EXTERNAL_STORAGE, PermisionsConstant.READ_EXTERNAL_STORAGE}, new PermissionsResultAction() {
//                    @Override
//                    public void onGranted() {
//                        takePicture();
//                    }
//
//                    @Override
//                    public void onDenied(String permission) {
//
//                    }
//                });
//
//            }
//        });
//        dialog.show();
    }

    /**
     * 拍照
     */
    private void takePicture(){
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            File path = new File(mImagePath);
            if(!path.exists()){
                path.mkdir();
            }
            mCurrentFile = new File(mImagePath, System.currentTimeMillis()+".jpg");
            Uri uri=getUriByFile(mCurrentFile);
            Log.d(TAG,"filePath = "+uri.toString());
            cameraIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            cameraIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, uri);
            startActivityForResult(cameraIntent, CODE_CAMERA_REQUEST);
        }
    }




    /**
     * 选择照片
     */
    private void choosePicture(){

        Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pickIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        pickIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        pickIntent.setType("image/*");
        // 判断系统中是否有处理该 Intent 的 Activity
        if (pickIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(pickIntent, CODE_GALLERY_REQUEST);
        } else {
            ToastUtil.toast(this,"can't find pic");
        }



    }

    /**
     * 裁剪照片
     * @param originUri
     */
    public void cropRawPhoto(Uri originUri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        intent.putExtra("crop", "true");
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        intent.putExtra("outputX", 200);
        intent.putExtra("outputY", 200);
        intent.putExtra("return-data", true);
        intent.putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        intent.setDataAndType(originUri, "image/*");
        startActivityForResult(intent, CODE_RESULT_REQUEST);
    }


    private Uri getUriByFile(File file){
        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            return FileProvider.getUriForFile(this, authorities, file);
        }else {
            return Uri.fromFile(file);
        }
    }

    protected void startUploadImage() {
//        App.executeOn(App.getConcurrentThread(), new ICBlock() {
//            @Override
//            public void run() {
//
//                final String remoteUrl = uploadHeadPic(mCropedImageFile);
//                App.executeOn(App.getMainQueue(), new ICBlock() {
//
//                    @Override
//                    public void run() {
//                        if (remoteUrl != null) {
//                            ImageLoaderHelper.getInstance().displayImage(remoteUrl,
//                                customerHeadView, ImageLoaderHelper.LOADING_TRANSPARENT, mDisplayer);
//                        } else {
//                            safetyToast("头像上传失败");
//                        }
//                    }
//                });
//            }
//        });
    }

    // 上传头像、更改头像,返回修改后的头像链接
    private String uploadHeadPic(File file) {
//        if (file == null) {
//            return null;
//        }
//        try {
//            UploadResp resp = AppFramework.getAppFramework().getStreamManager()
//                .upload(file.getAbsolutePath());
//            boolean isUploadSucc = (resp != null && resp.errorCode == 0);
//            if (isUploadSucc) {
//                int errorCode = AppFramework.getAppFramework().getUserManager()
//                    .editHeadPic(resp.httpUrl);
//                if (errorCode == 0) {
//                    return resp.httpUrl;
//                }
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return null;
    }
    /**
     * 小图模式中，保存图片后，设置到视图中
     */
    protected void setPicToView(Intent data, ImageView headIv) {
        Bundle extras = data.getExtras();
        if (extras != null) {
            Bitmap photo = extras.getParcelable("data"); // 直接获得内存中保存的 bitmap
            // 创建 smallIcon 文件夹
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {

                File dirFile = new File(mImagePath + "/small_pic");
                if (!dirFile.exists()) {
                    if (!dirFile.mkdirs()) {
                        Log.e("TAG", "文件夹创建失败");
                    } else {
                        Log.e("TAG", "文件夹创建成功");
                    }
                }
                File file = new File(dirFile, System.currentTimeMillis() + ".jpg");
                // 保存图片
                FileOutputStream outputStream = null;
                try {
                    outputStream = new FileOutputStream(file);
                    photo.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                    outputStream.flush();
                    outputStream.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            base64Bitmap= BitmapUtil.encode2Base64ByBitmap(photo);
            // 在视图中显示图片
            headIv.setImageBitmap(photo);
        }
    }
}
