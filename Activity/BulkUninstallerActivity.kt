package com.software.app.update.smart.Activity

import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.icu.text.SimpleDateFormat
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.Adapter.BulkUninstallerAdapter
import com.software.app.update.smart.Model.AppData
import com.software.app.update.smart.R
import java.io.File
import java.util.Date
import java.util.Locale

class BulkUninstallerActivity : BaseActivity() {

    private lateinit var adapter: BulkUninstallerAdapter
    private lateinit var appsList: MutableList<AppData>
    private var uninstallQueue: MutableList<String> = mutableListOf()

    private lateinit var container: LinearLayout
    private lateinit var txtSizeInfo: TextView
    private lateinit var btnDelete: TextView
    private lateinit var btnCancel: TextView
    private lateinit var imgBack: ImageView
    private lateinit var rlBottomView: RelativeLayout

    private val uninstallLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
        if (uninstallQueue.isNotEmpty()) {
            uninstallQueue.removeAt(0)
            reloadAppList()

            if (uninstallQueue.isNotEmpty()) {
                launchUninstall(uninstallQueue.first())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bulk_uninstaller)

        txtSizeInfo = findViewById(R.id.txtImageSize)
        btnDelete = findViewById(R.id.btnDelete)
        btnCancel = findViewById(R.id.btnCancel)
        imgBack = findViewById(R.id.imgBack)
        rlBottomView = findViewById(R.id.rlBottomView)
        val btnSelectAll = findViewById<ImageView>(R.id.txtDone)
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)

        imgBack.setOnClickListener { onBackPressed() }


        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")




        appsList = getInstalledAppsList().toMutableList()
        adapter = BulkUninstallerAdapter(appsList) { selected ->
            rlBottomView.visibility = if (selected.isEmpty()) View.GONE else View.VISIBLE
            if (selected.isEmpty()){
                btnSelectAll.setImageResource(R.drawable.ic_done)
            }else{
                btnSelectAll.setImageResource(R.drawable.ic_done1)
            }
            updateSelectedSize()
        }

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        btnSelectAll.setOnClickListener {
            if (adapter.toggleSelectAll()) {
                rlBottomView.visibility = View.VISIBLE
                btnSelectAll.setImageResource(R.drawable.ic_done1)

            } else {
                rlBottomView.visibility = View.GONE
                btnSelectAll.setImageResource(R.drawable.ic_done)
            }
            updateSelectedSize()
        }

        btnCancel.setOnClickListener {
            rlBottomView.visibility = View.GONE
            adapter.clearSelection()
            updateSelectedSize()
        }

