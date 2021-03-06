import cv2 as cv
import numpy as np
from skimage.metrics import structural_similarity as compare_ssim
import os


def applySSIM(template, grayScaleImage):
    # Reading the current letter template from 'Cropped templates' folder.
    tmp = cv.imread(f'Cropped templates/{template}', 0)

    # Resizing it such that is resembles our image dimensions.
    tmp = cv.resize(tmp, (grayScaleImage.shape[1], grayScaleImage.shape[0]))

    # Applying SSIM.
    score, _ = compare_ssim(grayScaleImage, tmp, full=True)

    # Returning the score (necessary for sorting) and the letter name (included in the image name).
    return score, template.split('.png')[0]


# For each letter that was detected by the camera.
# for srcImage in os.listdir('dest'):
#     img = cv.imread(f'dest/{srcImage}')
#     gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)
#
#     # Getting the matching letter with the highest score.
#     _, letter = max([applySSIM(template, gray) for template in os.listdir('Cropped templates')])
#
#     img = cv.resize(img, (500, 500))
#     cv.putText(img=img, text='Detected character: ' + letter, org=(50, 400), fontFace=cv.FONT_HERSHEY_SIMPLEX,
#                fontScale=1, color=(255, 0, 0), thickness=2)
#     cv.imshow('Image', img)
#     cv.waitKey(0)

def main(strImg):
    height, width = map(int, strImg.split('[')[0].split(';'))

    strImg = strImg.split('[')[1].replace("]", "").replace('\n', '').split(' ')
    strImg = list(filter(''.__ne__, strImg))

    imgArr = np.zeros((height, width), dtype='uint8')
    for i in range(height):
        for j in range(width):
            imgArr[i][j] = (strImg[i*width + j])

    score, letter = max([applySSIM(template, imgArr) for template in os.listdir('Cropped templates')])

    return score, letter
