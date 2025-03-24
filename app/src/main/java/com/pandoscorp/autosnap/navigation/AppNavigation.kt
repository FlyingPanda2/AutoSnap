package com.pandoscorp.autosnap.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.pandoscorp.autosnap.ScreenObject
import com.pandoscorp.autosnap.ui.clients.ClientsForm
import com.pandoscorp.autosnap.ui.login.LoginForm
import com.pandoscorp.autosnap.ui.login.RegistrationForm
import com.pandoscorp.autosnap.ui.login.WelcomeForm
import com.pandoscorp.autosnap.ui.main_screen.MainForm
import com.pandoscorp.autosnap.ui.profile.ProfileForm


@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = ScreenObject.ProfileScreen.route) {
        composable(ScreenObject.MainScreen.route){
            MainForm(navController)
        }
        composable(ScreenObject.RegScreen.route){
            RegistrationForm(navController)
        }
        composable(ScreenObject.LoginScreen.route){
            LoginForm(navController)
        }
        composable(ScreenObject.WelcomeScreen.route){
            WelcomeForm(navController)
        }
        composable(ScreenObject.ClientsScreen.route){
            ClientsForm(navController)
        }
        composable(ScreenObject.ProfileScreen.route){
            ProfileForm(navController)
        }
    }
}