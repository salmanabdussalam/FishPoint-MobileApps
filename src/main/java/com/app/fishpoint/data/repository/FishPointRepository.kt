package com.app.fishpoint.data.repository


import com.app.fishpoint.data.api.*
import com.app.fishpoint.data.model.FishingSpot
import com.app.fishpoint.data.model.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.File

class FishPointRepository(private val api: FishPointApi) {

    suspend fun login(username: String, password: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.authenticate(AuthRequest(action = "login", username = username, password = password))
            if (response.status == "success" && response.data != null) Result.success(response.data)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun register(username: String, password: String, fullName: String): Result<User> = withContext(Dispatchers.IO) {
        try {
            val response = api.authenticate(AuthRequest(action = "register", username = username, password = password, full_name = fullName))
            if (response.status == "success" && response.data != null) Result.success(response.data)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getSpots(): Result<List<FishingSpot>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getSpots()
            if (response.status == "success" && response.data != null) Result.success(response.data)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addSpot(
        context: android.content.Context, userId: Int, name: String, waterType: String, latitude: Double, longitude: Double, targetFish: String, description: String, photos: List<String>
    ): Result<Int> = withContext(Dispatchers.IO) {
        try {
            val photoParts = mutableListOf<okhttp3.MultipartBody.Part>()
            for (uriString in photos) {
                try {
                    val uri = android.net.Uri.parse(uriString)
                    val inputStream = context.contentResolver.openInputStream(uri) ?: continue
                    val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}_${photoParts.size}.jpg")
                    val outputStream = tempFile.outputStream()
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    photoParts.add(okhttp3.MultipartBody.Part.createFormData("photos[]", tempFile.name, reqFile))
                } catch (e: Exception) { e.printStackTrace() }
            }

            if (photoParts.isNotEmpty()) {
                val textPlain = "text/plain".toMediaTypeOrNull()
                val response = api.manageSpotWithPhotos(
                    action = "create".toRequestBody(textPlain), id = null, userId = userId.toString().toRequestBody(textPlain), name = name.toRequestBody(textPlain), waterType = waterType.toRequestBody(textPlain), category = "".toRequestBody(textPlain), latitude = latitude.toString().toRequestBody(textPlain), longitude = longitude.toString().toRequestBody(textPlain), targetFish = targetFish.toRequestBody(textPlain), description = description.toRequestBody(textPlain), photos = photoParts
                )
                if (response.status == "success" && response.data != null) Result.success(response.data.id) else Result.failure(Exception(response.message))
            } else {
                val response = api.manageSpot(SpotRequest(action = "create", user_id = userId, name = name, water_type = waterType, latitude = latitude, longitude = longitude, target_fish = targetFish, description = description))
                if (response.status == "success" && response.data != null) Result.success(response.data.id) else Result.failure(Exception(response.message))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun updateSpot(
        context: android.content.Context, spotId: Int, userId: Int, name: String, waterType: String, latitude: Double, longitude: Double, targetFish: String, description: String, photos: List<String>
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val photoParts = mutableListOf<okhttp3.MultipartBody.Part>()
            for (uriString in photos) {
                // Jangan upload foto yang sudah punya remoteUrl (bukan URI lokal)
                if (uriString.startsWith("http")) continue

                try {
                    val uri = android.net.Uri.parse(uriString)
                    val inputStream = context.contentResolver.openInputStream(uri) ?: continue
                    val tempFile = File(context.cacheDir, "upload_${System.currentTimeMillis()}_${photoParts.size}.jpg")
                    val outputStream = tempFile.outputStream()
                    inputStream.copyTo(outputStream)
                    inputStream.close()
                    outputStream.close()

                    val reqFile = tempFile.asRequestBody("image/*".toMediaTypeOrNull())
                    photoParts.add(okhttp3.MultipartBody.Part.createFormData("photos[]", tempFile.name, reqFile))
                } catch (e: Exception) { e.printStackTrace() }
            }

            if (photoParts.isNotEmpty()) {
                val textPlain = "text/plain".toMediaTypeOrNull()
                val response = api.manageSpotWithPhotos(
                    action = "update".toRequestBody(textPlain), id = spotId.toString().toRequestBody(textPlain), userId = userId.toString().toRequestBody(textPlain), name = name.toRequestBody(textPlain), waterType = waterType.toRequestBody(textPlain), category = "".toRequestBody(textPlain), latitude = latitude.toString().toRequestBody(textPlain), longitude = longitude.toString().toRequestBody(textPlain), targetFish = targetFish.toRequestBody(textPlain), description = description.toRequestBody(textPlain), photos = photoParts
                )
                if (response.status == "success") Result.success(Unit) else Result.failure(Exception(response.message))
            } else {
                val response = api.manageSpot(SpotRequest(action = "update", id = spotId, user_id = userId, name = name, water_type = waterType, latitude = latitude, longitude = longitude, target_fish = targetFish, description = description))
                if (response.status == "success") Result.success(Unit) else Result.failure(Exception(response.message))
            }
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun addReview(spotId: Int, userId: Int, rating: Int, comment: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.addReview(ReviewRequest(spot_id = spotId, user_id = userId, rating = rating, comment = comment))
            if (response.status == "success") Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteSpot(spotId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // Kita butuh param dummy (userId=0, dll) karena model SpotRequest me-require-nya, tapi backend cuma cek id
            val response = api.manageSpot(SpotRequest(action = "delete", id = spotId, user_id = 0, name = "", water_type = "", latitude = 0.0, longitude = 0.0))
            if (response.status == "success") Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun deleteReview(reviewId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.addReview(ReviewRequest(action = "delete", id = reviewId))
            if (response.status == "success") Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun getUsers(): Result<List<User>> = withContext(Dispatchers.IO) {
        try {
            val response = api.getUsers()
            if (response.status == "success" && response.data != null) Result.success(response.data)
            else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }

    suspend fun manageUser(action: String, userId: Int): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            val response = api.manageUser(UserManageRequest(action = action, id = userId))
            if (response.status == "success") Result.success(Unit) else Result.failure(Exception(response.message))
        } catch (e: Exception) { Result.failure(e) }
    }
}