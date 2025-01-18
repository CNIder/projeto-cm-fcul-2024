package com.example.projeto_cm_24_25.utils

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log

object ManifestUtils {

    fun getApiKeyFromManifest(context: Context, metaDataName: String): String? {
        return try {
            val appInfo = context.packageManager.getApplicationInfo(
                context.packageName,
                PackageManager.GET_META_DATA
            )
            appInfo.metaData?.getString(metaDataName)
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
            Log.e("ManifestUtils", "Meta-data not found: $metaDataName")
            null
        }
    }
}
