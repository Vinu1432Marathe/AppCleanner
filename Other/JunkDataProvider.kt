package com.software.app.update.smart.Other

import android.content.Context
import com.software.app.update.smart.Model.JunkCategory
import com.software.app.update.smart.Model.JunkItem
import com.software.app.update.smart.R

object JunkDataProvider {
    fun getMockJunkData(context: Context): List<JunkCategory> {
        return listOf(
            JunkCategory(
                "Useless APKs", 24.6, listOf(
                    JunkItem("App-Release.Apk", context.getDrawable(R.drawable.device_id)!!, 10.0),
                    JunkItem("Final-Build.Apk", context.getDrawable(R.drawable.dublicate)!!, 14.6)
                )
            ),
            JunkCategory(
                "System Cache", 1505.6, listOf(
                    JunkItem("YouTube", context.getDrawable(R.drawable.check_update)!!, 734.7),
                    JunkItem("Google Chrome", context.getDrawable(R.drawable.check_update)!!, 339.9),
                    JunkItem("Files by Google", context.getDrawable(R.drawable.check_update)!!, 400.0),
                    JunkItem("Google Docs", context.getDrawable(R.drawable.check_update)!!, 30.5)
                )
            )
        )
    }

}