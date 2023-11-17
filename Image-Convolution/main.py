import numpy as np
from PIL import Image, ImageDraw
import matplotlib.pyplot as plt

import view

clicked = None


# 합성곱
def conv(bigImage, findImage):
    bigRow = len(bigImage)
    bigCol = len(bigImage[0])
    findRow = len(findImage)
    findCol = len(findImage[0])
    result = np.zeros((int((bigRow - findRow) + 1), int((bigCol - findCol) + 1)))
    # findImage = np.flip(np.flip(findImage, 0), 1)

    for i in range(len(bigImage[0])):
        if len(bigImage[0]) - findCol < i:
            break
        for y in range(len(bigImage)):
            if y > len(bigImage) - findRow:
                break
            try:
                result[y, i] = (findImage * bigImage[y: y + findRow, i: i + findCol]).sum()
            except:
                break

    return result


# 정규화 및 반전
def normalize(array):
    min = np.min(array)
    max = np.max(array)
    normalized_array = 1 - (array - min) / (max - min)
    return normalized_array


def clicked(imagePath):
    global clicked
    clicked = imagePath
    print(f"{imagePath} Convolution Start")

    image_A = np.array(Image.open(clicked).convert('L'))
    image_B = np.array(Image.open(view.tablePath).convert('L'))

    result = normalize(conv(image_B, image_A))
    output_list = result.flatten().tolist()
    output_list.sort()
    index_highest = int(len(output_list) * 0.9996)
    value_highest = output_list[index_highest]
    newMap = np.where(result >= value_highest, result, np.nan)

    # 확인용
    # plt.imshow(result, cmap='hot', interpolation='nearest')
    # plt.show()

    newResult = np.where(~np.isnan(newMap))

    image = Image.open(view.tablePath)
    image_copy = image.copy()
    draw = ImageDraw.Draw(image_copy)

    box_size = 140
    for i in range(len(newResult[0])):
        x = newResult[0][i]
        y = newResult[1][i]
        draw.rectangle([y - 10, x - 10, y + box_size+10, x + box_size+10], outline = 'red')

    # Save
    image_copy.save(view.yourPath + '/result.png')
    print("Complete!")


root = view.view(clicked)
root.mainloop()
