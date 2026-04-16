package com.solosystem.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.*
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import com.solosystem.app.ui.screens.*
import com.solosystem.app.ui.theme.*
import com.solosystem.app.viewmodel.AppViewModel
import com.solosystem.app.worker.QuestReminderWorker

data class NavItem(val route: String, val icon: ImageVector, val label: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        QuestReminderWorker.scheduleDailyReminder(this, hour = 9, minute = 0)
        setContent {
            SoloSystemTheme {
                SoloSystemApp()
            }
        }
    }
}

@Composable
fun SoloSystemApp() {
    val vm: AppViewModel = viewModel()
    val profile by vm.userProfile.collectAsState()
    val navController = rememberNavController()

    // Check penalty on launch
    LaunchedEffect(Unit) { vm.checkPenalty() }

    // Show onboarding if no profile
    if (profile == null) {
        OnboardingScreen(vm)
        return
    }

    val navItems = listOf(
        NavItem("dashboard",   Icons.Default.Home,        "Home"),
        NavItem("quests",      Icons.Default.CheckCircle, "Quests"),
        NavItem("stats",       Icons.Default.BarChart,    "Stats"),
        NavItem("inventory",   Icons.Default.Star,        "Inventory"),
        NavItem("leaderboard", Icons.Default.EmojiEvents, "Ranks"),
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route ?: "dashboard"

    Scaffold(
        containerColor = Black,
        bottomBar = {
            NavigationBar(containerColor = DeepNavy, tonalElevation = 0.dp) {
                navItems.forEach { item ->
                    val selected = currentRoute == item.route
                    val rc = Color(profile!!.rank.color)
                    NavigationBarItem(
                        selected = selected,
                        onClick  = { navController.navigate(item.route) { launchSingleTop = true; popUpTo("dashboard") } },
                        icon     = { Icon(item.icon, item.label, modifier = Modifier.size(if(selected) 26.dp else 22.dp)) },
                        label    = { Text(item.label, fontSize = 9.sp, fontWeight = if(selected) FontWeight.ExtraBold else FontWeight.Normal, letterSpacing = 0.5.sp) },
                        colors   = NavigationBarItemDefaults.colors(
                            selectedIconColor   = rc,
                            selectedTextColor   = rc,
                            unselectedIconColor = Color(0xFF4A5568),
                            unselectedTextColor = Color(0xFF4A5568),
                            indicatorColor      = rc.copy(alpha = 0.15f)
                        )
                    )
                }
            }
        }
    ) { padding ->
        Box(Modifier.fillMaxSize().padding(padding)) {
            NavHost(navController, startDestination = "dashboard") {
                composable("dashboard")   { DashboardScreen(vm)  { route -> navController.navigate(route) } }
                composable("quests")      { QuestsScreen(vm) }
                composable("stats")       { StatsScreen(vm) }
                composable("inventory")   { InventoryScreen(vm) }
                composable("leaderboard") { LeaderboardScreen(vm) }
            }
        }
    }
}