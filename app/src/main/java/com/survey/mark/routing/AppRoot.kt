package com.survey.mark.routing

import com.survey.mark.ui.newmark.NewMarkScreen
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.survey.mark.ui.home.HomeScreen

@Composable
fun SurveyMarkApp() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DIRECTORY
    ) {
        composable(Routes.DIRECTORY) {
            HomeScreen(navController)
        }

        composable(Routes.NEW_MARK) {
            NewMarkScreen(onBack = { navController.popBackStack() })
        }
    }
}