package com.example.invoicegenerator.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import org.koin.compose.viewmodel.koinViewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.invoicegenerator.ui.screens.BusinessSetupScreen
import com.example.invoicegenerator.ui.screens.CustomersScreen
import com.example.invoicegenerator.ui.screens.DashboardScreen
import com.example.invoicegenerator.ui.screens.InvoiceCreateScreen
import com.example.invoicegenerator.ui.screens.InvoicePreviewScreen
import com.example.invoicegenerator.ui.screens.InvoicesListScreen
import com.example.invoicegenerator.ui.screens.ItemsScreen
import com.example.invoicegenerator.ui.screens.SettingsScreen
import com.example.invoicegenerator.ui.screens.WelcomeScreen
import com.example.invoicegenerator.ui.screens.LanguageSelectionScreen
import com.example.invoicegenerator.viewmodel.BusinessViewModel
import com.example.invoicegenerator.viewmodel.SettingsViewModel

@Composable
fun NavGraph() {
    val navController: NavHostController = rememberNavController()
    val businessViewModel: BusinessViewModel = koinViewModel()
    val settingsViewModel: SettingsViewModel = koinViewModel()
    val businessProfile by businessViewModel.businessProfile.collectAsState()
    val language by settingsViewModel.language.collectAsState(initial = null)

    androidx.compose.runtime.LaunchedEffect(language) {
        language?.let {
            com.example.invoicegenerator.getPlatform().setLanguage(it)
        }
    }

    NavHost(
        navController = navController,
        startDestination = if (businessProfile == null) Screen.Welcome.route else Screen.Dashboard.route
    ) {
        composable(Screen.Welcome.route) {
            WelcomeScreen(
                onCreateInvoice = {
                    if (language == null) {
                        navController.navigate(Screen.LanguageSelection.route)
                    } else if (businessProfile == null) {
                        navController.navigate(Screen.BusinessSetup.route)
                    } else {
                        navController.navigate(Screen.CreateInvoice.route)
                    }
                },
                onViewSample = {
                    navController.navigate(Screen.InvoicePreview.createRoute(-1L))
                }
            )
        }

        composable(Screen.LanguageSelection.route) {
            LanguageSelectionScreen(
                onLanguageSelected = {
                    if (businessProfile == null) {
                        navController.navigate(Screen.BusinessSetup.route) {
                            popUpTo(Screen.Welcome.route) { inclusive = false }
                        }
                    } else {
                        navController.popBackStack()
                    }
                }
            )
        }

        composable(Screen.BusinessSetup.route) {
            BusinessSetupScreen(
                onSetupComplete = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Welcome.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNewInvoice = { navController.navigate(Screen.CreateInvoice.route) },
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.CreateInvoice.route) {
            InvoiceCreateScreen(
                onPreview = { invoiceId ->
                    navController.navigate(Screen.InvoicePreview.createRoute(invoiceId))
                },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Screen.InvoicePreview.route) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId")?.toLongOrNull() ?: 0L
            InvoicePreviewScreen(
                invoiceId = invoiceId,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Screen.Invoices.route) {
            InvoicesListScreen(
                onInvoiceClick = { id -> navController.navigate(Screen.InvoicePreview.createRoute(id)) },
                onNewInvoice = { navController.navigate(Screen.CreateInvoice.route) },
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Customers.route) {
            CustomersScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Items.route) {
            ItemsScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) { saveState = true }
                        launchSingleTop = true
                        restoreState = true
                    }
                }
            )
        }

        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateTo = { route ->
                    navController.navigate(route) {
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                onEditBusiness = {
                    navController.navigate(Screen.BusinessSetup.route)
                }
            )
        }


    }
}
