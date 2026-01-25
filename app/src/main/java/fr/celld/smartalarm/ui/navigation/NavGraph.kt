package fr.celld.smartalarm.ui.navigation

/**
 * Navigation routes definition in the application
 */
object NavRoutes {
    const val ALARM_LIST = "alarm_list"
    const val ALARM_EDIT = "alarm_edit"
    const val ALARM_DETAIL = "alarm_detail/{alarmId}"
    const val SETTINGS = "settings"

    fun alarmDetail(alarmId: Long): String = "alarm_detail/$alarmId"
}

/**
 * Navigation destinations for the navigation bar
 */
sealed class Screen(val route: String, val title: String) {
    data object AlarmList : Screen(NavRoutes.ALARM_LIST, "Alarmes")
    data object Settings : Screen(NavRoutes.SETTINGS, "Paramètres")
}
