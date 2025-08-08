package com.software.app.update.smart.Model

import android.graphics.drawable.Drawable


data class AppInfo(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val iconDrawable: Drawable,
    val installerPackageName: String?  // null if unknown
)

data class AndroVersion(val Version: List<AndroCategory>)
data class AndroCategory(
    val id: Int,
    val AndroVersion: String,
    val VerName: String,
    val ReleaseDate: String,
    val data: List<String>
)

data class DuplicateSet(
    val setId: Int,
    val imagePaths: List<String>
)

data class AppInfo1(
    val appName: String,
    val packageName: String,
    val version: String,
    val installDate: String,
    val size: String,
    val icon: Drawable
)

data class AppUsageModel(
    val appName: String,
    val packageName: String,
    val icon: Drawable,
    val timeSpent: Long,
    val launchCount: Int,
    val lastUsed: Long,
    val dataUsedBytes: Long
)
data class AppInfoModel(
    val appName: String,
    val packageName: String,
    val version: String,
    val installDate: String,
    val size: String,
    val icon: Drawable
)
data class JunkItem(
    val appName: String,
    val appIcon: Drawable,
    val sizeInMB: Double,
    val isChecked: Boolean = true
)

data class JunkCategory(
    val title: String,
    val totalSize: Double,
    val items: List<JunkItem>,
    var isExpanded: Boolean = true
)

data class BulUninstall(
    val appName: String,
    val packageName: String,
    val version: String,
    val installDate: String,
    val size: String,
    val icon: Drawable,
    var isSelected: Boolean = false
)

data class Model_slide(val Image: Int, val Title: String, val Dis: String, val ads_show : Int)

//data class UpdateApps(
//    val appName: String,
//    val packageName: String,
//    val versionName: String,
//    val versionCode: Long,
//    val icon: Drawable,
//    val isUpdateAvailable: Boolean = false
//)
data class UpdateApps(
    val appName: String,
    val packageName: String,
    val versionName: String,
    val versionCode: Long,
    val icon: Drawable,
    val installDate: String,
    val size: String
)

data class UpdateApp(
    val total: Int,
    val updateNeeded: Int,
    val upToDate: Int,
    val unknown: Int
)
data class AppCacheModel(
    val appName: String,
    val packageName: String,
    val cacheSize: Long,
    val icon: Drawable
)

data class ImageInfo(val path: String, val size: Long)

//data class AppData(val id: Int, val icon: Drawable, val name: String, val packageName: String)
data class AppData(
//    val id: Int,
//    val icon: Drawable,
//    val name: String,
//    val packageName: String,
//    val size: Long
    val id: Int,
    val icon: Drawable,
    val name: String,
    val packageName: String,
    val versionName: String,
    val installDate: String,
    val size: Long
)

data class AppInfoModel11(
    val appName: String,
    val packageName: String,
    val versionInstalled: String,
    val icon: Drawable,
    var latestVersion: String? = null
)