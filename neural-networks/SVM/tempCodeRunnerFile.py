import matplotlib.pyplot as plt
import pandas as pd
import seaborn as sns
import numpy as np
results = {
    'param_C': [0.1, 0.1, 1, 1, 10, 10, 0.1, 0.1, 1, 1, 10, 10],
    'param_gamma': [0.01, 0.1, 0.01, 0.1, 0.01, 0.1, 0.01, 0.1, 0.01, 0.1, 0.01, 0.1],
    'param_coef0': [0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 1.0, 1.0, 1.0, 1.0, 1.0, 1.0],
    'param_degree': [2, 2, 2, 2, 2, 2, 3, 3, 3, 3, 3, 3],
    'mean_test_score': [0.75, 0.78, 0.80, 0.82, 0.83, 0.85, 0.77, 0.79, 0.81, 0.83, 0.84, 0.86]
}

# Convert results to a DataFrame
results_df = pd.DataFrame(results)

# Unique values for coef0 and degree
coef0_values = results_df['param_coef0'].unique()
degree_values = results_df['param_degree'].unique()

# Loop through each combination of coef0 and degree
for coef0 in coef0_values:
    for degree in degree_values:
        # Filter data for the current slice
        subset = results_df[(results_df['param_coef0'] == coef0) & (results_df['param_degree'] == degree)]

        pivot_table = results_df.pivot_table(
            index="param_C",          # Row labels (C values)
            columns="param_gamma",    # Column labels (gamma values)
            values="mean_test_score"  # Values to display (mean test score)
        )

        

        # Create the heatmap
        plt.figure(figsize=(8, 6))
        sns.heatmap(pivot_table, annot=True, fmt=".2f", cmap="coolwarm", cbar_kws={'label': 'Mean Accuracy'})

        # Add labels and title
        plt.title(f"Heatmap for Coef0={coef0}, Degree={degree}")
        plt.xlabel("C (Regularization)")
        plt.ylabel("Gamma (Kernel Coefficient)")
        plt.tight_layout()

        # Show the heatmap
        plt.show()