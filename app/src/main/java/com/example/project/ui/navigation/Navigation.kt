// This file defines the navigation setup for the app using Jetpack Compose's navigation component.
package com.example.project.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.project.data.model.domain.Sighting
import com.example.project.ui.screen.sightings.SightingListScreen
import org.koin.compose.koinInject
import com.example.project.ui.screen.sightings.*
import com.example.project.ui.screen.map.*
import com.example.project.ui.screen.discussion.*
import com.example.project.ui.screen.settings.*
import com.example.project.ui.screen.user.*
import com.example.project.ui.screen.auth.*
import com.example.project.ui.screen.SplashScreen

// Composable function that sets up the navigation host for the app.
@Composable
fun NavHostController() {
    // Create and remember a NavController to manage navigation state across recompositions.
    val navController = rememberNavController()
    // Inject NavigatorImpl via Koin for custom navigation logic.
    val navigator: NavigatorImpl = koinInject()
    // Use LaunchedEffect to initialize the navigator with the navController once on first composition.
    LaunchedEffect(navController) {
        navigator.initialize(navController)
    }
    // Define the navigation graph with NavHost, starting at the SightingListScreen.
    NavHost(navController = navController, startDestination = Screen.SightingListScreen.route) {
        composable(route = Screen.SightingListScreen.route) {
            SightingListScreen()
        }
        composable(route = Screen.SightingViewScreen.route) {
            SightingViewScreen()
        }
        composable(route = Screen.NewSightingScreen.route) {
            NewSightingScreen()
        }
        composable(route = Screen.MapScreen.route) {
            MapScreen()
        }
        composable(route = Screen.DiscussionListScreen.route) {
            DiscussionListScreen()
        }
        composable(route = Screen.SettingsScreen.route) {
            SettingsScreen()
        }
        composable(route = Screen.LoginScreen.route) {
            LoginScreen()
        }
        composable(route = Screen.RegisterScreen.route) {
            RegisterScreen()
        }
        composable(route = Screen.SplashScreen.route) {
            SplashScreen()
        }
        composable(route = Screen.DiscussionScreen.route) {
            DiscussionScreen()
        }
        composable(route = Screen.NewDiscussionScreen.route) {
            NewDiscussionScreen()
        }
        composable(route = Screen.ProfileScreen.route) {
            ProfileScreen()
        }
    }
}

sealed class Screen(val route: String) {
    object SightingViewScreen : Screen("sightingview_screen")
    object SightingListScreen : Screen("sightinglist_screen")
    object NewSightingScreen : Screen("newsighting_screen")
    object MapScreen : Screen("mapscreen_screen")
    object DiscussionListScreen : Screen("discussionlist_screen")
    object SettingsScreen : Screen("settings_screen")
    object LoginScreen : Screen("login_screen")
    object RegisterScreen : Screen("register_screen")
    object DiscussionScreen : Screen("discussion_screen")
    object NewDiscussionScreen : Screen("newdicussion_screen")
    object SplashScreen : Screen("splash_screen")
    object ProfileScreen : Screen("profile_screen")

}