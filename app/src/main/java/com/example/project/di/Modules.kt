package com.example.project.di

import androidx.activity.ComponentActivity
import com.example.project.data.repository.FirestoreRepository
import com.example.project.data.repository.*
import com.example.project.service.MediaLauncherImpl
import com.example.project.ui.navigation.NavigatorImpl
import com.example.project.service.FirebaseAuthService
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.dsl.module
import com.example.project.ui.viewmodel.*
import com.google.firebase.auth.FirebaseAuth
import org.koin.core.annotation.KoinReflectAPI
import org.koin.core.module.dsl.singleOf
import com.example.project.ui.viewmodel.*
@OptIn(KoinReflectAPI::class)
val sharedModule = module {

    single<NavigatorImpl> {
        NavigatorImpl()
    }

    viewModelOf(::SightingsViewModel)
    viewModelOf(::NewSightingViewModel)
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::ProfileViewModel)
    viewModelOf(::SettingsViewModel)
    //viewModelOf(::SightingViewModel)
    //viewModelOf(::DiscussionViewModel)
   // viewModelOf(::NewDiscussionViewModel)
}

val firebaseModule = module {
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }
    single { FirebaseStorage.getInstance() }

    single { FirestoreRepository() }
    single { UserRepository() }
    single { SightingCommentRepository() }
    single { SightingRepository() }
    single { DiscussionCommentRepository() }
    single { DiscussionRepository() }

    singleOf(::FirebaseAuthService)
}

val appModule = module {
    single<MediaLauncherImpl> { MediaLauncherImpl() }
}