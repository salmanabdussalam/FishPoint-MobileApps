package com.app.fishpoint.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.app.fishpoint.ui.screens.*
import com.app.fishpoint.ui.viewmodel.AuthViewModel
import com.app.fishpoint.ui.viewmodel.SpotViewModel

@Composable
fun FishPointNavGraph(navController: NavHostController) {
    val authViewModel: AuthViewModel = viewModel()
    val spotViewModel: SpotViewModel = viewModel()

    NavHost(
        navController    = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    val destination = if (authViewModel.isAdmin) Screen.AdminDashboard.route else Screen.Beranda.route
                    navController.navigate(destination) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onNavigateToRegister = { navController.navigate(Screen.Register.route) },
                onContinueAsGuest = {
                    navController.navigate(Screen.Beranda.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                authViewModel = authViewModel,
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Beranda.route) { popUpTo(Screen.Login.route) { inclusive = true } }
                },
                onBackClick       = { navController.popBackStack() },
                onNavigateToLogin = { navController.popBackStack() },
                authViewModel     = authViewModel,
            )
        }

        composable(Screen.Beranda.route) {
            val isGuest = !authViewModel.isLoggedIn
            BerandaScreen(
                isGuest       = isGuest,
                viewModel     = spotViewModel,
                onSpotClick   = { spotId -> navController.navigate(Screen.DetailSpot.createRoute(spotId)) },
                onTambahClick = { navController.navigate(Screen.TambahSpot.route) },
                onProfilClick = {
                    if (isGuest) navController.navigate(Screen.Login.route)
                    else navController.navigate(Screen.Profil.route)
                }
            )
        }

        composable(
            route     = Screen.DetailSpot.route,
            arguments = listOf(navArgument("spotId") { type = NavType.IntType })
        ) { backStackEntry ->
            val spotId = backStackEntry.arguments?.getInt("spotId") ?: 1
            DetailSpotScreen(
                spotId          = spotId,
                currentUsername = authViewModel.currentUsername,
                currentUserId   = authViewModel.currentUserId,
                viewModel       = spotViewModel,
                onBackClick     = { navController.popBackStack() },
                onProfilClick   = {
                    if (authViewModel.isLoggedIn) navController.navigate(Screen.Profil.route)
                    else navController.navigate(Screen.Login.route)
                },
                onRequireLogin  = { navController.navigate(Screen.Login.route) }
            )
        }

        composable(Screen.TambahSpot.route) { backStackEntry ->
            val mapResult = backStackEntry.savedStateHandle.get<DoubleArray>(Screen.RESULT_COORDINATES)?.let { it[0] to it[1] }
            val currentUser = authViewModel.currentUser
            TambahSpotScreen(
                userId          = currentUser?.id ?: -1,
                ownerUsername   = currentUser?.username ?: "",
                viewModel       = spotViewModel,
                mapPickerResult = mapResult,
                onBackClick     = { navController.popBackStack() },
                onSimpanSuccess = { navController.popBackStack() },
                onOpenMapPicker = { lat, lng -> navController.navigate(Screen.MapPicker.createRoute(lat, lng)) }
            )
        }

        composable(
            route     = Screen.EditSpot.route,
            arguments = listOf(navArgument("spotId") { type = NavType.IntType })
        ) { backStackEntry ->
            val spotId = backStackEntry.arguments?.getInt("spotId") ?: -1
            val mapResult = backStackEntry.savedStateHandle.get<DoubleArray>(Screen.RESULT_COORDINATES)?.let { it[0] to it[1] }
            val currentUser = authViewModel.currentUser
            EditSpotScreen(
                spotId           = spotId,
                userId           = currentUser?.id ?: -1,
                currentUsername  = currentUser?.username ?: "",
                viewModel        = spotViewModel,
                mapPickerResult  = mapResult,
                onBackClick      = { navController.popBackStack() },
                onSimpanSuccess  = { navController.popBackStack() },
                onOpenMapPicker  = { lat, lng -> navController.navigate(Screen.MapPicker.createRoute(lat, lng)) }
            )
        }

        composable(
            route = Screen.MapPicker.route,
            arguments = listOf(
                navArgument("lat") { type = NavType.StringType; defaultValue = Screen.MapPicker.DEFAULT_LAT.toString() },
                navArgument("lng") { type = NavType.StringType; defaultValue = Screen.MapPicker.DEFAULT_LNG.toString() },
            )
        ) { backStackEntry ->
            val initialLat = backStackEntry.arguments?.getString("lat")?.toDoubleOrNull() ?: Screen.MapPicker.DEFAULT_LAT
            val initialLng = backStackEntry.arguments?.getString("lng")?.toDoubleOrNull() ?: Screen.MapPicker.DEFAULT_LNG
            MapPickerScreen(
                initialLat = initialLat,
                initialLng = initialLng,
                onBackClick = { navController.popBackStack() },
                onLocationPicked = { lat, lng ->
                    navController.previousBackStackEntry?.savedStateHandle?.set(Screen.RESULT_COORDINATES, doubleArrayOf(lat, lng))
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Profil.route) {
            ProfilScreen(
                onBerandaClick = { navController.navigate(Screen.Beranda.route) { popUpTo(Screen.Beranda.route) { inclusive = false } } },
                onTambahClick   = { navController.navigate(Screen.TambahSpot.route) },
                onLogoutClick   = {
                    authViewModel.resetAllState()
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                },
                onEditSpotClick = { spotId -> navController.navigate(Screen.EditSpot.createRoute(spotId)) },
                authViewModel   = authViewModel,
                spotViewModel   = spotViewModel,
            )
        }

        composable(Screen.AdminDashboard.route) {
            AdminDashboardScreen(
                onLogoutClick = {
                    authViewModel.resetAllState()
                    navController.navigate(Screen.Login.route) { popUpTo(0) { inclusive = true } }
                },
                authViewModel = authViewModel,
                spotViewModel = spotViewModel
            )
        }
    }
}