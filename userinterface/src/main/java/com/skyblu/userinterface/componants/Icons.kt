package com.skyblu.userinterface.componants

import androidx.annotation.DrawableRes
import androidx.navigation.NavController
import com.skyblu.configuration.Concept
import com.skyblu.userinterface.R



///**
// * Concepts are ideas that can link a title with an icon and an optional route. These can be useful to generate IconButtons or routes for navigation. Concepts can be combined eg 'Account Settings'
// * @param title the name of the concept
// * @param icon an icon to represent the concept
// * @param route a route to a screen this concept would represent
// */
//sealed class Concept(
//    val title: String,
//    @DrawableRes val icon: Int,
//    val route: String = ""
//) {
//
//    object Home : Concept(
//        title = "Home",
//        icon = R.drawable.home,
//        route = "home"
//    )
//
//    object Splash : Concept(
//        title = "Splash",
//        icon = R.drawable.aircraft,
//        route = "splash"
//    )
//
//    object Delete : Concept(
//        title = "Delete",
//        icon = R.drawable.delete,
//        route = "delete"
//    )
//
//    object Map : Concept(
//        title = "Map",
//        icon = R.drawable.map,
//        route = "map"
//    )
//
//    object Refresh : Concept(
//        title = "Refresh",
//        icon = R.drawable.refresh,
//        route = "refresh"
//    )
//
//
//    object Mapping : Concept(
//        title = "Mapping",
//        icon = R.drawable.map,
//        route = "map"
//    )
//
//    object Account : Concept(
//        title = "Account",
//        icon = R.drawable.person,
//        route = "account"
//    )
//
//    object Profile : Concept(
//        title = "Profile",
//        icon = R.drawable.person,
//        route = "profile"
//    )
//
//    object Login : Concept(
//        title = "Login",
//        icon = R.drawable.login,
//        route = "login"
//    )
//
//    object Settings : Concept(
//        title = "Settings",
//        icon = R.drawable.settings,
//        route = "settings"
//    )
//
//    object TrackSkydive : Concept(
//        title = "Track Skydive",
//        icon = R.drawable.parachute,
//        route = "track"
//    )
//
//    object Awards : Concept(
//        title = "Awards",
//        icon = R.drawable.award,
//        route = "awards"
//    )
//
//    object CreateAccount : Concept(
//        title = "Create Account",
//        icon = R.drawable.add_circle,
//        route = "createAccount"
//    )
//
//    object Welcome : Concept(
//        title = "Welcome",
//        icon = R.drawable.wave,
//        route = "welcome"
//    )
//
//    object LoggedIn : Concept(
//        title = "Logged In",
//        icon = R.drawable.person,
//        route = "logged_in"
//    )
//
//    object LoggedOut : Concept(
//        title = "Logged Out",
//        icon = R.drawable.no_person,
//        route = "logged_out"
//    )
//
//    object Add : Concept(
//        "Add",
//        R.drawable.add
//    )
//
//    object AddPhoto : Concept(
//        "Add Photo",
//        R.drawable.add_photo
//    )
//
//    object AirPressure : Concept(
//        "Air Pressure",
//        R.drawable.air
//    )
//
//    object GroundAirPressure : Concept(
//        "Base Air Pressure",
//        R.drawable.air
//    )
//
//    object Award : Concept(
//        "Awards",
//        R.drawable.award
//    )
//
//    object Ground : Concept(
//        "Ground",
//        R.drawable.bottom
//    )
//
//    object Time : Concept(
//        "Time Elapsed",
//        R.drawable.clock
//    )
//
//    object Edit : Concept(
//        "Edit",
//        R.drawable.edit,
//        route = "edit"
//    )
//
//    object Email : Concept(
//        JUMP_DETAILS_STRING,
//        R.drawable.email
//    )
//
//    object Group : Concept(
//        "Group",
//        R.drawable.group
//    )
//
//    object Altitude : Concept(
//        "Altitude",
//        R.drawable.height
//    )
//
//    object BaseAltitude : Concept(
//        "Base Altitude",
//        R.drawable.height
//    )
//
//    object Help : Concept(
//        "Help",
//        R.drawable.help
//    )
//
//    object Info : Concept(
//        "Info",
//        R.drawable.info
//    )
//
//    object Key : Concept(
//        "Key",
//        R.drawable.key
//    )
//
//    object Location : Concept(
//        "Location",
//        R.drawable.location
//    )
//
//    object LocationNotTracking : Concept(
//        "Not Tracking",
//        R.drawable.location_not_tracking
//    )
//
//    object LocationTracking : Concept(
//        "Tracking",
//        R.drawable.location_tracking,
//        route = "tracking"
//    )
//
//    object More : Concept(
//        "More",
//        R.drawable.more
//    )
//
//    object Next : Concept(
//        "Next",
//        R.drawable.next
//    )
//
//    object Parachute : Concept(
//        "Parachute",
//        R.drawable.parachute
//    )
//
//    object Password : Concept(
//        "Password",
//        R.drawable.password
//    )
//
//    object Person : Concept(
//        "Person",
//        R.drawable.person
//    )
//
//    object Photo : Concept(
//        "Photo",
//        R.drawable.photo
//    )
//
//    object Plane : Concept(
//        DROPZONE_STRING,
//        R.drawable.blue_plane
//    )
//
//    object Previous : Concept(
//        "Previous",
//        R.drawable.previous
//    )
//
//    object Save : Concept(
//        "Save",
//        R.drawable.upload
//    )
//
//    object Send : Concept(
//        "Send",
//        R.drawable.send
//    )
//
//    object Sensor : Concept(
//        "Sensor",
//        R.drawable.sensor
//    )
//
//    object Share : Concept(
//        "Share",
//        R.drawable.share
//    )
//
//    object Star : Concept(
//        "Star",
//        R.drawable.star
//    )
//
//    object Tag : Concept(
//        "Tag",
//        R.drawable.tag
//    )
//
//    object Up : Concept(
//        "Up",
//        R.drawable.up
//    )
//
//    object Logout : Concept(
//        "Logout",
//        R.drawable.logout
//    )
//
//    object Close : Concept(
//        "Close",
//        R.drawable.close
//    )
//
//    object Latitude : Concept(
//        "Latitude",
//        R.drawable.latitude
//    )
//
//    object Longitude : Concept(
//        "Longitude",
//        R.drawable.longitude
//    )
//
//    object PointsAccepted : Concept(
//        "Points Accepted",
//        R.drawable.location_tracking
//    )
//
//    object PointsRejectd : Concept(
//        "Points Rejected",
//        R.drawable.location_not_tracking
//    )
//
//    object PointsTotal : Concept(
//        "Points Total",
//        R.drawable.number
//    )
//
//    object JumpStatus : Concept(
//        "Jump Status",
//        R.drawable.help
//    )
//
//    object TotalDistance : Concept(
//        "Total Distance",
//        R.drawable.map
//    )
//
//    object SectorDistance : Concept(
//        "Sector Distance",
//        R.drawable.map
//    )
//}

/**
 * Connects a concept to an action
 * @param concept a related icon, title and optional route
 * @param action a action connected to the concept
 * @see Concept
 */
data class ActionConcept(
    val concept: Concept,
    val action: () -> Unit,
    )
