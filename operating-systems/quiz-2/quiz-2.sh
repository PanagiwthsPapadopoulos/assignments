#!/bin/bash

# Check if the filename is passed as an argument, or declare it within the script
input_file="${1:-usernames.txt}"

# Verify if the input file exists
if [[ ! -f "$input_file" ]]; then
  echo "Error: File '$input_file' not found!"
  exit 1
fi

# Read each username from the input file
while read -r username; do
  if [[ -n "$username" ]]; then
    echo "User: $username"
    echo "##########"
    
    # Get the last 20 login records for the user
    echo "Last login date(s):"
    last -n 20 "$username" | awk '!/wtmp/ && NF {print $4, $5, $6, $7}' | sed '/^$/d' # Fetch and>
    echo " "
    echo "Last login IP(s)/hostname(s):"
    last -n 20 "$username" | awk '!/wtmp/ && NF {print $3}' | sed '/^$/d' | while read -r ip; do
      # Resolve hostname from IP if possible
      if [[ "$ip" =~ ^[0-9]+\.[0-9]+\.[0-9]+\.[0-9]+$ ]]; then
        hostname=$(nslookup "$ip" 2>/dev/null | awk '/name =/ {print $4}' | sed 's/\.$//')
        echo "$ip"
        echo "${hostname:-No hostname found}"
      else
        echo "$ip - (Not an IP address)"
      fi
    done
    echo " "
  fi
done < "$input_file"
