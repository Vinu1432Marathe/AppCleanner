package com.software.app.update.smart.Activity

import android.Manifest
import android.app.RecoverableSecurityException
import android.content.ContentUris
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.view.isVisible
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.addsdemo.mysdk.ADPrefrences.MyApp
import com.addsdemo.mysdk.ADPrefrences.NativeAds_Class
import com.software.app.update.smart.Adapter.DuplicateSetAdapter
import com.software.app.update.smart.Model.ImageInfo
import com.software.app.update.smart.R

class DublicateActivity : BaseActivity() {

    private lateinit var btnSelectAll: ImageView
    private lateinit var imgBack: ImageView
    private lateinit var btnCancel: TextView
    private lateinit var txtHeader: TextView

    private lateinit var container: LinearLayout
    private lateinit var rlBottomView: RelativeLayout
    private lateinit var recyclerView: RecyclerView
    private lateinit var txtDelete: TextView
    private lateinit var txtSize: TextView
    private lateinit var txtImageSize: TextView
    private lateinit var adapter: DuplicateSetAdapter
    private var deletePendingUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dublicate)

        recyclerView = findViewById(R.id.recyclerView)
        txtDelete = findViewById(R.id.btnDelete)

        rlBottomView = findViewById(R.id.rlBottomView)
        btnSelectAll = findViewById(R.id.txtDone)
        imgBack = findViewById(R.id.imgBack)
        btnCancel = findViewById(R.id.btnCancel)
        txtHeader = findViewById(R.id.txtHeader)
        txtImageSize = findViewById(R.id.txtImageSize)

        recyclerView.layoutManager = GridLayoutManager(this, 3)

        imgBack.setOnClickListener { onBackPressed() }

        checkPermissionAndLoad()

        container = findViewById(R.id.container)
        container.isVisible =
            MyApp.ad_preferences.getRemoteConfig()?.isAdShow == true

        val llline_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llline_full)
        val llnative_full = findViewById<LinearLayout>(com.addsdemo.mysdk.R.id.llnative_full)
        NativeAds_Class.Fix_NativeFull_Show(this, llnative_full, llline_full, "medium")



        btnSelectAll.setOnClickListener {

            if (::adapter.isInitialized) {
                if (adapter.isAllSelected()) {
                    adapter.clearSelection()
                    rlBottomView.visibility = View.GONE
//                    btnSelectAll.text = "Select All"
                } else {
                    adapter.selectAll()
                    rlBottomView.visibility = View.VISIBLE
//                    btnSelectAll.text = "Deselect All"
                }
            }
        }
        btnCancel.setOnClickListener {
            rlBottomView.visibility = View.GONE
        }

        txtDelete.setOnClickListener {
            val selected = adapter.getSelectedImages()
            if (selected.isEmpty()) {
                Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            selected.forEach {
                deleteImageByPath(it.path)
            }
        }
    }

    private fun checkPermissionAndLoad() {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)

        ActivityCompat.requestPermissions(this, permissions, 101)
    }

    private fun getAllImages(): List<ImageInfo> {
        val images = mutableListOf<ImageInfo>()
        val projection = arrayOf(MediaStore.Images.Media.DATA, MediaStore.Images.Media.SIZE)

        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection, null, null, null
        )

        cursor?.use {
            val pathIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)

            while (it.moveToNext()) {
                val path = it.getString(pathIndex)
                val size = it.getLong(sizeIndex)
                images.add(ImageInfo(path, size))
            }
        }

        return images
    }

    private fun groupDuplicatesBySize(images: List<ImageInfo>): List<List<ImageInfo>> {
        return images.groupBy { it.size }
            .filter { it.value.size > 1 }
            .values.toList()
    }

    private fun loadImages() {
        val images = getAllImages()
        val duplicates = groupDuplicatesBySize(images)

        adapter = DuplicateSetAdapter(duplicates) { selected ->
            val totalSize = selected.sumOf { it.size } / (1024.0 * 1024.0)
            txtImageSize.text = getString(R.string.free_up_space_2f_mb).format(totalSize)
            if (selected.isEmpty()){
                rlBottomView.visibility = View.GONE
            }else{
                rlBottomView.visibility = View.VISIBLE
            }
        }

        recyclerView.adapter = adapter
    }

    private fun deleteImageByPath(path: String) {
        val cursor = contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA),
            null, null, null
        )

        cursor?.use {
            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            rlBottomView.visibility = View.GONE
            while (it.moveToNext()) {
                if (it.getString(dataIndex) == path) {
                    val id = it.getLong(idIndex)
                    val uri =
                        ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                    try {
                        contentResolver.delete(uri, null, null)
                    } catch (e: SecurityException) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
                            deletePendingUri = uri
                            startIntentSenderForResult(
                                e.userAction.actionIntent.intentSender,
                                999, null, 0, 0, 0
                            )
                        }
                    }
                    break
                }
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 999 && resultCode == RESULT_OK && deletePendingUri != null) {
            contentResolver.delete(deletePendingUri!!, null, null)
            deletePendingUri = null
            loadImages()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        results: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, results)
        if (results.all { it == PackageManager.PERMISSION_GRANTED }) {
            loadImages()
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
        }
    }
}


