# Zenith - Build & Architecture Guide

## Quick Start

### Prerequisites
- Android Studio Flamingo or later
- JDK 17+
- Android SDK 28+ (minSdkVersion: 28, targetSdkVersion: 34)
- 2GB RAM minimum for emulator

### First Build

1. **Clone & Open**
   ```bash
   git clone https://github.com/erwschrodingers-shree/zenith-focus-android.git
   cd zenith-focus-android
   open -a "Android Studio" .
   ```

2. **Sync Gradle**
   - Android Studio will automatically sync `build.gradle.kts`
   - Wait for Gradle build to complete

3. **Run on Emulator**
   - Create AVD (Android Virtual Device) - Pixel 5 recommended
   - Click Run (Shift+F10)
   - Select target emulator

4. **Grant Permissions on First Launch**
   - Notification Access: Settings → Apps → Special app access → Notification access
   - Screen Overlay: Settings → Apps → Special app access → Display over other apps
   - Battery Optimization: Settings → Battery → Battery optimization → Remove Zenith
   - Background Execution: Settings → Apps → Zenith → Battery → Background restriction OFF

---

## Project Structure

```
zenith-focus-android/
├── app/
│   ├── build.gradle.kts          # App-level dependencies
│   ├── proguard-rules.pro        # ProGuard configuration
│   └── src/
│       ├── main/
│       │   ├── AndroidManifest.xml
│       │   ├── java/com/zenith/focus/
│       │   │   ├── data/              # Database & repositories
│       │   │   ├── domain/            # Use cases & business logic
│       │   │   ├── presentation/      # UI & Activities
│       │   │   ├── service/           # Foreground & listener services
│       │   │   ├── receiver/          # Broadcast receivers
│       │   │   └── util/              # Utilities
│       │   └── res/
│       │       ├── values/            # Strings, colors, dimensions
│       │       ├── values-night/      # Dark theme resources
│       │       └── mipmap/            # App icons
│       ├── test/
│       └── androidTest/
├── build.gradle.kts              # Project-level configuration
├── settings.gradle.kts           # Module configuration
├── gradle.properties             # Gradle properties
└── .gitignore
```

---

## Gradle Dependency Tree

### Core Android
- `androidx.core:core-ktx` - Kotlin extensions
- `androidx.appcompat:appcompat` - Compatibility library
- `com.google.android.material:material` - Material Design components

### Jetpack Compose
- `androidx.compose.ui:ui` - Core UI framework
- `androidx.compose.material3:material3` - Material Design 3
- `androidx.compose.runtime:runtime-livedata` - LiveData integration
- `androidx.activity:activity-compose` - Activity integration
- `androidx.navigation:navigation-compose` - Navigation between screens

### State Management
- `androidx.lifecycle:lifecycle-runtime-ktx` - Lifecycle awareness
- `androidx.lifecycle:lifecycle-viewmodel-compose` - ViewModel integration

### Database
- `androidx.room:room-runtime` - Database ORM
- `androidx.room:room-compiler` - Code generation (kapt)
- `androidx.room:room-ktx` - Coroutine support

### Data Storage
- `androidx.datastore:datastore-preferences` - Key-value storage

### Background Tasks
- `androidx.work:work-runtime-ktx` - WorkManager

### Media
- `io.coil-kt:coil-compose` - Image loading

### Permissions
- `com.google.accompanist:accompanist-permissions` - Permission UI wrapper

### Testing
- `junit:junit` - Unit testing
- `androidx.test.ext:junit` - Android test extension
- `androidx.test.espresso:espresso-core` - UI testing
- `androidx.compose.ui:ui-test-junit4` - Compose testing

---

## Key Architecture Patterns

### MVVM Architecture
```
UI (Composable)
    ↓
ViewModel (State Management)
    ↓
UseCase (Business Logic)
    ↓
Repository (Data Access)
    ↓
Database / SharedPreferences / Services
```

### State Flow Pattern
```kotlin
// ViewModel
private val _timerState = MutableStateFlow(TimerUIState())
val timerState: StateFlow<TimerUIState> = _timerState.asStateFlow()

// UI (Composable)
val timerState by timerViewModel.timerState.collectAsState()
```

### Service Lifecycle
```
StartForegroundService()
    ↓
ZenModeService.onStartCommand()
    ↓
startForeground() with Notification
    ↓
Coroutine Loop (Timer Tick)
    ↓
stopForeground() / stopSelf()
```

---

## Critical System Integrations

### 1. Foreground Service (ZenModeService.kt)
```kotlin
// Start
val intent = Intent(context, ZenModeService::class.java).apply {
    action = ZenModeService.ACTION_START
    putExtra(ZenModeService.EXTRA_DURATION, 15) // minutes
}
context.startForegroundService(intent)

// Stop
val intent = Intent(context, ZenModeService::class.java).apply {
    action = ZenModeService.ACTION_STOP
}
context.startForegroundService(intent)
```

### 2. Notification Listener Service (ZenthNotificationListenerService.kt)
- User must grant Notification Access permission
- Service runs continuously in background
- Intercepts and cancels notifications when `isZenModeActive = true`

