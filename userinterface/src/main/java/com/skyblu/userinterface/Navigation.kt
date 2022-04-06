package com.skyblu.userinterface

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.skyblu.uicomponants.componants.SplashScreen
import com.skyblu.userinterface.componants.Concept
import com.skyblu.userinterface.screens.*
import com.skyblu.userinterface.screens.settingsScreens.AccountSettingsScreen
import com.skyblu.userinterface.screens.settingsScreens.TrackingSettingsScreen
import com.skyblu.userinterface.viewmodels.HomeViewModel

@Composable
fun Navigation(
) {
    val vm : HomeViewModel = hiltViewModel()
    val navHostController: NavHostController = rememberNavController()
    val context = LocalContext.current


    NavHost(
        navController = navHostController,
        startDestination = Concept.Splash.route,
    ) {



        composable(Concept.Splash.route) {
            SplashScreen(navController = navHostController)
        }



        navigation(
            route = Concept.LoggedIn.route,
            startDestination = Concept.Home.route
        ) {
            composable(Concept.Home.route) {
                HomeScreen(navController = navHostController, viewModel =  vm)
            }
            composable(Concept.Profile.route) {
                ProfileScreen(navController = navHostController)
            }

            composable(Concept.TrackSkydive.route) {
                TrackingScreen(
                    navController = navHostController,
                )
            }
            composable("${Concept.Map.route}/{${context.getString(R.string.skydive_ID)}}") { backStackEntry ->
                backStackEntry.arguments?.getString(stringResource(R.string.skydive_ID))
                    ?.let {
                        MapScreen(
                            navController = navHostController,
                            it
                        )
                    }
            }





            composable(Concept.Settings.route) {
                SettingsScreen(navController = navHostController)
            }
            composable(Concept.LocationTracking.route + Concept.Settings.route) {
                TrackingSettingsScreen(navController = navHostController)
            }
            composable(Concept.Account.route + Concept.Settings.route) {
                AccountSettingsScreen(navController = navHostController)
            }


        }





        navigation(
            route = Concept.LoggedOut.route,
            startDestination = Concept.Welcome.route
        ) {
            composable(Concept.Welcome.route) { WelcomeScreen(navController = navHostController)}
            composable(route = Concept.Login.route) { LoginScreen(navController = navHostController) }
            composable(Concept.CreateAccount.route) { CreateAccountScreen(navController = navHostController) }
        }
    }
}


