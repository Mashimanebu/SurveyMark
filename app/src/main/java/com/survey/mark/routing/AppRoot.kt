package com.survey.mark.routing

import com.survey.mark.ui.newmark.NewMarkScreen
import androidx.compose.runtime.Composable
import androidx.compose.ui.input.key.Key.Companion.Ro
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.survey.mark.ui.detailScreen.DetailScreen
import com.survey.mark.ui.home.HomeScreen
import dagger.hilt.android.HiltAndroidApp
import okhttp3.Route

@Composable
fun SurveyMarkNav() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Routes.DIRECTORY
    ) {
        composable(Routes.DIRECTORY) {
            HomeScreen(
                navController = navController,
                onControlPointClick = { markId ->
                    navController.navigate(Routes.detail(markId))
                }
            )
        }

        composable(Routes.NEW_MARK) {
            NewMarkScreen(onBack = { navController.popBackStack() })
        }

        composable(
            Routes.DETAIL,
            arguments = listOf(
                navArgument("markId") {
                    type = NavType.StringType
                }
            )) { backStackEntry ->
            val markId = backStackEntry.arguments?.getString("markId") ?: ""

            DetailScreen(
                markId = markId,
                onBack = { navController.popBackStack() },
            )
        }
    }
}
