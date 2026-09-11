package com.zenith.focus.domain.usecase

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.zenith.focus.data.model.AppInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GetInstalledAppsUseCase(private val context: Context) {

    suspend fun execute(): List<AppInfo> = withContext(Dispatchers.Default) {
        val pm = context.packageManager
        val apps = mutableListOf<AppInfo>()

        try {
            val packages = pm.getInstalledApplications(PackageManager.GET_META_DATA)
            packages.forEach { appInfo ->
                // Filter out system apps
                if ((appInfo.flags and ApplicationInfo.FLAG_SYSTEM) == 0 ||
                    isSystemAppAllowed(appInfo.packageName)
                ) {
                    apps.add(
                        AppInfo(
                            packageName = appInfo.packageName,
                            appName = pm.getApplicationLabel(appInfo).toString(),
                            isSelected = false
                        )
                    )
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        apps.sortedBy { it.appName }
    }

    private fun isSystemAppAllowed(packageName: String): Boolean {
        return packageName in listOf(
            "com.android.phone",
            "com.android.contacts",
            "com.android.messaging",
            "com.android.calculator2"
        )
    }

    fun isAppLaunchable(packageName: String): Boolean {
        val intent = context.packageManager.getLaunchIntentForPackage(packageName)
        return intent != null
    }

    fun launchApp(packageName: String) {
        try {
            val intent = context.packageManager.getLaunchIntentForPackage(packageName)
            intent?.let { context.startActivity(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}
