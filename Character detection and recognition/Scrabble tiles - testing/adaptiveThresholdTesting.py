import cv2 as cv
import imageInfo as imgInfo

windowName = "Adaptive Thresholding"
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

    cv.createTrackbar('Th_max', windowName, 0, 255, lambda _: _)
    cv.createTrackbar('Th_type', windowName, 0, 1, lambda _: _)
    cv.createTrackbar('Block size', windowName, 3, 100, lambda _: _)
    cv.setTrackbarMin('Block size', windowName, 3)
    cv.createTrackbar('Constant', windowName, 0, 100, lambda _: _)

    # Adaptive Threshold method:
    # 0 - ADAPTIVE_THRESH_MEAN_C
    # 1 - ADAPTIVE_THRESH_GAUSSIAN_C
    cv.createTrackbar('Th method', windowName, 0, 1, lambda _: _)
    cv.createTrackbar('Blur', windowName, 0, 10, lambda _: _)

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
else:
    src = readImg()

generateTrackbars(windowName)

while True:
    if READ_FROM_CAMERA == True:
        _, scrabbleImage = src.read()
        scrabbleImage = cv.resize(scrabbleImage, imgInfo.testingDimension)
    else:
        scrabbleImage = readImg()

    grayImage = applyGrayScale(scrabbleImage)
    blurredImage = applyBlur(windowName, grayImage)
    thresh = applyThresholding(windowName, blurredImage)

    # Finding all the contours of the threshold image/frame.
    contours, hierarchy = cv.findContours(thresh, cv.RETR_TREE, cv.CHAIN_APPROX_NONE)

    for idx in range(len(contours)):
        contour = contours[idx]

        # Getting the moments of the current contour.
        M = cv.moments(contour)

        if M['m00'] != 0:

            # Calculating the centroid.
            cx = int(M['m10'] / M['m00'])
            cy = int(M['m01'] / M['m00'])

            contourYcoords = [c[0][1] for c in contour]
            contourXcoords = [c[0][0] for c in contour]

            maxContourY, minContourY = max(contourYcoords), min(contourYcoords)
            maxContourX, minContourX = max(contourXcoords), min(contourXcoords)

            dist_CentroidToUpperBound = abs(maxContourY - cy)
            dist_CentroidToLowerBound = abs(minContourY - cy)

            dist_CentroidToRightBound = abs(maxContourX - cx)
            dist_CentroidToLeftBound = abs(minContourX - cx)

            dist_ContourUpperToLowerBound = maxContourY - minContourY
            dist_ContourLeftToRightBound = maxContourX - minContourX

            centroid = (cx, cy)

            contourArea = cv.contourArea(contour)

            if abs(dist_CentroidToUpperBound - dist_CentroidToLowerBound) < 3 and abs(dist_CentroidToLeftBound - dist_CentroidToRightBound) < 3 and dist_ContourUpperToLowerBound > 5 and dist_ContourLeftToRightBound > 5:
                cv.circle(scrabbleImage, centroid, 3, (0, 0, 255), -1)
                cv.drawContours(scrabbleImage, contours, idx, (255, 0, 0), 2)

    cv.imshow('Original', scrabbleImage)
    cv.imshow(windowName, thresh)

    k = cv.waitKey(1) & 0xFF

    if k == ord('q'):
        break

cv.destroyAllWindows()
