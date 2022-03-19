package com.skyblu.userinterface.componants

import androidx.annotation.DrawableRes
import com.skyblu.userinterface.R

data class MenuAction(
    val onClick: () -> Unit,
    val appIcon: AppIcon
)



sealed class AppIcon(
    val title: String,
    @DrawableRes val icon : Int,
    val route : String = ""
){
    object Home : AppIcon(
        title = "Home",
        icon = R.drawable.home,
        route = "home"
    )
    object Map : AppIcon(
        title = "Map",
        icon = R.drawable.map,
        route = "map"
    )
    object Profile : AppIcon(
        title = "Profile",
        icon = R.drawable.person,
        route = "profile"
    )
    object Login : AppIcon(
        title = "Login",
        icon = R.drawable.login,
        route = "login"
    )
    object Settings : AppIcon(
        title = "Settings",
        icon = R.drawable.settings,
        route = "settings"
    )
    object TrackSkydive : AppIcon(
        title = "Track Skydive",
        icon = R.drawable.parachute,
        route = "track"
    )
    object Awards : AppIcon(
        title = "Awards",
        icon = R.drawable.award,
        route = "awards"
    )
    object CreateAccount : AppIcon(
        title = "Create Account",
        icon = R.drawable.add_circle,
        route = "createAccount"
    )
    object Welcome : AppIcon(
        title = "Welcome",
        icon = R.drawable.wave,
        route = "createAccount"
    )


    object Add : AppIcon("Add" ,
        R.drawable.add)
    object AddPhoto : AppIcon("Add Photo" ,
        R.drawable.add_photo)
    object AirPressure : AppIcon("Air Pressure" ,
        R.drawable.air)
    object GroundAirPressure : AppIcon("Base Air Pressure" ,
        R.drawable.air)
    object Award : AppIcon("Awards" ,
        R.drawable.award)
    object Ground : AppIcon("Ground" ,
        R.drawable.bottom)
    object Time : AppIcon("Time Elapsed" ,
        R.drawable.clock)
    object Edit : AppIcon("Edit" ,
        R.drawable.edit)
    object Email : AppIcon("Email" ,
        R.drawable.email)
    object Group : AppIcon("Group" ,
        R.drawable.group)
    object Altitude : AppIcon("Altitude" ,
        R.drawable.height)
    object BaseAltitude : AppIcon("Base Altitude" ,
        R.drawable.height)
    object Help : AppIcon("Help" ,
        R.drawable.help)
    object Info : AppIcon("Info" ,
        R.drawable.info)
    object Key : AppIcon("Key" ,
        R.drawable.key)
    object Location : AppIcon("Location" ,
        R.drawable.location)
    object LocationNotTracking : AppIcon("Not Tracking" ,
        R.drawable.location_not_tracking)
    object LocationTracking : AppIcon("Tracking" ,
        R.drawable.location_tracking)
    object More : AppIcon("More" ,
        R.drawable.more)
    object Next : AppIcon("Next" ,
        R.drawable.next)
    object Parachute : AppIcon("Parachute" ,
        R.drawable.parachute)
    object Password : AppIcon("Password" ,
        R.drawable.password)
    object Person : AppIcon("Person" ,
        R.drawable.person)
    object Photo : AppIcon("Photo" ,
        R.drawable.photo)
    object Plane : AppIcon("Aircraft" ,
        R.drawable.blue_plane)
    object Previous : AppIcon("Previous" ,
        R.drawable.previous)
    object Save : AppIcon("Save" ,
        R.drawable.save)
    object Send : AppIcon("Send" ,
        R.drawable.send)
    object Sensor : AppIcon("Sensor" ,
        R.drawable.sensor)
    object Share : AppIcon("Share" ,
        R.drawable.share)
    object Star : AppIcon("Star" ,
        R.drawable.star)
    object Tag : AppIcon("Tag" ,
        R.drawable.tag)
    object Up : AppIcon("Up" ,
        R.drawable.up)
    object Logout : AppIcon("Logout" ,
        R.drawable.logout)
    object Close : AppIcon( "Close",
        R.drawable.close
    )
    object Latitude : AppIcon( "Latitude",
        R.drawable.latitude
    )
    object Longitude : AppIcon( "Longitude",
        R.drawable.longitude
    )
    object PointsAccepted : AppIcon( "Points Accepted",
        R.drawable.location_tracking
    )
    object PointsRejectd : AppIcon( "Points Rejected",
        R.drawable.location_not_tracking
    )
    object PointsTotal : AppIcon( "Points Total",
        R.drawable.number
    )
    object JumpStatus : AppIcon( "Jump Status",
        R.drawable.help
    )
    object TotalDistance : AppIcon( "Total Distance",
        R.drawable.map
    )
    object SectorDistance : AppIcon( "Sector Distance",
        R.drawable.map
    )
}
