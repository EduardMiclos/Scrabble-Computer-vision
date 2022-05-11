package com.andyedy.scrabble_computer_vision;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.andyedy.scrabble_computer_vision.Util.IdealConstants;
import com.andyedy.scrabble_computer_vision.Util.ImageTransform;
import com.andyedy.scrabble_computer_vision.Util.Letter;
import com.andyedy.scrabble_computer_vision.Util.LetterScanner;
import com.andyedy.scrabble_computer_vision.Util.MockScanner;
import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.google.android.material.textfield.TextInputEditText;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Mat;
import org.opencv.core.Rect;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

public class PictureActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, IdealConstants {

    ImageView imageView;
    Button btnOpen;

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    Vector<Rect> lastBoundingRectangles;
    Mat lastFrame;

    List<Letter> letterList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture);

        requestCameraPermission();

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
            cameraBridgeViewBase.setCameraPermissionGranted();
        }
        cameraBridgeViewBase.setCvCameraViewListener(this);


        baseLoaderCallback = new BaseLoaderCallback(this) {
            @Override
            public void onManagerConnected(int status) {
                super.onManagerConnected(status);

                switch (status){
                    case BaseLoaderCallback.SUCCESS:
                        cameraBridgeViewBase.enableView();
                        break;
                    default:
                        super.onManagerConnected(status);
                        break;
                }
            }
        };

        imageView = findViewById(R.id.image_view);
        btnOpen = findViewById(R.id.bt_open);

        StringBuilder myStringBuilder = new StringBuilder();

        btnOpen.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                //String str = callPython();
                //Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                //startActivityForResult(intent, 100);

                onDestroy();

                int x, y, width, height;

                /* Iterating through all the bounding rectangles. */
                for(Rect rectangle : lastBoundingRectangles){
                    x = rectangle.x;
                    y = rectangle.y;

                    width = rectangle.width;
                    height = rectangle.height;

                    /* Cropping the grayscale image. */
                    Mat ROI = lastFrame.submat(y, y + height, x, x + width);

                    /* Converting to byte[] array. */
                    byte[] bytes = new byte[(int) (ROI.total() * ROI.elemSize())];
                    ROI.get(0, 0, bytes);

                /* Converting to string in order to send the parameter
                to a Python program. */
                    String outputPythonString = ROI.rows() + ";" + ROI.cols() + Arrays.toString(bytes);

                    String res = callPython("structuralSimilarity", "main", outputPythonString);
                    //Log.d("gottenLetters", res);
                    char c = res.split(", '")[1].toCharArray()[0];
                    if(Character.isAlphabetic(c))
                        myStringBuilder.append(c);
                }

                letterList = Letter.getArrayFromString(myStringBuilder.toString());
            }
        });
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {

        /* We use this frame to extract the cropped images and feed
        them to the SSIM algorithm. */
        Mat frame = inputFrame.gray();

        /* Applying immutable image transformations on current frame. */
        Vector<Rect> boundingRects = ImageTransform.transform(inputFrame);

        /* This is the 'user frame'. This is the frame on which
        we draw the rectangles. We show it on the screen. */
        Mat drawnFrame = ImageTransform.getFrame();

        int x, y, width, height;

        lastBoundingRectangles = (Vector<Rect>) boundingRects.clone();
        lastFrame = frame.clone();

//        /* Iterating through all the bounding rectangles. */
//        for(Rect rectangle : boundingRects){
//            x = rectangle.x;
//            y = rectangle.y;
//
//            width = rectangle.width;
//            height = rectangle.height;
//
//            /* Cropping the grayscale image. */
//            Mat ROI = frame.submat(y, y + height, x, x + width);
//
//            /* Converting to byte[] array. */
//            byte[] bytes = new byte[(int) (ROI.total() * ROI.elemSize())];
//            ROI.get(0, 0, bytes);
//
//            /* Converting to string in order to send the parameter
//            to a Python program. */
//            String outputPythonString = ROI.rows() + ";" + ROI.cols() + Arrays.toString(bytes);
//
//            String res = callPython("structuralSimilarity", "main", outputPythonString);
//        }

        return drawnFrame;
    }

    @Override
    protected void onResume(){
        super.onResume();

        if (!OpenCVLoader.initDebug()){
            Toast.makeText(getApplicationContext(),"An error occurred resuming the camera module!", Toast.LENGTH_SHORT).show();
        }
        else {
            baseLoaderCallback.onManagerConnected(baseLoaderCallback.SUCCESS);
        }
    }

    @Override
    protected void onPause(){
        super.onPause();

        if(cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
        if (cameraBridgeViewBase != null){
            cameraBridgeViewBase.disableView();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);
        }
    }

    private void requestCameraPermission() {
        if(ContextCompat.checkSelfPermission(PictureActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    PictureActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
    }

    private String callPython(String moduleName, String functionName, String argument){
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule(moduleName);
        return pythonFile.callAttr(functionName, argument).toString();
    }
}