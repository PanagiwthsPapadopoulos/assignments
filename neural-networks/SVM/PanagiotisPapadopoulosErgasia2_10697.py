import numpy as np
from tensorflow.keras.datasets import cifar10
from sklearn.model_selection import train_test_split
from sklearn.svm import SVC
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import classification_report
import matplotlib.pyplot as plt

# Step 1: Load CIFAR-10 dataset
(X_train, y_train), (X_test, y_test) = cifar10.load_data()

# Step 2: Split the training data into training and validation sets
X_train, X_val, y_train, y_val = train_test_split(X_train, y_train, test_size=0.2, random_state=42)

# Step 3: Normalize the pixel values to the range [0, 1]
X_train = X_train.astype('float32') / 255.0
X_val = X_val.astype('float32') / 255.0
X_test = X_test.astype('float32') / 255.0

# Step 4: Flatten the images from 32x32x3 to 3072-dimensional vectors (3 channels * 32 * 32)
X_train = X_train.reshape(X_train.shape[0], -1)
X_val = X_val.reshape(X_val.shape[0], -1)
X_test = X_test.reshape(X_test.shape[0], -1)

# Step 5: Standardize the data (mean 0, variance 1)
scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_val = scaler.transform(X_val)
X_test = scaler.transform(X_test)

# Step 6: Train an SVM classifier with RBF kernel
svm = SVC(kernel='rbf', C=1.0, gamma='scale', max_iter=2)  # You can adjust C and gamma for better performance
svm.fit(X_train, y_train.ravel())  # Flatten y_train if necessary (to 1D array)

# Step 7: Make predictions on the validation set
y_pred = svm.predict(X_val)

# Step 8: Evaluate the SVM classifier
print("Classification report on validation data:")
print(classification_report(y_val, y_pred))

# Step 9: Evaluate on the test set (optional)
y_test_pred = svm.predict(X_test)
print("Classification report on test data:")
print(classification_report(y_test, y_test_pred))

# Optional: Display some test images and their predicted labels
plt.figure(figsize=(10, 5))
for i in range(8):
    plt.subplot(2, 4, i + 1)
    plt.imshow(X_test[i].reshape(32, 32, 3))  # Reshape to (32, 32, 3) for visualization
    plt.title(f"Pred: {y_test_pred[i]+1}")
    plt.axis('off')
plt.show()