package com.app.fishpoint.data.api


data class ApiResponse<T>(val status: String, val message: String, val data: T? = null)
data class AuthRequest(val action: String, val username: String, val password: String, val full_name: String = "")
data class SpotRequest(val action: String, val id: Int = 0, val user_id: Int, val name: String, val water_type: String, val category: String = "", val latitude: Double, val longitude: Double, val target_fish: String = "", val description: String = "")
data class SpotIdResponse(val id: Int)
data class ReviewRequest(val action: String = "create", val id: Int = 0, val spot_id: Int = 0, val user_id: Int = 0, val rating: Int = 0, val comment: String = "")
data class UserManageRequest(val action: String, val id: Int)