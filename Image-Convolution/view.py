import tkinter as tk
from functools import partial
from PIL import Image, ImageTk

yourPath = '/Users/hyun/PycharmProjects/HIM2023-2/resources'
tablePath = yourPath + '/table.png'


def view(callback):
    root = tk.Tk()
    root.geometry("600x650")
    root.title("같은 그림 찾기")

    large_image = Image.open(tablePath)
    large_image = large_image.resize((400, 650), Image.LANCZOS)
    large_photo = ImageTk.PhotoImage(large_image)
    large_label = tk.Label(root, image=large_photo)
    large_label.image = large_photo  # Add this line
    large_label.pack(side="right", padx=20)

    button_frame = tk.Frame(root)  # Add this line
    button_frame.pack(anchor='center')  # Add this line

    imageName = ['/circle.png', '/pentagon.png', '/square.png', '/triangle.png', '/star.png']
    for path in imageName:
        imagePath = yourPath + path
        image = Image.open(imagePath)
        image = image.resize((100, 100), Image.LANCZOS)
        photo = ImageTk.PhotoImage(image)
        button = tk.Button(button_frame, image=photo, command=partial(callback, imagePath))
        button.image = photo
        button.pack(anchor="center", pady=10)

    return root
