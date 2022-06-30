package com.example.pictsmanager.fragment

import android.Manifest
import android.annotation.TargetApi
import android.app.DownloadManager
import android.content.Context
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.PorterDuff
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.pictsmanager.R
import com.example.pictsmanager.RequestService
import com.example.pictsmanager.data.PictsManagerPhoto
import com.example.pictsmanager.utils.ScalingUtilities
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_image.*
import kotlinx.android.synthetic.main.fragment_image.blueFilter
import kotlinx.android.synthetic.main.fragment_image.edit_btn
import kotlinx.android.synthetic.main.fragment_image.greenFilter
import kotlinx.android.synthetic.main.fragment_image.redFilter
import kotlinx.android.synthetic.main.fragment_image.rotateLeft
import kotlinx.android.synthetic.main.fragment_image.rotateRight
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream


class ImageFragment(private val pictsManagerPhoto: PictsManagerPhoto, private val albumName: String) : Fragment() {

    private var color = ""
    private var angle = 0f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_image, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        Picasso.get().load(pictsManagerPhoto.url).into(image)

        btn_edit.setOnClickListener {
            btn_save.visibility = View.INVISIBLE
            btn_edit.visibility = View.INVISIBLE
            edit_panel.visibility = View.VISIBLE
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            askPermissions()
        } else {
            downloadImage(pictsManagerPhoto.url!!)
        }

        btn_save.setOnClickListener {
            val width = if(edit_width.text.toString().isEmpty()) 500 else edit_width.text.toString().toInt()
            val height = if(edit_height.text.toString().isEmpty()) 500 else edit_height.text.toString().toInt()
            val quality = if(edit_quality.text.toString().isEmpty()) 70 else edit_quality.text.toString().toInt()
            editPicture(File(getString(R.string.path_img) + pictsManagerPhoto.url?.substring(pictsManagerPhoto.url.lastIndexOf("/"))),
                width,
                height,
                quality)
        }

