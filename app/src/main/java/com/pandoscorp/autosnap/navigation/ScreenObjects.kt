package com.pandoscorp.autosnap.navigation

sealed class ScreenObject(val route: String) {

    object WelcomeScreen : ScreenObject("Render")
    object RegScreen : ScreenObject("RegistrationForm")
    object LoginScreen : ScreenObject("LoginForm")
    object MainScreen : ScreenObject("MainForm")
    object ProfileScreen : ScreenObject("ProfileForm")
    object ClientsScreen : ScreenObject("ClientsForm")
    object AddClientScreen : ScreenObject("AddClientForm")
    object SheduleScreen : ScreenObject("SheduleForm")
    object ChatScreen : ScreenObject("ChatForm")
    object NewAppointmentScreen : ScreenObject("NewAppointmentForm")
    object ClientCarsScreen : ScreenObject("ClientCarsForm")
    object ServiceChooseScreen : ScreenObject("ServiceChooseForm")
    object AddServiceScreen : ScreenObject("AddServiceForm")

    data class ClientsForSelection(val forSelection: Boolean = true) : ScreenObject(
        route = "clients?forSelection=$forSelection"
    ) {
        companion object {
            // Альтернативный способ создания маршрута
            fun createRoute(forSelection: Boolean) = "clients?forSelection=$forSelection"
        }
    }

}