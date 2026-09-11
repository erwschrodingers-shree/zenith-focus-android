#!/bin/bash
# Zenith Build Script

set -e

echo "🎯 Zenith - Focus App Build Script"
echo "==================================="

# Check Java version
echo "✓ Checking Java version..."
java -version

# Check Android SDK
echo "✓ Checking Android SDK..."
if [ ! -d "$ANDROID_HOME" ]; then
    echo "❌ ANDROID_HOME not set. Please install Android SDK."
    exit 1
fi

# Sync Gradle
echo "✓ Syncing Gradle dependencies..."
./gradlew sync

# Run linter
echo "✓ Running Android Lint..."
./gradlew lint

# Run unit tests
echo "✓ Running unit tests..."
./gradlew test

# Build debug APK
echo "✓ Building debug APK..."
./gradlew assembleDebug

echo ""
echo "✅ Debug build complete!"
echo "📱 APK location: app/build/outputs/apk/debug/app-debug.apk"
echo ""
echo "To build release AAB for Play Store:"
echo "  ./gradlew bundleRelease"
echo ""
echo "To install on device:"
echo "  adb install app/build/outputs/apk/debug/app-debug.apk"
