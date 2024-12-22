package com.example.project.ui.navigation

interface Navigator {
    fun navToSighting()
    fun navToLogin()
    fun navToRegister()
    fun navToSettings()
    fun navToSplash()
    fun popBackStack()
    fun navToDiscussionList()
    fun navToDiscussion()
    fun navToNewDiscussion()
    fun navToNewSighting()
    fun navToMap()
    fun navToProfile()

}