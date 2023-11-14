
package com.example.lunchtray

import androidx.annotation.StringRes
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.lunchtray.datasource.DataSource
import com.example.lunchtray.ui.AccompanimentMenuScreen
import com.example.lunchtray.ui.CheckoutScreen
import com.example.lunchtray.ui.EntreeMenuScreen
import com.example.lunchtray.ui.OrderViewModel
import com.example.lunchtray.ui.SideDishMenuScreen
import com.example.lunchtray.ui.StartOrderScreen

// TODO: Screen enum
enum class LaunchTrayScreen(
    @StringRes val title: Int
) {
    Start(          title = R.string.app_name),
    Entree(         title = R.string.choose_entree),
    SideDish(       title = R.string.choose_side_dish),
    Accompaniment(  title = R.string.choose_accompaniment),
    Checkout(       title = R.string.order_checkout)
}

// TODO: AppBar
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaunchTrayAppBar(
    @StringRes currentScreen: Int,
    canNavigateBack: Boolean,
    navigateUp: () -> Unit,
    modifier: Modifier = Modifier
) {
    TopAppBar(
        title = { 
              Text(
                 text = stringResource(id = currentScreen) 
              )  
        },
        colors = TopAppBarDefaults.mediumTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        navigationIcon = {
            if (canNavigateBack) {//Si podemos navegar atrás
                IconButton(
                    onClick = {
                        navigateUp()
                    }
                ) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LunchTrayApp(
    viewModel: OrderViewModel = viewModel(),
    navHostController: NavHostController = rememberNavController()
) {

    val backStackEntry by navHostController.currentBackStackEntryAsState()

    val currentScreen = LaunchTrayScreen.valueOf(
        backStackEntry?.destination?.route ?: LaunchTrayScreen.Start.name
    )

    Scaffold(
        topBar = {
            // TODO: AppBar
            LaunchTrayAppBar(
                currentScreen = currentScreen.title,
                canNavigateBack = navHostController.previousBackStackEntry != null,
                navigateUp = { navHostController.navigateUp() }
            )
        }
    ) { innerPadding ->

        val uiState by viewModel.uiState.collectAsState()

        // TODO: Navigation host
        NavHost(
            navController = navHostController,
            startDestination = LaunchTrayScreen.Start.name,
        ){
            this.composable(route = LaunchTrayScreen.Start.name){
                StartOrderScreen(
                    onStartOrderButtonClicked = {
                        navHostController.navigate(LaunchTrayScreen.Entree.name)
                    },
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                )
            }

            this.composable(route = LaunchTrayScreen.Entree.name){
                EntreeMenuScreen(
                    options = DataSource.entreeMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navHostController.popBackStack(
                            navHostController.graph.startDestinationId,
                            inclusive = false
                        )
                    },
                    onNextButtonClicked = {
                        navHostController.navigate(LaunchTrayScreen.SideDish.name)
                    },
                    onSelectionChanged = { entreeItem ->
                        viewModel.updateEntree(entreeItem)
                    },
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)

                )
            }

            this.composable(route = LaunchTrayScreen.SideDish.name){
                SideDishMenuScreen(
                    options = DataSource.sideDishMenuItems,
                    onCancelButtonClicked = {
                       viewModel.resetOrder()
                       navHostController.popBackStack(
                           navHostController.graph.startDestinationId,
                           inclusive = false
                       )
                    },
                    onNextButtonClicked = {
                        navHostController.navigate( LaunchTrayScreen.Checkout.name )
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateSideDish(item)
                    },
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                )
            }

            this.composable(route = LaunchTrayScreen.Accompaniment.name){
                AccompanimentMenuScreen(
                    options = DataSource.accompanimentMenuItems,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navHostController.popBackStack(
                            navHostController.graph.startDestinationId,
                            inclusive = false
                        )
                    },
                    onNextButtonClicked = {
                        navHostController.navigate( LaunchTrayScreen.Checkout.name )
                    },
                    onSelectionChanged = { item ->
                        viewModel.updateAccompaniment(item)
                    },
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(innerPadding)
                )
            }

            this.composable(route = LaunchTrayScreen.Checkout.name){
                CheckoutScreen(
                    orderUiState = uiState,
                    onCancelButtonClicked = {
                        viewModel.resetOrder()
                        navHostController.popBackStack(
                            navHostController.graph.startDestinationId,
                            inclusive = false
                        )
                    },
                    onNextButtonClicked = {
                        viewModel.resetOrder()
                        navHostController.popBackStack(
                            navHostController.graph.startDestinationId,
                            inclusive = false
                        )
                    },
                    modifier = Modifier
                        .verticalScroll(rememberScrollState())
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding(),
                            start = dimensionResource(R.dimen.padding_medium),
                            end = dimensionResource(R.dimen.padding_medium),
                        )
                )
            }

        }
    }
}
