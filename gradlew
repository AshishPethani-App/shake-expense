#!/bin/sh
#
# Shake Expense Gradle launcher
#
# The GitHub Actions workflows use Gradle 8.9 directly via
# gradle/actions/setup-gradle@v4 because this project archive did not include
# the Gradle wrapper JAR.
#
# For local builds, install Gradle 8.9 and run:
#   gradle assembleDebug
#
echo "Gradle Wrapper JAR is not included in this archive."
echo "Install Gradle 8.9 and run: gradle assembleDebug"
exit 1
