package com.donorpath.app.ui.navigation

sealed class Screen(val route: String) {
    data object Home : Screen("home")
    data object Rewards : Screen("rewards")
    data object History : Screen("history")
    data object Profile : Screen("profile")

    data object OrganizationDetail : Screen("organization/{orgId}") {
        fun createRoute(orgId: Long) = "organization/$orgId"
    }

    data object Donate : Screen("donate/{orgId}") {
        fun createRoute(orgId: Long) = "donate/$orgId"
    }

    companion object {
        val bottomBarScreens = listOf(Home, Rewards, History, Profile)
    }
}
