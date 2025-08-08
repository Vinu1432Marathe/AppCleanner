package com.software.app.update.smart.Other

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.software.app.update.smart.Language.Model_Language
import com.software.app.update.smart.R

object AppUtils {

    val lstAndroVersion = listOf(
        R.drawable.andro16,
        R.drawable.andro15,
        R.drawable.andro14,
        R.drawable.andro14,
        R.drawable.andro13,
        R.drawable.andro12,
        R.drawable.andro12,
        R.drawable.andro11,
        R.drawable.andro10,
        R.drawable.andro9,
        R.drawable.andro_or,
        R.drawable.andro_nougat,
        R.drawable.andro_marsh,
        R.drawable.andro_marsh,
        R.drawable.andro_lollipop,
        R.drawable.andro_kitkat,
        R.drawable.andro_jellybean,
        R.drawable.andro_icsand,
        R.drawable.andro_honeycomb,
        R.drawable.andro_gingerbre,
        R.drawable.andro_froyo,
        R.drawable.andro_eclair,
        R.drawable.andro_donut,
        R.drawable.andro_donut,
        R.drawable.andro_cupcake,
        R.drawable.andro_petitfour,
        R.drawable.andro1
    )


    val languages_list = listOf(
        Model_Language("English(US)", "en", R.drawable.us),
        Model_Language("English(UK)", "en", R.drawable.united_kingdom),
        Model_Language("Hindi", "hi", R.drawable.hindi),
        Model_Language("Spanish", "es", R.drawable.spain),
        Model_Language("French", "fr", R.drawable.france),
        Model_Language("German", "de", R.drawable.germany),
        Model_Language("Portuguese", "pt", R.drawable.portuguese),
        Model_Language("Arabic", "ar", R.drawable.arabic),
        Model_Language("Russian", "ru", R.drawable.russian),
        Model_Language("Turkish", "tr", R.drawable.turkish),
    )




    fun shareApp(context: Context) {
        val shareIntent = Intent(Intent.ACTION_SEND)
        shareIntent.type = "text/plain"
        shareIntent.putExtra(
            Intent.EXTRA_TEXT,
            "Hey, check out this awesome app! ${
                context.packageManager.getPackageInfo(
                    context.packageName,
                    0
                ).applicationInfo?.loadLabel(context.packageManager)
            } https://play.google.com/store/apps/details?id=${context.packageName}"
        )
        context.startActivity(Intent.createChooser(shareIntent, "Share via"))
    }

    fun rateUs(context: Context) {
        val packageName = context.packageName
        val uri = Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
        val intent = Intent(Intent.ACTION_VIEW, uri)
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            context.startActivity(
                Intent(
                    Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=$packageName")
                )
            )
        }
    }


    fun openPrivacy(context: Context) {

        val intent = Intent(Intent.ACTION_VIEW)
        val configPref = MyApp.ad_preferences.getRemoteConfig()

        if (configPref?.privacyPolicy?.isNotEmpty() == true) {

            intent.data = Uri.parse(configPref.privacyPolicy)
            try {
                context.startActivity(intent)
            } catch (e: Exception) {
            }
        } else {
            Toast.makeText(context, "Unable to load!", Toast.LENGTH_SHORT).show()
        }


    }

}