package com.donorpath.app.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.donorpath.app.data.repository.DonorPathRepository
import com.donorpath.app.ui.screens.donate.DonateScreen
import com.donorpath.app.ui.screens.history.HistoryScreen
import com.donorpath.app.ui.screens.home.HomeScreen
import com.donorpath.app.ui.screens.orgdetail.OrganizationDetailScreen
import com.donorpath.app.ui.screens.profile.ProfileScreen
import com.donorpath.app.ui.screens.rewards.RewardsScreen

private data class BottomBarEntry(val screen: Screen, val label: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)

private val bottomBarEntries = listOf(
    BottomBarEntry(Screen.Home, "Home", Icons.Filled.Home),
    BottomBarEntry(Screen.Rewards, "Rewards", Icons.Filled.CardGiftcard),
    BottomBarEntry(Screen.History, "History", Icons.Filled.History),
    BottomBarEntry(Screen.Profile, "Profile", Icons.Filled.Person)
)

@Composable
fun DonorPathNavHost(repository: DonorPathRepository) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            val navBackStackEntry by navController.currentBackStackEntryAsState()
            val currentDestination = navBackStackEntry?.destination

            val showBottomBar = bottomBarEntries.any { entry ->
                currentDestination?.hierarchy?.any { it.route == entry.screen.route } == true
            }

            if (showBottomBar) {
                NavigationBar {
                    bottomBarEntries.forEach { entry ->
                        val selected = currentDestination?.hierarchy?.any { it.route == entry.screen.route } == true
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(entry.screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(entry.icon, contentDescription = entry.label) },
                            label = { Text(entry.label) }
                        )
                    }
                }
            }
        }
    ) { padding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(padding)
        ) {
            composable(Screen.Home.route) {
                HomeScreen(
                    repository = repository,
                    onOrganizationClick = { orgId ->
                        navController.navigate(Screen.OrganizationDetail.createRoute(orgId))
                    }
                )
            }

            composable(Screen.OrganizationDetail.route) { backStackEntry ->
                val orgId = backStackEntry.arguments?.getString("orgId")?.toLongOrNull() ?: return@composable
                OrganizationDetailScreen(
                    repository = repository,
                    orgId = orgId,
                    onDonateClick = { navController.navigate(Screen.Donate.createRoute(it)) },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Donate.route) { backStackEntry ->
                val orgId = backStackEntry.arguments?.getString("orgId")?.toLongOrNull() ?: return@composable
                DonateScreen(
                    repository = repository,
                    orgId = orgId,
                    onDonationComplete = {
                        navController.navigate(Screen.History.route) {
                            popUpTo(Screen.Home.route)
                        }
                    },
                    onBack = { navController.popBackStack() }
                )
            }

            composable(Screen.Rewards.route) {
                RewardsScreen(repository = repository)
            }

            composable(Screen.History.route) {
                HistoryScreen(repository = repository)
            }

            composable(Screen.Profile.route) {
                ProfileScreen(repository = repository)
            }
        }
    }
}