        edit_btn.setOnClickListener {
            val width = if(edit_width.text.toString().isEmpty()) 500 else edit_width.text.toString().toInt()
            val height = if(edit_height.text.toString().isEmpty()) 500 else edit_height.text.toString().toInt()

            Picasso.get().load(pictsManagerPhoto.url).resize(width, height).rotate(angle).into(image)

            when (color) {
                "red" -> image.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN)
                "blue" -> image.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN)
                "green" -> image.setColorFilter(Color.GREEN, PorterDuff.Mode.DARKEN)
            }
            edit_panel.visibility = View.INVISIBLE
            btn_save.visibility = View.VISIBLE
            btn_edit.visibility = View.VISIBLE
        }

        reset_btn.setOnClickListener {
            image.setColorFilter(Color.TRANSPARENT, PorterDuff.Mode.LIGHTEN)
            Picasso.get().load(pictsManagerPhoto.url).rotate(0f).into(image)
            color = ""
            angle = 0f
            edit_panel.visibility = View.INVISIBLE
            btn_save.visibility = View.VISIBLE
            btn_edit.visibility = View.VISIBLE
        }

        rotateLeft.setOnClickListener {
            angle -= 90f
            Picasso.get().load(pictsManagerPhoto.url).rotate(angle).into(image)
        }

        rotateRight.setOnClickListener {
            angle += 90f
            Picasso.get().load(pictsManagerPhoto.url).rotate(angle).into(image)
        }

        redFilter.setOnClickListener {
            image.setColorFilter(Color.RED, PorterDuff.Mode.LIGHTEN)
            color = "red"
        }

        blueFilter.setOnClickListener {
            image.setColorFilter(Color.BLUE, PorterDuff.Mode.LIGHTEN)
            color = "blue"
        }

        greenFilter.setOnClickListener {
            image.setColorFilter(Color.GREEN, PorterDuff.Mode.DARKEN)
            color = "green"
        }
    }

    private fun editPicture(path: File, width: Int, height: Int, quality: Int): String? {
        val strMyImagePath: String?
        var scaledBitmap: Bitmap?
        val bitmap: Bitmap?

        val color = this.color
        val angle = this.angle

        try {

            val imgFile = File(path.absolutePath)
            strMyImagePath = imgFile.absolutePath

            val unscaledBitmap: Bitmap? = ScalingUtilities.decodeFile(strMyImagePath, width, height, ScalingUtilities.ScalingLogic.FIT)
            bitmap = ScalingUtilities.createScaledBitmap(unscaledBitmap, width, height, ScalingUtilities.ScalingLogic.FIT)

            scaledBitmap = if(angle != 0f) {
                ScalingUtilities.rotationBitmap(bitmap, angle)
            } else {
                bitmap
            }

            if(color.isNotEmpty()) {
                scaledBitmap = ScalingUtilities.filterBitmap(scaledBitmap, color)
            }

            val fos: FileOutputStream?
            try {
                fos = FileOutputStream(imgFile)
                if (imgFile.extension == "jpg" || imgFile.extension == "jpeg") {
                    scaledBitmap.compress(Bitmap.CompressFormat.JPEG, quality, fos)
                } else {
                    scaledBitmap.compress(Bitmap.CompressFormat.PNG, quality, fos)
                }
                fos.flush()
                fos.close()
                Log.d("Test", "Success")

                val retrofit = Retrofit.Builder()
                    .baseUrl("http://51.77.150.250:8080")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()

                val service = retrofit.create(RequestService::class.java)
                val sharedPreferences = requireContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

                val requestBody: RequestBody = imgFile.asRequestBody("*/*".toMediaTypeOrNull())

                val fileToUpload = MultipartBody.Part.createFormData("file", imgFile.name, requestBody)

                CoroutineScope(Dispatchers.IO).launch {
                    val response = service.uploadImage(
                        sharedPreferences.getString("cookie", "").toString(),
                        sharedPreferences.getString("id", "").toString(),
                        fileToUpload,
                        albumName.toRequestBody("text/plain".toMediaTypeOrNull())
                    )

                    withContext(Dispatchers.Main) {
                        if(!response.isSuccessful) {
                            Toast.makeText(
                                requireContext(),
                                "Error Occurred: ${response.message()}",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }

            } catch (e: FileNotFoundException) {

                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
            }

            scaledBitmap.recycle()

            if (strMyImagePath == null) {
                Log.d("test", "Erreur")
                return path.toString()
            }
            Log.d("test", "Success")
            return strMyImagePath

        } catch (e: Throwable) {
            e.printStackTrace()
            return null
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    fun askPermissions() {
        if (ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    requireActivity(),
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
                )
            ) {
                AlertDialog.Builder(requireContext())
                    .setTitle("Permission required")
                    .setMessage("Permission required to save photos from the Web.")
                    .setPositiveButton("Allow") { _, _ ->
                        ActivityCompat.requestPermissions(
                            requireActivity(),
                            arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                        )
                        requireActivity().finish()
                    }
                    .setNegativeButton("Deny") { dialog, _ -> dialog.cancel() }
                    .show()
            } else {
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE
                )
            }
        } else {
            downloadImage(pictsManagerPhoto.url!!)
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    downloadImage(pictsManagerPhoto.url!!)
                }
                return
            }
        }
    }

    private var msg: String? = ""
    private var lastMsg = ""

    private fun downloadImage(url: String) {
        val directory = File(Environment.DIRECTORY_PICTURES)

        if (!directory.exists()) {
            directory.mkdirs()
        }

        val downloadManager = requireActivity().getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

        val downloadUri = Uri.parse(url)

        val request = DownloadManager.Request(downloadUri).apply {
            setAllowedNetworkTypes(DownloadManager.Request.NETWORK_WIFI or DownloadManager.Request.NETWORK_MOBILE)
                .setAllowedOverRoaming(false)
                .setTitle(url.substring(url.lastIndexOf("/")))
                .setDescription("")
                .setDestinationInExternalPublicDir(
                    directory.toString(),
                    url.substring(url.lastIndexOf("/"))
                )
        }

        val downloadId = downloadManager.enqueue(request)
        val query = DownloadManager.Query().setFilterById(downloadId)
        Thread {
            var downloading = true
            while (downloading) {
                val cursor: Cursor = downloadManager.query(query)
                cursor.moveToFirst()
                if (cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS)) == DownloadManager.STATUS_SUCCESSFUL) {
                    downloading = false
                }
                val status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS))
                msg = statusMessage(url, directory, status)
                if (msg != lastMsg) {
                    requireActivity().runOnUiThread {
                        Toast.makeText(requireContext(), msg, Toast.LENGTH_SHORT).show()
                    }
                    lastMsg = msg ?: ""
                }
                cursor.close()
            }
        }.start()
    }

    private fun statusMessage(url: String, directory: File, status: Int): String {
        return when (status) {
            DownloadManager.STATUS_FAILED -> "Download has been failed, please try again"
            DownloadManager.STATUS_PAUSED -> "Paused"
            DownloadManager.STATUS_PENDING -> "Pending"
            DownloadManager.STATUS_RUNNING -> "Downloading..."
            DownloadManager.STATUS_SUCCESSFUL -> "Image downloaded successfully in $directory" + File.separator + url.substring(
                url.lastIndexOf("/") + 1
            )
            else -> "There's nothing to download"
        }
    }

    companion object {
        private const val MY_PERMISSIONS_REQUEST_WRITE_EXTERNAL_STORAGE = 1
    }
}