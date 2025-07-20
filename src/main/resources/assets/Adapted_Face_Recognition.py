import cv2
import sys
import numpy as np

def load_image(image_path):
    # Load the image in grayscale
    image = cv2.imread(image_path, cv2.IMREAD_GRAYSCALE)
    if image is None:
        print(f"Failed to load image from path: {image_path}")
    return image

def compare_faces(captured_image, stored_image):
    # Resize the captured image to match the stored image size
    resized_captured_image = cv2.resize(captured_image, (stored_image.shape[1], stored_image.shape[0]))

    # Initialize the LBPH face recognizer
    face_recognizer = cv2.face.LBPHFaceRecognizer_create()

    # Train the recognizer on the stored image
    face_recognizer.train([stored_image], np.array([1]))

    # Predict on the captured image
    label, confidence = face_recognizer.predict(resized_captured_image)

    # Print the result for debugging
    print(f"Prediction label: {label}, confidence: {confidence}")

    # A lower confidence value indicates a closer match
    confidence_threshold = 50.0
    return label == 1 and confidence < confidence_threshold

def main(captured_image_path, stored_image_paths):
    # Load the captured image
    captured_image = load_image(captured_image_path)
    if captured_image is None:
        print("Error: Captured image could not be loaded.")
        return False

    # Iterate over each stored image and compare
    for stored_image_path in stored_image_paths:
        stored_image = load_image(stored_image_path)
        if stored_image is None:
            print(f"Skipping comparison for {stored_image_path} as it could not be loaded.")
            continue

        if compare_faces(captured_image, stored_image):
            print("Face recognized successfully.")
            return True

    print("Face not recognized.")
    return False

if __name__ == "__main__":
    # Example usage: python script.py captured_image.jpg stored_image1.jpg stored_image2.jpg ...
    if len(sys.argv) < 3:
        print("Usage: python script.py captured_image_path stored_image_path1 [stored_image_path2 ...]")
        sys.exit(1)

    captured_image_path = sys.argv[1]
    stored_image_paths = sys.argv[2:]

    result = main(captured_image_path, stored_image_paths)

    # Output the result as "True" or "False"
    print("True" if result else "False")
