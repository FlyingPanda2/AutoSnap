package com.pandoscorp.autosnap.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.database.DatabaseReference
import com.pandoscorp.autosnap.repository.UserRepository
import com.pandoscorp.autosnap.ui.screens.ClientsForm
import com.pandoscorp.autosnap.ui.screens.LoginForm
import com.pandoscorp.autosnap.ui.screens.RegistrationForm
import com.pandoscorp.autosnap.ui.screens.WelcomeForm
import com.pandoscorp.autosnap.ui.screens.MainForm
import com.pandoscorp.autosnap.ui.screens.ProfileForm
import com.pandoscorp.autosnap.ui.viewmodel.AuthViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userRepository = UserRepository()
    val authViewModel = AuthViewModel(userRepository)

    NavHost(navController = navController, startDestination = ScreenObject.RegScreen.route) {
        composable(ScreenObject.MainScreen.route){
            MainForm(navController)
        }
        composable(ScreenObject.RegScreen.route){
            RegistrationForm(navController, authViewModel)
        }
        composable(ScreenObject.LoginScreen.route){
            LoginForm(navController, authViewModel)
        }
        composable(ScreenObject.WelcomeScreen.route){
            WelcomeForm(navController)
        }
        composable(ScreenObject.ClientsScreen.route){
            ClientsForm(navController)
        }
        composable(ScreenObject.ProfileScreen.route + "/{userId}"){ backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            if(userId.isNotEmpty()){
                ProfileForm(navController, userId, userRepository)
            }
            else{
                Text("Пользователь не найден")
            }
        }
    }
}