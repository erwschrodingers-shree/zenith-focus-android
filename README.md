# Zenith - Focus App 🎯

**A strict, minimalist digital detox and focus lock application for Android**

Zenith is a production-ready Android app inspired by minimalist design principles and the Blockit aesthetic. It provides an unbreakable focus lock with a retro digital flip-clock interface, strict system-level enforcement, and rewarding break mechanics.

---

## 🎨 Features

### Core Functionality
- **Zen Mode Timer**: Retro digital flip-clock display with preset durations (15, 45, 90 min) or custom time selection
- **Strict Lock Guarantee**: Once activated, the timer CANNOT be cancelled, force-closed, or overridden until completion
- **Slide-to-Start Mechanism**: Haptic-feedback enabled slider to enter focus mode
- **Allowed Apps Whitelist**: Select up to 6 whitelisted apps (Phone, WhatsApp, Calculator, etc.) accessible during Zen Mode

### Display Modes
- **Dimmed Clock Display**: Screen dims to 5% brightness showing only the flip-clock
- **Screen-Off Mode**: Display turns off while timer runs seamlessly in background via foreground service

### Audio & Notifications
- **MP3 Loop Player**: Play ambient audio, white noise, or rain sounds during focus sessions with inline play/pause/volume control
- **Do Not Disturb Toggle**: Suppresses all system notifications and floating alerts
- **Notification Interception**: Mutes incoming distractions completely during active sessions

### Reward System
- **Reward Break Switch**: Automatically pauses the lock at 50% completion and opens a short break timer (1/5th of total duration) with gentle chime feedback

