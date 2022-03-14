import cv2 as cv
import imageInfo as imgInfo

windowName = "Thresholding"

# We can either read image from camera or from inside src folder.
READ_FROM_CAMERA = True

def readImg():
    destImg = cv.imread(imgInfo.imagePath)
    destImg = cv.resize(destImg, imgInfo.testingDimension)
    return destImg

def readCamera():
    destImg = cv.VideoCapture(0)
    return destImg

def generateTrackbars(windowName):
    cv.namedWindow(windowName, cv.WINDOW_AUTOSIZE)

    # Lambda function does nothing (no callback function is needed).
    cv.createTrackbar('Th_min', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Th_max', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Blur', windowName, 0, 10, lambda _: _)


    # Threshold method:
    # 0 - THRESH_BINARY
    # 1 - THRESH_BINARY_INV
    # 2 - THRESH_TRUNC
    # 3 - THRESH_TOZERO
    # 4 - THRESH_TOZERO_INV
    cv.createTrackbar('Th method', windowName, 0, 5, lambda _: _)

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

if READ_FROM_CAMERA == True:
    src = readCamera()
else:
    scrabbleImage = readImg()

generateTrackbars(windowName)

while True:
    # If we're reading from the camera, we need
    # to read frame by frame everytime we reenter the loop.
    if READ_FROM_CAMERA == True:
        _, scrabbleImage = src.read()
        scrabbleImage = cv.resize(scrabbleImage, imgInfo.testingDimension)

    grayImage = applyGrayScale(scrabbleImage)
    blurredImage = applyBlur(windowName, grayImage)
    _, thresh = applyThresholding(windowName, blurredImage)

    cv.imshow('Original', scrabbleImage)
    cv.imshow(windowName, thresh)

    k = cv.waitKey(1) & 0xFF

    if k == ord('q'):
        break

cv.destroyAllWindows()
