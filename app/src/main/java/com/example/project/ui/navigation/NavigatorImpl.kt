package com.example.project.ui.navigation

import androidx.navigation.NavController
import org.koin.core.component.KoinComponent

class NavigatorImpl:Navigator, KoinComponent {
    private lateinit var navController: NavController

    fun initialize(navController: NavController) {
        this.navController = navController
    }

    override fun navToSighting() {
        navController.navigate(Screen.SightingViewScreen.route)
    }

    override fun navToLogin() {
        navController.navigate(Screen.LoginScreen.route)
    }

    override fun navToRegister() {
        navController.navigate(Screen.RegisterScreen.route)
    }

    override fun navToSettings() {
        navController.navigate(Screen.SettingsScreen.route)
    }

    override fun navToSplash() {
        navController.navigate(Screen.SplashScreen.route)
    }

    override fun popBackStack() {
        navController.popBackStack()
    }

    override fun navToDiscussionList() {
        navController.navigate(Screen.DiscussionListScreen.route)
    }

    override fun navToDiscussion() {
        navController.navigate(Screen.DiscussionScreen.route)
    }

    override fun navToNewDiscussion() {
        navController.navigate(Screen.NewDiscussionScreen.route)
    }

    override fun navToNewSighting() {
        navController.navigate(Screen.NewSightingScreen.route)
    }

    override fun navToMap() {
        navController.navigate(Screen.MapScreen.route)
    }

    override fun navToProfile() {
        navController.navigate(Screen.ProfileScreen.route)
    }
}