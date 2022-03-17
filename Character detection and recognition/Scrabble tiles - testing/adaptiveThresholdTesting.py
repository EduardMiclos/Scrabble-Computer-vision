import cv2 as cv
import imageInfo as imgInfo
import numpy as np

import idealTrackbarValues

windowName1 = "Adaptive Thresholding"
windowName2 = 'Original'
READ_FROM_CAMERA = False

def readImg():
    destImg = cv.imread(imgInfo.imagePath)
    destImg = cv.resize(destImg, imgInfo.testingDimension)
    return destImg

def readCamera():
    destImg = cv.VideoCapture(0)
    return destImg

def generateTrackbars(windowName1, windowName2):
    cv.namedWindow(windowName1, cv.WINDOW_AUTOSIZE)
    cv.namedWindow(windowName2, cv.WINDOW_AUTOSIZE)

    cv.createTrackbar('Th_max', windowName1, 0, 255, lambda _: _)
    cv.setTrackbarPos('Th_max', windowName1, idealTrackbarValues.thMax)

    cv.createTrackbar('Th_type', windowName1, 0, 1, lambda _: _)
    cv.setTrackbarPos('Th_type', windowName1, idealTrackbarValues.thType)

    cv.createTrackbar('Block size', windowName1, 3, 100, lambda _: _)
    cv.setTrackbarMin('Block size', windowName1, 3)
    cv.setTrackbarPos('Block size', windowName1, idealTrackbarValues.blockSize)

    cv.createTrackbar('Constant', windowName1, 0, 100, lambda _: _)
    cv.setTrackbarPos('Constant', windowName1, idealTrackbarValues.constant)

    cv.createTrackbar('Rect min_h', windowName2, 0, 100, lambda _: _)
    cv.setTrackbarPos('Rect min_h', windowName2, idealTrackbarValues.rectMinH)

    cv.createTrackbar('Rect max_h', windowName2, 0, 100, lambda _: _)
    cv.setTrackbarPos('Rect max_h', windowName2, idealTrackbarValues.rectMaxH)

    cv.createTrackbar('Rect min_w', windowName2, 0, 100, lambda _: _)
    cv.setTrackbarPos('Rect min_w', windowName2, idealTrackbarValues.rectMinW)

    cv.createTrackbar('Rect max_w', windowName2, 0, 100, lambda _: _)
    cv.setTrackbarPos('Rect max_w', windowName2, idealTrackbarValues.rectMaxW)

    # Adaptive Threshold method:
    # 0 - ADAPTIVE_THRESH_MEAN_C
    # 1 - ADAPTIVE_THRESH_GAUSSIAN_C
    cv.createTrackbar('Th method', windowName1, 0, 1, lambda _: _)
    cv.setTrackbarPos('Th method', windowName1, idealTrackbarValues.thMethod)

    cv.createTrackbar('Blur', windowName1, 0, 10, lambda _: _)
    cv.setTrackbarPos('Blur', windowName1, idealTrackbarValues.blur)

def readRectBounds(windowName):
    _rectMinH = cv.getTrackbarPos('Rect min_h', windowName)
    _rectMaxH = cv.getTrackbarPos('Rect max_h', windowName)
    _rectMinW = cv.getTrackbarPos('Rect min_w', windowName)
    _rectMaxW = cv.getTrackbarPos('Rect max_w', windowName)

    return _rectMinH, _rectMaxH, _rectMinW, _rectMaxW

def readThresholdValues(windowName):
    thMax = cv.getTrackbarPos('Th_max', windowName)
    thType = cv.getTrackbarPos('Th_type', windowName)
    return thMax, thType

def readThresholdMethod(windowName):
    return cv.getTrackbarPos('Th method', windowName)

def readBlocksize(windowName):
    return cv.getTrackbarPos('Block size', windowName)

def readConstantC(windowName):
    return cv.getTrackbarPos('Constant', windowName)

def readBlurValue(windowName):
    return cv.getTrackbarPos('Blur', windowName)

def applyThresholding(windowName, img):
    thMax, thType = readThresholdValues(windowName)
    thMethod = readThresholdMethod(windowName)
    blockSize = readBlocksize(windowName)
    blockSize = (blockSize if blockSize % 2 else blockSize + 1)
    constantC = readConstantC(windowName)

    return cv.adaptiveThreshold(img, thMax, thMethod, thType, blockSize, constantC)

def applyBlur(windowName, img):
    blurVal = readBlurValue(windowName)
    blurVal = (blurVal if blurVal % 2 else blurVal + 1)
    return cv.GaussianBlur(img, (blurVal, blurVal), 0)

def applyGrayScale(img):
    return cv.cvtColor(img, cv.COLOR_BGR2GRAY)

if READ_FROM_CAMERA == True:
    src = readCamera()

generateTrackbars(windowName1, windowName2)

while True:
    if READ_FROM_CAMERA == True:
        _, scrabbleImage = src.read()
        scrabbleImage = cv.resize(scrabbleImage, imgInfo.testingDimension)
    else:
        scrabbleImage = readImg()

    grayImage = applyGrayScale(scrabbleImage)
    blurredImage = applyBlur(windowName1, grayImage)
    thresh = applyThresholding(windowName1, blurredImage)

    # Finding all the contours of the threshold image/frame.
    contours, hierarchy = cv.findContours(thresh, cv.RETR_TREE, cv.CHAIN_APPROX_NONE)

    blank = thresh.copy()
    blank[:] = 0

    for idx in range(len(contours)):
        contour = contours[idx]
        cv.fillPoly(blank, pts=[contour], color=(255, 255, 255))

        rectMinH, rectMaxH, rectMinW, rectMaxW = readRectBounds(windowName2)

    contours, hierarchy = cv.findContours(blank, cv.RETR_TREE, cv.CHAIN_APPROX_NONE)

    edges = cv.Canny(thresh, 100, 200)

    for contour in contours:
        (x, y, w, h) = cv.boundingRect(contour)

        if (h > rectMinH and h < rectMaxH and w < rectMaxW and w > rectMinW):
            cv.rectangle(scrabbleImage, (x, y), (x + w, y + h), (0, 255, 0), 2)

            # Getting the moments of the current contour.
            M = cv.moments(contour)

            if M['m00'] != 0:
                # Calculating the centroid.
                cx = int(M['m10'] / M['m00'])
                cy = int(M['m01'] / M['m00'])

                rect = cv.rectangle(scrabbleImage, (cx - 10, cy - 50) , (cx - 15, cy + 50), (255, 0, 0))
                cv.circle(scrabbleImage, (cx, cy), 3, (0, 0, 255), -1)

    cv.imshow('Edges', edges)
    cv.imshow('Blank', blank)
    cv.imshow(windowName1, thresh)
    cv.imshow(windowName2, scrabbleImage)

    k = cv.waitKey(1) & 0xFF

    if k == ord('q'):
        break

cv.destroyAllWindows()
