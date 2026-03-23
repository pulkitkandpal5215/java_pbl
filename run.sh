#!/bin/bash
# ============================================================
#  Online Examination System — Java Swing GUI
#  Build & Run Script
# ============================================================

echo "🎓 Building Online Examination System..."
echo ""

# Create output directory
mkdir -p out

# Find all Java files
SOURCES=$(find src -name "*.java")

# Compile
echo "⚙️  Compiling Java files..."
javac -d out $SOURCES

if [ $? -ne 0 ]; then
    echo ""
    echo "❌ Compilation failed. Please check the errors above."
    exit 1
fi

echo "✅ Compilation successful!"
echo ""
echo "🚀 Launching the application..."
echo ""
echo "Demo Accounts:"
echo "  Admin:   admin@exam.com   / admin123"
echo "  Student: alice@exam.com   / alice123"
echo "  Student: bob@exam.com     / bob123"
echo ""

# Run
java -cp out com.examgui.Main
