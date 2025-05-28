#!/bin/bash

# Exit on error
set -e

# Check if a file path is provided as an argument
if [ $# -ne 1 ]; then
    echo "Error: Please provide the path to the file to serve."
    echo "Usage: $0 <file-to-serve>"
    exit 1
fi

FILE_PATH="$1"

# Check if the file exists
if [ ! -f "$FILE_PATH" ]; then
    echo "Error: File '$FILE_PATH' does not exist or is not a regular file."
    exit 1
fi

echo "Starting line-server with file: $FILE_PATH"

# Run the Java application with the file path as an argument
java -jar build/libs/line-server-0.0.1-SNAPSHOT.jar "$FILE_PATH"
