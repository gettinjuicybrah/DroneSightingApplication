package com.example.project.di
// This file contains the initialization logic for Koin, the dependency injection framework.
import org.koin.core.context.startKoin
import org.koin.dsl.KoinAppDeclaration

// Function to initialize Koin with the app's dependency modules.
fun initKoin(config: KoinAppDeclaration? = null) {
    // Start Koin and configure it with the provided declaration and modules.
    startKoin {
        // Apply optional configuration if provided (e.g., for custom setups).
        config?.invoke(this)
        modules(sharedModule, firebaseModule, appModule)
    }
}