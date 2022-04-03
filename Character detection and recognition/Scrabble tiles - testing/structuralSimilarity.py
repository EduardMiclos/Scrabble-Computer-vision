import cv2 as cv
from skimage.metrics import structural_similarity as compare_ssim
import os


def applySSIM(template, gray):
    tmp = cv.imread(f'Cropped templates/{template}', 0)
    tmp = cv.resize(tmp, (img.shape[1], img.shape[0]))

    score, _ = compare_ssim(gray, tmp, full=True)
    return score, template.split('.png')[0]


for srcImage in os.listdir('dest'):
    img = cv.imread(f'dest/{srcImage}')
    gray = cv.cvtColor(img, cv.COLOR_BGR2GRAY)

    _, letter = max([applySSIM(template, gray) for template in os.listdir('Cropped templates')])

    img = cv.resize(img, (500, 500))
    cv.putText(img=img, text='Detected character: ' + letter, org=(50, 400), fontFace=cv.FONT_HERSHEY_SIMPLEX,
               fontScale=1, color=(255, 0, 0), thickness=2)
    cv.imshow('Image', img)
    cv.waitKey(0)
