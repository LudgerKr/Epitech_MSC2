package com.example.pictsmanager.fragment

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.view.*
import android.widget.*
import androidx.fragment.app.Fragment
import com.example.pictsmanager.R
import com.example.pictsmanager.RequestService
import com.google.gson.JsonElement
import kotlinx.android.synthetic.main.fragment_social.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class SocialFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_social, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sharedPreferences = requireContext().getSharedPreferences("AUTHENTICATION", Context.MODE_PRIVATE)

        val retrofit = Retrofit.Builder()
            .baseUrl("http://51.77.150.250:8080")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        val service = retrofit.create(RequestService::class.java)

        val noFriend = arrayOf("You have no friend")
        val searchHint = arrayOf(getText(R.string.search_friend_hint))
        val noResult = arrayOf(getText(R.string.no_result))
        val searchProgress = arrayOf(getText(R.string.search_progress))

        var listUser: HashMap<String, String> = HashMap()
        var listRequest: HashMap<String, String> = HashMap()

        if(sharedPreferences.getInt("friendRequest", 0) > 0) {
            fab_friend_request.visibility = View.VISIBLE

            CoroutineScope(Dispatchers.IO).launch {
                val response = service.getFriendsRequest(
                    sharedPreferences.getString("cookie", "").toString(),
                    sharedPreferences.getString("id", "").toString()
                )

                withContext(Dispatchers.Main) {
                    if(response.isSuccessful) {
                        if (response.body() != null && response.body()!!.size() > 0) {
                            listRequest = HashMap()
                            val users = ArrayList<String>()
                            for (request : JsonElement in response.body()!!) {
                                listRequest[request.asJsonObject.get("userId").asJsonObject.get("username").asString] = request.asJsonObject.get("id").asString
                                users.add(request.asJsonObject.get("userId").asJsonObject.get("username").asString)
                            }
                            val requestAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_list_item_1,
                                users
                            )
                            request_list.adapter = requestAdapter
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

        result_search.setOnItemClickListener { _, _, position, _ ->
            val confirmRequest = AlertDialog.Builder(requireContext())
            confirmRequest.setMessage("Do you want to send a friend request to " + result_search.getItemAtPosition(position))
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val params: HashMap<String?, RequestBody?> = HashMap()

                        params["friendId"] = listUser[result_search.getItemAtPosition(position)]?.toRequestBody("text/plain".toMediaTypeOrNull())

                        val response = service.sendFriendRequest(
                            sharedPreferences.getString("cookie", "").toString(),
                            sharedPreferences.getString("id", "").toString(),
                            params
                        )

                        withContext(Dispatchers.Main) {
                            if(response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Request sent successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
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

        request_list.setOnItemClickListener { _, _, position, _ ->
            val confirmRequest = AlertDialog.Builder(requireContext())
            confirmRequest.setMessage("Do you want to accept the request of " + request_list.getItemAtPosition(position))
                .setCancelable(true)
                .setPositiveButton("Yes") { dialog, _ ->
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = service.acceptRequest(
                            sharedPreferences.getString("cookie", "").toString(),
                            listRequest[request_list.getItemAtPosition(position)]!!
                        )

                        withContext(Dispatchers.Main) {
                            if(response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Request accepted successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
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
                    CoroutineScope(Dispatchers.IO).launch {
                        val response = service.denyRequest(
                            sharedPreferences.getString("cookie", "").toString(),
                            listRequest[request_list.getItemAtPosition(position)]!!
                        )

                        withContext(Dispatchers.Main) {
                            if(response.isSuccessful) {
                                Toast.makeText(
                                    requireContext(),
                                    "Request denied",
                                    Toast.LENGTH_LONG
                                ).show()
                                dialog.dismiss()
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
            val alert = confirmRequest.create()
            alert.show()
        }

        CoroutineScope(Dispatchers.IO).launch {
            val response = service.getFriends(
                sharedPreferences.getString("cookie", "").toString(),
                sharedPreferences.getString("id", "").toString()
            )

            withContext(Dispatchers.Main) {
                if(response.isSuccessful) {
                    if (response.body() != null && response.body()!!.size() > 0) {
                        val friends = ArrayList<String>()
                        for (user : JsonElement in response.body()!!) {
                            friends.add(user.asJsonObject.get("friendId").asJsonObject.get("username").asString)
                        }
                        val friendAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            friends
                        )
                        friend_listView.adapter = friendAdapter
                    }
                    else {
                        val friendAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            noFriend
                        )
                        friend_listView.adapter = friendAdapter
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

        fab_search_friend.setOnClickListener {
            friend_searchView.isIconified = false
            friend_searchView.visibility = View.VISIBLE
            result_search.visibility = View.VISIBLE
            fab_search_friend.visibility = View.INVISIBLE
        }

        fab_friend_request.setOnClickListener {
            request_inwaiting.visibility = View.VISIBLE
            fab_search_friend.visibility = View.INVISIBLE
            fab_friend_request.visibility = View.INVISIBLE
        }

        btn_cancel.setOnClickListener {
            request_inwaiting.visibility = View.INVISIBLE
            fab_search_friend.visibility = View.VISIBLE
            if(sharedPreferences.getInt("friendRequest", 0) > 0) {
                fab_friend_request.visibility = View.VISIBLE
            }
        }

        friend_searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {

            override fun onQueryTextSubmit(query: String?): Boolean {
                TODO("Not yet implemented")
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText != null) {
                    if(newText.length < 2) {
                        val resultAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            searchHint
                        )
                        result_search.adapter = resultAdapter
                    } else {
                        val resultAdapter = ArrayAdapter(
                            requireContext(),
                            android.R.layout.simple_list_item_1,
                            searchProgress
                        )
                        result_search.adapter = resultAdapter
                        CoroutineScope(Dispatchers.IO).launch {
                            val response = service.searchFriends(
                                sharedPreferences.getString("cookie", "").toString(),
                                newText
                            )

                            withContext(Dispatchers.Main) {
                                if (response.isSuccessful) {
                                    if (response.body() != null && response.body()!!.size() > 0) {
                                        val users = ArrayList<String>()
                                        listUser = HashMap()
                                        for (user : JsonElement in response.body()!!) {
                                            listUser[user.asJsonObject.get("username").asString] = user.asJsonObject.get("id").asString
                                            users.add(user.asJsonObject.get("username").asString)
                                        }
                                        val resultAdapter = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_list_item_1,
                                            users
                                        )
                                        result_search.adapter = resultAdapter
                                    } else {
                                        val resultAdapter = ArrayAdapter(
                                            requireContext(),
                                            android.R.layout.simple_list_item_1,
                                            noResult
                                        )
                                        result_search.adapter = resultAdapter
                                    }
                                } else {
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
                return false
            }
        })

        friend_searchView.setOnCloseListener {
            friend_searchView.visibility = View.INVISIBLE
            result_search.visibility = View.INVISIBLE
            fab_search_friend.visibility = View.VISIBLE
            false
        }
    }
}