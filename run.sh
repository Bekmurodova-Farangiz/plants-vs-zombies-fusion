#!/bin/sh

set -eu

SCRIPT_DIR=$(CDPATH= cd -- "$(dirname "$0")" && pwd)

"$SCRIPT_DIR/build.sh"

find_javafx_lib_dir() {
    if [ -n "${JAVAFX_HOME:-}" ] && [ -d "${JAVAFX_HOME}/lib" ]; then
        printf '%s\n' "${JAVAFX_HOME}/lib"
        return 0
    fi

    for candidate in \
        "$SCRIPT_DIR/../javafx-sdk-"* \
        "$HOME/Desktop/javafx-sdk-"* \
        "$SCRIPT_DIR/javafx-sdk-"*
    do
        if [ -d "$candidate/lib" ]; then
            printf '%s\n' "$candidate/lib"
            return 0
        fi
    done

    return 1
}

JAVAFX_LIB_DIR=$(find_javafx_lib_dir || true)

if [ -z "$JAVAFX_LIB_DIR" ]; then
    echo "JavaFX SDK not found."
    echo "Set JAVAFX_HOME or place a javafx-sdk-* folder next to this project."
    exit 1
fi

java \
    --module-path "$JAVAFX_LIB_DIR" \
    --add-modules javafx.controls,javafx.fxml \
    -cp "$SCRIPT_DIR/out" \
    Main
