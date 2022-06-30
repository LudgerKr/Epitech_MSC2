package com.example.pictsmanager

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.http.*
import java.io.File


interface RequestService {

    @Multipart
    @POST("/api/login")
    suspend fun login(@PartMap map: HashMap<String?, RequestBody?>): Response<ResponseBody>

    @Multipart
    @POST("/api/users/signup")
    suspend fun register(@PartMap map: HashMap<String?, RequestBody?>): Response<ResponseBody>

    @Multipart
    @POST("/api/auth/users/{id}")
    suspend fun update(
        @Header("Cookie") cookie: String,
        @Path("id") id: String,
        @PartMap map: HashMap<String?, RequestBody?>
    ): Response<ResponseBody>

    @GET("/api/logout")
    suspend fun logout(
        @Header("Cookie") cookie: String,
    ): Response<ResponseBody>

    @GET("/api/auth/users/search")
    suspend fun searchFriends(
        @Header("Cookie") cookie: String,
        @Query("username") username: String
    ): Response<JsonArray>

    @GET("/api/auth/users/{id}")
    suspend fun getUserInfo(
        @Header("Cookie") cookie: String,
        @Path("id") id: String
    ): Response<JsonObject>

    @GET("/api/auth/friendRequest/accept/{id}")
    suspend fun getFriends(
        @Header("Cookie") cookie: String,
        @Path("id") id: String
    ): Response<JsonArray>

    @GET("/api/auth/friendRequest/inWaiting/{id}")
    suspend fun getFriendsRequest(
        @Header("Cookie") cookie: String,
        @Path("id") id: String
    ): Response<JsonArray>

    @Multipart
    @POST("/api/auth/friendRequest/send/{id}")
    suspend fun sendFriendRequest(
        @Header("Cookie") cookie: String,
        @Path("id") id: String,
        @PartMap map: HashMap<String?, RequestBody?>
    ): Response<ResponseBody>

    @PATCH("/api/auth/friendRequest/accept/{id}")
    suspend fun acceptRequest(
        @Header("Cookie") cookie: String,
        @Path("id") id: String
    ): Response<ResponseBody>

    @DELETE("api/auth/friendRequest/delete/{id}")

    suspend fun denyRequest(@Header("Cookie") cookie: String, @Path("id") id: String): Response<ResponseBody>

    @Multipart
    @POST("/api/auth/users/{id}/upload")
    suspend fun uploadImage(
        @Header("Cookie") cookie: String, @Path("id") id: String,
        @Part file: MultipartBody.Part, @Part("albumName") albumName: RequestBody
    ): Response<ResponseBody>

    @GET("/api/auth/users/{id}/albums")
    suspend fun getAlbums(@Header("Cookie") cookie: String, @Path("id") id: String): Response<JsonArray>

    @GET("/api/auth/albums/{album}/users/{id}/files/")
    suspend fun getPhotos(@Header("Cookie") cookie: String, @Path("album") album: String, @Path("id") id: String): Response<JsonArray>

    @DELETE("/api/auth/users/{id}/albums/{album}")
    suspend fun deleteAlbum(@Header("Cookie") cookie: String, @Path("id") id: String, @Path("album") album: String): Response<ResponseBody>

    @Multipart
    @PATCH("/api/auth/users/{id}/albums/{album}")
    suspend fun renameAlbum(@Header("Cookie") cookie: String, @Path("id") id: String, @Path("album") album: String, @PartMap map: HashMap<String?, RequestBody?>): Response<ResponseBody>
}