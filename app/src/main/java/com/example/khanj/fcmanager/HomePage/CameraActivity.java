package com.example.khanj.fcmanager.HomePage;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.ImageView;

import com.example.khanj.fcmanager.R;

import java.io.ByteArrayOutputStream;

public class CameraActivity extends AppCompatActivity {

    ImageView cameraButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        final CameraPreview surfaceView = (CameraPreview) findViewById(R.id.camerapreview);
        SurfaceHolder holder = surfaceView.getHolder();
        holder.addCallback(surfaceView);
        holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

        cameraButton = (ImageView) findViewById(R.id.button);
        cameraButton.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        cameraButton.setImageResource(R.drawable.cbutton);
                        break;
                    }
                    case MotionEvent.ACTION_UP:{
                        surfaceView.takePhoto(new Camera.PictureCallback() {
                            @Override
                            public void onPictureTaken(byte[] data, Camera camera) {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(data,0,data.length);
                                Matrix m = new Matrix();
                                m.setRotate(90,(float)bitmap.getWidth(),(float)bitmap.getHeight());
                                Bitmap rotateBitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), m, false);
                                bitmap.recycle();
                                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                                rotateBitmap.compress(Bitmap.CompressFormat.JPEG,100,bytes);
                                String path = MediaStore.Images.Media.insertImage(getContentResolver(),rotateBitmap,"title",null);
                                Intent intent = new Intent(getApplicationContext(),HomeFragment.class);
                                intent.putExtra("path",path);
                                setResult(Activity.RESULT_OK,intent);
                                Uri.parse(path);
                                finish();
                            }
                        });
                    }
                    case MotionEvent.ACTION_CANCEL:{
                        cameraButton.setImageResource(R.drawable.cbutton2);
                    }

                }
                return false;
            }
        });
        cameraButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }
}