        btnDelete.setOnClickListener {
            val selectedApps = adapter.getSelectedApps()
            if (selectedApps.isEmpty()) {
                Toast.makeText(this, "No apps selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            uninstallQueue.clear()
            uninstallQueue.addAll(selectedApps.map { it.packageName })
            launchUninstall(uninstallQueue.first())
        }
    }

    private fun launchUninstall(packageName: String) {
        val intent = Intent(Intent.ACTION_DELETE).apply {
            data = Uri.parse("package:$packageName")
        }
        uninstallLauncher.launch(intent)
    }

    private fun reloadAppList() {
        appsList = getInstalledAppsList().toMutableList()
        adapter.updateData(appsList)
        updateSelectedSize()
    }

    private fun getInstalledAppsList(): List<AppData> {
        val pm = packageManager
        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
            .mapNotNull { appInfo ->
                try {
                    val packageInfo = pm.getPackageInfo(appInfo.packageName, 0)
                    val size = File(appInfo.sourceDir).length()
                    AppData(
                        id = appInfo.uid,
                        icon = pm.getApplicationIcon(appInfo),
                        name = pm.getApplicationLabel(appInfo).toString(),
                        packageName = appInfo.packageName,
                        versionName = packageInfo.versionName ?: "N/A",
                        installDate = dateFormatter.format(Date(packageInfo.firstInstallTime)),
                        size = size
                    )
                } catch (e: Exception) {
                    null
                }
            }.sortedBy { it.name }
    }

    private fun updateSelectedSize() {
        val totalBytes = adapter.getSelectedApps().sumOf { it.size }
        val sizeInMB = totalBytes / (1024.0 * 1024.0)
        txtSizeInfo.text = String.format("Selected Size: %.2f MB", sizeInMB)
    }
}


//class BulkUninstallerActivity : BaseActivity() {
//
//    private lateinit var adapter: BulkUninstallerAdapter
//    private lateinit var appsList: MutableList<AppData>
//    private var isAllSelected = false
//    private var uninstallQueue: MutableList<String> = mutableListOf()
//
//    private lateinit var txtSizeInfo: TextView
//    private lateinit var btnDelete: TextView
//    private lateinit var btnCancel: TextView
//    private lateinit var txtImageSize: TextView
//    private lateinit var imgBack: ImageView
//    private lateinit var rlBottomView: RelativeLayout
//
//    private val uninstallLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
//        if (uninstallQueue.isNotEmpty()) {
//            val uninstalledPackage = uninstallQueue.removeAt(0)
//            appsList.removeAll { it.packageName == uninstalledPackage }
//            adapter.updateData(appsList)
//            updateSelectedSize()
//
//            if (uninstallQueue.isNotEmpty()) {
//                launchUninstall(uninstallQueue.first())
//            }
//        }
//    }
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bulk_uninstaller)
//
//        val btnSelectAll = findViewById<ImageView>(R.id.txtDone)
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//        txtSizeInfo = findViewById(R.id.txtImageSize)
//        imgBack = findViewById(R.id.imgBack)
//        btnDelete = findViewById(R.id.btnDelete)
//        btnCancel = findViewById(R.id.btnCancel)
//        rlBottomView = findViewById(R.id.rlBottomView)
//
//        imgBack.setOnClickListener { onBackPressed() }
//
//        appsList = getInstalledAppsList().toMutableList()
//
//        adapter = BulkUninstallerAdapter(appsList) { selected ->
//
//            if (selected.isEmpty()){
//                rlBottomView.visibility = View.GONE
//            }else{
//                rlBottomView.visibility = View.VISIBLE
//            }
//            updateSelectedSize()
//        }
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//        btnSelectAll.setOnClickListener {
//            rlBottomView.visibility = View.VISIBLE
//            if (isAllSelected) adapter.clearSelection() else adapter.selectAll()
//            isAllSelected = !isAllSelected
//            updateSelectedSize()
//        }
//
//        btnCancel.setOnClickListener {
//            rlBottomView.visibility = View.GONE
//        }
//
//        btnDelete.setOnClickListener {
//            val selectedApps = adapter.getSelectedApps()
//            if (selectedApps.isEmpty()) {
//                Toast.makeText(this, "No apps selected", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            uninstallQueue.clear()
//            uninstallQueue.addAll(selectedApps.map { it.packageName })
//            launchUninstall(uninstallQueue.first())
//        }
//    }
//
//    private fun launchUninstall(packageName: String) {
//        val intent = Intent(Intent.ACTION_DELETE).apply {
//            data = Uri.parse("package:$packageName")
//        }
//        uninstallLauncher.launch(intent)
//    }
//
//
//    private fun getInstalledAppsList(): List<AppData> {
//        val pm = packageManager
//        val dateFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
//
//        return pm.getInstalledApplications(PackageManager.GET_META_DATA)
//            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
//            .mapNotNull { appInfo ->
//                try {
//                    val packageInfo = pm.getPackageInfo(appInfo.packageName, 0)
//                    val size = File(appInfo.sourceDir).length()
//                    AppData(
//                        id = appInfo.uid,
//                        icon = pm.getApplicationIcon(appInfo),
//                        name = pm.getApplicationLabel(appInfo).toString(),
//                        packageName = appInfo.packageName,
//                        versionName = packageInfo.versionName ?: "N/A",
//                        installDate = dateFormatter.format(Date(packageInfo.firstInstallTime)),
//                        size = size
//                    )
//                } catch (e: Exception) {
//                    null // if any app info can't be retrieved
//                }
//            }.sortedBy { it.name }
//    }
//
//
//    private fun updateSelectedSize() {
//        val totalBytes = adapter.getSelectedApps().sumOf { it.size }
//        val sizeInMB = totalBytes / (1024.0 * 1024.0)
//        txtSizeInfo.text = String.format("Selected Size: %.2f MB", sizeInMB)
//    }
//}

//class BulkUninstallerActivity : AppCompatActivity() {
//
//    private lateinit var adapter: BulkUninstallerAdapter
//    private lateinit var appsList: List<AppData>
//    private var isAllSelected = false
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_bulk_uninstaller)
//
//        val btnSelectAll = findViewById<ImageView>(R.id.txtDone)
//        val btnUninstall = findViewById<Button>(R.id.btnUninstall)
//        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
//
//        appsList = getInstalledAppsList()
//        adapter = BulkUninstallerAdapter(appsList) {
//            // Optional: handle selection change
//        }
//
//        recyclerView.layoutManager = LinearLayoutManager(this)
//        recyclerView.adapter = adapter
//
//        btnSelectAll.setOnClickListener {
//            if (isAllSelected) {
//                adapter.clearSelection()
//                Toast.makeText(this, "Unselected All", Toast.LENGTH_SHORT).show()
//            } else {
//                adapter.selectAll()
//                Toast.makeText(this, "Selected All", Toast.LENGTH_SHORT).show()
//            }
//            isAllSelected = !isAllSelected
//        }
//
//        btnUninstall.setOnClickListener {
//            val selectedPackages = adapter.getSelectedApps().map { it.packageName }
//            selectedPackages.forEach { packageName ->
//                val intent = Intent(Intent.ACTION_DELETE).apply {
//                    data = Uri.parse("package:$packageName")
//                }
//                startActivity(intent)
//            }
//        }
//    }
//
//    private fun getInstalledAppsList(): List<AppData> {
//        return packageManager.getInstalledApplications(PackageManager.GET_META_DATA)
//            .filter { it.flags and ApplicationInfo.FLAG_SYSTEM == 0 }
//            .map {
//                AppData(
//                    id = it.uid,
//                    icon = packageManager.getApplicationIcon(it),
//                    name = packageManager.getApplicationLabel(it).toString(),
//                    packageName = it.packageName
//                )
//            }.sortedBy { it.name }
//    }
//}
