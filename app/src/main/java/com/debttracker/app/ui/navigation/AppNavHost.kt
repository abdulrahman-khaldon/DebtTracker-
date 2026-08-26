package com.debttracker.app.ui.navigation

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.debttracker.app.ui.contact.AddEditContactScreen
import com.debttracker.app.ui.contact.ContactDetailScreen
import com.debttracker.app.ui.home.HomeScreen
import com.debttracker.app.ui.settings.SettingsScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Home.route,
        enterTransition = {
            slideInHorizontally(animationSpec = tween(300)) { it / 3 } + fadeIn(tween(300))
        },
        exitTransition = { fadeOut(tween(250)) },
        popEnterTransition = { fadeIn(tween(250)) },
        popExitTransition = {
            slideOutHorizontally(animationSpec = tween(300)) { it / 3 } + fadeOut(tween(300))
        }
    ) {
        composable(Screen.Home.route) {
            HomeScreen(
                onContactClick = { contactId ->
                    navController.navigate(Screen.ContactDetail.createRoute(contactId))
                },
                onAddContact = {
                    navController.navigate(Screen.AddEditContact.createRoute())
                },
                onEditContact = { contactId ->
                    navController.navigate(Screen.AddEditContact.createRoute(contactId))
                },
                onOpenSettings = {
                    navController.navigate(Screen.Settings.route)
                }
            )
        }

        composable(
            route = Screen.AddEditContact.route,
            arguments = listOf(
                navArgument(Screen.AddEditContact.ARG_CONTACT_ID) {
                    type = NavType.LongType
                    defaultValue = Screen.AddEditContact.NEW_CONTACT
                }
            )
        ) {
            AddEditContactScreen(onBack = { navController.popBackStack() })
        }

        composable(
            route = Screen.ContactDetail.route,
            arguments = listOf(
                navArgument(Screen.ContactDetail.ARG_CONTACT_ID) {
                    type = NavType.LongType
                }
            )
        ) {
            ContactDetailScreen(
                onBack = { navController.popBackStack() },
                onEditContact = { contactId ->
                    navController.navigate(Screen.AddEditContact.createRoute(contactId))
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(onBack = { navController.popBackStack() })
        }
    }
}
