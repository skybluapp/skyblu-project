package com.skyblu.userinterface

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.skyblu.configuration.Concept
import com.skyblu.models.jump.JumpParams
import com.skyblu.models.jump.UserParameterNames
import com.skyblu.uicomponants.componants.SplashScreen

import com.skyblu.userinterface.screens.*
import com.skyblu.userinterface.screens.settingsScreens.AccountSettingsScreen
import com.skyblu.userinterface.screens.settingsScreens.TrackingSettingsScreen
import com.skyblu.userinterface.viewmodels.HomeViewModel
import com.skyblu.userinterface.viewmodels.ProfileViewModel
import com.skyblu.userinterface.viewmodels.UniversalViewModel

/**
 * @author Oliver Stocks
 * [Documentation](https://developer.android.com/jetpack/compose/navigation)
 * Manages Navigation for Skyblu
 */
@Composable
fun Navigation(
) {
    val homeViewModel: HomeViewModel = hiltViewModel()
    val profileViewModel: ProfileViewModel = hiltViewModel()
    val navHostController: NavHostController = rememberNavController()
    val universalViewModel : UniversalViewModel = hiltViewModel()


    NavHost(
        navController = navHostController,
        startDestination = Concept.Splash.route,
    ) {

        /**
         * Splash Screen Composable
         */
        composable(Concept.Splash.route) {
            SplashScreen(navController = navHostController)
        }

        /**
         * Logged In Destinations
         */
        navigation(
            route = Concept.LoggedIn.route,
            startDestination = Concept.Home.route
        ) {

            composable(Concept.Home.route) {
                HomeScreen(
                    navController = navHostController,
                    viewModel = homeViewModel,
                    universalViewModel = universalViewModel
                )
            }

            composable(Concept.TrackSkydive.route) {
                TrackingScreen(
                    navController = navHostController,
                )
            }
            composable(route = "${Concept.Map.route}/{${JumpParams.JUMP_ID}}") { backStackEntry ->
                backStackEntry.arguments?.getString(JumpParams.JUMP_ID)
                    ?.let { jumpID ->
                        MapScreen(
                            navController = navHostController,
                            jumpID = jumpID
                        )
                    }
            }

            composable("${Concept.Profile.route}{${UserParameterNames.ID}}") { backStackEntry ->
                backStackEntry.arguments?.getString(UserParameterNames.ID)
                    ?.let {
                        ProfileScreen(
                            navController = navHostController,
                            userID = it,
                            viewModel = profileViewModel,
                            universalViewModel = universalViewModel
                        )
                    }
            }

            composable(Concept.Edit.route) {
                EditScreen(navController = navHostController)
            }
        }
        /**
         * Logged Out Destinations
         */
        navigation(
            route = Concept.LoggedOut.route,
            startDestination = Concept.Welcome.route
        ) {
            composable(Concept.Welcome.route) { WelcomeScreen(navController = navHostController) }
            composable(route = Concept.Login.route) { LoginScreen(navController = navHostController) }
            composable(Concept.CreateAccount.route) { CreateAccountScreen(navController = navHostController) }
        }
        /**
         * Settings Destinations
         */
        navigation(
            route = Concept.Settings.route,
            startDestination = Concept.Settings.route + Concept.Home.route
        ) {
            composable(Concept.Settings.route + Concept.Home.route) { SettingsScreen(navController = navHostController) }
            composable(route = Concept.LocationTracking.route + Concept.Settings.route) { TrackingSettingsScreen(navController = navHostController) }
            composable(Concept.Account.route + Concept.Settings.route) { AccountSettingsScreen(navController = navHostController) }
        }

    }
}


