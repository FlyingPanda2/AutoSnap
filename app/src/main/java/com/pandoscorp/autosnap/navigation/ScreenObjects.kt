package com.pandoscorp.autosnap.navigation

sealed class ScreenObject(val route: String){

    object WelcomeScreen: ScreenObject("Render")
    object RegScreen: ScreenObject("RegistrationForm")
    object LoginScreen: ScreenObject("LoginForm")
    object MainScreen: ScreenObject("MainForm")
    object ProfileScreen: ScreenObject("ProfileForm")
    object ClientsScreen: ScreenObject("ClientsForm")
}