#!/bin/bash
# Zenith Release Build Script

set -e

echo "🎯 Zenith - Release Build"
echo "========================"

# Check environment
if [ -z "$ZENITH_KEYSTORE_PATH" ]; then
    echo "❌ Error: ZENITH_KEYSTORE_PATH not set"
    echo "Set environment variables:"
    echo "  export ZENITH_KEYSTORE_PATH=path/to/keystore.jks"
    echo "  export ZENITH_KEY_ALIAS=zenith-key"
    echo "  export ZENITH_KEY_PASSWORD=yourpassword"
    echo "  export ZENITH_STORE_PASSWORD=yourpassword"
    exit 1
fi

echo "✓ Environment variables configured"
echo "✓ Building release AAB bundle..."

./gradlew bundleRelease

echo ""
echo "✅ Release build complete!"
echo "📦 AAB location: app/build/outputs/bundle/release/app-release.aab"
echo ""
echo "Ready for Play Store submission."
echo "Upload to: https://play.google.com/console"
