import cv2 as cv
import numpy as np
import structuralSimilarity

img = cv.imread(f'dest/img5.jpg', cv.IMREAD_GRAYSCALE)

height, width = img.shape[:2]

outputStr = f'{height};{width}'

outputStr += str(np.asarray(img).reshape(-1))
score, letter = structuralSimilarity.main(outputStr)

cv.imshow('Image', img)

print(f'{letter}, score: {round((score + 1)/2, 2)}')

cv.waitKey(0)