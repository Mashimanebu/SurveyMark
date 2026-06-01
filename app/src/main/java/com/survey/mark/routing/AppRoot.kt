package com.survey.mark.routing

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.survey.mark.ui.detailScreen.ControlPointDetailScreen
import com.survey.mark.ui.field.FieldNavScreen
import com.survey.mark.ui.home.HomeScreen
import com.survey.mark.ui.log.OccupationLogScreen
import com.survey.mark.ui.newmark.NewMarkScreen
import com.survey.mark.ui.report.ConditionReportScreen

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
            NewMarkScreen(
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(Routes.DETAIL) { backStackEntry ->
            val markId = backStackEntry.arguments?.getString("controlPointId") ?: return@composable
            ControlPointDetailScreen(
                markId = markId,
                onBack = { navController.popBackStack() },
                onNavigateClick = { navController.navigate(Routes.fieldNav(markId)) },
                onReportClick = { navController.navigate(Routes.report(markId)) },
                onLogClick = { navController.navigate(Routes.log(markId)) },
            )
        }

        composable(Routes.REPORT) { backStackEntry ->
            val cpId = backStackEntry.arguments?.getString("controlPointId") ?: return@composable
            ConditionReportScreen(
                preselectedControlPointId = cpId,
                onBack = { navController.popBackStack() },
                onSubmitSuccess = { navController.popBackStack() }
            )
        }

        composable(Routes.LOG) { backStackEntry ->
            val cpId = backStackEntry.arguments?.getString("controlPointId") ?: return@composable
            OccupationLogScreen(
                preselectedControlPointId = cpId,
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.FIELD_NAV) { backStackEntry ->
            val cpId = backStackEntry.arguments?.getString("controlPointId") ?: return@composable
            FieldNavScreen(
                controlPointId = cpId,
                onBack = { navController.popBackStack() },
                onArrived = { navController.navigate(Routes.report(cpId)) }
            )
        }
    }
}