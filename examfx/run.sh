#!/bin/bash
# ============================================================
#  Online Examination System — JavaFX
#  Build & Run Script (Linux / macOS)
# ============================================================

echo "🎓 Building ExamPortal (JavaFX)..."
echo ""

# ── Step 1: Locate JavaFX ─────────────────────────────────────────────────────
# Set JAVAFX_PATH to your JavaFX SDK lib folder.
# Download from: https://gluonhq.com/products/javafx/
#
# Examples:
#   export JAVAFX_PATH=/opt/javafx-sdk-21/lib
#   export JAVAFX_PATH=$HOME/javafx-sdk-21/lib
#
# If JAVAFX_PATH is already set in the environment, that will be used.

if [ -z "$JAVAFX_PATH" ]; then
    # Auto-detect common locations
    for candidate in \
        /opt/javafx-sdk-21/lib \
        /opt/javafx-sdk-22/lib \
        /opt/javafx-sdk-23/lib \
        $HOME/javafx-sdk-21/lib \
        $HOME/javafx-sdk-22/lib \
        $HOME/Downloads/javafx-sdk-21/lib \
        /usr/share/openjfx/lib; do
        if [ -d "$candidate" ]; then
            JAVAFX_PATH="$candidate"
            echo "✅ Found JavaFX at: $JAVAFX_PATH"
            break
        fi
    done
fi

if [ -z "$JAVAFX_PATH" ]; then
    echo "❌ JavaFX SDK not found."
    echo ""
    echo "Please either:"
    echo "  1. Download JavaFX SDK from https://gluonhq.com/products/javafx/"
    echo "     and set: export JAVAFX_PATH=/path/to/javafx-sdk/lib"
    echo "  2. Or install via: sudo apt install openjfx  (Ubuntu/Debian)"
    echo "     and set: export JAVAFX_PATH=/usr/share/openjfx/lib"
    exit 1
fi

# ── Step 2: Compile ───────────────────────────────────────────────────────────
mkdir -p out
SOURCES=$(find src -name "*.java")

echo "⚙️  Compiling..."
javac \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.fxml \
    -d out \
    $SOURCES

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Compilation failed."
    exit 1
fi

echo "✅ Compilation successful!"
echo ""

# ── Step 3: Run ───────────────────────────────────────────────────────────────
echo "🚀 Launching ExamPortal..."
echo ""
echo "Demo Accounts:"
echo "  Admin:   admin@exam.com   / admin123"
echo "  Student: pulkit@gmail.com / pulkit123"
echo ""

java \
    --module-path "$JAVAFX_PATH" \
    --add-modules javafx.controls,javafx.fxml \
    -cp out \
    com.examgui.Main
