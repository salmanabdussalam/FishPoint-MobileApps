package com.app.fishpoint.navigation

sealed class Screen(val route: String) {
    object Login          : Screen("login")
    object Register       : Screen("register")
    object Beranda        : Screen("beranda")

    object DetailSpot     : Screen("detail_spot/{spotId}") {
        fun createRoute(spotId: Int) = "detail_spot/$spotId"
    }

    object TambahSpot     : Screen("tambah_spot")

    object EditSpot       : Screen("edit_spot/{spotId}") {
        fun createRoute(spotId: Int) = "edit_spot/$spotId"
    }

    object Profil         : Screen("profil")
    object AdminDashboard : Screen("admin_dashboard")

    object MapPicker      : Screen("map_picker?lat={lat}&lng={lng}") {
        fun createRoute(lat: Double? = null, lng: Double? = null): String {
            val latParam = lat ?: DEFAULT_LAT
            val lngParam = lng ?: DEFAULT_LNG
            return "map_picker?lat=$latParam&lng=$lngParam"
        }

        const val DEFAULT_LAT = -7.5755
        const val DEFAULT_LNG = 110.8243
    }

    companion object {
        const val RESULT_COORDINATES = "result_coordinates"
    }
}