### 3. Boot Receiver (BootCompletedReceiver.kt)
- Triggered on device boot (`ACTION_BOOT_COMPLETED`)
- Checks SharedPreferences for active session
- Restarts ZenModeService with remaining time if needed

### 4. System Overlay (OverlayManager.kt)
```kotlin
overlayManager.enableStrictLock(activity)
// Sets window flags:
// - FLAG_KEEP_SCREEN_ON
// - FLAG_DISMISS_KEYGUARD
// - FLAG_SHOW_WHEN_LOCKED
// - SYSTEM_UI_FLAG_IMMERSIVE_STICKY
```

### 5. WakeLock (ZenModeService.kt)
```kotlin
wakeLock = powerManager.newWakeLock(
    PowerManager.PARTIAL_WAKE_LOCK,
    "Zenith::FocusLock"
).apply { acquire() }
```

---

## Data Persistence

### SharedPreferences (SettingsRepository.kt)
```kotlin
prefs.edit {
    putInt("selected_duration", 15)
    putBoolean("dark_theme", false)
    putString("allowed_apps", "com.package1,com.package2")
    putBoolean("session_active", true)
    putInt("remaining_minutes", 10)
}
```

### Room Database (ZenthDatabase.kt)
```kotlin
@Entity(tableName = "zen_sessions")
data class ZenSession(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val durationMinutes: Int,
    val startTime: Long,
    val endTime: Long? = null,
    val completed: Boolean = false,
    // ... other fields
)
```

---

## Testing Strategy

### Unit Tests (src/test/)
- ViewModel logic
- Use case execution
- Database queries

### Instrumented Tests (src/androidTest/)
- UI component rendering
- Navigation flows
- Permission handling

### Run Tests
```bash
# Unit tests
./gradlew test

# Instrumented tests (requires emulator/device)
./gradlew connectedAndroidTest
```

---

## Release Build Process

### 1. Create Keystore
```bash
keytool -genkey -v -keystore zenith-release.jks \
  -keyalg RSA -keysize 2048 -validity 10000 \
  -alias zenith-key
```

### 2. Configure gradle.properties
```properties
ZENITH_KEYSTORE_PATH=../zenith-release.jks
ZENITH_KEY_ALIAS=zenith-key
ZENITH_KEY_PASSWORD=yourpassword
ZENITH_STORE_PASSWORD=yourpassword
```

### 3. Update build.gradle.kts
```kotlin
signingConfigs {
    release {
        storeFile = file(System.getenv("ZENITH_KEYSTORE_PATH") ?: "../keystore.jks")
        storePassword = System.getenv("ZENITH_STORE_PASSWORD")
        keyAlias = System.getenv("ZENITH_KEY_ALIAS")
        keyPassword = System.getenv("ZENITH_KEY_PASSWORD")
    }
}

buildTypes {
    release {
        signingConfig = signingConfigs.release
        isMinifyEnabled = true
        proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
    }
}
```

### 4. Build AAB
```bash
./gradlew bundleRelease
```

Output: `app/build/outputs/bundle/release/app-release.aab`

---

## Performance Optimization

### ProGuard Configuration
```proguard
# Keep Compose classes
-keep class androidx.compose.** { *; }

# Keep Room entities
-keep class androidx.room.** { *; }

# Keep our app classes
-keep class com.zenith.focus.** { *; }
```

### Coroutine Best Practices
- Use `Dispatchers.Default` for timer calculations
- Use `Dispatchers.Main.immediate` for UI updates
- Properly cancel coroutines in `onDestroy()`

### Memory Management
- Release WakeLock when service destroyed
- Cancel MediaPlayer when audio stops
- Clear large bitmaps/resources

---

## Debugging Tips

### Logcat Filters
```bash
# Timer events
adb logcat | grep "ZenModeService"

# Notification interception
adb logcat | grep "ZenthNotificationListener"

# Boot events
adb logcat | grep "ZenthBootReceiver"
```

### Android Studio Debugger
- Set breakpoints in ViewModel
- Inspect coroutine state
- Monitor foreground service lifecycle

### Profiler
- Monitor CPU usage during timer
- Track memory leaks
- Analyze battery drain (WakeLock usage)

---

## Common Issues & Solutions

| Issue | Cause | Solution |
|-------|-------|----------|
| Crash on permission screens | Missing manifest entries | Run `./gradlew androidLint` |
| Timer stops in background | Service killed by system | Ensure foreground notification shown |
| Notification access not working | Permission not granted | User must enable in settings manually |
| Screen overlay permission denied | Android 6+ requires explicit grant | Direct user to Settings |
| Crashes after device reboot | Boot receiver not registered | Check AndroidManifest.xml |

---

## Publishing Checklist

- [ ] Build type set to Release
- [ ] Version code incremented
- [ ] Version name updated (semantic versioning)
- [ ] ProGuard enabled
- [ ] All permissions justified in Play Store listing
- [ ] Privacy policy created
- [ ] Screenshots prepared (5-8 in 1280x720)
- [ ] Feature graphic created (1024x500)
- [ ] App description written (80 char headline, 4000 char description)
- [ ] Content rating form completed
- [ ] AAB bundle built and tested on multiple devices

---

**For more details, see README.md**
