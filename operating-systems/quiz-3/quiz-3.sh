#!/bin/bash

# Check if the correct number of arguments is provided
if [ "$#" -ne 2 ]; then
    echo "Usage: $0 <usernames file> <file to check>"
    exit 1
fi

# Assign arguments to variables
usernames_file=$1
file_to_check=$2

# Verify that the usernames file exists
if [ ! -f "$usernames_file" ]; then
    echo "Error: Usernames file '$usernames_file' does not exist."
    exit 1
fi

# Loop through each username in the file
while read -r username; do
    echo ""
    echo "$username"
    echo "##########"

    # Get the home directory of the user
    home_dir=$(eval echo "~$username")

    # Check if the home directory exists and is accessible
    if [ ! -d "$home_dir" ]; then
        echo "$file_to_check: Permission denied."
        continue
    fi

    # Check if the file exists in the home directory
    file_path="$home_dir/$file_to_check"
    if [ ! -f "$file_path" ]; then
        echo "$file_to_check: File not found."
    else
        # Get the file size if the file exists
        file_size=$(stat -c%s "$file_path")
        echo "$file_to_check: File found, size $file_size bytes."
    fi

done < "$usernames_file"