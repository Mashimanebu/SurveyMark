package com.survey.mark.routing

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.*
import com.survey.mark.auth.domain.AuthState
import com.survey.mark.auth.domain.UserRole
import com.survey.mark.auth.ui.admin.AdminDashboardScreen
import com.survey.mark.auth.ui.AuthViewModel
import com.survey.mark.auth.ui.login.LoginScreen
import com.survey.mark.auth.ui.register.PendingApprovalScreen
import com.survey.mark.auth.ui.register.SignUpScreen
import com.survey.mark.ui.detailScreen.ControlPointDetailScreen
import com.survey.mark.ui.field.FieldNavScreen
import com.survey.mark.ui.home.HomeScreen
import com.survey.mark.ui.log.OccupationLogScreen
import com.survey.mark.ui.newmark.NewMarkScreen
import com.survey.mark.ui.report.ConditionReportScreen

@Composable
fun SurveyMarkNav(
    authViewModel: AuthViewModel = hiltViewModel()
) {
    val navController = rememberNavController()
    val authState by authViewModel.authState.collectAsState()

    fun safeBack() {
        if (navController.previousBackStackEntry != null) {
            navController.popBackStack()
        }
    }

    NavHost(
        navController = navController,
        startDestination = Routes.SPLASH
    ) {

        composable(Routes.SPLASH) {
            LaunchedEffect(authState) {
                when (authState) {

                    is AuthState.Unauthenticated -> {
                        navController.navigate(Routes.LOGIN) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }

                    is AuthState.PendingApproval -> {
                        navController.navigate(Routes.PENDING) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }

                    is AuthState.Authenticated -> {
                        val user = (authState as AuthState.Authenticated).user

                        when (user.role) {

                            UserRole.SURVEYOR_GENERAL -> {
                                navController.navigate(Routes.ADMIN) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            }

                            UserRole.SURVEYOR -> {
                                navController.navigate(Routes.DIRECTORY) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            }

                            else -> {
                                navController.navigate(Routes.LOGIN) {
                                    popUpTo(Routes.SPLASH) { inclusive = true }
                                }
                            }
                        }
                    }

                    else -> Unit
                }
            }
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onNavigateToSignUp = {
                    navController.navigate(Routes.SIGNUP)
                },
                onNavigateToHome = {
                    navController.navigate(Routes.DIRECTORY) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToAdmin = {
                    navController.navigate(Routes.ADMIN) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                },
                onNavigateToPending = {
                    navController.navigate(Routes.PENDING) {
                        popUpTo(Routes.LOGIN) {
                            inclusive = true
                        }
                    }
                }
            )
        }

        composable(Routes.SIGNUP) {
            SignUpScreen(
                onBack = {
                    navController.popBackStack()
                },
                onRegistrationDone = {
                    navController.navigate(Routes.PENDING) {
                        popUpTo(Routes.LOGIN) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PENDING) {
            PendingApprovalScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.PENDING) {
            PendingApprovalScreen(
                onSignOut = {
                    authViewModel.signOut()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

        composable(Routes.ADMIN) {
            AdminDashboardScreen(
                onSignOut = {
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }

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
                onBack = { safeBack() },
                onSubmitSuccess = { safeBack() }
            )
        }

        composable(Routes.DETAIL) { backStackEntry ->
            val markId = backStackEntry.arguments?.getString("controlPointId")
                ?: return@composable

            ControlPointDetailScreen(
                onBack = { safeBack() },
                onNavigateClick = { navController.navigate(Routes.fieldNav(markId)) },
                onReportClick = { navController.navigate(Routes.report(markId)) },
                onLogClick = { navController.navigate(Routes.log(markId)) }
            )
        }

        composable(Routes.REPORT) { backStackEntry ->
            val cpId = backStackEntry.arguments?.getString("controlPointId")
                ?: return@composable

            ConditionReportScreen(
                preselectedControlPointId = cpId,
                onBack = { safeBack() },
                onSubmitSuccess = { safeBack() }
            )
        }

        composable(Routes.LOG) { backStackEntry ->
            val cpId = backStackEntry.arguments?.getString("controlPointId")
                ?: return@composable

            OccupationLogScreen(
                preselectedControlPointId = cpId,
                onBack = { safeBack() }
            )
        }

        composable(Routes.FIELD_NAV) { backStackEntry ->
            val cpId = backStackEntry.arguments?.getString("controlPointId")
                ?: return@composable

            FieldNavScreen(
                controlPointId = cpId,
                onBack = { safeBack() },
                onArrived = { navController.navigate(Routes.report(cpId)) }
            )
        }
    }
}