import cv2 as cv
import numpy as np
from skimage.metrics import structural_similarity as compare_ssim
import os
from os.path import dirname, join
import sys

def applySSIM(template, grayScaleImage):
    # Reading the current letter template from 'Cropped templates' folder.
    # tmp = cv.imread(f'Cropped templates/{template}', 0)
    tmp = cv.imread(join(dirname(__file__), f'Cropped templates/{template}'), 0)

    # Resizing it such that is resembles our image dimensions.
    
    tmp = cv.resize(tmp, (grayScaleImage.shape[1], grayScaleImage.shape[0]))

    # Applying SSIM.
    score, _ = compare_ssim(grayScaleImage, tmp, full=True)

    # Returning the score (necessary for sorting) and the letter name (included in the image name).
    return score, template.split('.png')[0]


def main(strImg):
    height, width = map(int, strImg.split('[')[0].split(';'))

    strImg = strImg.split('[')[1].replace("]", "").replace('\n', '').split(', ')
    strImg = list(filter(''.__ne__, strImg))

    imgArr = np.zeros((height, width), dtype='uint8')
    for i in range(height):
        for j in range(width):
            imgArr[i][j] = (strImg[i*width + j])

    try:
        score, letter = max([applySSIM(template, imgArr) for template in os.listdir(join(dirname(__file__), 'Cropped templates'))])
    except Exception as e:
        score, letter = -1, '-1'
    
    return score, letter