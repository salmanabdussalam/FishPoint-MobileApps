package com.app.fishpoint.data.model

import com.google.gson.annotations.SerializedName

enum class UserRole {
    @SerializedName("member") MEMBER,
    @SerializedName("admin") ADMIN
}

data class SpotPhoto(
    val id: Int,
    @SerializedName("sort_order") val sortOrder: Int,
    @SerializedName("file_path") val remoteUrl: String? = null,
    val localUri: String? = null,
)

data class Review(
    val id: Int,
    val username: String? = null,
    val rating: Int,
    @SerializedName("comment") val text: String? = null,
) {
    init {
        require(rating in 1..5) { "Rating harus antara 1 dan 5, didapat: $rating" }
    }
    val avatarInitial: String
        get() = username?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}

data class FishingSpot(
    val id: Int,
    @SerializedName("owner_username") val ownerUsername: String? = null,
    val name: String,
    @SerializedName("water_type") val waterType: String? = null,
    val category: String? = null,
    val latitude: Double,
    val longitude: Double,
    @SerializedName("target_fish") val targetFish: String? = null,
    val description: String? = null,
    val photos: List<SpotPhoto> = emptyList(),
    val reviews: List<Review> = emptyList(),
) {
    val averageRating: Double?
        get() = if (reviews.isEmpty()) null else reviews.map { it.rating }.average()

    val reviewCount: Int get() = reviews.size

    val coverPhotoUrl: String?
        get() = photos.minByOrNull { it.sortOrder }?.let { it.remoteUrl ?: it.localUri }

    fun hasReviewFrom(username: String): Boolean = reviews.any { it.username == username }
    fun isOwnedBy(username: String): Boolean = ownerUsername == username
}

data class User(
    val id: Int = 0,
    val username: String,
    val password: String? = null,
    @SerializedName("full_name") val fullName: String? = null,
    @SerializedName("created_at") val memberSince: String? = null,
    val role: UserRole = UserRole.MEMBER,
    @SerializedName("is_banned") val isBanned: Boolean = false,
) {
    val avatarInitial: String get() = fullName?.firstOrNull()?.uppercaseChar()?.toString() ?: "?"
}