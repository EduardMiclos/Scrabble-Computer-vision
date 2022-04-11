import cv2 as cv
from skimage.metrics import structural_similarity as compare_ssim
import os


def applySSIM(template, grayScaleImage):
    # Reading the current letter template from 'Cropped templates' folder.
    tmp = cv.imread(f'Cropped templates/{template}', 0)

    # Resizing it such that is resembles our image dimensions.
    tmp = cv.resize(tmp, (img.shape[1], img.shape[0]))

    # Applying SSIM.
    score, _ = compare_ssim(grayScaleImage, tmp, full=True)

    # Returning the score (necessary for sorting) and the letter name (included in the image name).
    return score, template.split('.png')[0]


# For each letter that was detected by the camera.
for srcImage in os.listdir('dest'):
    img = cv.imread(f'dest/{srcImage}')
    gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    # Getting the matching letter with the highest score.
    _, letter = max([applySSIM(template, gray) for template in os.listdir('Cropped templates')])

    img = cv.resize(img, (500, 500))
    cv.putText(img=img, text='Detected character: ' + letter, org=(50, 400), fontFace=cv.FONT_HERSHEY_SIMPLEX,
               fontScale=1, color=(255, 0, 0), thickness=2)
    cv.imshow('Image', img)
    cv.waitKey(0)
