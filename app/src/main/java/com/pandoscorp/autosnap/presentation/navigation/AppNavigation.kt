package com.pandoscorp.autosnap.navigation

import AddClientForm
import AddClientViewModel
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.google.firebase.auth.FirebaseAuth
import com.pandoscorp.autosnap.domain.model.User
import com.pandoscorp.autosnap.repository.ClientRepository
import com.pandoscorp.autosnap.repository.UserRepository
import com.pandoscorp.autosnap.ui.screens.AddClientCarScreen
import com.pandoscorp.autosnap.ui.screens.AddServiceForm
import com.pandoscorp.autosnap.ui.screens.AutoServiceChooseScreen
import com.pandoscorp.autosnap.ui.screens.ChatForm
import com.pandoscorp.autosnap.ui.screens.ClientCarsForm
import com.pandoscorp.autosnap.ui.screens.ClientCreateAppointmentScreen
import com.pandoscorp.autosnap.ui.screens.ClientMainScreen
import com.pandoscorp.autosnap.ui.screens.ClientsForm
import com.pandoscorp.autosnap.ui.screens.GarageMasterChat
import com.pandoscorp.autosnap.ui.screens.LoginForm
import com.pandoscorp.autosnap.ui.screens.MainForm
import com.pandoscorp.autosnap.ui.screens.NewAppointmentForm
import com.pandoscorp.autosnap.ui.screens.ProfileForm
import com.pandoscorp.autosnap.ui.screens.RegistrationForm
import com.pandoscorp.autosnap.ui.screens.ServiceChooseForm
import com.pandoscorp.autosnap.ui.screens.SheduleForm
import com.pandoscorp.autosnap.ui.screens.WelcomeForm
import com.pandoscorp.autosnap.ui.viewmodel.AddClientCarViewModel
import com.pandoscorp.autosnap.ui.viewmodel.AddServiceViewModel
import com.pandoscorp.autosnap.ui.viewmodel.AppointmentSharedViewModel
import com.pandoscorp.autosnap.ui.viewmodel.AuthViewModel
import com.pandoscorp.autosnap.ui.viewmodel.AutoServiceChooseViewModel
import com.pandoscorp.autosnap.ui.viewmodel.ClientCreateAppointmentViewModel
import com.pandoscorp.autosnap.ui.viewmodel.ClientViewModel
import com.pandoscorp.autosnap.ui.viewmodel.ClientsMainViewModel
import com.pandoscorp.autosnap.ui.viewmodel.MainViewModel
import com.pandoscorp.autosnap.ui.viewmodel.ServiceViewModel


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun AppNavigation() {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    val navController = rememberNavController()
    val clientRepository = ClientRepository()
    val userRepository = UserRepository()
    val authViewModel = AuthViewModel(userRepository, clientRepository)
    val addClientViewModel = AddClientViewModel()
    val clientViewModel = ClientViewModel()
    val clientsMainViewModel = ClientsMainViewModel()
    val appointmentSharedViewModel = AppointmentSharedViewModel()
    val addServiceViewModel = AddServiceViewModel()
    val serviceViewModel = ServiceViewModel()
    val mainViewModel = MainViewModel()
    val autoServiceViewChooseModel = AutoServiceChooseViewModel()
    val addClientCarViewModel = AddClientCarViewModel()
    val clientCreateAppointmentViewModel = ClientCreateAppointmentViewModel()

    var currentUser by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(userId) {
        userId?.let {
            currentUser = userRepository.getUserById(it)
        }
    }



    NavHost(navController = navController, startDestination = ScreenObject.LoginScreen.route) {
        composable(ScreenObject.MainScreen.route) {
            MainForm(navController, mainViewModel)
        }
        composable(ScreenObject.RegScreen.route) {
            RegistrationForm(navController, authViewModel)
        }
        composable(ScreenObject.ClientCreateAppointmentScreen.route) {
            ClientCreateAppointmentScreen(navController, clientCreateAppointmentViewModel, clientsMainViewModel)
        }

        composable(ScreenObject.LoginScreen.route) {
            LoginForm(navController, authViewModel)
        }
        composable(ScreenObject.WelcomeScreen.route) {
            WelcomeForm(navController)
        }
        composable(ScreenObject.ClientsScreen.route) {
            ClientsForm(navController, clientViewModel)
        }
        composable(ScreenObject.ClientMainScreen.route) {
            ClientMainScreen(navController, clientsMainViewModel)
        }
        composable("clients/selection") {
            ClientsForm(
                navController = navController,
                viewModel = clientViewModel,
                forSelection = true,
                onClientSelected = { client ->
                    appointmentSharedViewModel.selectClient(client)
                }
            )
        }
        composable(
            route = ScreenObject.SheduleScreen.route,
            arguments = listOf(
                navArgument("forDateSelection") {
                    type = NavType.BoolType
                    defaultValue = false
                }
            )
        ) { backStackEntry ->
            val forDateSelection = backStackEntry.arguments?.getBoolean("forDateSelection") ?: false
            SheduleForm(navController, appointmentSharedViewModel)
        }
        composable(ScreenObject.AddClientScreen.route) {
            AddClientForm(navController, addClientViewModel)
        }

        composable(ScreenObject.AutoServiceChoose.route) {
            AutoServiceChooseScreen(navController, autoServiceViewChooseModel, clientsMainViewModel)
        }
        composable(ScreenObject.AddClientCarScreen.route) {
            AddClientCarScreen(navController, addClientCarViewModel)
        }
        composable(ScreenObject.Chat.route) {
            GarageMasterChat()
        }

        composable(ScreenObject.ProfileScreen.route + "/{userId}") { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            if (userId.isNotEmpty()) {
                ProfileForm(navController, userId, userRepository)
            } else {
                Text("Пользователь не найден")
            }
        }
        composable(ScreenObject.ChatScreen.route + "chat/{shopId}") { backStackEntry ->
            val shopId = backStackEntry.arguments?.getString("shopId") ?: ""
            val userId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
            ChatForm()
        }
        composable(ScreenObject.NewAppointmentScreen.route) {
            NewAppointmentForm(navController, appointmentSharedViewModel)
        }
        composable(ScreenObject.ServiceChooseScreen.route) {
            ServiceChooseForm(
                navController,
                serviceViewModel,
                appointmentSharedViewModel,
                currentUser
            )
        }
        composable(ScreenObject.AddServiceScreen.route) {
            if (userId != null) {
                AddServiceForm(navController, userId, addServiceViewModel)
            }
        }

        composable(ScreenObject.ClientCarsScreen.route) {
            ClientCarsForm(
                navController = navController,
                appointmentSharedViewModel = appointmentSharedViewModel,
                onCarSelected = { car ->
                    appointmentSharedViewModel.selectCar(car)
                }
            )
        }


    }
}

