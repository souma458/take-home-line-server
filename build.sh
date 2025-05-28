#!/bin/bash

# Exit on error
set -e

echo "Building line-server application..."

# Use Gradle wrapper to build the application
./gradlew clean build

echo "Build completed successfully. The JAR file is located at build/libs/"
