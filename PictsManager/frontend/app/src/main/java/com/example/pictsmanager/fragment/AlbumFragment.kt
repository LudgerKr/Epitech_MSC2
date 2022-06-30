package com.example.pictsmanager.fragment

import android.content.Context
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.pictsmanager.MainActivity
import com.example.pictsmanager.R
import com.example.pictsmanager.RequestService
import com.example.pictsmanager.data.PictsManagerPhoto
import com.google.gson.JsonElement
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_album.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AlbumFragment(private val albumName: String) : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_album, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val layoutManager = GridLayoutManager(requireContext(), 2)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestService::class.java)

        val sharedPreferences = requireContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getPhotos(
                sharedPreferences.getString("cookie", "").toString(),
                albumName,
                sharedPreferences.getString("id", "").toString()
            )

            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    if (response.body() != null && response.body()!!.size() > 0) {
                        val photosUrl = ArrayList<String>()
                        for (photo : JsonElement in response.body()!!) {
                            photosUrl.add(photo.asJsonObject.get("path").asString)
                        }
                        val photos = ArrayList<PictsManagerPhoto>()
                        for (url : String in photosUrl) {
                            photos.add(PictsManagerPhoto(url))
                        }
                        list_image.setHasFixedSize(true)
                        list_image.layoutManager = layoutManager
                        list_image.adapter = ImageGalleryAdapter(requireContext(), photos)
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

    private inner class ImageGalleryAdapter(val context: Context, val pictsManagerPhoto: ArrayList<PictsManagerPhoto>)
        : RecyclerView.Adapter<ImageGalleryAdapter.MyViewHolder>() {

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageGalleryAdapter.MyViewHolder {
            val context = parent.context
            val inflater = LayoutInflater.from(context)
            val photoView = inflater.inflate(R.layout.item_image, parent, false)
            return MyViewHolder(photoView)
        }

        override fun onBindViewHolder(holder: ImageGalleryAdapter.MyViewHolder, position: Int) {
            val pictsManagerPhoto = pictsManagerPhoto[position]
            val imageView = holder.photoImageView

            Picasso.get()
                .load(pictsManagerPhoto.url)
                .placeholder(R.drawable.ic_placeholder)
                .error(R.drawable.ic_error)
                .fit()
                .tag(context)
                .into(imageView)

        }

        override fun getItemCount(): Int {
            return pictsManagerPhoto.size
        }

        inner class MyViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView), View.OnClickListener {

            var photoImageView: ImageView = itemView.findViewById(R.id.image_photo)

            init {
                itemView.setOnClickListener(this)
            }

            override fun onClick(view: View) {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val pictsManagerPhoto = pictsManagerPhoto[position]
                    val mainAct : MainActivity = activity as MainActivity
                    mainAct.replaceFragment(ImageFragment(pictsManagerPhoto, albumName))
                }
            }
        }
    }
}