package com.rupiksha.distributer.presentation.navigation

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.rupiksha.distributer.di.AppContainer
import com.rupiksha.distributer.presentation.auth.login.LoginScreen
import com.rupiksha.distributer.presentation.auth.register.RegistrationScreen
import com.rupiksha.distributer.presentation.dashboard.DashboardScreen

private const val TRANSITION_DURATION = 350
private val transitionEasing = FastOutSlowInEasing

@Composable
fun AppNavGraph(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    appContainer: AppContainer
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Login.route,
        modifier = modifier,
        // Fallback for any transition not explicitly overridden below.
        enterTransition = {
            fadeIn(tween(TRANSITION_DURATION, easing = transitionEasing))
        },
        exitTransition = {
            fadeOut(tween(TRANSITION_DURATION, easing = transitionEasing))
        },
        popEnterTransition = {
            fadeIn(tween(TRANSITION_DURATION, easing = transitionEasing))
        },
        popExitTransition = {
            fadeOut(tween(TRANSITION_DURATION, easing = transitionEasing))
        }
    ) {
        composable(
            route = Screen.Login.route,
            enterTransition = {
                // Coming back to Login after a successful registration or a logout:
                // a gentle settle-in rather than a hard cut.
                fadeIn(tween(TRANSITION_DURATION, easing = transitionEasing)) +
                        scaleIn(
                            initialScale = 0.96f,
                            animationSpec = tween(TRANSITION_DURATION, easing = transitionEasing)
                        )
            },
            exitTransition = {
                // Moving forward into Dashboard or Register.
                fadeOut(tween(TRANSITION_DURATION, easing = transitionEasing)) +
                        scaleOut(
                            targetScale = 0.96f,
                            animationSpec = tween(TRANSITION_DURATION, easing = transitionEasing)
                        )
            }
        ) {
            LoginScreen(
                appContainer = appContainer,
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) { inclusive = true }
                    }
                },
                onRegisterClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }
        composable(
            route = Screen.Register.route,
            enterTransition = {
                // Pushed in from Login: slide in from the right, like moving forward in a flow.
                slideInHorizontally(
                    animationSpec = tween(TRANSITION_DURATION, easing = transitionEasing)
                ) { fullWidth -> fullWidth } + fadeIn(tween(TRANSITION_DURATION, easing = transitionEasing))
            },
            exitTransition = {
                // Registration success navigates forward to Login: slide out to the left.
                slideOutHorizontally(
                    animationSpec = tween(TRANSITION_DURATION, easing = transitionEasing)
                ) { fullWidth -> -fullWidth / 3 } + fadeOut(tween(TRANSITION_DURATION, easing = transitionEasing))
            },
            popExitTransition = {
                // User taps back: slide back out to the right, matching the forward entry direction.
                slideOutHorizontally(
                    animationSpec = tween(TRANSITION_DURATION, easing = transitionEasing)
                ) { fullWidth -> fullWidth } + fadeOut(tween(TRANSITION_DURATION, easing = transitionEasing))
            }
        ) {
            RegistrationScreen(
                appContainer = appContainer,
                onNavigateBack = {
                    navController.popBackStack()
                },
                onRegistrationSuccess = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Register.route) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = Screen.Dashboard.route,
            enterTransition = {
                // Login success lands on Dashboard: fade + soft upward slide feels like an arrival.
                fadeIn(tween(TRANSITION_DURATION, easing = transitionEasing)) +
                        slideInHorizontally(
                            animationSpec = tween(TRANSITION_DURATION, easing = transitionEasing)
                        ) { fullWidth -> fullWidth / 4 }
            },
            exitTransition = {
                // Logout moves back to Login.
                fadeOut(tween(TRANSITION_DURATION, easing = transitionEasing))
            }
        ) {
            DashboardScreen(
                appContainer = appContainer,
                onLogout = {
                    navController.navigate(Screen.Login.route) {
                        popUpTo(Screen.Dashboard.route) { inclusive = true }
                    }
                }
            )
        }
    }
}