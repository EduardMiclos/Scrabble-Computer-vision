import cv2 as cv
import os

for template in os.listdir('Templates'):
    image = cv.imread(f'Templates/{template}', 0)
    contours, _ = cv.findContours(image, cv.RETR_TREE, cv.CHAIN_APPROX_NONE)

    (x, y, w, h) = cv.boundingRect(contours[1])
    image = image[y:y+h, x:x+w]

    cv.imwrite(f'Cropped templates/{template}', image)
    cv.waitKey(0)
