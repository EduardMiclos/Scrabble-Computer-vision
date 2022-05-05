package com.example.detectionmodule;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.Image;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.inline.InlineContentView;

import com.chaquo.python.PyObject;
import com.chaquo.python.Python;
import com.chaquo.python.android.AndroidPlatform;

import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.Vector;

public class MainActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2, IdealConstants {
    ImageView imageView;
    Button btnOpen;

    CameraBridgeViewBase cameraBridgeViewBase;
    BaseLoaderCallback baseLoaderCallback;

    Vector<Rect> lastBoundingRectangles;
    Mat lastFrame;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        requestCameraPermission();

        cameraBridgeViewBase = (JavaCameraView)findViewById(R.id.CameraView);
        cameraBridgeViewBase.setVisibility(SurfaceView.VISIBLE);
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
        /*
        imageView = findViewById(R.id.image_view);

        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
                PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{
                            Manifest.permission.CAMERA
                    },
                    100);
        }
*/
        imageView = findViewById(R.id.image_view);
        btnOpen = findViewById(R.id.bt_open);
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
                Log.d("MyApp", res);
            }
            }
        });
    }

    @Override
    public void onCameraViewStarted(int width, int height) {

    }

    @Override
    public void onCameraViewStopped() {

    }

    /*
    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        Mat frame = inputFrame.rgba();
        Mat grayFrame = inputFrame.gray();
        Mat threshImage = new Mat();

        Imgproc.adaptiveThreshold(grayFrame, threshImage, 255, 0, 1, 11, 15);

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        Imgproc.findContours(threshImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        //Mat blankImage = new Mat(grayFrame.rows(), grayFrame.cols(), CvType.CV_8UC1, Scalar.all(Color.BLACK));


        //Imgproc.fillPoly(blankImage, contours, Scalar.all(255));


        for(MatOfPoint contour : contours){

            Rect rect = Imgproc.boundingRect(contour);

            if(rect.height > rectangleMinHeight && rect.height < rectangleMaxHeight && rect.width > rectangleMinWidth && rect.width < rectangleMaxWidth){
                Moments moments = Imgproc.moments(contour);

                if(moments.m00 != 0){
                    int cx = (int)(moments.m10 / moments.m00);
                    int cy = (int)(moments.m01 / moments.m00);

                    if (Math.abs(cx - frame.cols()/2) < maxCenterErrorX && Math.abs(cy - frame.rows()/2) < maxCenterErrorY){
                        Point startPoint = new Point();
                        Point finishPoint = new Point();

                        startPoint.x = rect.x;
                        startPoint.y = rect.y;

                        finishPoint.x = rect.x + rect.width;
                        finishPoint.y = rect.y + rect.height;

                        Scalar color = new Scalar(0, 255, 0);

                        Imgproc.rectangle(frame, startPoint, finishPoint, color, 1);
                    }
                }
            }
        }

//        File outputFile = null;
//        outputFile = new File("fisier.txt");
//
//
//        try(FileOutputStream outputStream = new FileOutputStream(outputFile)){
//            outputStream.write(bytes);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


//        byte[] ROW_byteArr;
//
//        MatOfByte bytes = new MatOfByte(frame.reshape(1, frame.rows()*frame.cols()));
//        byte[] bytes_ = bytes.toArray();
//
//        String s = Base64.encodeToString(bytes_, Base64.DEFAULT);


//        ROW_byteArr = new byte[frame.cols()];
//        String outputStr = "";

        //      outputStr += Arrays.toString(ROW_byteArr) + ";";

//        for(int colIdx = 0; colIdx < frame.cols(); colIdx++){
//               frame.get(0, colIdx, ROW_byteArr);
//               outputStr += Arrays.toString(ROW_byteArr) + ";";
//        }

        //outputStr = frame.dump();

        //String inputStr = callPython("test", "testFunc", outputStr);

        //Log.d("MainActivity", inputStr);

//        Mat newFrame = new Mat(frame.rows(), frame.cols(), CvType.CV_8UC1);
//        int ch = newFrame.channels();


//        for(int i = 0; i < newFrame.rows(); i++){
//            for(int j = 0; j < newFrame.cols(); j++){
//                newFrame.put(i, j, 166);
//            }
//        }

        /*
        Mat row;
        for(int i = 0; i < frame.rows(); i++){
            row = frame.row(i);

            for(int j = 0; j < frame.cols(); j++){
                Mat num = row.col(j);
                Log.d("MainActivity", num.get(0, 0).toString());
            }
        }
        return frame;
    }
*/

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

        /* Iterating through all the bounding rectangles. */
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
//            Log.d("MyApp", res);
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

    private void requestCameraPermission() {
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    MainActivity.this,
                    new String[]{Manifest.permission.CAMERA},
                    100
            );
        }
//        if (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.CAMERA) !=
//                PackageManager.PERMISSION_GRANTED) {
//            ActivityCompat.requestPermissions(MainActivity.this,
//                    new String[]{
//                            Manifest.permission.CAMERA
//                    },
//                    100);
//        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Bitmap captureImage = (Bitmap)data.getExtras().get("data");
            imageView.setImageBitmap(captureImage);
        }
    }

    private String callPython(String moduleName, String functionName, String argument){
        Python python = Python.getInstance();
        PyObject pythonFile = python.getModule(moduleName);
        return pythonFile.callAttr(functionName, argument).toString();
    }
}