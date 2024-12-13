import numpy as np
import pandas as pd
from sklearn.decomposition import PCA
from sklearn.neighbors import KNeighborsClassifier
from tensorflow.keras.datasets import cifar10
from sklearn.model_selection import GridSearchCV, StratifiedShuffleSplit, train_test_split
from sklearn.svm import SVC
from sklearn.preprocessing import StandardScaler
from sklearn.metrics import ConfusionMatrixDisplay, classification_report, confusion_matrix, accuracy_score
import matplotlib.pyplot as plt
import seaborn as sns
import time

# Step 1: Load CIFAR-10 dataset
(X_train, y_train), (X_test, y_test) = cifar10.load_data()


# Step 3: Normalize the pixel values to the range [0, 1]
X_train = X_train.astype('float32') / 255.0
X_test = X_test.astype('float32') / 255.0

# Step 4: Flatten the images from 32x32x3 to 3072-dimensional vectors (3 channels * 32 * 32)
X_train = X_train.reshape(X_train.shape[0], -1)
X_test = X_test.reshape(X_test.shape[0], -1)

print('Performing standardization')
# Step 5: Standardize the data (mean 0, variance 1)
scaler = StandardScaler()
X_train = scaler.fit_transform(X_train)
X_test = scaler.transform(X_test)

# print('Performing pca')
# # Reduce dimensions to 2D for visualization
# pca = PCA(n_components=0.95)  # Retain 95% variance
# X_train = pca.fit_transform(X_train)
# X_test = pca.transform(X_test)

# Plot the test data in 2D space, colored by true labels
# plt.figure(figsize=(10, 8))
# scatter = plt.scatter(X_test[:, 0], X_test[:, 1], c=y_test, cmap='tab10', alpha=0.7)
# plt.colorbar(scatter, label="True Label")
# plt.title("2D Visualization of CIFAR-10 Test Data")
# plt.xlabel("PCA Component 1")
# plt.ylabel("PCA Component 2")
# plt.show()


# ------------------------------------------     KNN Classifier    --------------------------------------------------
# K-Nearest_neighbors classifier

# knn = KNeighborsClassifier(n_neighbors=3, n_jobs=-1)
# knn.fit(X_train, y_train)

# # Step 7: Make predictions on the test set
# y_pred = knn.predict(X_test)

# # Step 8: Evaluate the model
# print("Classification Report:")
# print(classification_report(y_test, y_pred))

# accuracy = accuracy_score(y_test, y_pred)
# print(f"Overall Accuracy: {accuracy:.4f}")

# # Compute the confusion matrix
# cm = confusion_matrix(y_test, y_pred)

# # Plot the confusion matrix
# plt.figure(figsize=(10, 8))
# sns.heatmap(cm, annot=True, fmt="d", cmap="Blues", xticklabels=range(10), yticklabels=range(10))
# plt.xlabel("Predicted Label")
# plt.ylabel("True Label")
# plt.title("Confusion Matrix")
# plt.show()



# ------------------------------------------  KNN Classifier END  -------------------------------------------------

# Define the parameter grid
param_grid = [
    {
        'kernel': ['linear'],
        'C': [0.01, 0.1, 1, 10, 100]
    },
    {
        'kernel': ['rbf'],
        'C': [0.01, 0.1, 1, 10, 100, 1000],
        'gamma': [0.01, 0.1, 1, 10, 100, 1000]
    },
    {
        'kernel': ['poly'],
        'C': [0.1, 1, 10, 100],
        'gamma': ['scale', 'auto', 0.1, 1],
        'degree': [2, 3, 4],
        'coef0': [0, 1]
    },
    {
        'kernel': ['sigmoid'],
        'C': [0.1, 1, 10, 100],
        'gamma': ['scale', 'auto', 0.1, 1],
        'coef0': [0, 1]
    }
]



# Step 6: Train an SVM classifier with RBF kernel
# svm = SVC(kernel='rbf', C=1.0, gamma='scale', max_iter=2)  # You can adjust C and gamma for better performance
# svm.fit(X_train, y_train.ravel())  # Flatten y_train if necessary (to 1D array)

print('creating model')
# Create the SVM model
svm = SVC(max_iter=60)

print('Creating Grid Search')
start_time = time.time()
cv = StratifiedShuffleSplit(n_splits=5, test_size=0.2, random_state=42)

# Create the GridSearchCV object
grid_search = GridSearchCV(svm, param_grid[1], cv=cv, n_jobs=-1)


print("pre fitment") 


# Fit the model
grid_search.fit(X_train, y_train)

end_time = time.time()
print("training time: " + str(start_time - end_time))
print("Ended fitting")
results = grid_search.cv_results_
print(results)



# Print the best parameters found
print(f"Best Parameters: {grid_search.best_params_}")

# Get the best model from grid search
best_model = grid_search.best_estimator_

# Evaluate the model on the test set
test_accuracy = best_model.score(X_test, y_test)
print(f"Test Accuracy: {test_accuracy:.4f}")


# Step 7: Make predictions on the test set
y_pred = grid_search.best_estimator_.predict(X_test)
# y_pred = svm.predict(X_val)

# Step 8: Evaluate the SVM classifier
print("Classification report on validation data:")
print(classification_report(y_test, y_pred))


# --------------------------------    Confusion Matrix for best estimator  --------------------------  #

cm = confusion_matrix(y_test, y_pred)
ConfusionMatrixDisplay(confusion_matrix=cm).plot(cmap="Blues")
plt.title("Confusion Matrix")
plt.show()


# ----------- HeatMap of C and Gamma --------------------- #

# Convert the GridSearchCV results to a DataFrame
results_df = pd.DataFrame(grid_search.cv_results_)
pd.set_option('display.max_rows', None)  # Show all rows
pd.set_option('display.max_columns', None)  # Show all columns
pd.set_option('display.width', None)  # Adjust the display width to fit the data
pd.set_option('display.max_colwidth', None)  # Show full column content
print(results_df)

# Create a pivot table to visualize the mean_test_score for C and gamma
pivot_table = results_df.pivot_table(
    index="param_C",          # Row labels (C values)
    columns="param_gamma",    # Column labels (gamma values)
    values="mean_test_score"  # Values to display (mean test score)
)

# Visualize the pivot table as a heatmap
sns.heatmap(pivot_table, annot=True, fmt=".3f", cmap="coolwarm")
plt.title("Hyperparameter Heatmap (C vs. Gamma)")
plt.xlabel("Gamma")
plt.ylabel("C")
plt.show()

print("training time: " + str(start_time - end_time))