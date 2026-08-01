#!/usr/bin/env bash
################################################################################
# Gradle start up script for UN*X
#
# This file is a slightly modified copy of the standard Gradle wrapper script.
# It assumes a gradle/wrapper/gradle-wrapper.jar exists. If it does not, run
# 'gradle wrapper' locally to generate wrapper jar, or install Gradle and run
# './gradlew wrapper' after creating the jar.
################################################################################

set -e

DIR="$(cd "$(dirname "$0")" && pwd)"
JAVA_CMD="java"

if [ -n "${JAVA_HOME-}" ] ; then
    if [ -x "${JAVA_HOME}/bin/java" ] ; then
        JAVA_CMD="${JAVA_HOME}/bin/java"
    fi
fi

WRAPPER_JAR="$DIR/gradle/wrapper/gradle-wrapper.jar"
WRAPPER_PROPERTIES="$DIR/gradle/wrapper/gradle-wrapper.properties"

if [ ! -f "$WRAPPER_JAR" ] ; then
  echo "Warning: gradle-wrapper.jar not found. You may need to generate it by running 'gradle wrapper' locally or installing Gradle."
fi

CLASSPATH="$WRAPPER_JAR"

exec "$JAVA_CMD" -cp "$CLASSPATH" org.gradle.wrapper.GradleWrapperMain "$@"
