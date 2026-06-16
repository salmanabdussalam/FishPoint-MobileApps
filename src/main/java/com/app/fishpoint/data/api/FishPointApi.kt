package com.app.fishpoint.data.api

import com.app.fishpoint.data.model.FishingSpot
import com.app.fishpoint.data.model.User
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Multipart
import retrofit2.http.Part

interface FishPointApi {
    @POST("api/auth.php")
    suspend fun authenticate(@Body request: AuthRequest): ApiResponse<User>

    @GET("api/spots.php")
    suspend fun getSpots(): ApiResponse<List<FishingSpot>>

    @POST("api/spots.php")
    suspend fun manageSpot(@Body request: SpotRequest): ApiResponse<SpotIdResponse>

    @POST("api/reviews.php")
    suspend fun addReview(@Body request: ReviewRequest): ApiResponse<Any>

    @Multipart
    @POST("api/spots.php")
    suspend fun manageSpotWithPhotos(
        @Part("action") action: okhttp3.RequestBody,
        @Part("id") id: okhttp3.RequestBody?,
        @Part("user_id") userId: okhttp3.RequestBody,
        @Part("name") name: okhttp3.RequestBody,
        @Part("water_type") waterType: okhttp3.RequestBody,
        @Part("category") category: okhttp3.RequestBody,
        @Part("latitude") latitude: okhttp3.RequestBody,
        @Part("longitude") longitude: okhttp3.RequestBody,
        @Part("target_fish") targetFish: okhttp3.RequestBody,
        @Part("description") description: okhttp3.RequestBody,
        @Part photos: List<okhttp3.MultipartBody.Part>
    ): ApiResponse<SpotIdResponse>

    @GET("api/users.php")
    suspend fun getUsers(): ApiResponse<List<com.app.fishpoint.data.model.User>>

    @POST("api/users.php")
    suspend fun manageUser(@Body request: UserManageRequest): ApiResponse<Unit>

    companion object {
        // Gunakan 10.0.2.2 untuk mengakses localhost (Laragon) dari dalam Android Emulator.
        // Jika menggunakan HP asli (fisik), ganti dengan IP lokal laptop kamu (contoh: http://192.168.x.x/backend_api/)
        private const val BASE_URL = "http://172.24.184.224/backend_api/"

        fun create(): FishPointApi {
            val client = OkHttpClient.Builder().build()
            
            val gson = com.google.gson.GsonBuilder()
                .registerTypeAdapter(Boolean::class.java, com.google.gson.JsonDeserializer { json, _, _ ->
                    if (json.isJsonPrimitive) {
                        val primitive = json.asJsonPrimitive
                        if (primitive.isBoolean) return@JsonDeserializer primitive.asBoolean
                        if (primitive.isNumber) return@JsonDeserializer primitive.asInt != 0
                    }
                    false
                })
                .registerTypeAdapter(Boolean::class.javaObjectType, com.google.gson.JsonDeserializer { json, _, _ ->
                    if (json.isJsonPrimitive) {
                        val primitive = json.asJsonPrimitive
                        if (primitive.isBoolean) return@JsonDeserializer primitive.asBoolean
                        if (primitive.isNumber) return@JsonDeserializer primitive.asInt != 0
                    }
                    false
                })
                .create()

            return Retrofit.Builder().baseUrl(BASE_URL).client(client)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build().create(FishPointApi::class.java)
        }
    }
}