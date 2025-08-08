package com.software.app.update.smart.Other

import android.content.Context
import android.os.Environment
import java.io.File

object FileScannerHelper {

    fun getUselessAPKs(): List<File> {
        val apkFiles = mutableListOf<File>()
        val dirs = listOf(
            Environment.getExternalStorageDirectory(),
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        )

        dirs.forEach { dir ->
            dir?.listFiles()?.forEach { file ->
                if (file.isFile && file.name.endsWith(".apk", true)) {
                    apkFiles.add(file)
                }
            }
        }
        return apkFiles
    }

    fun getSystemCacheDirs(context: Context): List<File> {
        val caches = mutableListOf<File>()
        caches.add(context.cacheDir)
        context.externalCacheDirs?.let { caches.addAll(it) }
        return caches
    }

    fun getDownloadedFiles(): List<File> {
        val downloadsDir =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
        return downloadsDir.listFiles()?.toList() ?: emptyList()
    }

    fun getLargeFiles(minSizeMB: Long = 100): List<File> {
        val largeFiles = mutableListOf<File>()
        val storageDir = Environment.getExternalStorageDirectory()
        storageDir.walkTopDown().forEach { file ->
            if (file.isFile && file.length() > minSizeMB * 1024 * 1024) {
                largeFiles.add(file)
            }
        }
        return largeFiles
    }

}
