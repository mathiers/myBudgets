package com.tees.mybudgets.screens


//base class for defining a closed set of possible screen destinations.
//it has a single property, screen, of type String, which holds the name or identifier of the screen.
sealed class Screens(val screen: String){
    data object Home: Screens("home")
    data object Search: Screens("search")
    data object Notification: Screens("notification")
    data object Profile: Screens("profile")
    data object Post: Screens("post")
    data object Settings: Screens("settings")
}