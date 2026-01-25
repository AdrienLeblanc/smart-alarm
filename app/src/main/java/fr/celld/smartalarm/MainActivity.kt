package fr.celld.smartalarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.navigationsuite.NavigationSuiteScaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import fr.celld.smartalarm.data.local.AlarmDatabase
import fr.celld.smartalarm.data.preferences.AppPreferences
import fr.celld.smartalarm.data.repository.AlarmRepository
import fr.celld.smartalarm.service.AlarmScheduler
import fr.celld.smartalarm.ui.navigation.NavRoutes
import fr.celld.smartalarm.ui.screens.AlarmEditScreen
import fr.celld.smartalarm.ui.screens.AlarmListScreen
import fr.celld.smartalarm.ui.screens.SettingsScreen
import fr.celld.smartalarm.ui.theme.SmartAlarmTheme
import fr.celld.smartalarm.ui.viewmodel.AlarmEditViewModel
import fr.celld.smartalarm.ui.viewmodel.AlarmListViewModel
import fr.celld.smartalarm.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Dependencies initialization
        val database = AlarmDatabase.getDatabase(applicationContext)
        val repository = AlarmRepository(database.alarmDao())
        val preferences = AppPreferences(applicationContext)
        val alarmScheduler = AlarmScheduler(applicationContext)

        // ViewModels creation
        val alarmListViewModel = AlarmListViewModel(repository, alarmScheduler)
        val alarmEditViewModel = AlarmEditViewModel(repository, alarmScheduler)
        val settingsViewModel = SettingsViewModel(preferences)

        setContent {
            SmartAlarmTheme {
                SmartAlarmApp(
                    alarmListViewModel = alarmListViewModel,
                    alarmEditViewModel = alarmEditViewModel,
                    settingsViewModel = settingsViewModel
                )
            }
        }
    }
}

@Composable
fun SmartAlarmApp(
    alarmListViewModel: AlarmListViewModel,
    alarmEditViewModel: AlarmEditViewModel,
    settingsViewModel: SettingsViewModel
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationSuiteScaffold(
        navigationSuiteItems = {
            item(
                icon = {
                    Icon(
                        Icons.Default.Home,
                        contentDescription = "Alarmes"
                    )
                },
                label = { Text("Alarmes") },
                selected = currentRoute == NavRoutes.ALARM_LIST || currentRoute?.startsWith("alarm_edit") == true,
                onClick = {
                    navController.navigate(NavRoutes.ALARM_LIST) {
                        popUpTo(NavRoutes.ALARM_LIST) { inclusive = true }
                        launchSingleTop = true
                    }
                }
            )
            item(
                icon = {
                    Icon(
                        Icons.Default.Settings,
                        contentDescription = "Paramètres"
                    )
                },
                label = { Text("Paramètres") },
                selected = currentRoute == NavRoutes.SETTINGS,
                onClick = {
                    navController.navigate(NavRoutes.SETTINGS) {
                        popUpTo(NavRoutes.ALARM_LIST)
                        launchSingleTop = true
                    }
                }
            )
        }
    ) {
        Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
            NavHost(
                navController = navController,
                startDestination = NavRoutes.ALARM_LIST,
                modifier = Modifier.padding(innerPadding)
            ) {
                composable(NavRoutes.ALARM_LIST) {
                    AlarmListScreen(
                        viewModel = alarmListViewModel,
                        onAddAlarm = {
                            navController.navigate(NavRoutes.ALARM_EDIT)
                        },
                        onEditAlarm = { alarmId ->
                            navController.navigate("alarm_edit?alarmId=$alarmId")
                        }
                    )
                }

                composable(
                    route = "alarm_edit?alarmId={alarmId}",
                    arguments = listOf(
                        navArgument("alarmId") {
                            type = NavType.LongType
                            defaultValue = 0L
                        }
                    )
                ) { backStackEntry ->
                    val alarmId = backStackEntry.arguments?.getLong("alarmId")
                    AlarmEditScreen(
                        viewModel = alarmEditViewModel,
                        alarmId = alarmId,
                        onNavigateBack = { navController.popBackStack() }
                    )
                }

                composable(NavRoutes.SETTINGS) {
                    SettingsScreen(viewModel = settingsViewModel)
                }
            }
        }
    }
}
