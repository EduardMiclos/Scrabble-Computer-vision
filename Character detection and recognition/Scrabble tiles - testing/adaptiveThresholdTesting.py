import cv2 as cv
import imageInfo as imgInfo

windowName = "Adaptive Thresholding"

def readImg():
    destImg = cv.imread(imgInfo.imagePath)
    destImg = cv.resize(destImg, imgInfo.testingDimension)
    return destImg

def generateTrackbars(windowName):
    cv.namedWindow(windowName, cv.WINDOW_AUTOSIZE)

    cv.createTrackbar('Th_type', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Block size', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Constant', windowName, 0, 100, lambda _: _)
    cv.createTrackbar('Th method', windowName, 0, 7, lambda _: _)

# TBE