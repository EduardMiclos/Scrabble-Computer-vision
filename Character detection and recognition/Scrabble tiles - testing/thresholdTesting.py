import cv2 as cv
import imageInfo as imgInfo

windowName = "Thresholding"

def readImg():
    destImg = cv.imread(imgInfo.imagePath)
    destImg = cv.resize(destImg, imgInfo.testingDimension)
    return destImg

def generateTrackbars(windowName):
    cv.namedWindow(windowName, cv.WINDOW_AUTOSIZE)

    cv.createTrackbar('Th_min', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Th_max', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Blur', windowName, 0, 10, lambda _: _)
    cv.createTrackbar('Th method', windowName, 0, 7, lambda _: _)

def readThresholdValues(windowName):
    thMin = cv.getTrackbarPos('Th_min', windowName)
    thMax = cv.getTrackbarPos('Th_max', windowName)
    return thMin, thMax

def readBlurValue(windowName):
    return cv.getTrackbarPos('Blur', windowName)

def readThresholdMethod(windowName):
    return cv.getTrackbarPos('Th method', windowName)

def applyThresholding(windowName, img):
    thMin, thMax = readThresholdValues(windowName)
    threshMethod = readThresholdMethod(windowName)
    return cv.threshold(img, thMin, thMax, threshMethod)

def applyBlur(windowName, img):
    blurVal = readBlurValue(windowName)
    blurVal = (blurVal if blurVal % 2 else blurVal + 1)
    return cv.GaussianBlur(img, (blurVal, blurVal), 0)

def applyGrayScale(img):
    return cv.cvtColor(img, cv.COLOR_BGR2GRAY)

scrabbleImage = readImg()
generateTrackbars(windowName)

while True:
    grayImage= applyGrayScale(scrabbleImage)
    blurredImage = applyBlur(windowName, grayImage)
    _, thresh = applyThresholding(windowName, blurredImage)

    cv.imshow('Original', scrabbleImage)
    cv.imshow(windowName, thresh)

    k = cv.waitKey(1) & 0xFF

    if k == ord('q'):
        break

cv.destroyAllWindows()
