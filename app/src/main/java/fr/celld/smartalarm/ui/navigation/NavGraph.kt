package fr.celld.smartalarm.ui.navigation

/**
 * Définition des routes de navigation dans l'application
 */
object NavRoutes {
    const val ALARM_LIST = "alarm_list"
    const val ALARM_EDIT = "alarm_edit"
    const val ALARM_DETAIL = "alarm_detail/{alarmId}"
    const val SETTINGS = "settings"

    fun alarmDetail(alarmId: Long): String = "alarm_detail/$alarmId"
}

/**
 * Destinations de navigation pour la barre de navigation
 */
sealed class Screen(val route: String, val title: String) {
    data object AlarmList : Screen(NavRoutes.ALARM_LIST, "Alarmes")
    data object Settings : Screen(NavRoutes.SETTINGS, "Paramètres")
}
