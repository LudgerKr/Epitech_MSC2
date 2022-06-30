package com.example.pictsmanager.fragment

import android.app.AlertDialog
import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.PopupMenu
import android.widget.Toast
import com.example.pictsmanager.MainActivity
import com.example.pictsmanager.R
import com.example.pictsmanager.RequestService
import com.google.android.material.textview.MaterialTextView
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_gallery.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class GalleryFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_gallery, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestService::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        val noAlbum = arrayOf("You have no album")

        val listAlbums: HashMap<String, String> = HashMap()

        // get the layout for list view header view
        val header: MaterialTextView = layoutInflater.inflate(
            android.R.layout.simple_dropdown_item_1line,
            list_album,
            false
        ) as MaterialTextView

        header.text = getString(R.string.albums_header)
        header.setTypeface(header.typeface, Typeface.BOLD)
        header.setBackgroundColor(Color.LTGRAY)
        header.setTextColor(Color.BLACK)
        list_album.addHeaderView(
            header,
            null,
            false
        )

        btn_rename.setOnClickListener {
            CoroutineScope(Dispatchers.IO).launch {
                val params: HashMap<String?, RequestBody?> = HashMap()
                params["name"] = edit_rename.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                val response = service.renameAlbum(
                    sharedPreferences.getString("cookie", "").toString(),
                    sharedPreferences.getString("id", "").toString(),
                    listAlbums[edit_rename.text.toString()]!!,
                    params
                )

                withContext(Dispatchers.Main) {
                    if(response.isSuccessful) {
                        Toast.makeText(
                            requireContext(),
                            "Album successfully renamed",
                            Toast.LENGTH_LONG
                        ).show()
                        val mainAct : MainActivity = activity as MainActivity
                        mainAct.replaceFragment(GalleryFragment())
                    }
                    else {
                        Toast.makeText(
                            requireContext(),
                            "Error Occurred: ${response.message()}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }

        btn_cancel.setOnClickListener {
            edit_rename.setText("")
            rename_form.visibility = View.INVISIBLE
        }

        list_album.setOnItemClickListener { _, _, position, _ ->
            val mainAct : MainActivity = activity as MainActivity
            mainAct.replaceFragment(AlbumFragment(list_album.getItemAtPosition(position) as String))
        }

        list_album.setOnItemLongClickListener { _, _, position, _ ->

            rename_form.visibility = View.INVISIBLE

            val popup = PopupMenu(requireContext(), list_album.getChildAt(position))
            popup.inflate(R.menu.album_menu)

            popup.setOnMenuItemClickListener { item: MenuItem? ->

                when (item!!.itemId) {
                    R.id.rename -> {
                        edit_rename.setText(list_album.getItemAtPosition(position) as String)
                        rename_form.visibility = View.VISIBLE
                    }
                    R.id.delete -> {
                        val confirmRequest = AlertDialog.Builder(requireContext())
                        confirmRequest.setMessage("Do you want to delete the album " + list_album.getItemAtPosition(position) as String)
                            .setPositiveButton("Yes") { dialog, _ ->
                                CoroutineScope(Dispatchers.IO).launch {
                                    val response = service.deleteAlbum(
                                        sharedPreferences.getString("cookie", "").toString(),
                                        sharedPreferences.getString("id", "").toString(),
                                        listAlbums[list_album.getItemAtPosition(position)]!!
                                    )

                                    withContext(Dispatchers.Main) {
                                        if(response.isSuccessful) {
                                            Toast.makeText(
                                                requireContext(),
                                                "Album successfully deleted",
                                                Toast.LENGTH_LONG
                                            ).show()
                                            dialog.dismiss()
                                            val mainAct : MainActivity = activity as MainActivity
                                            mainAct.replaceFragment(GalleryFragment())
                                        }
                                        else {
                                            Toast.makeText(
                                                requireContext(),
                                                "Error Occurred: ${response.message()}",
                                                Toast.LENGTH_LONG
                                            ).show()
                                        }
                                    }
                                }
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                dialog.dismiss()
                            }
                        val alert = confirmRequest.create()
                        alert.show()
                    }
                    R.id.cancel -> {
                        popup.dismiss()
                    }
                }
                true
            }

            popup.show()

            true
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getAlbums(
                sharedPreferences.getString("cookie", "").toString(),
                sharedPreferences.getString("id", "").toString()
            )

            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    if (response.body() != null && response.body()!!.size() > 0) {
                        val albums = ArrayList<String>()
                        for (album : JsonElement in response.body()!!) {
                            albums.add(album.asJsonObject.get("name").asString)
                            listAlbums[album.asJsonObject.get("name").asString] = album.asJsonObject.get("id").asString
                        }
                        val albumAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            albums
                        )
                        list_album.adapter = albumAdapter
                    }
                    else {
                        val albumAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            noAlbum
                        )
                        list_album.adapter = albumAdapter
                    }
                }
                else {
                    Toast.makeText(
                        requireContext(),
                        "Error Occurred: ${response.message()}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }
}