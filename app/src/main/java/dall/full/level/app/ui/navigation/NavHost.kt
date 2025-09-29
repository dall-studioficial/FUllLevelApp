package dall.full.level.app.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import dall.full.level.app.ui.screens.ClinometerScreen
import dall.full.level.app.ui.screens.InfoScreen
import dall.full.level.app.ui.screens.SettingsScreen
import dall.full.level.app.viewmodel.InfoViewModel

@Composable
fun AppNavHost(navController: NavHostController) {
    NavHost(navController = navController, startDestination = Destinations.CLINOMETER) {
        composable(Destinations.CLINOMETER) {
            ClinometerScreen(
                onInfoClick = {
                    navController.navigate(Destinations.INFO)
                },
                onSettingsClick = {
                    navController.navigate(Destinations.SETTINGS)
                }
            )
        }
        composable(Destinations.INFO) {
            val infoViewModel: InfoViewModel = viewModel()
            InfoScreen(
                viewModel = infoViewModel,
                onBack = { navController.popBackStack() }
            )
        }
        composable(Destinations.SETTINGS) {
            SettingsScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}
