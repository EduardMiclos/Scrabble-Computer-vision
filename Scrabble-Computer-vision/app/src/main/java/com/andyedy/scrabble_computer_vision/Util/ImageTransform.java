package com.andyedy.scrabble_computer_vision.Util;

import androidx.annotation.NonNull;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ImageTransform implements IdealConstants {

    private static Mat frame;

    private static final Scalar boundingRectangleColor = new Scalar(0, 255, 0);
    private static final int boundingRectangleThickness = 2;

    @NonNull
    private static Mat applyAdaptiveThreshold(Mat grayImg){
        Mat threshImg = new Mat();
        Imgproc.adaptiveThreshold(grayImg, threshImg, 255, Imgproc.ADAPTIVE_THRESH_MEAN_C, Imgproc.THRESH_BINARY_INV, blockSize, constantC);
        return threshImg;
    }

    @NonNull
    private static List<MatOfPoint> getContours(Mat threshImage){
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();

        /* This variable is not used. */
        Mat hierarchy = new Mat();

        Imgproc.findContours(threshImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);

        return contours;
    }


    public static void drawRectangle(Mat frame, @NonNull Rect boundingRect){
        /* Top left corner of the rectangle. */
        Point startPoint = new Point();

        /* Bottom right corner of the rectangle. */
        Point finishPoint = new Point();

        startPoint.x = boundingRect.x;
        startPoint.y = boundingRect.y;

        finishPoint.x = startPoint.x + boundingRect.width;
        finishPoint.y = startPoint.y + boundingRect.height;

        Imgproc.rectangle(frame, startPoint, finishPoint, boundingRectangleColor, boundingRectangleThickness);
    }

    public static Vector<Rect> transform (@NonNull CameraBridgeViewBase.CvCameraViewFrame inputFrame){
        /* Creating a matrix from current input frame. */
        frame = inputFrame.rgba();

        /* Getting image dimensions. */
        int imageHeight = frame.rows();
        int imageWidth = frame.cols();

        /* Getting gray-scale image such that we can apply the thresholding effects. */
        Mat grayFrame = inputFrame.gray();

        /* Threshold image. */
        Mat threshImage = applyAdaptiveThreshold(grayFrame);;

        /* Getting the contours. */
        List<MatOfPoint> contours = getContours(threshImage);

        /* Vector of drawn rectangles to be returned by the function. */
        Vector<Rect> drawnRectangles = new Vector<Rect>();

        /* Iterating through all the contours. */
        for(MatOfPoint contour : contours) {
            Rect boundingRect = Imgproc.boundingRect(contour);

            /* Checking for all the conditions that need to be met.
            Take note that these conditions are the ones that determine:

            - how far/close the camera should be.
            - how likely it is to find and draw a rectangle on the screen.
             */
            if (boundingRect.height > rectangleMinHeight &&
                    boundingRect.height < rectangleMaxHeight &&
                    boundingRect.width > rectangleMinWidth &&
                    boundingRect.width < rectangleMaxWidth) {

                Moments moments = Imgproc.moments(contour);

                double blockArea = moments.m00;
                double xMassDistribution = moments.m10;
                double yMassDistribution = moments.m01;

                if (blockArea != 0) {

                    /* Determining the center of mass. */
                    int centroidX = (int) (xMassDistribution / blockArea);
                    int centroidY = (int) (yMassDistribution / blockArea);

                    if (Math.abs(centroidX - imageWidth / 2) < maxCenterErrorX &&
                            Math.abs(centroidY - imageHeight / 2) < maxCenterErrorY) {

                        drawRectangle(frame, boundingRect);
                        drawnRectangles.add(boundingRect);
                    }
                }
            }
        }
        return drawnRectangles;
    }

    public static Mat getFrame(){
        return frame;
    }

    public static Scalar getBoundingRectangleColor(){
        return boundingRectangleColor;
    }

    public static int getBoundingRectangleThickness(){
        return boundingRectangleThickness;
    }
}
