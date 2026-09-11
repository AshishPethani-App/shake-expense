#!/bin/sh
#
# Gradle start up script for UN*X
#
APP_HOME="$(cd "$(dirname "$0")" && pwd -P)"
exec "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@" || java -jar "$APP_HOME/gradle/wrapper/gradle-wrapper.jar" "$@"
