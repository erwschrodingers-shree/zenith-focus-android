# Zenith - Security & Permissions Policy

## Privacy Commitment

Zenith prioritizes user privacy and data security:

- ✅ No cloud connectivity (all data stored locally)
- ✅ No user tracking or analytics
- ✅ No advertisements or third-party SDKs
- ✅ No internet access required
- ✅ Open-source code (auditable)

## Permissions Explained

### Required Permissions

| Permission | Purpose | Justification |
|-----------|---------|---------------|
| `BIND_NOTIFICATION_LISTENER_SERVICE` | Suppress distracting notifications | Blocks alerts during focus sessions |
| `SYSTEM_ALERT_WINDOW` | Display full-screen overlay lock | Prevents app switching during Zen Mode |
| `REQUEST_IGNORE_BATTERY_OPTIMIZATIONS` | Keep timer running in low-power mode | Ensures focus lock persists |
| `FOREGROUND_SERVICE` | Run timer in background | Maintains session when screen off |
| `RECEIVE_BOOT_COMPLETED` | Restart sessions after reboot | Preserves active timers |
| `VIBRATE` | Haptic feedback | User interaction feedback |
| `MODIFY_AUDIO_SETTINGS` | Audio focus for MP3 player | Play ambient sounds without interruption |
| `QUERY_ALL_PACKAGES` | List installed apps for whitelist | Allow selection of allowed apps |
| `WAKE_LOCK` | Keep screen on during focus | Prevent automatic sleep |
| `SCHEDULE_EXACT_ALARM` | Break timer precision | Accurate break notifications |

### Data Stored Locally

All data stored in Android's private app storage:
- **SharedPreferences**: Settings, user preferences, session state
- **Room Database**: Focus session history, statistics
- **Cache**: Temporary session data

**Data is NOT shared with external services.**

## Security Best Practices

1. **ProGuard Obfuscation**: Code obfuscated in release builds
2. **No Hardcoded Secrets**: No API keys or credentials in code
3. **Secure Permissions**: Only request necessary permissions
4. **Input Validation**: Sanitize user inputs
5. **Crash Reporting**: None (no external logging)

## Permission Revocation

Users can revoke Zenith permissions at any time:

**Settings → Apps → Zenith → Permissions**

If permissions are revoked:
- Notification access won't suppress alerts
- Screen overlay won't display
- Battery optimization won't be excluded
- Foreground service may be killed by system

## Third-Party Libraries

All dependencies are open-source and audited:
- Jetpack libraries (Google)
- Material Design 3 (Google)
- Room database (Google)
- Kotlin standard library

**No third-party analytics, tracking, or advertising SDKs.**

## Vulnerability Reporting

If you discover a security vulnerability:

1. **DO NOT** open a public GitHub issue
2. Email: **security@zenith-focus.dev** (when available)
3. Include: Vulnerability description, reproduction steps, impact
4. Response time: Within 7 days

## Compliance

- ✅ GDPR compliant (no personal data collection)
- ✅ CCPA compliant (no data selling)
- ✅ No persistent device identifiers
- ✅ Respects Android privacy best practices

---

**Last Updated**: 2024
**Status**: Production-Ready
