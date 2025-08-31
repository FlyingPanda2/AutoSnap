package com.pandoscorp.autosnap.navigation

sealed class ScreenObject(val route: String) {

    object WelcomeScreen : ScreenObject("Render")
    object RegScreen : ScreenObject("RegistrationForm")
    object LoginScreen : ScreenObject("LoginForm")
    object MainScreen : ScreenObject("MainForm")
    object ProfileScreen : ScreenObject("ProfileForm")
    object ClientsScreen : ScreenObject("ClientsForm")
    object AddClientScreen : ScreenObject("AddClientForm")
    object ChatScreen : ScreenObject("ChatForm")
    object NewAppointmentScreen : ScreenObject("NewAppointmentForm")
    object ClientCarsScreen : ScreenObject("ClientCarsForm")
    object ServiceChooseScreen : ScreenObject("ServiceChooseForm")
    object AddServiceScreen : ScreenObject("AddServiceForm")
    object ClientMainScreen : ScreenObject("ClientMainForm")
    object AutoServiceChoose : ScreenObject("AutoServiceChooseScreen")
    object AddClientCarScreen : ScreenObject("AddClientCarScreen")
    object ClientCreateAppointmentScreen : ScreenObject("ClientCreateAppointmentScreen")
    object SheduleScreen : ScreenObject("schedule?forDateSelection={forDateSelection}")
    object Chat : ScreenObject("GarageMasterChat")
}