### Theme Support
- **Light Mode**: Off-white canvas (#F4F4F6), subtle gray borders, dark slate typography, Saffron Orange accents
- **Dark/OLED Mode**: Pure dark canvas (#0A0A0C), slate containers, white text, glowing Saffron Orange accents
- **Toggle-able Theme**: Switch between themes from dashboard

---

## 🏗️ Architecture

### Project Structure
```
app/src/main/java/com/zenith/focus/
├── data/
│   ├── model/          # Data classes (TimerUIState, ZenConfig, AppInfo)
│   ├── db/             # Room Database (ZenthDatabase, ZenSessionDao)
│   └── repository/     # SettingsRepository
├── domain/
│   └── usecase/        # Business logic (GetInstalledAppsUseCase, PermissionCheckUseCase, AudioLoopUseCase)
├── presentation/
│   ├── screen/         # Composable screens (Dashboard, ZenMode, PermissionOnboarding)
│   ├── ui/
│   │   ├── component/  # UI components (DigitalFlipClock, SlideToStart, AllowedAppsDock)
│   │   └── theme/      # Theme definitions (colors, typography, shapes)
│   ├── viewmodel/      # State management (TimerViewModel, PermissionViewModel)
│   ├── MainActivity.kt # Entry point with navigation
│   └── ZenModeActivity.kt # Immersive locked screen
├── service/
│   ├── ZenModeService.kt # Foreground timer service
│   └── ZenthNotificationListenerService.kt # Notification interception
├── receiver/
│   ├── BootCompletedReceiver.kt # Device reboot resilience
│   └── BreakAlarmReceiver.kt # Break timer triggers
└── util/
    ├── OverlayManager.kt # System-level overlay control
    ├── HapticFeedback.kt # Vibration feedback
    └── AudioLoopUseCase.kt # Audio playback
```

### Tech Stack
- **Language**: Kotlin
- **UI Framework**: Jetpack Compose (Material 3)
- **Database**: Room ORM
- **State Management**: StateFlow + ViewModel
- **Background**: Foreground Service + WorkManager
- **Navigation**: Compose Navigation
- **Permissions**: Accompanist Permissions

---

## 📋 Android Permissions & System Integration

Zenith implements critical system-level permissions for true device-level blocking:

### Required Permissions (AndroidManifest.xml)

1. **Notification Access Listener**
   - `android.permission.BIND_NOTIFICATION_LISTENER_SERVICE`
   - Intercepts and clears incoming alerts during Zen Mode

2. **Display Over Other Apps**
   - `android.permission.SYSTEM_ALERT_WINDOW`
   - Draws full-screen overlay preventing app switching

3. **Battery Optimization Exclusion**
   - `android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS`
   - Prevents Android OS from killing foreground service in low-power modes

4. **Foreground Service & Boot Resilience**
   - `android.permission.FOREGROUND_SERVICE`
   - `android.permission.RECEIVE_BOOT_COMPLETED`
   - Keeps timer running even when screen is off or device reboots

5. **Additional**
   - `android.permission.VIBRATE` - Haptic feedback
   - `android.permission.MODIFY_AUDIO_SETTINGS` - Audio focus for MP3 player
   - `android.permission.QUERY_ALL_PACKAGES` - App whitelist discovery
   - `android.permission.WAKE_LOCK` - Screen state control
   - `android.permission.SCHEDULE_EXACT_ALARM` - Break timing

---

## 🚀 Screen Breakdown

### Screen 1: Permission Onboarding
- Step-by-step permission request flow
- Visual indicators for permission status
- Checkboxes: Notification Access → Screen Overlay → Battery Optimization → Background Execution
- Progress bar showing onboarding completion

### Screen 2: Main Dashboard
- **Top Bar**: "Zenith" logo + Theme Toggle
- **Hero Section**: Retro Digital Flip Clock preview showing selected duration
- **Preset Grid**: Quick-select buttons for 15m, 45m, 90m
- **Custom Duration**: Slider for custom time selection
- **Settings Row**: Toggle switches for Audio Loop, DND Mode, Reward Break
- **Allowed Apps Dock**: 6 empty "+" slots to whitelist apps
- **Bottom Action**: Full-width "Slide to Start Zen" slider with haptic feedback

### Screen 3: Zen Mode Locked Screen
- **Immersive Display**: Full-screen with no escape routes
- **Digital Flip Clock**: Large retro clock counting down (MM:SS)
- **Allowed Apps Grid**: Only whitelisted apps accessible (max 6)
- **Audio Controller**: Play/Pause buttons + Volume slider (if audio enabled)
- **Screen Dim Button**: Toggle 5% brightness mode
- **Strict Lock**: No back button, home button, or task switcher access
- **Auto-complete**: Automatically exits when timer reaches 00:00:00

---

## 🔐 Strict Lock Implementation

Zenith enforces an unbreakable focus lock through:

1. **Foreground Service** (`ZenModeService.kt`)
   - Runs continuously with high-priority notification
   - Survives app force-close and device lock
   - WakeLock acquired to prevent system sleep

2. **System Overlay Flags** (`OverlayManager.kt`)
   - `FLAG_KEEP_SCREEN_ON` - Prevents screen off
   - `FLAG_DISMISS_KEYGUARD` - Bypasses lock screen
   - `SYSTEM_UI_FLAG_IMMERSIVE_STICKY` - Hides navigation

3. **Key Override** (`ZenModeActivity.kt`)
   - `onBackPressed()` - No-op (disabled)
   - `onKeyDown()` - Blocks BACK, HOME, APP_SWITCH
   - `onUserLeaveHint()` - Prevents task switcher swipe

4. **Boot Receiver** (`BootCompletedReceiver.kt`)
   - Restarts service if device reboots during session
   - Preserves remaining time for continuity

5. **Notification Listener** (`ZenthNotificationListenerService.kt`)
   - Intercepts all notifications
   - Suppresses distracting alerts
   - Only allows whitelisted apps to notify

---

## 📦 Build & Deployment

### Prerequisites
- Android Studio Flamingo or later
- Android SDK 28+ (minSdkVersion)
- Target SDK 34
- Kotlin 1.9.0+

### Build Commands

```bash
# Debug Build
./gradlew assembleDebug

# Release Build (AAB for Play Store)
./gradlew bundleRelease

# Run on Emulator
./gradlew installDebug

# Run Tests
./gradlew test
```

### Play Store Submission (.AAB)

1. Generate signed AAB:
   ```bash
   ./gradlew bundleRelease --build-type=release
   ```

2. Key Configuration (gradle.properties):
   ```properties
   ZENITH_KEYSTORE_PATH=/path/to/keystore.jks
   ZENITH_KEY_ALIAS=zenith-key
   ZENITH_KEY_PASSWORD=****
   ZENITH_STORE_PASSWORD=****
   ```

3. Upload to Play Console:
   - App name: "Zenith - Focus App"
   - Category: Productivity
   - Content rating: USK 3+
   - Permissions disclosure: DND, Notification Access, Screen Overlay

---

## 🎮 Usage Flow

1. **First Launch**: Permission Onboarding → Grant 4 permissions
2. **Dashboard**: Select duration → Configure settings → Add allowed apps → Slide to start
3. **Zen Mode**: Full-screen lock → Timer counts down → Only whitelisted apps accessible
4. **Completion**: Timer reaches 00:00 → Auto-exit → Return to dashboard
5. **Reward Break** (if enabled): At 50% → Pause 1/5th duration → Gentle chime → Resume

---

## 🛠️ Development Setup

### Clone Repository
```bash
git clone https://github.com/erwschrodingers-shree/zenith-focus-android.git
cd zenith-focus-android
```

### Open in Android Studio
```bash
android-studio .
```

### Run Debug Build
```bash
./gradlew installDebug
```

---

## 📝 Configuration Files

### AndroidManifest.xml
- Declares all permissions and system integrations
- Registers services, receivers, and activities
- Configures notification channels

### build.gradle.kts (App-level)
- Compose setup with Material 3
- Room, DataStore, and WorkManager dependencies
- Accompanist Permissions library
- ProGuard configuration for release builds

### strings.xml
- 40+ localized strings for UI labels
- Support for future i18n expansion

### colors.xml & dimens.xml
- Centralized design tokens
- Light/Dark theme color definitions
- Spacing and sizing constants

---

## 🎨 Design Philosophy

Zenith follows a **strict minimalist aesthetic** inspired by Blockit:

- **No Clutter**: Only essential elements visible
- **High Contrast**: Dark/Light modes with vibrant accent color (Saffron Orange)
- **Generous Whitespace**: Breathing room between components
- **Retro Digital**: Monospace font for timer display
- **Tactile Feedback**: Haptic vibrations on slider interactions
- **Grid-Based Layout**: Consistent 16dp spacing system
- **Rounded Corners**: 16dp border-radius for modern feel

---

## 📊 Session Tracking

Zenith records focus sessions in Room Database:

```kotlin
data class ZenSession(
    val id: Long,
    val durationMinutes: Int,
    val startTime: Long,
    val endTime: Long?,
    val completed: Boolean,
    val hasAudio: Boolean,
    val dndEnabled: Boolean,
    val rewardBreakEnabled: Boolean,
    val allowedApps: String // JSON serialized
)
```

### Queries Available
- Recent sessions (last 10)
- Completed session count
- Total focus minutes
- Session details by ID

---

## 🔄 Future Enhancements

- [ ] Cloud sync of session history
- [ ] Statistics & focus streak tracking
- [ ] Custom app blocking rules
- [ ] Integration with focus modes (Do Not Disturb)
- [ ] Widget support for quick focus launch
- [ ] Multilingual support (i18n)
- [ ] Accessibility improvements (TalkBack, high contrast)
- [ ] Custom audio file picker from device storage

---

## 📄 License

MIT License - See LICENSE file for details

---

## 👨‍💻 Developer

**Shree** - erwschrodingers@gmail.com

---

## 🤝 Contributing

Contributions welcome! Please:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/your-feature`)
3. Commit changes (`git commit -am 'Add feature'`)
4. Push to branch (`git push origin feature/your-feature`)
5. Open a Pull Request

---

## 📞 Support

For issues, feature requests, or questions, please open an [issue](https://github.com/erwschrodingers-shree/zenith-focus-android/issues).

---

**Zenith: Pure Focus. Zero Distractions. 🎯**