//
//class DublicateActivity : BaseActivity() {
//
//    private lateinit var rlBottomView: RelativeLayout
//    private lateinit var recyclerView: RecyclerView
//    private lateinit var btnSelectAll: ImageView
//    private lateinit var imgBack: ImageView
//    private lateinit var txtDelete: TextView
//    private lateinit var btnCancel: TextView
//    private lateinit var txtHeader: TextView
//    private lateinit var adapter: DuplicateSetAdapter
//    private var deletePendingUri: Uri? = null
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_dublicate)
//
//        rlBottomView = findViewById(R.id.rlBottomView)
//        imgBack = findViewById(R.id.imgBack)
//        recyclerView = findViewById(R.id.recyclerView)
//        btnSelectAll = findViewById(R.id.txtDone)
//        txtDelete = findViewById(R.id.btnDelete)
//        btnCancel = findViewById(R.id.btnCancel)
//        txtHeader = findViewById(R.id.txtHeader)
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
//
//        checkPermissionAndLoad()
//
//        imgBack.setOnClickListener { onBackPressed() }
//
//
//        btnCancel.setOnClickListener {
//            adapter.clearSelection()
//            rlBottomView.visibility = View.GONE
//        }
//
//
//        btnSelectAll.setOnClickListener {
//
//            if (::adapter.isInitialized) {
//                if (adapter.isAllSelected()) {
//                    adapter.clearSelection()
//                    rlBottomView.visibility = View.GONE
////                    btnSelectAll.text = "Select All"
//                } else {
//                    adapter.selectAll()
//                    rlBottomView.visibility = View.VISIBLE
////                    btnSelectAll.text = "Deselect All"
//                }
//            }
//        }
//
//
//        txtDelete.setOnClickListener {
////            deleteSelectedImages()
//            val selected = adapter.getSelectedImages()
//            if (selected.isEmpty()) {
//                Toast.makeText(this, "No images selected", Toast.LENGTH_SHORT).show()
//                return@setOnClickListener
//            }
//
//            selected.forEach { path ->
//                Log.e("ImagePath", "Image :: $path")
//                deleteImageByPath(path.toString())
//            }
////            showDeleteDialog(selected)
//        }
//
//    }
//
//    private fun deleteSelectedImages() {
//        val selected = adapter.getSelectedImages()
//
//        Log.e("CheckkkImG","IMG : $selected")
//
//        for (image in selected) {
//            val file = File(image.path)
//            if (file.exists()) {
//                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
//                    val uri = MediaStore.Images.Media.getContentUri("external")
//                    val selection = MediaStore.Images.Media.DATA + "=?"
//                    val selectionArgs = arrayOf(image.path)
//                    contentResolver.delete(uri, selection, selectionArgs)
//                } else {
//                    file.delete()
//                }
//            }
//        }
//        Toast.makeText(this, "${selected.size} images deleted", Toast.LENGTH_SHORT).show()
//        recreate() // Reload
//    }
//
//    private fun checkPermissionAndLoad() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 101)
//        } else {
//            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
//        }
//    }
//
//    private fun getAllImages(): List<ImageInfo> {
//        val images = mutableListOf<ImageInfo>()
//        val projection = arrayOf(
//            MediaStore.Images.Media.DATA,
//            MediaStore.Images.Media.SIZE // Include size column
//        )
//
//        val cursor = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            null, null, null
//        )
//
//        cursor?.use {
//            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            val sizeIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.SIZE)
//
//            while (it.moveToNext()) {
//                val path = it.getString(dataIndex)
//                val size = it.getLong(sizeIndex)
//                images.add(ImageInfo(path = path, size = size))
//            }
//        }
//
//        return images
//    }
//    private fun updateSelectedSize(selectedImages: List<ImageInfo>) {
//        val totalSizeBytes = selectedImages.sumOf { it.size }
//        val totalSizeMB = totalSizeBytes / (1024.0 * 1024.0)
//        txtHeader.text = "Selected Size: %.2f MB".format(totalSizeMB)
//    }
//
//
//    private fun groupDuplicatesByFileSize(images: List<ImageInfo>): List<List<ImageInfo>> {
//        return images.groupBy { it.size }
//            .filter { it.value.size > 1 } // Only groups with more than 1 image
//            .values.toList()
//    }
//
//    private fun loadImages() {
//        val images = getAllImages()
//        val duplicateSets = groupDuplicatesByFileSize(images)
//
//        adapter = DuplicateSetAdapter(duplicateSets) { check ->
//            rlBottomView.visibility = if (check) View.VISIBLE else View.GONE
//        }
//
//        recyclerView.adapter = adapter
//    }
//
//
//    private fun showDeleteDialog(images: List<String>) {
//        val totalSizeMB = images.sumOf { File(it).length() } / (1024 * 1024)
//        AlertDialog.Builder(this)
//            .setTitle("Delete selected images?")
//            .setMessage("Free Up Space: $totalSizeMB MB")
//            .setPositiveButton("Delete") { _, _ ->
//                images.forEach { deleteImageByPath(it) }
//            }
//            .setNegativeButton("Cancel", null)
//            .show()
//    }
//
//
//    private fun deleteImageByPath(path: String) {
//
//        Log.e("ImagePath", "Image11 :: $path")
//        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection, null, null, null
//        )
//        cursor?.use {
//            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            while (it.moveToNext()) {
//                if (it.getString(dataIndex) == path) {
//                    val id = it.getLong(idIndex)
//                    val uri = ContentUris.withAppendedId(
//                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id
//                    )
//                    try {
//                        contentResolver.delete(uri, null, null)
//                    } catch (e: SecurityException) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q &&
//                            e is RecoverableSecurityException
//                        ) {
//                            deletePendingUri = uri
//                            startIntentSenderForResult(
//                                e.userAction.actionIntent.intentSender,
//                                999, null, 0, 0, 0
//                            )
//                        }
//                    }
//                    break
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        if (requestCode == 999 && resultCode == RESULT_OK && deletePendingUri != null) {
//            contentResolver.delete(deletePendingUri!!, null, null)
//            deletePendingUri = null
//            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
//            loadImages()
//        }
//        super.onActivityResult(requestCode, resultCode, data)
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        results: IntArray
//    ) {
//        if (results.all { it == PackageManager.PERMISSION_GRANTED }) {
//            loadImages()
//        } else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//}


//
//class DublicateActivity : BaseActivity() {
//    private lateinit var recyclerView: RecyclerView
//
//    lateinit var adapter: DuplicateSetAdapter
//    lateinit var txtDone: ImageView
//    private var deletePendingUri: Uri? = null
//
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        setContentView(R.layout.activity_dublicate)
//
//        recyclerView = findViewById(R.id.recyclerView)
//        txtDone = findViewById(R.id.txtDone)
//        recyclerView.layoutManager = GridLayoutManager(this, 3)
//
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            requestPermissions(arrayOf(Manifest.permission.READ_MEDIA_IMAGES), 101)
//        } else {
//            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 101)
//        }
//
//        txtDone.setOnClickListener {
//            val selected = adapter.getSelectedImages()
//            selected.forEach { path ->
//                deleteImageByPath(path)
//            }
//        }
//    }
//
//    private fun loadImages() {
//        val images = getAllImages()
//        val duplicateSets = groupDuplicatesByFileSize(images)
//        adapter = DuplicateSetAdapter(duplicateSets)
//        recyclerView.adapter = adapter
//    }
//
//    private fun getAllImages(): List<String> {
//        val images = mutableListOf<String>()
//        val projection = arrayOf(MediaStore.Images.Media.DATA)
//        val cursor = contentResolver.query(
//            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//            projection,
//            null,
//            null,
//            null
//        )
//        cursor?.use {
//            val index = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            while (it.moveToNext()) {
//                images.add(it.getString(index))
//            }
//        }
//        return images
//    }
//
//    private fun groupDuplicatesByFileSize(paths: List<String>): List<List<String>> {
//        return paths.groupBy { File(it).length() }.filter { it.value.size > 1 }.values.toList()
//    }
//
//    private fun deleteImageByPath(path: String) {
//        val resolver = contentResolver
//        val uriExternal = MediaStore.Images.Media.EXTERNAL_CONTENT_URI
//        val projection = arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DATA)
//
//        val cursor = resolver.query(uriExternal, projection, null, null, null)
//        cursor?.use {
//            val dataIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
//            val idIndex = it.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
//            while (it.moveToNext()) {
//                val filePath = it.getString(dataIndex)
//                if (filePath == path) {
//                    val id = it.getLong(idIndex)
//                    val uri = ContentUris.withAppendedId(uriExternal, id)
//                    try {
//                        resolver.delete(uri, null, null)
//                    } catch (e: SecurityException) {
//                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && e is RecoverableSecurityException) {
//                            deletePendingUri = uri
//                            val intentSender = e.userAction.actionIntent.intentSender
//                            startIntentSenderForResult(intentSender, 999, null, 0, 0, 0)
//                        }
//                    }
//                    break
//                }
//            }
//        }
//    }
//
//    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
//        super.onActivityResult(requestCode, resultCode, data)
//        if (requestCode == 999 && resultCode == RESULT_OK) {
//            deletePendingUri?.let { contentResolver.delete(it, null, null) }
//            deletePendingUri = null
//            Toast.makeText(this, "Image deleted", Toast.LENGTH_SHORT).show()
//            loadImages()
//        }
//    }
//
//    override fun onRequestPermissionsResult(
//        requestCode: Int,
//        permissions: Array<out String>,
//        grantResults: IntArray
//    ) {
//        if (grantResults.all { it == PackageManager.PERMISSION_GRANTED }) {
//            loadImages()
//        } else {
//            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
//        }
//    }
//
//
//}