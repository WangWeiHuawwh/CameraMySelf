package com.wwh.cam;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;

/**
 * Created by Administrator on 2015/8/3.
 */
public class PhotoProcessActivity extends Activity
        implements View.OnClickListener {
    private ImageView photoImageView;
    private String path = "";
    private TextView backTextView;
    private TextView actionTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.photo_process_activity);
        path = getIntent().getStringExtra(CameraActivity.CAMERA_PATH_VALUE1);
        initView();
        initData();
    }

    private void initData() {
        Bitmap bitmap = null;
        try {
            bitmap = loadBitmap(path, true);
        } catch (Exception e) {
            e.printStackTrace();
        }
        photoImageView.setImageBitmap(bitmap);
        backTextView.setOnClickListener(this);
        actionTextView.setOnClickListener(this);
    }

    private void initView() {
        photoImageView = (ImageView) findViewById(R.id.photo_imageview);
        backTextView = (TextView) findViewById(com.wwh.cam.R.id.photo_process_back);
        actionTextView = (TextView) findViewById(R.id.photo_process_action);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    /**
     * 从给定路径加载图片
     */
    public static Bitmap loadBitmap(String imgpath) {
        return BitmapFactory.decodeFile(imgpath);
    }


    /**
     * 从给定的路径加载图片，并指定是否自动旋转方向
     */
    public static Bitmap loadBitmap(String imgpath, boolean adjustOritation) throws OutOfMemoryError {
        if (!adjustOritation) {
            return loadBitmap(imgpath);
        } else {
            Bitmap bm = loadBitmap(imgpath);
            int digree = 0;
            ExifInterface exif = null;
            try {
                exif = new ExifInterface(imgpath);
            } catch (IOException e) {
                e.printStackTrace();
                exif = null;
            }
            if (exif != null) {
                // 读取图片中相机方向信息
                int ori = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);
                // 计算旋转角度
                switch (ori) {
                    case ExifInterface.ORIENTATION_ROTATE_90:
                        digree = 90;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_180:
                        digree = 180;
                        break;
                    case ExifInterface.ORIENTATION_ROTATE_270:
                        digree = 270;
                        break;
                    default:
                        digree = 0;
                        break;
                }
            }
            if (digree != 0) {
                // 旋转图片
                Matrix m = new Matrix();
                m.postRotate(digree);
                bm = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
            }
            return bm;
        }
    }

    private void refreshGallery(String file) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        mediaScanIntent.setData(Uri.fromFile(new File(file)));
        sendBroadcast(mediaScanIntent);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.photo_process_back:
                Intent intent = new Intent();
                intent.putExtra(CameraActivity.CAMERA_PATH_VALUE2, path);
                setResult(0, intent);
                finish();
                break;
            case R.id.photo_process_action:
                refreshGallery(path);
                Intent intentOk = new Intent();
                intentOk.putExtra(CameraActivity.CAMERA_PATH_VALUE2, path);
                setResult(RESULT_OK, intentOk);
                finish();
                break;
        }
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
            Intent intent = new Intent();
            intent.putExtra(CameraActivity.CAMERA_PATH_VALUE2, path);
            setResult(0, intent);
            finish();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
