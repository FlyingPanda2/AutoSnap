package com.pandoscorp.autosnap.navigation

import AddClientForm
import AddClientViewModel
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.pandoscorp.autosnap.repository.UserRepository
import com.pandoscorp.autosnap.ui.screens.ChatForm
import com.pandoscorp.autosnap.ui.screens.ClientsForm
import com.pandoscorp.autosnap.ui.screens.LoginForm
import com.pandoscorp.autosnap.ui.screens.RegistrationForm
import com.pandoscorp.autosnap.ui.screens.WelcomeForm
import com.pandoscorp.autosnap.ui.screens.MainForm
import com.pandoscorp.autosnap.ui.screens.NewAppointmentForm
import com.pandoscorp.autosnap.ui.screens.ProfileForm
import com.pandoscorp.autosnap.ui.screens.SheduleForm
import com.pandoscorp.autosnap.ui.viewmodel.AuthViewModel
import com.pandoscorp.autosnap.ui.viewmodel.ClientsViewModel
import com.pandoscorp.autosnap.ui.viewmodel.SharedViewModel
import com.pandoscorp.autosnap.ui.viewmodel.SheduleViewModel


@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val userRepository = UserRepository()
    val authViewModel = AuthViewModel(userRepository)
    val addClientViewModel = AddClientViewModel()
    val clientViewModel = ClientsViewModel()
    val sheduleViewModel = SheduleViewModel()
    val sharedViewModel = SharedViewModel()

    NavHost(navController = navController, startDestination = ScreenObject.MainScreen.route) {
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
            ClientsForm(navController, clientViewModel)
        }
        composable("clients/selection") {
            ClientsForm(
                navController = navController,
                viewModel = clientViewModel,
                forSelection = true,
                onClientSelected = { client ->
                    sharedViewModel.selectClient(client)
                }
            )
        }
        composable(ScreenObject.SheduleScreen.route){
            SheduleForm(navController, sheduleViewModel)
        }
        composable(ScreenObject.AddClientScreen.route){
            AddClientForm(navController, addClientViewModel)
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
        composable(ScreenObject.ChatScreen.route + "chat/{shopId}") { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            ChatForm()
        }
        composable(ScreenObject.NewAppointmentScreen.route){
            NewAppointmentForm(navController, sharedViewModel = SharedViewModel())
        }


    }
}

